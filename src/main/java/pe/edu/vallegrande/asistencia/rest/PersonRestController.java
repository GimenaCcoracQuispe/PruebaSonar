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
import lombok.extern.slf4j.Slf4j;
import pe.edu.vallegrande.asistencia.model.Person;
import pe.edu.vallegrande.asistencia.service.PersonService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/person")
@Slf4j
@CrossOrigin(origins = "http://localhost:4200") 
public class PersonRestController {
    private final PersonService personService;

    public PersonRestController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/all")
    public Flux<Person> getPerson() {
        return personService.findAllPerson();
    }

    @GetMapping("/{id}")
    public Mono<Person> getPersonById(@PathVariable Long id) {
        return personService.findById(id);
    }

    @GetMapping("/active")
    public Flux<Person> getActivePerson() {
        return personService.findStatus("A");
    }

    @GetMapping("/inactive")
    public Flux<Person> getInactivePerson() {
        return personService.findStatus("I");
    }

    @PostMapping("/create")
    public Mono<Person> createPerson(@RequestBody Person person) {
        // El estado será 'A' automáticamente por defecto si no se pasa en el JSON
        return personService.createPerson(person);
    }

    @PutMapping("/update/{id}")
    public Mono<ResponseEntity<Person>> updatePerson(@PathVariable Long id, @RequestBody Person updatedPerson) {
        return personService.findById(id)
                .flatMap(existingPerson -> {
                    existingPerson.setName(updatedPerson.getName());
                    if (updatedPerson.getIdentificacion() != null) { // Verifica si identificación no es nulo
                        existingPerson.setIdentificacion(updatedPerson.getIdentificacion());
                    }
                    if (updatedPerson.getState() != null) {
                        existingPerson.setState(updatedPerson.getState());
                    }
                    return personService.save(existingPerson);
                })
                .map(updated -> ResponseEntity.ok(updated))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/activate/{id}")
    public Mono<ResponseEntity<Void>> activatePerson(@PathVariable Long id) {
        return personService.findById(id)
                .flatMap(existingPerson -> {
                    existingPerson.setState("A");
                    return personService.save(existingPerson).then(Mono.just(ResponseEntity.ok().<Void>build()));
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/deactivate/{id}")
    public Mono<ResponseEntity<Void>> deactivatePerson(@PathVariable Long id) {
        return personService.findById(id)
                .flatMap(existingPerson -> {
                    existingPerson.setState("I");
                    return personService.save(existingPerson).then(Mono.just(ResponseEntity.ok().<Void>build()));
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<Void>> deletePerson(@PathVariable Long id) {
        return personService.deleteById(id)
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
