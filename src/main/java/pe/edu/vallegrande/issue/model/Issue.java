package pe.edu.vallegrande.issue.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("issue")
public class Issue {
    @Id
    private Long id;
    private String name;
    private Integer workshopId;
    private String sesion;
    private LocalDateTime scheduledTime;
    private String observation;
    private String state;
}
