package pe.edu.vallegrande.asistencia.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import pe.edu.vallegrande.asistencia.model.Issue;
import pe.edu.vallegrande.asistencia.repository.IssueRepository;
import pe.edu.vallegrande.asistencia.service.IssueService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IssueServiceTest {
    @Mock
    private IssueRepository issueRepository;

    @InjectMocks
    private IssueService issueService;

    private Issue issue;

    @BeforeEach
    void setUp() {
        issue = new Issue();
        issue.setId(1L);
        issue.setState("A");
    }

    @Test
    void testFindAllIssue() {
        when(issueRepository.findAll()).thenReturn(Flux.just(issue));

        StepVerifier.create(issueService.findAllIssue())
                .expectNext(issue)
                .verifyComplete();

        verify(issueRepository).findAll();
    }

    @Test
    void testFindStatus() {
        when(issueRepository.findAllByState("A")).thenReturn(Flux.just(issue));

        StepVerifier.create(issueService.findStatus("A"))
                .expectNext(issue)
                .verifyComplete();

        verify(issueRepository).findAllByState("A");
    }

    @Test
    void testCreateIssue() {
        when(issueRepository.save(issue)).thenReturn(Mono.just(issue));

        StepVerifier.create(issueService.createIssue(issue))
                .expectNext(issue)
                .verifyComplete();

        verify(issueRepository).save(issue);
    }

    @Test
    void testDeleteById() {
        when(issueRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(issueService.deleteById(1L))
                .verifyComplete();

        verify(issueRepository).deleteById(1L);
    }
}

