package pe.edu.vallegrande.issue.model.event;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class IssueEvent {
    private Long id;
    private String name;
    private Integer workshopId;
    private String sesion;
    private LocalDateTime scheduledTime;
    private String observation;
    private String state;
}
