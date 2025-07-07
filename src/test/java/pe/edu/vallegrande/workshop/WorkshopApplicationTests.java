package pe.edu.vallegrande.workshop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class WorkshopApplicationTests {

	@Test
	void contextLoads() {
		// Este método prueba que el contexto de Spring Boot se cargue sin errores.
		// No necesita implementación adicional.
	}

}
