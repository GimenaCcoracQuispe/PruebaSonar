package pe.edu.vallegrande.workshop.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import pe.edu.vallegrande.workshop.model.Workshop;
import pe.edu.vallegrande.workshop.repository.WorkshopRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@ImportAutoConfiguration(exclude = {
                org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration.class,
                org.springframework.boot.autoconfigure.data.r2dbc.R2dbcDataAutoConfiguration.class
})
class SecurityIntegrationTest {

        @Autowired
        private WebTestClient webTestClient;

        @MockBean
        private WorkshopRepository workshopRepository;

        private Long workshopId;

        @BeforeEach
        void setup() {
                this.webTestClient = this.webTestClient.mutate()
                                .responseTimeout(Duration.ofSeconds(10))
                                .build();

                Workshop w = new Workshop();
                w.setId(1L);
                w.setName("Taller Seguridad");
                w.setDescription("Test Admin");
                w.setStartDate(LocalDate.of(2025, 1, 1));
                w.setEndDate(LocalDate.of(2025, 12, 31));
                w.setObservation("Prueba");
                w.setState("A");
                w.setPersonId("1,2");

                Mockito.when(workshopRepository.save(Mockito.any(Workshop.class)))
                                .thenReturn(Mono.just(w));

                Mockito.when(workshopRepository.findAll())
                                .thenReturn(Flux.just(w));

                Mockito.when(workshopRepository.findById(1L))
                                .thenReturn(Mono.just(w));

                Mockito.when(workshopRepository.deleteById(1L))
                                .thenReturn(Mono.empty());

                workshopId = w.getId();
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
                                                .authorities(Collections.singletonList(
                                                                new SimpleGrantedAuthority("ROLE_USER"))))
                                .get().uri("/api/workshops/list")
                                .exchange()
                                .expectStatus().isOk();
        }

        @Test
        @DisplayName("✅ GET con rol ADMIN debe ser permitido (200 OK)")
        void testGetWithAdminRole_Authorized() {
                webTestClient
                                .mutateWith(mockJwt()
                                                .authorities(Collections.singletonList(
                                                                new SimpleGrantedAuthority("ROLE_ADMIN"))))
                                .get().uri("/api/workshops/list")
                                .exchange()
                                .expectStatus().isOk();
        }

        @Test
        @DisplayName("❌ DELETE con rol USER debe devolver 403 FORBIDDEN")
        void testDeleteWithUserRole_Forbidden() {
                webTestClient
                                .mutateWith(mockJwt()
                                                .authorities(Collections.singletonList(
                                                                new SimpleGrantedAuthority("ROLE_USER"))))
                                .delete().uri("/api/workshops/deactive/" + workshopId)
                                .exchange()
                                .expectStatus().isForbidden();
        }

        @Test
        @DisplayName("✅ DELETE con rol ADMIN debe ser permitido (200 OK)")
        void testDeleteWithAdminRole_Authorized() {
                webTestClient
                                .mutateWith(mockJwt()
                                                .authorities(Collections.singletonList(
                                                                new SimpleGrantedAuthority("ROLE_ADMIN"))))
                                .delete().uri("/api/workshops/deactive/" + workshopId)
                                .exchange()
                                .expectStatus().isOk();
        }

        @Test
        @DisplayName("❌ Token inválido debe devolver 403 FORBIDDEN")
        void testInvalidToken_Forbidden() {
                webTestClient
                                .mutateWith(SecurityMockServerConfigurers.mockOpaqueToken()
                                                .attributes(attrs -> attrs.remove("sub")))
                                .get().uri("/api/workshops/list")
                                .exchange()
                                .expectStatus().isForbidden();
        }
}
