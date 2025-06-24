package pe.edu.vallegrande.issue.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.edu.vallegrande.issue.model.event.WorkshopEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {
    private final ObjectMapper objectMapper;
    private final R2dbcEntityTemplate template;

    /**
     * 🔹 Escucha eventos del topic "workshop-events" y sincroniza la información.
     */
    @KafkaListener(topics = "workshop-events", groupId = "issue-group")
    public void consumeWorkshopEvent(ConsumerRecord<String, String> consumerRecord) {
        try {
            String json = consumerRecord.value();
            WorkshopEvent dto = objectMapper.readValue(json, WorkshopEvent.class);
            log.info("📥 Recibido evento Kafka: {}", dto);

            WorkshopEvent workshop = WorkshopEvent.builder()
                    .id(dto.getId())
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .startDate(dto.getStartDate())
                    .endDate(dto.getEndDate())
                    .state(dto.getState())
                    .build();

            // Upsert manual usando solo R2dbcEntityTemplate
            template.selectOne(Query.query(Criteria.where("id").is(dto.getId())), WorkshopEvent.class)
                    .flatMap(existing -> {
                        // Si existe, actualiza
                        existing.setName(workshop.getName());
                        existing.setDescription(workshop.getDescription());
                        existing.setStartDate(workshop.getStartDate());
                        existing.setEndDate(workshop.getEndDate());
                        existing.setState(workshop.getState());
                        return template.update(existing);
                    })
                    .switchIfEmpty(template.insert(WorkshopEvent.class).using(workshop))
                    .subscribe(saved -> log.info("✅ Workshop insertado/actualizado: {}", saved));

        } catch (Exception e) {
            log.error("❌ Error procesando evento Kafka: {}", e.getMessage(), e);
        }
    }
}