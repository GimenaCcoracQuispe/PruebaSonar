package pe.edu.vallegrande.issue.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import pe.edu.vallegrande.issue.model.Issue;
import pe.edu.vallegrande.issue.service.IssueService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IssueControllerTest {
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
        issue.setWorkshopId(1);
        issue.setScheduledTime(null);
        issue.setObservation(null);
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
                .expectBodyList(Issue.class)
                .hasSize(1)
                .contains(issue);
    }

    @Test
    void testGetIssueByIdFound() {
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
    void testGetIssueByIdNotFound() {
        when(issueService.findById(1L)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/tema/1")
                .exchange()
                .expectStatus().isOk()       // ahora espera 200 OK
                .expectBody().isEmpty();     // y cuerpo vacío
    }

    @Test
    void testGetActiveIssues() {
        Issue issueA = sampleIssue();
        issueA.setState("A");
        when(issueService.findStatus("A")).thenReturn(Flux.just(issueA));

        webTestClient.get()
                .uri("/tema/active")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Issue.class)
                .hasSize(1)
                .contains(issueA);
    }

    @Test
    void testGetInactiveIssues() {
        Issue issueI = sampleIssue();
        issueI.setState("I");
        when(issueService.findStatus("I")).thenReturn(Flux.just(issueI));

        webTestClient.get()
                .uri("/tema/inactive")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Issue.class)
                .hasSize(1)
                .contains(issueI);
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
    void testUpdateIssueFound() {
        Issue existing = sampleIssue();
        existing.setName("Old");
        Issue updated = sampleIssue();
        updated.setName("New");

        when(issueService.findById(1L)).thenReturn(Mono.just(existing));
        when(issueService.save(any(Issue.class))).thenReturn(Mono.just(updated));

        webTestClient.put()
                .uri("/tema/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updated)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Issue.class)
                .value(issueBody ->
                    org.junit.jupiter.api.Assertions.assertEquals("New", issueBody.getName())
                );
    }

    @Test
    void testUpdateIssueNotFound() {
        when(issueService.findById(1L)).thenReturn(Mono.empty());

        webTestClient.put()
                .uri("/tema/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sampleIssue())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testActivateIssueFound() {
        Issue existing = sampleIssue();
        existing.setState("I");
        Issue after = sampleIssue();
        after.setState("A");

        when(issueService.findById(1L)).thenReturn(Mono.just(existing));
        when(issueService.save(any(Issue.class))).thenReturn(Mono.just(after));

        webTestClient.put()
                .uri("/tema/activate/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

    @Test
    void testActivateIssueNotFound() {
        when(issueService.findById(1L)).thenReturn(Mono.empty());

        webTestClient.put()
                .uri("/tema/activate/1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDeactivateIssueFound() {
        Issue existing = sampleIssue();
        existing.setState("A");
        Issue after = sampleIssue();
        after.setState("I");

        when(issueService.findById(1L)).thenReturn(Mono.just(existing));
        when(issueService.save(any(Issue.class))).thenReturn(Mono.just(after));

        webTestClient.delete()
                .uri("/tema/deactivate/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

    @Test
    void testDeactivateIssueNotFound() {
        when(issueService.findById(1L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/tema/deactivate/1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDeleteIssue() {
        when(issueService.deleteById(1L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/tema/delete/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }
}
