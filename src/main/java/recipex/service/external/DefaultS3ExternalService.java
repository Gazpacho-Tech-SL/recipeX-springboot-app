package recipex.service.external;

import reactor.core.publisher.Mono;

public interface DefaultS3ExternalService {
  Mono<String> uploadImage(String recipeId);

  Mono<String> getImage(String recipeId);
}
