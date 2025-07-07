package pe.edu.vallegrande.issue.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.core.ReactiveInsertOperation;
import pe.edu.vallegrande.issue.model.event.WorkshopEvent;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class KafkaConsumerServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private R2dbcEntityTemplate template;

    @Mock
    private ReactiveInsertOperation.ReactiveInsert<WorkshopEvent> reactiveInsert;

    @InjectMocks
    private pe.edu.vallegrande.issue.kafka.KafkaConsumer kafkaConsumer;

    private WorkshopEvent workshopEvent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        workshopEvent = WorkshopEvent.builder()
                .id(1L)
                .name("Test Workshop")
                .description("Description")
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 1, 30))
                .state("A")
                .build();
    }

    @Test
    void testConsumeWorkshopEvent_InsertNewWorkshop() throws Exception {
        ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<>("workshop-events", 0, 0L, null, "json-body");

        when(objectMapper.readValue("json-body", WorkshopEvent.class)).thenReturn(workshopEvent);
        when(template.selectOne(any(), eq(WorkshopEvent.class))).thenReturn(Mono.empty());
        when(template.insert(WorkshopEvent.class)).thenReturn(reactiveInsert);
        when(reactiveInsert.using(any(WorkshopEvent.class))).thenReturn(Mono.just(workshopEvent));

        kafkaConsumer.consumeWorkshopEvent(consumerRecord );

        verify(template).insert(WorkshopEvent.class);
        verify(reactiveInsert).using(any(WorkshopEvent.class));
    }

    @Test
    void testConsumeWorkshopEvent_ThrowsException() throws Exception {
        ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<>("workshop-events", 0, 0L, null, "invalid-json");

        when(objectMapper.readValue("invalid-json", WorkshopEvent.class))
                .thenThrow(new RuntimeException("Invalid JSON"));

        kafkaConsumer.consumeWorkshopEvent(consumerRecord );

        verify(template, never()).insert(any());
        verify(template, never()).update(any());
    }


}