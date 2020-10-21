package p1.controller;

import com.mongodb.*;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Collation;
import org.bson.*;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileControllerTest {

    FileController fileController;
    GridFsTemplate gridFsTemplate;
    MongoDatabaseFactory mongoDatabaseFactory;
    List<GridFSFile> gridFSFile;

    @BeforeEach
    public void setup() {
        gridFsTemplate = Mockito.mock(GridFsTemplate.class);
        mongoDatabaseFactory = Mockito.mock(MongoDatabaseFactory.class);
        fileController = new FileController(gridFsTemplate, mongoDatabaseFactory);
        BsonString id =  new BsonString("5f90697fa049ff6907524924");
        Document metaData =  new Document("contentType", "image/jpeg");
        GridFSFile file = new GridFSFile(id, "firstFile", 1l, 1, new Date(), metaData);
        BsonString id2 =  new BsonString("5f906a30a2515a6fd0a2e0a6");
        GridFSFile file2 = new GridFSFile(id2, "secondFile", 2l, 2, new Date(), metaData);
        gridFSFile = Arrays.asList(file, file2);
    }

    @Test
    public void getFiles_ShouldReturnListOfFiles(){
        //arrange

        GridFSFindIterable iterable = new GridFSFindIterable() {
            private MongoCursor mongoCursor = new MongoCursor() {
                private int i = 0;
                @Override
                public void close() {}
                @Override
                public boolean hasNext() {return (i < 2);}
                @Override
                public Object next() {return gridFSFile.get(i++);}
                @Override
                public Object tryNext() {return null;}
                @Override
                public ServerCursor getServerCursor() {return null;}
                @Override
                public ServerAddress getServerAddress() {return null;}
            };
            @Override
            public GridFSFindIterable filter(Bson filter) {return null;}
            @Override
            public GridFSFindIterable limit(int limit) {return null;}
            @Override
            public GridFSFindIterable skip(int skip){return null;}
            @Override
            public GridFSFindIterable sort(Bson sort) {return null;}
            @Override
            public GridFSFindIterable noCursorTimeout(boolean noCursorTimeout) {return null;}
            @Override
            public GridFSFindIterable maxTime(long maxTime, TimeUnit timeUnit) {return null;}
            @Override
            public GridFSFindIterable batchSize(int batchSize) {return null;}
            @Override
            public GridFSFindIterable collation(Collation collation) {return null;}
            @Override
            public MongoCursor<GridFSFile> iterator() {return mongoCursor;}
            @Override
            public MongoCursor<GridFSFile> cursor() {return null;}
            @Override
            public GridFSFile first() {return null;}
            @Override
            public <U> MongoIterable<U> map(Function<GridFSFile, U> mapper) {return null;}
            @Override
            public <A extends Collection<? super GridFSFile>> A into(A target) {return null;}
        };


        Mockito.when(gridFsTemplate.find(new Query())).thenReturn(iterable);

        //act
        List<String> response = fileController.list();
        List<String> expected = Arrays.asList("firstFile", "secondFile");

        //assert
        Mockito.verify(gridFsTemplate).find(new Query());
        assertEquals(expected, response);
    }

    @Test
    public void createOrUpdate_ShouldCreated() throws IOException {
        //arrange
        File file = null;
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
        MultipartFile multipartFile = new MockMultipartFile("copy"+file.getName(),file.getName(), "text/plain",fileInputStream);
        DBObject metaData = new BasicDBObject();
        metaData.put("contentType", multipartFile.getContentType());
        Mockito.when(gridFsTemplate.store(multipartFile.getInputStream(), "test.txt", multipartFile.getContentType(), metaData)).thenReturn(null);

        //act
        HttpEntity<byte[]> response = fileController.createOrUpdate(multipartFile);

        //assert
//        Mockito.verify(gridFsTemplate).store(ArgumentMatchers.any(java.io.InputStream.class),
//                ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(org.bson.Document.class));
       assertEquals(new HttpEntity<>("<script>window.location = '/';</script>".getBytes()), response);

    }

    @Test
    public void createOrUpdate_ShouldDeleteAndCreated() throws IOException {
        //arrange
        File file = null;
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
        MultipartFile multipartFile = new MockMultipartFile("copy"+file.getName(),file.getName(), "text/plain",fileInputStream);
        DBObject metaData = new BasicDBObject();
        metaData.put("contentType", multipartFile.getContentType());
        Mockito.when(gridFsTemplate.findOne(Query.query(GridFsCriteria.whereFilename().is("test.txt")))).thenReturn(gridFSFile.get(0));
        Mockito.when(gridFsTemplate.store(multipartFile.getInputStream(), "test.txt", multipartFile.getContentType(), metaData)).thenReturn(null);

        //act
        HttpEntity<byte[]> response = fileController.createOrUpdate(multipartFile);

        //assert
//        Mockito.verify(gridFsTemplate).store(ArgumentMatchers.any(java.io.InputStream.class),
//                ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(org.bson.Document.class));
        assertEquals(new HttpEntity<>("<script>window.location = '/';</script>".getBytes()), response);

    }

}

