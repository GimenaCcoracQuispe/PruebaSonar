package pe.edu.vallegrande.issue.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class IssueKafkaEventDto {
    private Long id;
    private String name;
    private Integer workshopId;
    private String sesion;
    private LocalDateTime scheduledTime;
    private String observation;
    private String state;
}