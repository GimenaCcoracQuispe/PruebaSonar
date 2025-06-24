package pe.edu.vallegrande.issue.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Table("workshop")
public class Workshop {
     @Id
    private Long id;
    private String name;
    private String description;
    @Column("startDate")
    private LocalDate startDate;
    @Column("endDate")
    private LocalDate endDate;
    private String state;
}
