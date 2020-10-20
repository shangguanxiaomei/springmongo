package p1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import p1.entity.Person;
import p1.exception.PersonNotFoundException;
import p1.service.PersonCrud;

import java.util.List;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/person")
public class PersonController {

    private final PersonCrud personCrud;

    public PersonController(PersonCrud personCrud) {
        this.personCrud= personCrud;
    }

    @RequestMapping()
    public String test() {
        return "hello world";
    }

    @RequestMapping("/list")
    public ResponseEntity<List<Person>> getAllPersons() {
        return new ResponseEntity<>(personCrud.getAll(), OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Person> getPersonById(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(personCrud.getPersonById(id), OK);
        } catch (PersonNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<Person> patchPerson(@PathVariable("id") long id, @RequestBody Person person) {
        try {
            return new ResponseEntity<>(personCrud.patchPerson(id, person), OK);
        } catch (PersonNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Person> putPerson(@PathVariable("id") long id, @RequestBody Person person) {
        return new ResponseEntity<>(personCrud.putPerson(id, person), CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Person> deletePerson(@PathVariable("id") long id) {
        try {
            personCrud.deletePerson(id);
            return new ResponseEntity<>(null, NO_CONTENT);
        } catch (PersonNotFoundException e) {
            return new ResponseEntity<>(null, NOT_FOUND);
        }
    }
}
