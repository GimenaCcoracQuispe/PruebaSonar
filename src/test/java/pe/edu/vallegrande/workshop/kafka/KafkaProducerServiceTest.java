package pe.edu.vallegrande.workshop.kafka;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import pe.edu.vallegrande.workshop.dto.WorkshopKafkaEventDto;

public class KafkaProducerServiceTest {
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        kafkaProducerService = new KafkaProducerService(kafkaTemplate, objectMapper);
    }

    @Test
    void testSendWorkshopEvent_Success() throws Exception {
        // Arrange
        WorkshopKafkaEventDto eventDto = new WorkshopKafkaEventDto();
        eventDto.setId(1L);
        eventDto.setName("Curso de Kafka");
        eventDto.setState("A");

        String expectedMessage = objectMapper.writeValueAsString(eventDto);

        // Act
        kafkaProducerService.sendWorkshopEvent(eventDto);

        // Assert
        verify(kafkaTemplate, times(1)).send("workshop-events", "1", expectedMessage);
    }

    @Test
    void testSendWorkshopEvent_ThrowsException() throws Exception {
        // Arrange
        WorkshopKafkaEventDto eventDto = mock(WorkshopKafkaEventDto.class);
        when(eventDto.getId()).thenReturn(1L);
        when(objectMapper.writeValueAsString(eventDto)).thenThrow(new RuntimeException("Serialization failed"));

        kafkaProducerService = new KafkaProducerService(kafkaTemplate, objectMapper);

        // Act
        kafkaProducerService.sendWorkshopEvent(eventDto);

        // Assert
        verify(kafkaTemplate, never()).send(anyString(), anyString(), anyString());
    }
}
