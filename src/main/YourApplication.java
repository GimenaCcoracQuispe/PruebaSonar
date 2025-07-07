import io.github.cdimascio.dotenv.Dotenv;

public class YourApplication {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        SpringApplication.run(YourApplication.class, args);
    }
}
