package pe.edu.vallegrande.workshop.config;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import io.github.cdimascio.dotenv.Dotenv;

public class DotenvInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
   @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Dotenv dotenv = Dotenv.load();  // Lee el archivo .env automáticamente
        Map<String, Object> envVariables = new HashMap<>();

        dotenv.entries().forEach(entry -> {
            envVariables.put(entry.getKey(), entry.getValue());
            // También ponerlo como variable del sistema (opcional)
            System.setProperty(entry.getKey(), entry.getValue());
        });

        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        environment.getPropertySources().addFirst(new MapPropertySource("dotenvProperties", envVariables));
    }

}