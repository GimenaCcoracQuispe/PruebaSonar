package pe.edu.vallegrande.issue.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;
import pe.edu.vallegrande.issue.model.Issue;
import pe.edu.vallegrande.issue.service.IssueService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
public class IssueControllerTest {
    @Mock
    private IssueService issueService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        IssueController controller = new IssueController(issueService);
        webTestClient = WebTestClient.bindToController(controller).build();
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
    void testGetAllIssues() {
        Issue issue = sampleIssue();
        when(issueService.findAllIssue()).thenReturn(Flux.just(issue));

        webTestClient.get()
                .uri("/tema/all")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Issue.class).hasSize(1);
    }

    @Test
    void testGetIssueById() {
        Issue issue = sampleIssue();
        when(issueService.findById(1L)).thenReturn(Mono.just(issue));

        webTestClient.get()
                .uri("/tema/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Issue.class)
                .isEqualTo(issue);
    }

    @Test
    void testCreateIssue() {
        Issue issue = sampleIssue();
        when(issueService.createIssue(any(Issue.class))).thenReturn(Mono.just(issue));

        webTestClient.post()
                .uri("/tema/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(issue)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Issue.class)
                .isEqualTo(issue);
    }

    @Test
    void testActivateIssue() {
        Issue issue = sampleIssue();
        issue.setState("I");

        when(issueService.findById(1L)).thenReturn(Mono.just(issue));
        when(issueService.save(any())).thenReturn(Mono.just(issue));

        webTestClient.put()
                .uri("/tema/activate/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testDeleteIssue() {
        when(issueService.deleteById(1L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/tema/delete/1")
                .exchange()
                .expectStatus().isOk();
    }
}
