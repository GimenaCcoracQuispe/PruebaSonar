package pe.edu.vallegrande.issue.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import pe.edu.vallegrande.issue.config.TestSecurityConfig;
import pe.edu.vallegrande.issue.model.Issue;
import pe.edu.vallegrande.issue.service.IssueService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = IssueController.class)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Import(TestSecurityConfig.class) // si tienes configuración de seguridad para tests
class IssueControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private IssueService issueService;

    private Issue sampleIssue;

    @BeforeEach
    void setUp() {
        sampleIssue = new Issue();
        sampleIssue.setId(1L);
        sampleIssue.setName("Tema 1");
        sampleIssue.setWorkshopId(1);
        sampleIssue.setScheduledTime(null);
        sampleIssue.setObservation(null);
        sampleIssue.setState("A");
    }

    @Test
    void testGetAllIssues() {
        Mockito.when(issueService.findAllIssue()).thenReturn(Flux.just(sampleIssue));

        webTestClient.get()
                .uri("/tema/all")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Issue.class)
                .hasSize(1)
                .contains(sampleIssue);
    }

    @Test
    void testGetIssueByIdFound() {
        Mockito.when(issueService.findById(1L)).thenReturn(Mono.just(sampleIssue));

        webTestClient.get()
                .uri("/tema/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Issue.class)
                .isEqualTo(sampleIssue);
    }

    @Test
    void testGetIssueByIdNotFound() {
        Mockito.when(issueService.findById(1L)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/tema/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                // Según tu controlador devuelve Mono<Issue>, vacío → 200 + cuerpo vacío
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

    @Test
    void testGetActiveIssues() {
        Issue active = new Issue();
        active.setId(2L);
        active.setName("Activo");
        active.setWorkshopId(2);
        active.setState("A");
        Mockito.when(issueService.findStatus("A")).thenReturn(Flux.just(active));

        webTestClient.get()
                .uri("/tema/active")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Issue.class)
                .hasSize(1)
                .contains(active);
    }

    @Test
    void testGetInactiveIssues() {
        Issue inactive = new Issue();
        inactive.setId(3L);
        inactive.setName("Inactivo");
        inactive.setWorkshopId(3);
        inactive.setState("I");
        Mockito.when(issueService.findStatus("I")).thenReturn(Flux.just(inactive));

        webTestClient.get()
                .uri("/tema/inactive")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Issue.class)
                .hasSize(1)
                .contains(inactive);
    }

    @Test
    void testCreateIssue() {
        Mockito.when(issueService.createIssue(Mockito.any(Issue.class))).thenReturn(Mono.just(sampleIssue));

        webTestClient.post()
                .uri("/tema/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sampleIssue)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Issue.class)
                .isEqualTo(sampleIssue);
    }

    @Test
    void testUpdateIssueFound() {
        Issue existing = new Issue();
        existing.setId(1L);
        existing.setName("Old");
        existing.setWorkshopId(1);
        existing.setState("A");

        Issue updated = new Issue();
        updated.setId(1L);
        updated.setName("New");
        updated.setWorkshopId(1);
        updated.setState("A");

        Mockito.when(issueService.findById(1L)).thenReturn(Mono.just(existing));
        Mockito.when(issueService.save(Mockito.any(Issue.class))).thenReturn(Mono.just(updated));

        webTestClient.put()
                .uri("/tema/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updated)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Issue.class)
                .value(issueBody -> org.junit.jupiter.api.Assertions.assertEquals("New", issueBody.getName()));
    }

    @Test
    void testUpdateIssueNotFound() {
        Mockito.when(issueService.findById(1L)).thenReturn(Mono.empty());

        webTestClient.put()
                .uri("/tema/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sampleIssue)
                .exchange()
                // Tu controlador devuelve defaultIfEmpty(ResponseEntity.notFound()), pero en tu caso original devolvías Mono<Issue>? 
                // Si tu update devuelve Mono<ResponseEntity<Issue>>, aquí sería .expectStatus().isNotFound()
                // Si no, y devuelves Mono<Issue> directamente, ajusta a 200+vacío:
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

    @Test
    void testActivateIssueFound() {
        Issue existing = new Issue();
        existing.setId(1L);
        existing.setState("I");
        Issue after = new Issue();
        after.setId(1L);
        after.setState("A");

        Mockito.when(issueService.findById(1L)).thenReturn(Mono.just(existing));
        Mockito.when(issueService.save(Mockito.any(Issue.class))).thenReturn(Mono.just(after));

        webTestClient.put()
                .uri("/tema/activate/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

    @Test
    void testActivateIssueNotFound() {
        Mockito.when(issueService.findById(1L)).thenReturn(Mono.empty());

        webTestClient.put()
                .uri("/tema/activate/1")
                .exchange()
                // Si tu controlador para not found usa defaultIfEmpty(ResponseEntity.notFound()), 
                // y tu WebFluxTest ve 404, cambia a isNotFound()
                .expectStatus().isOk()  
                .expectBody().isEmpty();
    }

    @Test
    void testDeactivateIssueFound() {
        Issue existing = new Issue();
        existing.setId(1L);
        existing.setState("A");
        Issue after = new Issue();
        after.setId(1L);
        after.setState("I");

        Mockito.when(issueService.findById(1L)).thenReturn(Mono.just(existing));
        Mockito.when(issueService.save(Mockito.any(Issue.class))).thenReturn(Mono.just(after));

        webTestClient.delete()
                .uri("/tema/deactivate/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

    @Test
    void testDeactivateIssueNotFound() {
        Mockito.when(issueService.findById(1L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/tema/deactivate/1")
                .exchange()
                .expectStatus().isOk() // o isNotFound() si configuras así
                .expectBody().isEmpty();
    }

    @Test
    void testDeleteIssue() {
        Mockito.when(issueService.deleteById(1L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/tema/delete/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }
}
