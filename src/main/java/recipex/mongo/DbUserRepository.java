package recipex.mongo;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import recipex.db.DbUser;

@Repository
public interface DbUserRepository extends ReactiveMongoRepository<DbUser, String> {
  @Query(value = "{ 'username.email': ?0 }", fields = "{ 'username.email': 1 }")
  Mono<DbUser> findByEmail(String email);
}
