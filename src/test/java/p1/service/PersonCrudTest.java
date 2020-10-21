package p1.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import p1.entity.Address;
import p1.entity.Person;
import p1.exception.PersonNotFoundException;
import p1.repository.PersonRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PersonCrudTest {

    PersonCrud personCrud;
    PersonRepository personRepository;
    List<Person> person;

    @BeforeEach
    public void setup() {
        personRepository = Mockito.mock(PersonRepository.class);
        personCrud = new PersonCrud(personRepository);
        person = Arrays.asList(
                new Person(0L, "caleb", "caleb@test", 24, true, new Address("1", "main street", "ny", "usa")),
                new Person(1L, "mario", "nintendo@test", 56, false, new Address("11", "kamitoba", "kyoto", "jp")),
                new Person(2L, "oreo", "kraft@test", 108, false, null)
        );
    }

    @Test
    public void getAll_ShouldCallRepoFindAll() {
        //arrange
        List<Person> expected = person;
        when(personRepository.findAll()).thenReturn(expected);

        //act
        personCrud.getAll();

        //assert
        Mockito.verify(personRepository).findAll();
    }

    @Test
    public void getPersonById_ThrowsPersonNotFoundExceptionWhenPersonNotFound() {
        Assertions.assertThrows(PersonNotFoundException.class, () -> {
            personCrud.getPersonById(20l);
        });
    }

    @Test
    public void getPersonById_ReturnsPerson() {
        //arrange
        Person expected = person.get(1);
        when(personRepository.findById(1L)).thenReturn(Optional.of(expected));

        //act
        Person actual = personCrud.getPersonById(1L);

        //assert
        assertEquals(expected, actual);
        verify(personRepository).findById(1L);
    }

    @Test
    public void putPerson_SavesAndReturnsModifiedRecord() {
        //arrange
        Person input = person.get(1);
        input.setId(null);
        Person expected = person.get(1);
        when(personRepository.save(expected)).thenReturn(expected);
        //act
        Person actual = personCrud.putPerson(1L, input);

        //assert
        verify(personRepository).save(expected);
        assertEquals(expected, actual);
    }

    @Test
    public void patchPerson_updatesForExistingRecord() {
        //arrange
        Person input = new Person(null, "luigi", null, 0, true, null);
        Person fromRepo = person.get(1);
        Person expected = person.get(1);
        expected.setName("luigi");
        expected.setAge(0);
        expected.setDeveloper(true);
        when(personRepository.findById(1L)).thenReturn(Optional.of(fromRepo));
        when(personRepository.save(expected)).thenReturn(expected);

        //act
        Person actual = personCrud.patchPerson(1L, input);

        //assert
        assertEquals(expected, actual);
    }

    @Test
    public void patchPerson_ShouldThrowErrorIfPersonDoesNotExist() {
        //arrange
        when(personRepository.findById(1L)).thenReturn(Optional.empty());
        Person input = person.get(0);

        //assert
        Assertions.assertThrows(PersonNotFoundException.class, () -> {
            //act
            personCrud.patchPerson(0L, input);
        });
    }

    @Test
    public void patchPerson_IgnoresIdPassedWithPersonParameter() {
        //arrange
        Person input = new Person(8L, null, null, 0, true, null);
        Person fromRepo = person.get(1);
        Person expected = person.get(1);
        expected.setName("luigi");
        expected.setAge(0);
        expected.setDeveloper(true);
        when(personRepository.findById(1L)).thenReturn(Optional.of(fromRepo));
        when(personRepository.save(expected)).thenReturn(expected);

        //act
        Person actual = personCrud.patchPerson(1L, input);

        //assert
        assertEquals(expected, actual);
    }

    @Test
    public void patchPerson_UpdatesAllFields() {
        //arrange
        Person input = person.get(0);
        Person fromRepo = person.get(1);
        Person expected = person.get(1);
        expected.setName(input.getName());
        expected.setEmail(input.getEmail());
        expected.setAge(input.getAge());
        expected.setDeveloper(input.isDeveloper());
        expected.setAddress(input.getAddress());
        when(personRepository.findById(1L)).thenReturn(Optional.of(fromRepo));
        when(personRepository.save(expected)).thenReturn(expected);

        //act
        Person actual = personCrud.patchPerson(1L, input);

        //assert
        assertEquals(expected, actual);
    }

    @Test
    public void deletePerson_ThrowsErrorIfRecordNotFound() {
        //arrange
        when(personRepository.findById(1L)).thenReturn(Optional.empty());

        //assert
        Assertions.assertThrows(PersonNotFoundException.class, () -> {
            //act
            personCrud.deletePerson(1L);
        });
    }

    @Test
    public void deletePerson_CallsRepoToDeleteRecord() {
        //arrange
        Person fromRepo = person.get(1);
        when(personRepository.findById(1L)).thenReturn(Optional.of(fromRepo));

        //act
        personCrud.deletePerson(1L);

        //assert
        verify(personRepository).delete(fromRepo);
    }
}
