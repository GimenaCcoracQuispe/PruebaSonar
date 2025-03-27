package pe.edu.vallegrande.asistencia.service;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pe.edu.vallegrande.asistencia.model.Attendance;
import pe.edu.vallegrande.asistencia.repository.AttendanceRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    
    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public Flux<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    public Mono<Attendance> getAttendanceById(Long id) {
        return attendanceRepository.findById(id);
    }

    public Flux<Attendance> getAttendanceByPerson(Long personId) {
        return attendanceRepository.findByPersonId(personId);
    }

    public Flux<Attendance> getAttendanceByTopic(Long topicId) {
        return attendanceRepository.findByTopicId(topicId);
    }

    public Mono<Attendance> saveAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    public Mono<Void> deleteAttendance(Long id) {
        return attendanceRepository.deleteById(id);
    }

    public Mono<Attendance> updateAttendance(Long id, Attendance updatedAttendance) {
        return attendanceRepository.findById(id)
                .flatMap(existingAttendance -> {
                    existingAttendance.setTopicId(updatedAttendance.getTopicId());
                    existingAttendance.setPersonId(updatedAttendance.getPersonId());
                    existingAttendance.setEntryTime(updatedAttendance.getEntryTime());
                    existingAttendance.setJustificationDocument(updatedAttendance.getJustificationDocument());
                    return attendanceRepository.save(existingAttendance);
                });
    }
    
}
 