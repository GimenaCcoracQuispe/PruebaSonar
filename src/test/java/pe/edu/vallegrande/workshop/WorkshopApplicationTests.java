package pe.edu.vallegrande.workshop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import pe.edu.vallegrande.workshop.config.DotenvInitializer;

@ActiveProfiles("test")
@SpringBootTest
@ContextConfiguration(initializers = DotenvInitializer.class)
class WorkshopApplicationTests {

	@Test
	void contextLoads() {
		// Este método prueba que el contexto de Spring Boot se cargue sin errores.
		// No necesita implementación adicional.
	}

}
