package pe.edu.vallegrande.asistencia.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Table("Person")
public class Person {
    @Id
    private Long id;
    private String name;
    private String identificacion;
    private String state;
}
