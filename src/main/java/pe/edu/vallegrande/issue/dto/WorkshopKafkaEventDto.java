package pe.edu.vallegrande.issue.dto;
import java.time.LocalDate;

import lombok.Data;

@Data
public class WorkshopKafkaEventDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String state;
}
