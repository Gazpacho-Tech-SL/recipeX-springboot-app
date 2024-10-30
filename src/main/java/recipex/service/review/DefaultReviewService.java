package recipex.service.review;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import recipex.domain.Review;
import recipex.rest.RestReview;

public interface DefaultReviewService {
  Mono<RestReview> createReview(String recipeId, Review review);

  Flux<RestReview> getReviews(String recipeId);
}
