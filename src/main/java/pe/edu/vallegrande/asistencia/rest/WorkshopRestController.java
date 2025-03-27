package pe.edu.vallegrande.asistencia.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import pe.edu.vallegrande.asistencia.model.Workshop;
import pe.edu.vallegrande.asistencia.service.WorkshopService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/taller")
@Slf4j
@CrossOrigin(origins = "http://localhost:4200") 
public class WorkshopRestController {
    private final WorkshopService workshopService;

    public WorkshopRestController(WorkshopService workshopService) {
        this.workshopService = workshopService;
    }

    @GetMapping("/lis")
    public Flux<Workshop> getWorkshop() {
        return workshopService.findAllWorkshop();
    }

    @GetMapping("/{id}")
    public Mono<Workshop> getWorkshopById(@PathVariable Long id) {
        return workshopService.findById(id);
    }

    @GetMapping("/active")
    public Flux<Workshop> getActiveWorkshop() {
        return workshopService.findStatus("A");
    }

    @GetMapping("/inactive")
    public Flux<Workshop> getInactiveWorkshop() {
        return workshopService.findStatus("I");
    }


    @PostMapping("/create")
    public Mono<Workshop> createWorkshop(@RequestBody Workshop workshop) {
        return workshopService.createWorkshop(workshop);
               
    }

    @PutMapping("/activate/{id}")
    public Mono<ResponseEntity<Void>> activateWorkshop(@PathVariable Long id) {
        return workshopService.findById(id)
                .flatMap(existingWorkshop -> {
                    existingWorkshop.setState("A"); 
                    return workshopService.save(existingWorkshop).then(Mono.just(ResponseEntity.ok().<Void>build()));
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/deactivate/{id}")
    public Mono<ResponseEntity<Void>> deactivateWorkshop(@PathVariable Long id) {
        return workshopService.findById(id)
                .flatMap(existingWorkshop -> {
                    existingWorkshop.setState("I"); 
                    return workshopService.save(existingWorkshop).then(Mono.just(ResponseEntity.ok().<Void>build()));
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<Void>> deleteWorkshop(@PathVariable Long id) {
        return workshopService.deleteById(id)
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
