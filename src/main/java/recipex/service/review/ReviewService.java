package recipex.service.review;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import recipex.db.DbUserRecipe;
import recipex.domain.Review;
import recipex.mapper.DbMapper;
import recipex.mapper.RestMapper;
import recipex.mongo.DbRecipeRepository;
import recipex.rest.RestReview;

@Slf4j
@Service
@AllArgsConstructor
public class ReviewService implements DefaultReviewService {
  private final DbRecipeRepository recipeRepository;
  private final DbMapper dbMapper;
  private final RestMapper restMapper;

  @Override
  public Mono<RestReview> createReview(String recipeId, Review review) {
    log.info("Attempting to create or update review for recipeId: {} by userId: {}", recipeId, review.getUserId());

    return recipeRepository.findById(recipeId)
        .flatMap(recipe -> handleReviewUpdateOrAddition(recipe, review, recipeId))
        .flatMap(recipeRepository::save)
        .map(savedRecipe -> restMapper.toRestDto(dbMapper.toDbDto(buildNewReview(recipeId, review))))
        .switchIfEmpty(Mono.error(new RuntimeException("Recipe not found with ID: " + recipeId)));
  }

  private Mono<DbUserRecipe> handleReviewUpdateOrAddition(DbUserRecipe recipe, Review review, String recipeId) {

    return checkIfUserReviewed(recipe, review.getUserId())
        .flatMap(isReviewed -> isReviewed
            ? updateCommentInExistingReview(recipe, review)
            : addNewReviewToRecipe(recipe, review, recipeId));
  }

  private Mono<DbUserRecipe> addNewReviewToRecipe(DbUserRecipe recipe, Review review, String recipeId) {
    log.debug("Adding new review to recipeId: {}", recipeId);

    var newReview = buildNewReview(recipeId, review);

    recipe.setReviews(Optional.ofNullable(recipe.getReviews()).orElseGet(ArrayList::new));
    recipe.getReviews().add(dbMapper.toDbDto(newReview));

    return Mono.just(recipe);
  }

  private Mono<DbUserRecipe> updateCommentInExistingReview(DbUserRecipe recipe, Review review) {
    log.info("Updating comment for existing review by userId: {}", review.getUserId());

    var reviews = Optional.ofNullable(recipe.getReviews()).orElseGet(ArrayList::new);

    reviews.stream()
        .filter(r -> r.getUserId().equals(review.getUserId()))
        .findFirst()
        .ifPresent(existingReview -> existingReview.setComment(review.getComment()));

    return Mono.just(recipe);
  }


  private RestReview buildNewReview(String recipeId, Review review) {

    return new RestReview()
        .setReviewId(UUID.randomUUID())
        .setRecipeId(recipeId)
        .setUserId(review.getUserId())
        .setRating(review.getRating())
        .setComment(review.getComment())
        .setCreatedAt(LocalDateTime.now());
  }

  private Mono<Boolean> checkIfUserReviewed(DbUserRecipe recipe, String userId) {
    log.info("Checking if userId: {} has previously rated", userId);

    return Mono.just(Optional.ofNullable(recipe.getReviews())
        .orElseGet(ArrayList::new)
        .stream()
        .anyMatch(existingReview -> existingReview.getUserId().equals(userId)));
  }

  @Override
  public Flux<RestReview> getReviews(String recipeId) {
    log.info("Fetching reviews for recipeId: {}", recipeId);

    return recipeRepository.findById(recipeId)
        .flatMapMany(dbUserRecipe -> Flux.fromIterable(dbUserRecipe.getReviews()))
        .map(restMapper::toRestDto);
  }
}
