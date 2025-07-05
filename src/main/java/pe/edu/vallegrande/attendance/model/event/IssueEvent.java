package pe.edu.vallegrande.attendance.model.event;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Table("issue")
public class IssueEvent {
    @Id
    private Long id;
    private String name;
    private Integer  workshopId;
    private String sesion;
    private LocalDateTime scheduledTime;
    private String state;
}
