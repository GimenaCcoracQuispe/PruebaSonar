package pe.edu.vallegrande.asistencia.service;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pe.edu.vallegrande.asistencia.model.Issue;
import pe.edu.vallegrande.asistencia.repository.IssueRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class IssueService {
    private final IssueRepository issueRepository;
    
    public IssueService(IssueRepository issueRepository){
        this.issueRepository = issueRepository;
    }

    public Flux<Issue> findAllIssue(){
        return issueRepository.findAll();
    }

    public Flux<Issue> findStatus(String state){
        return issueRepository.findAllByState(state);
    }

    public Flux<Issue> getIssueBystate(String state){
        return issueRepository.findAllByState(state);
    }

    public Mono<Void> inactiveIssue(Long id){
        return issueRepository.inactiveIssue(id);
    }

    public Mono<Issue> createIssue(Issue issue){
        return issueRepository.save(issue);
    }

    public Mono<Issue> findById(Long id) {
        return issueRepository.findById(id);
    }
    
    public Mono<Issue> save(Issue issue) {
        return issueRepository.save(issue);
    }

    public Mono<Void> deleteById(Long id) {
        return issueRepository.deleteById(id);
    }
}
