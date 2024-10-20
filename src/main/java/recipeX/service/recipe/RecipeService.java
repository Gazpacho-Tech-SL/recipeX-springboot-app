package recipeX.service.recipe;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import recipeX.db.DbUserRecipe;
import recipeX.domain.Ids;
import recipeX.domain.UserRecipe;
import recipeX.mapper.DbMapper;
import recipeX.mapper.RestMapper;
import recipeX.mapper.UuidMapper;
import recipeX.mongo.DbRecipeRepository;
import recipeX.rest.RestUserRecipe;

@Slf4j
@Service
@AllArgsConstructor
public class RecipeService implements DefaultRecipeService {
  private static final String POST_NOT_DELETED_MESSAGE = "Error deleting post {}";
  private static final String POST_NOT_SAVED_MESSAGE = "Error saving user {} post {} ";
  private static final String SUCCESSFULLY_CREATED_RECIPES = "Successfully created recipes for user: {}";
  private static final String FETCHING_RECIPE = "Fetching recipes: {}";
  private static final String COMPLETED_FETCHING_RECIPES_BY_NAME = "Completed fetching recipes by name: {}";
  private static final String ERROR_FETCHING_RECIPE = "Error fetching recipe: {}";
  private static final String COMPLETED_FETCHING_RECIPES_BY_TAGS = "Completed fetching recipes by tags: {}";
  private static final String SUCCESSFULLY_DELETED_RECIPE = "Successfully deleted recipe with ID: {}";
  private static final String ERROR_UPDATING_RECIPE = "Error updating recipe with ID: {}";

  private final DbRecipeRepository dbRecipeRepository;
  private final DbMapper dbMapper;
  private final UuidMapper uuidMapper;
  private final RestMapper restMapper;

  @Override
  public Flux<DbUserRecipe> createRecipes(UUID userId, List<UserRecipe> recipes) {
    log.info("Creating recipes: {} for user: {}", recipes, userId);

    var dbUserRecipes = recipes.stream()
        .map(restUserPost -> dbMapper.toDbDto(restUserPost
                .setUserId(userId)
                .setRecipeId(UUID.randomUUID()))
            .setCreatedAt(LocalDateTime.now()))
        .toList();

    return dbRecipeRepository.saveAll(dbUserRecipes)
        .doOnError(error -> log.error(POST_NOT_SAVED_MESSAGE, userId, dbUserRecipes))
        .doOnComplete(() -> log.info(SUCCESSFULLY_CREATED_RECIPES, userId))
        .thenMany(Flux.fromIterable(dbUserRecipes));
  }

  @Override
  public Mono<RestUserRecipe> getRecipe(String recipeId) {
    log.info("Fetching recipe with ID: {}", recipeId);

    return dbRecipeRepository.findById(recipeId.toUpperCase())
        .doOnSuccess(recipe -> log.info(FETCHING_RECIPE, recipe))
        .map(restMapper::toRestDto)
        .doOnError(error -> log.error(ERROR_FETCHING_RECIPE, recipeId, error));
  }

  @Override
  public Flux<RestUserRecipe> getRecipeByName(String name) {
    log.info("Fetching recipes by name: {}", name);

    return dbRecipeRepository.findByTitle(name)
        .doOnComplete(() -> log.info(COMPLETED_FETCHING_RECIPES_BY_NAME, name))
        .map(restMapper::toRestDto)
        .doOnError(error -> log.error(ERROR_FETCHING_RECIPE, name, error));
  }

  @Override
  public Flux<RestUserRecipe> getRecipeByTags(List<String> tags) {
    log.info("Fetching recipes by tags: {}", tags);

    return dbRecipeRepository.findByTagsContaining(tags)
        .doOnComplete(() -> log.info(COMPLETED_FETCHING_RECIPES_BY_TAGS, tags))
        .map(restMapper::toRestDto)
        .doOnError(error -> log.error(ERROR_FETCHING_RECIPE, tags, error));
  }

  @Override
  public Mono<RestUserRecipe> updateRecipe(UserRecipe recipe) {
    log.info("updating recipe: {}", recipe);

    var recipeId = uuidMapper.toString(recipe.getRecipeId());

    return dbRecipeRepository.findById(recipeId)
        .map(update -> dbMapper.toDbDto(recipe))
        .flatMap(dbRecipeRepository::save)
        .map(restMapper::toRestDto)
        .doOnError(error -> log.error(ERROR_UPDATING_RECIPE, recipeId, error));
  }

  @Override
  public Mono<Void> deleteRecipe(Ids ids) {
    log.info("Deleting recipe with ID: {} for user: {}", ids.getRecipeId(), ids.getUserId());

    return dbRecipeRepository.findById(ids.getRecipeId())
        .filter(dbUserPost -> dbUserPost.getUserId().equals(ids.getUserId()))
        .then(dbRecipeRepository.deleteById(ids.getRecipeId()))
        .doOnSuccess(unused -> log.info(SUCCESSFULLY_DELETED_RECIPE, ids.getRecipeId()))
        .doOnError(error -> log.error(POST_NOT_DELETED_MESSAGE, ids.getRecipeId()));
  }
}
