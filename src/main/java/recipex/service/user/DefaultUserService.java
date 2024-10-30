package recipex.service.user;

import java.util.UUID;
import reactor.core.publisher.Mono;
import recipex.domain.Username;
import recipex.rest.RestUser;

public interface DefaultUserService {
  Mono<RestUser> createUser(Username username);

  Mono<RestUser> getUser(UUID userId);

  Mono<Void> deleteUser(UUID userId);
}
