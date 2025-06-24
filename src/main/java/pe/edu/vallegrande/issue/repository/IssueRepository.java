package pe.edu.vallegrande.issue.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import pe.edu.vallegrande.issue.model.Issue;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IssueRepository extends ReactiveCrudRepository<Issue, Long>{
    Flux<Issue> findAllByState(String state);

    @Modifying
    @Query("update issue set state = 'I' where id = id")
    Mono<Void> inactiveIssue(Long id);

    @Modifying
    @Query("update issue set state = 'A' where id = id")
    Mono<Void> activateIssue(Long id);

    Mono<Integer> findMaxCodeByWorkshopId(Integer workshopId);
    Mono<Issue> findTopByOrderByIdDesc();
}
