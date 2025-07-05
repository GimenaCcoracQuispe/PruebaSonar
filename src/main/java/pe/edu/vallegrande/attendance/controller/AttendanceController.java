package pe.edu.vallegrande.attendance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import pe.edu.vallegrande.attendance.model.Attendance;
import pe.edu.vallegrande.attendance.service.AttendanceService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/asistencia")
@Slf4j
@CrossOrigin(origins = "*")
public class AttendanceController {
    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/list")
    public Flux<Attendance> getAllAttendance() {
        return attendanceService.getAllAttendance();
    }

    @GetMapping("/{id}")
    public Mono<Attendance> getAttendanceById(@PathVariable Long id) {
        return attendanceService.getAttendanceById(id);
    }

    @GetMapping("/person/{personId}")
    public Flux<Attendance> getAttendanceByPerson(@PathVariable Long personId) {
        return attendanceService.getAttendanceByPerson(personId);
    }

    @GetMapping("/issue/{issueId}")
    public Flux<Attendance> getAttendanceByTopic(@PathVariable Long topicId) {
        return attendanceService.getAttendanceByTopic(topicId);
    }

    @PostMapping("/create")
    public Mono<Attendance> saveAttendance(@RequestBody Attendance attendance) {
        log.info("Recibiendo asistencia: {}", attendance);

        if (attendance.getIssueId() == null || attendance.getPersonId() == null || attendance.getEntryTime() == null) {
            log.error("Error: topicId, personId y entryTime son obligatorios");
            return Mono.error(new IllegalArgumentException("topicId, personId y entryTime son obligatorios"));
        }
        return attendanceService.saveAttendance(attendance);
    }

    @PutMapping("/update/{id}")
    public Mono<Attendance> updateAttendance(@PathVariable Long id, @RequestBody Attendance updatedAttendance) {
        log.info("Actualizando asistencia con ID {}: {}", id, updatedAttendance);

        if (updatedAttendance.getIssueId() == null || updatedAttendance.getPersonId() == null
                || updatedAttendance.getEntryTime() == null) {
            log.error("Error: topicId, personId y entryTime son obligatorios");
            return Mono.error(new IllegalArgumentException("topicId, personId y entryTime son obligatorios"));
        }

        return attendanceService.getAttendanceById(id)
                .flatMap(existingAttendance -> {
                    existingAttendance.setIssueId(updatedAttendance.getIssueId());
                    existingAttendance.setPersonId(updatedAttendance.getPersonId());
                    existingAttendance.setEntryTime(updatedAttendance.getEntryTime());
                    existingAttendance.setJustificationDocument(updatedAttendance.getJustificationDocument());
                    return attendanceService.saveAttendance(existingAttendance);
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Asistencia no encontrada con ID: " + id)));
    }

    @PutMapping("/activate/{id}")
    public Mono<ResponseEntity<Void>> activateAttendance(@PathVariable Long id) {
        log.info("Restaurando asistencia con ID {}", id);
        return attendanceService.getAttendanceById(id)
                .flatMap(existingAttendance -> {
                    existingAttendance.setState("A");
                    return attendanceService.saveAttendance(existingAttendance)
                            .then(Mono.just(ResponseEntity.ok().<Void>build()));
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Eliminado lógico
    @DeleteMapping("/deactivate/{id}")
    public Mono<Attendance> logicalDeleteAttendance(@PathVariable Long id) {
        log.info("Eliminando lógico asistencia con ID {}", id);
        return attendanceService.logicalDelete(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Asistencia no encontrada con ID: " + id)));
    }

    // Eliminado físico
    @DeleteMapping("/delete/{id}")
    public Mono<Void> deleteAttendance(@PathVariable Long id) {
        log.info("Eliminando físicamente asistencia con ID {}", id);
        return attendanceService.deleteById(id);
    }

}
