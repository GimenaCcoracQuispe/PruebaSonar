package pe.edu.vallegrande.workshop.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import pe.edu.vallegrande.workshop.model.Workshop;
import pe.edu.vallegrande.workshop.repository.WorkshopRepository;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@DisplayName("🔐 Tests de Seguridad para WorkshopController")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class SecurityIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private WorkshopRepository workshopRepository;

    private Long workshopId;

    @BeforeEach
    void setup() {
        this.webTestClient = this.webTestClient.mutate()
                .responseTimeout(Duration.ofSeconds(10))
                .build();

        // Crear un nuevo workshop y guardar el ID generado
        Workshop workshop = new Workshop();
        workshop.setName("Taller de Seguridad");
        workshop.setDescription("Test para Admin");
        workshop.setStartDate(LocalDate.of(2025, 1, 1));
        workshop.setEndDate(LocalDate.of(2025, 12, 31));
        workshop.setObservation("Prueba de seguridad");
        workshop.setState("A");
        workshop.setPersonId("1,2");

        this.workshopId = workshopRepository.save(workshop).map(Workshop::getId).block();
    }

    @Test
    @DisplayName("❌ GET sin token debe devolver 401 UNAUTHORIZED")
    void testGetWithoutToken_Unauthorized() {
        webTestClient.get()
                .uri("/api/workshops/list")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("✅ GET con rol USER debe ser permitido (200 OK)")
    void testGetWithUserRole_Authorized() {
        webTestClient
                .mutateWith(mockJwt()
                        .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))))
                .get().uri("/api/workshops/list")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("✅ GET con rol ADMIN debe ser permitido (200 OK)")
    void testGetWithAdminRole_Authorized() {
        webTestClient
                .mutateWith(mockJwt()
                        .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .get().uri("/api/workshops/list")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("❌ DELETE con rol USER debe devolver 403 FORBIDDEN")
    void testDeleteWithUserRole_Forbidden() {
        webTestClient
                .mutateWith(mockJwt()
                        .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))))
                .delete().uri("/api/workshops/deactive/" + workshopId)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("✅ DELETE con rol ADMIN debe ser permitido (200 OK o 204 No Content)")
    void testDeleteWithAdminRole_Authorized() {
        webTestClient
                .mutateWith(mockJwt()
                        .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .delete().uri("/api/workshops/deactive/" + workshopId)
                .exchange()
                .expectStatus().isOk(); // o .isNoContent();
    }

    @Test
    @DisplayName("❌ Token inválido debe devolver 403 FORBIDDEN (WebFlux)")
    void testInvalidToken_Forbidden() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockOpaqueToken()
                        .attributes(attrs -> attrs.remove("sub")))
                .get().uri("/api/workshops/list")
                .exchange()
                .expectStatus().isForbidden();
    }
}
