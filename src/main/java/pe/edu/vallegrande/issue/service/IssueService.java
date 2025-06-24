package pe.edu.vallegrande.issue.service;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pe.edu.vallegrande.issue.dto.IssueKafkaEventDto;
import pe.edu.vallegrande.issue.model.Issue;
import pe.edu.vallegrande.issue.repository.IssueRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class IssueService {
    private final IssueRepository issueRepository;
    private final kafkaProducerService kafkaProducer;

    public IssueService(IssueRepository issueRepository, kafkaProducerService kafkaProducer) {
        this.issueRepository = issueRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public Flux<Issue> findAllIssue() {
        return issueRepository.findAll();
    }

    public Flux<Issue> findStatus(String state) {
        return issueRepository.findAllByState(state);
    }

    public Flux<Issue> getIssueBystate(String state) {
        return issueRepository.findAllByState(state);
    }

    public Mono<Void> inactiveIssue(Long id) {
        return issueRepository.inactiveIssue(id);
    }

    public Mono<Issue> createIssue(Issue issue) {
        return issueRepository.save(issue)
                .doOnSuccess(saved -> kafkaProducer.sendWorkshopEvent(toKafkaDto(saved)));
    }

    public Mono<Issue> findById(Long id) {
        return issueRepository.findById(id);
    }

    public Mono<Issue> save(Issue issue) {
        return issueRepository.save(issue)
                .doOnSuccess(saved -> kafkaProducer.sendWorkshopEvent(toKafkaDto(saved)));
    }

    public Mono<Void> deleteById(Long id) {
        return findById(id)
                .flatMap(issue -> {
                    kafkaProducer.sendWorkshopEvent(toKafkaDto(issue)); // enviar antes de borrar
                    return issueRepository.deleteById(id);
                });
    }

    // 🔸 Convierte Issue → IssueKafkaEventDto
    private IssueKafkaEventDto toKafkaDto(Issue issue) {
        IssueKafkaEventDto dto = new IssueKafkaEventDto();
        dto.setId(issue.getId());
        dto.setName(issue.getName());
        dto.setWorkshopId(issue.getWorkshopId());
        dto.setSesion(issue.getSesion());
        dto.setScheduledTime(issue.getScheduledTime());
        dto.setObservation(issue.getObservation());
        dto.setState(issue.getState());
        return dto;
    }
}
