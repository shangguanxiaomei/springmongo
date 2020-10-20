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

    @BeforeEach
    public void setup() {
        personRepository = Mockito.mock(PersonRepository.class);
        personCrud = new PersonCrud(personRepository);
    }

    @Test
    public void getAll_ShouldCallRepoFindAll() {
        //arrange
        List<Person> expected = Arrays.asList(
            new Person(0L, "caleb", "caleb@test", 24, true, new Address("1", "main street", "ny", "usa")),
            new Person(1L, "mario", "nintendo@test", 56, false, new Address("11", "kamitoba", "kyoto", "jp")),
            new Person(2L, "oreo", "kraft@test", 108, false, null)
        );
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
        Person expected = new Person(1L, "mario", "nintendo@test", 56, false, new Address("11", "kamitoba", "kyoto", "jp"));
        when(personRepository.findById(1L)).thenReturn(Optional.of(expected));

        //act
        Person actual = personCrud.getPersonById(1L);

        //assert
        assertEquals(expected, actual);
        verify(personRepository).findById(1L);
    }
}
