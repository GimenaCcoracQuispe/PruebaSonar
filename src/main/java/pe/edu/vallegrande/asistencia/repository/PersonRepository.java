package pe.edu.vallegrande.asistencia.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import pe.edu.vallegrande.asistencia.model.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonRepository extends ReactiveCrudRepository<Person, Long> {
    Flux<Person> findAllByState(String state);

    @Modifying
    @Query("Update person set state = 'I' where id = id")
    Mono<Void> inactivePerson(Long id);

    @Modifying
    @Query("update person set state ='A' where id = id")
    Mono<Void> activatePerson(Long id);

    Mono<Person> findTopByOrderByIdDesc();
}
