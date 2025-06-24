package pe.edu.vallegrande.issue.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
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

        dto = new WorkshopKafkaEventDto();
        dto.setId(1L);
        dto.setName("Test Workshop");
        dto.setDescription("Description");
        dto.setStartDate(LocalDate.of(2025, 1, 1));
        dto.setEndDate(LocalDate.of(2025, 1, 30));
        dto.setState("A");

        // Usar constructor completo
        workshop = new Workshop(
            dto.getId(),
            dto.getName(),
            dto.getDescription(),
            dto.getStartDate(),
            dto.getEndDate(),
            dto.getState()
        );
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

    // Crear el objeto Workshop sin usar constructor privado
    Workshop newWorkshop = new Workshop();
    newWorkshop.setId(dto.getId());
    newWorkshop.setName(dto.getName());
    newWorkshop.setDescription(dto.getDescription());
    newWorkshop.setStartDate(dto.getStartDate());
    newWorkshop.setEndDate(dto.getEndDate());
    newWorkshop.setState(dto.getState());

    // Simular comportamiento de insert usando cualquier tipo
    R2dbcEntityTemplate mockedTemplate = mock(R2dbcEntityTemplate.class);
    when(template.insert(eq(Workshop.class))).thenReturn(new Object() {
        public Mono<Workshop> using(Workshop obj) {
            return Mono.just(obj);
        }
    });

    kafkaConsumerService.consumeWorkshopEvent(record);

    verify(template).insert(eq(Workshop.class));
}


    @Test
    void testConsumeWorkshopEvent_ThrowsException() throws Exception {
        ConsumerRecord<String, String> record = new ConsumerRecord<>("workshop-events", 0, 0L, null, "invalid-json");

        when(objectMapper.readValue("invalid-json", WorkshopKafkaEventDto.class)).thenThrow(new RuntimeException("Invalid JSON"));

        kafkaConsumerService.consumeWorkshopEvent(record);

        verifyNoInteractions(workshopRepository);
    }
}
