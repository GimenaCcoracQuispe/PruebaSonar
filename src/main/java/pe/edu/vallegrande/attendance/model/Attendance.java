package pe.edu.vallegrande.attendance.model;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Table("attendance")
public class Attendance {
    @Id
    private Long id;
    private Long issueId;
    private Long personId;
    private LocalDateTime entryTime;
    private String record;
    private String justificationDocument;
    private String state;
}
