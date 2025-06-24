package pe.edu.vallegrande.issue;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import pe.edu.vallegrande.issue.config.TestSecurityConfig;

@SpringBootTest(
    classes = {},  // o una clase de configuración mínima propia
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {
        "spring.autoconfigure.exclude=" +
          "org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration," +
          "org.springframework.boot.autoconfigure.data.r2dbc.R2dbcRepositoriesAutoConfiguration," +
          "org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration," +
          "org.springframework.boot.autoconfigure.kafka.KafkaReactiveStreamsAutoConfiguration"
    }
)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class IssueApplicationTests {

    // @Test
    // void contextLoads() { }
}
