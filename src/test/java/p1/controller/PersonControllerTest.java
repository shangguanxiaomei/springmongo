package p1.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import p1.entity.Address;
import p1.entity.Person;
import p1.exception.PersonNotFoundException;
import p1.service.PersonCrud;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersonControllerTest {

    PersonController personController;
    PersonCrud personService;
    List<Person> person;

    @BeforeEach
    public void setup() {
        personService = Mockito.mock(PersonCrud.class);
        personController = new PersonController(personService);
        person = Arrays.asList(
                new Person(0L, "caleb", "caleb@test", 24, true, new Address("1", "main street", "ny", "usa")),
                new Person(1L, "mario", "nintendo@test", 56, false, new Address("11", "kamitoba", "kyoto", "jp")),
                new Person(2L, "oreo", "kraft@test", 108, false, null)
        );
    }

    @RequestMapping()
    public String test() {
        return "hello world";
    }

    @Test
    public void testOfTest(){
        String response= personController.test();
        assertEquals("hello world", response);
    }

    @Test
    public void getAll_callsService_OKResult(){
        //arrange
        List<Person> expected = person;
        Mockito.when(personService.getAll()).thenReturn(expected);

        //act
        ResponseEntity<List<Person>> response= personController.getAllPersons();

        //assert
        Mockito.verify(personService).getAll();
        // you can also write:
        // Mockito.verify(personService, times(1)).getAll();
        assertEquals(expected, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getPersonById_CallsServiceAndReturns_OKResult() {
        //arrange
        Person expected = person.get(0);
        Mockito.when(personService.getPersonById(0L)).thenReturn(expected);

        //act
        ResponseEntity<Person> response = personController.getPersonById(0L);

        //assert
        Mockito.verify(personService).getPersonById(0L);
        assertEquals(expected, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getPersonById_ShouldReturnError404_WhenPersonNotFoundException() {
        //arrange
        Mockito.when(personService.getPersonById(3L)).thenThrow(new PersonNotFoundException());

        //act
        ResponseEntity<Person> response = personController.getPersonById(3L);

        //assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    public void patchPerson_ShouldReturnUpdatedRecordAndOK() {
        //arrange
        Person input = new Person(null, "luigi", null, 0, true, null);
        Person expected = person.get(0);
        expected.setName(input.getName());
        expected.setDeveloper(input.isDeveloper());
        expected.setAddress(input.getAddress());
        Mockito.when(personService.patchPerson(0L, input)).thenReturn(expected);

        //act
        ResponseEntity<Person> response = personController.patchPerson(0L, input);

        //assert
        Mockito.verify(personService).patchPerson(0L, input);
        assertEquals(expected, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void patchPerson_ShouldReturnError404_WhenPersonNotFoundException() {
        //arrange
        Person input = new Person();
        Mockito.when(personService.patchPerson(100L, input)).thenThrow(new PersonNotFoundException());

        //act
        ResponseEntity<Person> response = personController.patchPerson(100L, input);

        //assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    public void putPerson_ShouldReturnSavedRecordAndCreated(){
        //arrange
        Person expected = person.get(2);
        Mockito.when(personService.putPerson(2L, expected)).thenReturn(expected);

        //act
        ResponseEntity<Person> response = personController.putPerson(2L, expected);

        //assert
        Mockito.verify(personService).putPerson(2L, expected);
        assertEquals(expected, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void deletePerson_ShouldReturnUpdatedRecordAnd_NO_CONTENT() {
        //arrange
        //no need to use Mockito.when().thenReturn(); because personService.deletePerson(3L) returns void;

        //act
        ResponseEntity<Person> response = personController.deletePerson(3L);

        //assert
        Mockito.verify(personService).deletePerson(3L);
        assertEquals(null, response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void deletePerson_ShouldReturnError404_WhenPersonNotFoundException() {
        //arrange
        // Need to do this because of void return type
        // https://www.journaldev.com/21834/mockito-mock-void-method#mockito-mock-void-method-with-exception
        Mockito.doThrow(PersonNotFoundException.class).when(personService).deletePerson(3L);

        //act
        ResponseEntity<Person> response = personController.deletePerson(3L);

        //assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }
}
