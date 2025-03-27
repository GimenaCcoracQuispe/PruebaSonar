package pe.edu.vallegrande.asistencia.model;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    @JsonProperty("topicId")
    @Column("topic_id") 
    private Long topicId;
    @JsonProperty("personId")
    @Column("person_id")
    private Long personId;
    @JsonProperty("entryTime")
    @Column("entry_time")
    private LocalDateTime entryTime;

    @Column("state")
    private String state; // 'A' (Asistencia), 'T' (Tardanza), 'F' (Falta), 'J' (Justificado)

    @Column("justification_document")
    private String justificationDocument;

}
