package pe.edu.vallegrande.asistencia.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import pe.edu.vallegrande.asistencia.model.Workshop;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WorkshopRepository extends ReactiveCrudRepository<Workshop, Long>{
    Flux<Workshop> findAllByState(String state);

    @Modifying
    @Query("update workshop set state = 'I' where id = id")
    Mono<Void> inactiveWorkshop(Long id);

    @Modifying
    @Query("update workshop set state = 'A' where id = id")
    Mono<Void> activateWorkshop(Long id);

    Mono<Workshop> findTopByOrderByIdDesc();
}
