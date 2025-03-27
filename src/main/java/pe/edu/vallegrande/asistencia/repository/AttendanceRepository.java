package pe.edu.vallegrande.asistencia.repository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import pe.edu.vallegrande.asistencia.model.Attendance;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AttendanceRepository extends ReactiveCrudRepository<Attendance, Long> {
    Flux<Attendance> findAllByState(String state);
    // Obtener todas las asistencias de una persona
    Flux<Attendance> findByPersonId(Long personId);
    Flux<Attendance> findByTopicId(Long topicId);
    Mono<Attendance> findTopByOrderByIdDesc();
}
