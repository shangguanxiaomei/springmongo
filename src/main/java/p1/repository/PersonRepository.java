package p1.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import p1.entity.Person;

@RepositoryRestResource(path="personapi")
public interface PersonRepository extends MongoRepository<Person, Long> {
}
