package pe.edu.vallegrande.asistencia.service;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pe.edu.vallegrande.asistencia.model.Workshop;
import pe.edu.vallegrande.asistencia.repository.WorkshopRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class WorkshopService {
    private final WorkshopRepository workshopRepository;

    public WorkshopService(WorkshopRepository workshopRepository){
        this.workshopRepository = workshopRepository;
    }

    public Flux<Workshop> findAllWorkshop(){
        return workshopRepository.findAll();
    }

    public Flux<Workshop> findStatus(String state){
        return workshopRepository.findAllByState(state);
    }

    public Flux<Workshop> getWorkshopBystate(String state){
        return workshopRepository.findAllByState(state);
    }

    public Mono<Void> inactiveWorkshop(Long id){
        return workshopRepository.inactiveWorkshop(id);
    }

    public Mono<Workshop> createWorkshop(Workshop workshop){
        return workshopRepository.save(workshop);
    }

    public Mono<Workshop> findById(Long id) {
        return workshopRepository.findById(id);
    }
    
    public Mono<Workshop> save(Workshop workshop) {
        return workshopRepository.save(workshop);
    }

    public Mono<Void> deleteById(Long id) {
        return workshopRepository.deleteById(id);
    }
}
