package pe.edu.vallegrande.attendance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pe.edu.vallegrande.attendance.kafka.KafkaProducerService;
import pe.edu.vallegrande.attendance.model.Attendance;
import pe.edu.vallegrande.attendance.model.event.AttendanceEvent;
import pe.edu.vallegrande.attendance.repository.AttendanceRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final KafkaProducerService kafkaProducerService;

    public Flux<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    public Mono<Attendance> getAttendanceById
    (Long id) {
        return attendanceRepository.findById(id);
    }

    public Flux<Attendance> getAttendanceByPerson(Long personId) {
        return attendanceRepository.findByPersonId(personId);
    }

    public Flux<Attendance> getAttendanceByTopic(Long topicId) {
        return attendanceRepository.findByIssueId(topicId);
    }

    public Mono<Attendance> saveAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance)
                .doOnNext(saved -> {
                    AttendanceEvent event = buildEvent(saved, "CREATED");
                    kafkaProducerService.sendAttendanceEvent(event);
                });
    }

    public Mono<Attendance> updateAttendance(Long id, Attendance updatedAttendance) {
        return attendanceRepository.findById(id)
                .flatMap(existingAttendance -> {
                    existingAttendance.setIssueId(updatedAttendance.getIssueId());
                    existingAttendance.setPersonId(updatedAttendance.getPersonId());
                    existingAttendance.setEntryTime(updatedAttendance.getEntryTime());
                    existingAttendance.setJustificationDocument(updatedAttendance.getJustificationDocument());
                    return attendanceRepository.save(existingAttendance);
                })
                .doOnNext(updated -> {
                    AttendanceEvent event = buildEvent(updated, "UPDATED");
                    kafkaProducerService.sendAttendanceEvent(event);
                });
    }

    public Mono<Void> deleteById(Long id) {
        return attendanceRepository.findById(id)
                .flatMap(existing -> attendanceRepository.deleteById(id)
                        .doOnSuccess(unused -> {
                            AttendanceEvent event = buildEvent(existing, "DELETED");
                            kafkaProducerService.sendAttendanceEvent(event);
                        }));
    }


    public Mono<Attendance> logicalDelete(Long id) {
        return attendanceRepository.findById(id)
                .flatMap(attendance -> {
                    attendance.setState("I");
                    return attendanceRepository.save(attendance);
                })
                .doOnNext(updated -> {
                    AttendanceEvent event = buildEvent(updated, "UPDATED");
                    kafkaProducerService.sendAttendanceEvent(event);
                });
    }


    public Mono<Attendance> findById(Long id) {
        return attendanceRepository.findById(id);
    }


    private AttendanceEvent buildEvent(Attendance attendance, String eventType) {
        return AttendanceEvent.builder()
                .id(attendance.getId())
                .issueId(attendance.getIssueId())
                .personId(attendance.getPersonId())
                .entryTime(attendance.getEntryTime())
                .record(attendance.getRecord())
                .justificationDocument(attendance.getJustificationDocument())
                .state(attendance.getState())
                .eventType(eventType)
                .build();
    }
}
