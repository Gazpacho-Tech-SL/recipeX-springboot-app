package recipex.api;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import recipex.db.DbUserRecipe;
import recipex.domain.Review;
import recipex.domain.UserRecipe;
import recipex.domain.Username;
import recipex.rest.RestReview;
import recipex.rest.RestUser;
import recipex.rest.RestUserRecipe;
import recipex.service.external.DefaultS3ExternalService;
import recipex.service.recipe.DefaultRecipeService;
import recipex.service.review.DefaultReviewService;
import recipex.service.user.DefaultUserService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RecipeXController implements RecipeXApi {
  private final DefaultUserService userService;
  private final DefaultRecipeService recipeService;
  private final DefaultS3ExternalService defaultS3ExternalService;
  private final DefaultReviewService defaultReviewService;

  @Override
  public Mono<RestUser> createUser(Username username) {
    return userService.createUser(username);
  }

  @Override
  public Mono<RestUser> getUser(UUID userId) {
    return userService.getUser(userId);
  }

  @Override
  public Flux<DbUserRecipe> createUserRecipes(UUID userId, List<UserRecipe> recipes) {
    return recipeService.createRecipes(userId, recipes);
  }

  @Override
  public Mono<RestUserRecipe> getRecipe(String recipeId) {
    return recipeService.getRecipe(recipeId);
  }

  @Override
  public Flux<RestUserRecipe> getRecipeByName(String name) {
    return recipeService.getRecipeByName(name);
  }

  @Override
  public Flux<RestUserRecipe> getRecipeByTags(List<String> tags) {
    return recipeService.getRecipeByTags(tags);
  }

  @Override
  public Mono<DbUserRecipe> updateRecipe(RestUserRecipe recipe) {
    return recipeService.updateRecipe(recipe);
  }

  @Override
  public Mono<Void> deleteRecipe(String userId, String recipeId) {
    return recipeService.deleteRecipe(userId, recipeId);
  }

  @Override
  public Mono<String> uploadImage(String recipeId) {
    return defaultS3ExternalService.uploadImage(recipeId);
  }

  @Override
  public Mono<String> getImage(String recipeId) {
    return defaultS3ExternalService.getImage(recipeId);
  }

  @Override
  public Mono<Void> deleteUser(UUID userId) {
    return userService.deleteUser(userId);
  }

  @Override
  public Mono<RestReview> createRecipeReview(String recipeId, Review review) {
    return defaultReviewService.createReview(recipeId, review);
  }

  @Override
  public Flux<RestReview> getRecipeReviews(String recipeId) {
    return defaultReviewService.getReviews(recipeId);
  }
}