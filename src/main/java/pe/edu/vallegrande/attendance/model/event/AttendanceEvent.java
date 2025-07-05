package pe.edu.vallegrande.attendance.model.event;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceEvent {
    private Long id;
    private Long issueId;
    private Long personId;
    private LocalDateTime entryTime;
    private String record;
    private String justificationDocument;
    private String state;
    private String eventType; // CREATED, UPDATED, DELETED
}

