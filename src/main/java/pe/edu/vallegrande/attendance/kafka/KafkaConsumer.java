package pe.edu.vallegrande.attendance.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.edu.vallegrande.attendance.model.event.IssueEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {
    private final ObjectMapper objectMapper;
    private final R2dbcEntityTemplate template;

    /**
     * 🔹 Escucha eventos del topic "issue-events" y sincroniza la información.
     */
    @KafkaListener(topics = "issue-events", groupId = "attendance-group")
    public void consumeWorkshopEvent(ConsumerRecord<String, String> consumerRecord) {
        try {
            String json = consumerRecord.value();
            IssueEvent dto = objectMapper.readValue(json, IssueEvent.class);
            log.info("📥 Recibido evento Kafka: {}", dto);

            IssueEvent issue = IssueEvent.builder()
                    .id(dto.getId())
                    .name(dto.getName())
                    .workshopId(dto.getWorkshopId())
                    .sesion(dto.getSesion())
                    .scheduledTime(dto.getScheduledTime())
                    .state(dto.getState()) 
                    .build();

            // Upsert manual usando solo R2dbcEntityTemplate
            template.selectOne(Query.query(Criteria.where("id").is(dto.getId())), IssueEvent.class)
                    .flatMap(existing -> {
                        // Si existe, actualiza
                        existing.setName(issue.getName());
                        existing.setWorkshopId(issue.getWorkshopId());
                        existing.setSesion(issue.getSesion());
                        existing.setScheduledTime(issue.getScheduledTime());
                        existing.setState(issue.getState());
                        return template.update(existing);
                    })
                    .switchIfEmpty(template.insert(IssueEvent.class).using(issue))
                    .subscribe(saved -> log.info("✅ Issue insertado/actualizado: {}", saved));

        } catch (Exception e) {
            log.error("❌ Error procesando evento Kafka: {}", e.getMessage(), e);
        }
    }
}
