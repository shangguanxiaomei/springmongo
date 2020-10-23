package p1;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import p1.entity.Address;
import p1.entity.Person;
import p1.repository.PersonRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.NO_CONTENT;
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
    @Autowired
    GridFsTemplate gridFsTemplate;

    Person p1;
    Person p2;
    File file;
    MultipartFile multipartFile;

    @BeforeEach
    public void setup() throws IOException {
        p1 = new Person(100L, "mario", "nintendo@test", 56, false, new Address("11", "kamitoba", "kyoto", "jp") );
        p2 = new Person(200L, "oreo", "kraft@test", 108, false, null);
        personRepository.save(p1);
        personRepository.save(p2);
        try {
            file = new File("resources/test/test.txt");
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file);
            String sourceString = "testString";
            byte[] sourceByte = sourceString.getBytes();
            outStream.write(sourceByte);
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        multipartFile = new MockMultipartFile("copy"+file.getName(),file.getName(), "text/plain",fileInputStream);
        DBObject metaData = new BasicDBObject();
        metaData.put("contentType", multipartFile.getContentType());
        gridFsTemplate.store(multipartFile.getInputStream(), "test.txt", multipartFile.getContentType(), metaData);

    }

    @AfterEach
    public void cleanup() {
        personRepository.deleteById(100L);
        personRepository.deleteById(200L);
        gridFsTemplate.delete(Query.query(GridFsCriteria.whereFilename().is("test.txt")));
    }

    @Test
    void contextLoads() {
    }

    @Test
    public void applicationStarts() {
        SpringmongoApplication.main(new String[] {});
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

    @Test
    public void list_returnsListOfFiles() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/files"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        String list = response.getContentAsString();
        String str[] = list.substring(1, list.length()-1).split(",");
        List<String> actualResponse = Arrays.asList(str);

        Boolean fileExist = false;
        for (String temp : actualResponse) {
              if(temp.substring(1, temp.length()-1).equals("test.txt")) fileExist = true;

        }
        assertEquals(true, fileExist);
    }

    @Test
    public void get_ShouldReturnPersonWithGivenNme() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/files/test.txt"))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        byte[] actual = response.getContentAsByteArray();
        byte[] expected = Files.readAllBytes(Path.of("resources/test/test.txt"));

        Boolean filesAreSame = true;
        if(actual.length != expected.length)filesAreSame = false;
        for (int i = 0; i < actual.length; i++)if (actual[i] != expected[i])filesAreSame = false;

        assertEquals(true, filesAreSame);
    }

    @Test
    public void createOrUpdate_ShouldReturnFileWithGivenName() throws Exception {
        byte[] byteFile = Files.readAllBytes(Path.of("resources/test/test.txt"));
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                byteFile
        );
        mockMvc.perform(MockMvcRequestBuilders.multipart("/files")
                .file(mockMultipartFile))
                .andExpect(status().isCreated())
                .andReturn().getResponse();
    }

    @Test
    public void delete_ShouldDeleteFileWithGivenNme() throws Exception {
        mockMvc.perform((RequestBuilder) delete("/files/test.txt"))
                .andExpect(status().is(204))
                .andReturn().getResponse();
    }

}
