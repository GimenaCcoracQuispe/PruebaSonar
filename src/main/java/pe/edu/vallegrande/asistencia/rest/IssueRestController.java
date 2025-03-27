package pe.edu.vallegrande.asistencia.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import pe.edu.vallegrande.asistencia.model.Issue;
import pe.edu.vallegrande.asistencia.service.IssueService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.CrossOrigin;
@RestController
@RequestMapping("/tema")
@Slf4j
@CrossOrigin(origins = "http://localhost:4200") 
public class IssueRestController {
    private final IssueService issueService;

    public IssueRestController(IssueService issueService) {
        this.issueService = issueService;
    }

    @GetMapping("/all")
    public Flux<Issue> getIssue() {
        return issueService.findAllIssue();
    }

    @GetMapping("/{id}")  
    public Mono<Issue> getIssueById(@PathVariable Long id) {
        return issueService.findById(id);
    }

    @GetMapping("/active")
    public Flux<Issue> getActiveIssue() {
        return issueService.findStatus("A");
    }

    @GetMapping("/inactive")
    public Flux<Issue> getInactiveIssue() {
        return issueService.findStatus("I");
    }

    @PostMapping("/create")
    public Mono<Issue> createIssue(@RequestBody Issue issue) {
        // El estado será 'A' automáticamente por defecto si no se pasa en el JSON
        return issueService.createIssue(issue);
    }

    @PutMapping("/update/{id}")
    public Mono<ResponseEntity<Issue>> updateIssue(@PathVariable Long id, @RequestBody Issue updatedIssue) {
        return issueService.findById(id)
                .flatMap(existingIssue -> {
                    // Actualizar solo los campos proporcionados en el cuerpo del request
                    existingIssue.setName(updatedIssue.getName());
                    existingIssue.setWorkshopId(updatedIssue.getWorkshopId());
                    existingIssue.setScheduledTime(updatedIssue.getScheduledTime());
                    // NO actualizar el estado si no se envía en el request
                    if (updatedIssue.getState() != null) {
                        existingIssue.setState(updatedIssue.getState());
                    }
                    return issueService.save(existingIssue);
                })
                .map(updated -> ResponseEntity.ok(updated))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/activate/{id}")
    public Mono<ResponseEntity<Void>> activateIssue(@PathVariable Long id) {
        return issueService.findById(id)
                .flatMap(existingIssue -> {
                    existingIssue.setState("A"); 
                    return issueService.save(existingIssue).then(Mono.just(ResponseEntity.ok().<Void>build()));
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/deactivate/{id}")
    public Mono<ResponseEntity<Void>> deactivateIssue(@PathVariable Long id) {
        return issueService.findById(id)
                .flatMap(existingIssue -> {
                    existingIssue.setState("I"); 
                    return issueService.save(existingIssue).then(Mono.just(ResponseEntity.ok().<Void>build()));
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<Void>> deleteIssue(@PathVariable Long id) {
        return issueService.deleteById(id)
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
