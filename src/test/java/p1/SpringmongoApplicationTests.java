package p1;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import p1.entity.Address;
import p1.entity.Person;
import p1.repository.PersonRepository;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SpringmongoApplicationTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    PersonRepository personRepository;

    Person p1;
    Person p2;

    @BeforeEach
    public void setup() {
        p1 = new Person(100L, "mario", "nintendo@test", 56, false, new Address("11", "kamitoba", "kyoto", "jp") );
        p2 = new Person(200L, "oreo", "kraft@test", 108, false, null);
        personRepository.save(p1);
        personRepository.save(p2);
    }

    @AfterEach
    public void cleanup() {
        personRepository.deleteById(100L);
        personRepository.deleteById(200L);
    }

    @Test
    void contextLoads() {
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void getPersons_returnsListOfPersons() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/person/list"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        List<Person> list = mapper.readValue(
                response.getContentAsString(), new TypeReference<List<Person>>() {
                });

        Boolean p1Exist = false;
        Boolean p2Exist = false;

        for (Person temp : list) {
            if(temp.toString().equals(p1.toString())) p1Exist = true;
            if(temp.toString().equals(p2.toString())) p2Exist = true;
        }

        Boolean actual= p1Exist & p2Exist;

        assertEquals(true, actual);
    }

    @Test
    public void getPersons_ShouldReturnPersonWithGivenId() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/person/100"))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        String expected = p1.toString();
        String actual = mapper.readValue(response.getContentAsString(), Person.class).toString();
        assertEquals(expected, actual);


        response = mockMvc.perform(get("/person/200"))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        expected = p2.toString();
        actual = mapper.readValue(response.getContentAsString(), Person.class).toString();
        assertEquals(expected, actual);
    }

    @Test
    public void PatchPerson_ShouldUpdatePersonWithGivenId() throws Exception {
        Person input = new Person(null, "heinz", "heinz@test", 108, false, null);
        String expected = new Person(200L, "heinz", "heinz@test", 108, false, null).toString();

        MockHttpServletResponse response = mockMvc.perform(patch("/person/200")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(mapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        String actual = mapper.readValue(response.getContentAsString(), Person.class).toString();

        assertEquals(expected, actual);
    }

    @Test
    public void PutPerson_ShouldCreatePersonWithGivenId() throws Exception {
        Person input = new Person(null, "heinz", "heinz@test", 108, false, null);
        String expected = new Person(500L, "heinz", "heinz@test", 108, false, null).toString();

        MockHttpServletResponse response = mockMvc.perform(put("/person/500")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(mapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andReturn().getResponse();
        String actual = mapper.readValue(response.getContentAsString(), Person.class).toString();

        assertEquals(expected, actual);
    }

    @Test
    public void DeletePerson_ShouldDeletePersonWithGivenId() throws Exception {
        assertTrue(personRepository.existsById(100L));
        mockMvc.perform(delete("/person/100"))
                .andExpect(status().isNoContent())
                .andReturn();
        assertFalse(personRepository.existsById(100L));
    }

}
