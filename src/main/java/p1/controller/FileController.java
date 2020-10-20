package p1.controller;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
//import com.mongodb.gridfs.GridFSDBFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.NO_CONTENT;


@Controller
@RequestMapping("/files")
public class FileController {

//    @Resource
//    private GridFSBucket gridFSBucket;
    private final GridFsTemplate gridFsTemplate;
    private final MongoDatabaseFactory mongoDatabaseFactory;

    @Autowired
    public FileController(GridFsTemplate gridFsTemplate, MongoDatabaseFactory mongoDatabaseFactory) {
        this.gridFsTemplate = gridFsTemplate;
        this.mongoDatabaseFactory = mongoDatabaseFactory;
    }

    @RequestMapping(method = RequestMethod.POST)
    public HttpEntity<byte[]> createOrUpdate(@RequestParam("file") MultipartFile file) {
        String name = file.getOriginalFilename();
        try {
            Optional<GridFSFile> existing = maybeLoadFile(name);
            if (existing.isPresent()) {
                gridFsTemplate.delete(getFilenameQuery(name));
            }
            DBObject metaData = new BasicDBObject();
            metaData.put("contentType", file.getContentType());
            gridFsTemplate.store(file.getInputStream(), name, file.getContentType(), metaData);
            String resp = "<script>window.location = '/';</script>";
            return new HttpEntity<>(resp.getBytes());
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody List<String> list() {
        return getFiles().stream()
                .map(GridFSFile::getFilename)
                .collect(Collectors.toList());
    }

    @RequestMapping(path = "/{name:.+}", method = RequestMethod.GET)
    public HttpEntity<byte[]> get(@PathVariable("name") String name) {
        Optional<GridFSFile> optionalCreated = maybeLoadFile(name);
        if (optionalCreated.isPresent()) {
            GridFSFile gridFsFile = optionalCreated.get();
            GridFSBucket bucket = GridFSBuckets.create(mongoDatabaseFactory.getMongoDatabase());
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bucket.downloadToStream(gridFsFile.getId(), os);
            HttpHeaders headers = new HttpHeaders();
            headers.add(CONTENT_TYPE, String.valueOf(gridFsFile.getMetadata().get("contentType")));
            return new HttpEntity<>(os.toByteArray(), headers);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(path = "/{name:.+}", method = RequestMethod.DELETE)
    public HttpEntity<byte[]> delete(@PathVariable("name") String name) {
        Optional<GridFSFile> optionalCreated = maybeLoadFile(name);
        if (optionalCreated.isPresent()) {
            deleteFile(name);
            return new ResponseEntity<>(null, NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private List<GridFSFile> getFiles() {
        GridFSFindIterable iterable = gridFsTemplate.find(new Query());
        List<GridFSFile> results = new ArrayList<>();
        MongoCursor mongoCursor= iterable.iterator();
        while(mongoCursor.hasNext()){
            results.add((GridFSFile) mongoCursor.next());
        }
        return results;
    }

    private Optional<GridFSFile> maybeLoadFile(String name) {
        GridFSFile file = gridFsTemplate.findOne(getFilenameQuery(name));
        return Optional.ofNullable(file);
    }

    private void deleteFile(String name){gridFsTemplate.delete(getFilenameQuery(name));}

    private static Query getFilenameQuery(String name) {
        return Query.query(GridFsCriteria.whereFilename().is(name));
    }
}