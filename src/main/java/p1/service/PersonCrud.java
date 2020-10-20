package p1.service;

import org.springframework.stereotype.Service;
import p1.entity.Person;
import p1.exception.PersonNotFoundException;
import p1.repository.PersonRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PersonCrud {

    private final PersonRepository personRepository;

    public PersonCrud(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> getAll() {
        return personRepository.findAll();
    }

    public Person getPersonById(long id) {
        Optional<Person> optional = personRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new PersonNotFoundException();
    }

    public Person putPerson(long id, Person person) {
        person.setId(id);
        return personRepository.save(person);
    }

    public Person patchPerson(long id, Person person) {
        Optional<Person> optional = personRepository.findById(id);
        if (optional.isEmpty()) {
            throw new PersonNotFoundException();
        }

        Person personToUpdate = optional.get();

        if (person.getName() != null) {
            personToUpdate.setName(person.getName());
        }

        if (person.getEmail() != null) {
            personToUpdate.setEmail(person.getEmail());
        }

        if (person.getAge() != 0) {
            personToUpdate.setAge(person.getAge());
        }

        personToUpdate.setDeveloper(person.isDeveloper());

        return personRepository.save(personToUpdate);
    }

    public void deletePerson(long id) {
        Optional<Person> optional = personRepository.findById(id);
        if (optional.isEmpty()) {
            throw new PersonNotFoundException();
        }
        personRepository.delete(optional.get());
    }
}

