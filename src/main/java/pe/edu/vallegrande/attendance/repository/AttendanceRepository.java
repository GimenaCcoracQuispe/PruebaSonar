package pe.edu.vallegrande.attendance.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import pe.edu.vallegrande.attendance.model.Attendance;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AttendanceRepository extends ReactiveCrudRepository<Attendance, Long> {
    Flux<Attendance> findAllByState(String state);

    @Modifying
    @Query("update attendance set state = 'I' where id = id")
    Mono<Void> inactiveAttendance(Long id);

    @Modifying
    @Query("update attendance set state = 'A' where id = id")
    Mono<Void> activateAttendance(Long id);

    Flux<Attendance> findByPersonId(Long personId);
    Flux<Attendance> findByIssueId(Long topicId);
    Mono<Attendance> findTopByOrderByIdDesc();
}
