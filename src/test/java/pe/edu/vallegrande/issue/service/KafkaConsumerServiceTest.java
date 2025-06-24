package pe.edu.vallegrande.issue.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import pe.edu.vallegrande.issue.dto.WorkshopKafkaEventDto;
import pe.edu.vallegrande.issue.model.Workshop;
import pe.edu.vallegrande.issue.repository.WorkshopRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

class KafkaConsumerServiceTest {

    @Mock
    private WorkshopRepository workshopRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private R2dbcEntityTemplate template;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    private WorkshopKafkaEventDto dto;
    private Workshop workshop;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        dto = WorkshopKafkaEventDto.builder()
                .id(1L)
                .name("Test Workshop")
                .description("Description")
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 1, 30))
                .state("A")
                .build();

        workshop = Workshop.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .state(dto.getState())
                .build();
    }

    @Test
    void testConsumeWorkshopEvent_UpdateExistingWorkshop() throws Exception {
        ConsumerRecord<String, String> record = new ConsumerRecord<>("workshop-events", 0, 0L, null, "json-body");

        when(objectMapper.readValue("json-body", WorkshopKafkaEventDto.class)).thenReturn(dto);
        when(workshopRepository.findById(1L)).thenReturn(Mono.just(workshop));
        when(workshopRepository.save(any(Workshop.class))).thenReturn(Mono.just(workshop));

        kafkaConsumerService.consumeWorkshopEvent(record);

        verify(workshopRepository).save(any(Workshop.class));
    }

    @Test
    void testConsumeWorkshopEvent_InsertNewWorkshop() throws Exception {
        ConsumerRecord<String, String> record = new ConsumerRecord<>("workshop-events", 0, 0L, null, "json-body");

        when(objectMapper.readValue("json-body", WorkshopKafkaEventDto.class)).thenReturn(dto);
        when(workshopRepository.findById(1L)).thenReturn(Mono.empty());
        when(template.insert(Workshop.class)).thenReturn(new R2dbcEntityTemplate.InsertSpec<Workshop>() {
            @Override
            public Mono<Workshop> using(Workshop obj) {
                return Mono.just(obj);
            }
        });

        kafkaConsumerService.consumeWorkshopEvent(record);

        verify(template).insert(Workshop.class);
    }

    @Test
    void testConsumeWorkshopEvent_ThrowsException() throws Exception {
        ConsumerRecord<String, String> record = new ConsumerRecord<>("workshop-events", 0, 0L, null, "invalid-json");

        when(objectMapper.readValue("invalid-json", WorkshopKafkaEventDto.class)).thenThrow(new RuntimeException("Invalid JSON"));

        kafkaConsumerService.consumeWorkshopEvent(record);

        // No exception should propagate, just log error
        verifyNoInteractions(workshopRepository);
    }
}
