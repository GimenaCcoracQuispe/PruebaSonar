package pe.edu.vallegrande.asistencia.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Table("topic")
public class Issue {
    @Id
    private Long id;
    private String name;
    private Integer  workshopId;
    private LocalDateTime scheduledTime;
    private String state;
}
