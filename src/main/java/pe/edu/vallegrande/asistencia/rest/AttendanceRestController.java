package pe.edu.vallegrande.asistencia.rest;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import pe.edu.vallegrande.asistencia.model.Attendance;
import pe.edu.vallegrande.asistencia.service.AttendanceService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/asistencia")
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class AttendanceRestController {

    private final AttendanceService attendanceService;

    public AttendanceRestController(AttendanceService attendanceService) {
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

    @GetMapping("/topic/{topicId}")
    public Flux<Attendance> getAttendanceByTopic(@PathVariable Long topicId) {
        return attendanceService.getAttendanceByTopic(topicId);
    }

    @PostMapping("/create")
    public Mono<Attendance> saveAttendance(@RequestBody Attendance attendance) {
        log.info("Recibiendo asistencia: {}", attendance);

        if (attendance.getTopicId() == null || attendance.getPersonId() == null || attendance.getEntryTime() == null) {
            log.error("Error: topicId, personId y entryTime son obligatorios");
            return Mono.error(new IllegalArgumentException("topicId, personId y entryTime son obligatorios"));
        }
        return attendanceService.saveAttendance(attendance);
    }

    @PutMapping("/update/{id}")
    public Mono<Attendance> updateAttendance(@PathVariable Long id, @RequestBody Attendance updatedAttendance) {
        log.info("Actualizando asistencia con ID {}: {}", id, updatedAttendance);

        if (updatedAttendance.getTopicId() == null || updatedAttendance.getPersonId() == null
                || updatedAttendance.getEntryTime() == null) {
            log.error("Error: topicId, personId y entryTime son obligatorios");
            return Mono.error(new IllegalArgumentException("topicId, personId y entryTime son obligatorios"));
        }

        return attendanceService.getAttendanceById(id)
                .flatMap(existingAttendance -> {
                    existingAttendance.setTopicId(updatedAttendance.getTopicId());
                    existingAttendance.setPersonId(updatedAttendance.getPersonId());
                    existingAttendance.setEntryTime(updatedAttendance.getEntryTime());
                    existingAttendance.setJustificationDocument(updatedAttendance.getJustificationDocument());
                    return attendanceService.saveAttendance(existingAttendance);
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Asistencia no encontrada con ID: " + id)));
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteAttendance(@PathVariable Long id) {
        return attendanceService.deleteAttendance(id);
    }
}
