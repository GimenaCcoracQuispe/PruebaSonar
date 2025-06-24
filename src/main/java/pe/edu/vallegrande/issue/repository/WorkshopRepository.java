package pe.edu.vallegrande.issue.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import pe.edu.vallegrande.issue.model.Workshop;

@Repository
public interface  WorkshopRepository extends ReactiveCrudRepository<Workshop, Long>{
    
}
