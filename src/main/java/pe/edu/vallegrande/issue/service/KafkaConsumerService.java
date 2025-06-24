package pe.edu.vallegrande.issue.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.edu.vallegrande.issue.dto.WorkshopKafkaEventDto;
import pe.edu.vallegrande.issue.model.Workshop;
import pe.edu.vallegrande.issue.repository.WorkshopRepository;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final WorkshopRepository workshopRepository;
    private final ObjectMapper objectMapper;
    private final R2dbcEntityTemplate template;

    /**
     * 🔹 Escucha eventos del topic "workshop-events" y sincroniza la información.
     */
    @KafkaListener(topics = "workshop-events", groupId = "issue-group")
    public void consumeWorkshopEvent(ConsumerRecord<String, String> consumerRecord) {
        try {
            String json = record.value();
            WorkshopKafkaEventDto dto = objectMapper.readValue(json, WorkshopKafkaEventDto.class);
            log.info("📥 Recibido evento Kafka: {}", dto);

            // 🔄 Construye la entidad Workshop desde el DTO
            Workshop workshop = Workshop.builder()
                    .id(dto.getId())
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .startDate(dto.getStartDate())
                    .endDate(dto.getEndDate())
                    .state(dto.getState())
                    .build();

            // 💾 Si ya existe, actualiza; si no, inserta nuevo registro
            workshopRepository.findById(dto.getId())
                    .flatMap(existing -> {
                        existing.setName(workshop.getName());
                        existing.setDescription(workshop.getDescription());
                        existing.setStartDate(workshop.getStartDate());
                        existing.setEndDate(workshop.getEndDate());
                        existing.setState(workshop.getState());
                        return workshopRepository.save(existing); // ✅ UPDATE
                    })
                    .switchIfEmpty((Mono<? extends Workshop>) template.insert(Workshop.class).using(workshop)) // 👈
                                                                                                               // Cast
                                                                                                               // explícito
                    .subscribe(saved -> log.info("✅ Workshop insertado/actualizado: {}", saved));

        } catch (Exception e) {
            log.error("❌ Error procesando evento Kafka: {}", e.getMessage(), e);
        }
    }

}
