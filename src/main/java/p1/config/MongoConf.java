//package p1.configbean;
//
//import com.mongodb.client.MongoDatabase;
//import com.mongodb.client.gridfs.GridFSBucket;
//import com.mongodb.client.gridfs.GridFSBuckets;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.mongodb.MongoDatabaseFactory;
//
//@Configuration
//public class MongoConf {
//
//    @Autowired
//    private MongoDatabaseFactory mongoDatabaseFactory;
//
//
//    @Bean
//    public GridFSBucket getGridFSBucket() {
//        MongoDatabase db = mongoDatabaseFactory.getMongoDatabase();
//        return GridFSBuckets.create(db);
//    }
//}
