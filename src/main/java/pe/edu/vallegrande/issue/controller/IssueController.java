package pe.edu.vallegrande.issue.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import pe.edu.vallegrande.issue.model.Issue;
import pe.edu.vallegrande.issue.service.IssueService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/tema")
@Slf4j
@CrossOrigin(origins = "*")
public class IssueController {
    private final IssueService issueService;

    public IssueController(IssueService issueService) {
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
        return issueService.createIssue(issue);
    }

    @PutMapping("/update/{id}")
    public Mono<ResponseEntity<Issue>> updateIssue(@PathVariable Long id, @RequestBody Issue updatedIssue) {
        return issueService.findById(id)
                .flatMap(existingIssue -> {
                    existingIssue.setName(updatedIssue.getName());
                    existingIssue.setWorkshopId(updatedIssue.getWorkshopId());
                    existingIssue.setScheduledTime(updatedIssue.getScheduledTime());
                    existingIssue.setObservation(updatedIssue.getObservation());
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
