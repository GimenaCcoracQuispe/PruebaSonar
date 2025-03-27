package pe.edu.vallegrande.asistencia.service;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pe.edu.vallegrande.asistencia.model.Person;
import pe.edu.vallegrande.asistencia.repository.PersonRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class PersonService {
    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Flux<Person> findAllPerson(){
        return personRepository.findAll();
    }

    public Flux<Person> findStatus(String state){
        return personRepository.findAllByState(state);
    }

    public Flux<Person> getPersonBystate(String state){
        return personRepository.findAllByState(state);
    }

    public Mono<Void> inactivePerson(Long id){
        return personRepository.inactivePerson(id);
    }

    public Mono<Person> createPerson(Person person){
        return personRepository.save(person);
    }

    public Mono<Person> findById(Long id) {
        return personRepository.findById(id);
    }
    
    public Mono<Person> save(Person person) {
        return personRepository.save(person);
    }

    public Mono<Void> deleteById(Long id) {
        return personRepository.deleteById(id);
    }
}
