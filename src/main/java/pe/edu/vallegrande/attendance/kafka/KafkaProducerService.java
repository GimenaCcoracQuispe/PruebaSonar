package pe.edu.vallegrande.attendance.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.attendance.model.event.AttendanceEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "attendance-events";

    public void sendAttendanceEvent(AttendanceEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, event.getId().toString(), json);
            log.info("Evento enviado a Kafka ({}): {}", TOPIC, event);
        } catch (Exception e) {
            log.error("Error al enviar evento Kafka: {}", e.getMessage(), e);
        }
    }
}
