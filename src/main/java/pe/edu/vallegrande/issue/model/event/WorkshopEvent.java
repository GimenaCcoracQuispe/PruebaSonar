package pe.edu.vallegrande.issue.model.event;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class WorkshopEvent {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String state;
}
