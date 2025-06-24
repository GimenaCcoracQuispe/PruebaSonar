package pe.edu.vallegrande.issue.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import pe.edu.vallegrande.issue.dto.IssueKafkaEventDto;
import pe.edu.vallegrande.issue.model.Issue;
import pe.edu.vallegrande.issue.repository.IssueRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class IssueServiceTest {
     @Mock
    private IssueRepository issueRepository;

    @Mock
    private kafkaProducerService kafkaProducer;

    @InjectMocks
    private IssueService issueService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Issue sampleIssue() {
        Issue issue = new Issue();
        issue.setId(1L);
        issue.setName("Tema 1");
        issue.setWorkshopId(Integer.valueOf("1")); 
        issue.setState("A");
        return issue;
    }

    @Test
    void testFindAllIssue() {
        Issue issue = sampleIssue();
        when(issueRepository.findAll()).thenReturn(Flux.just(issue));

        StepVerifier.create(issueService.findAllIssue())
                .expectNext(issue)
                .verifyComplete();
    }

    @Test
    void testFindById() {
        Issue issue = sampleIssue();
        when(issueRepository.findById(1L)).thenReturn(Mono.just(issue));

        StepVerifier.create(issueService.findById(1L))
                .expectNext(issue)
                .verifyComplete();
    }

    @Test
    void testCreateIssue() {
        Issue issue = sampleIssue();
        when(issueRepository.save(issue)).thenReturn(Mono.just(issue));

        StepVerifier.create(issueService.createIssue(issue))
                .expectNext(issue)
                .verifyComplete();

        verify(kafkaProducer, times(1)).sendWorkshopEvent(any(IssueKafkaEventDto.class));
    }

    @Test
    void testDeleteById() {
        Issue issue = sampleIssue();
        when(issueRepository.findById(1L)).thenReturn(Mono.just(issue));
        when(issueRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(issueService.deleteById(1L))
                .verifyComplete();

        verify(kafkaProducer, times(1)).sendWorkshopEvent(any(IssueKafkaEventDto.class));
    }
}
