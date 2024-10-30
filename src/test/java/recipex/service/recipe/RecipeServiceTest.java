package recipex.service.recipe;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;
import recipex.DefaultSpringBootTest;
import recipex.domain.UserRecipe;
import recipex.mapper.DbMapper;
import recipex.mapper.RestMapper;
import recipex.mongo.DbRecipeRepository;
import recipex.rest.RestUserRecipe;

class RecipeServiceTest extends DefaultSpringBootTest {
  private final UUID userId = UUID.fromString("F9B3B0EC-8FBB-4B91-9FF1-5B45C6B0E05A");
  private final UUID recipeId = UUID.fromString("7F2D50F9-6A41-47F1-937B-C91D3F0FD8F1");

  @Autowired
  RecipeService recipeService;
  @Autowired
  DbRecipeRepository dbRecipeRepository;
  RestMapper restMapper = Mappers.getMapper(RestMapper.class);
  DbMapper dbMapper = Mappers.getMapper(DbMapper.class);

  @Test
  void createRecipes_shouldSaveRecipesSuccessfully() {
    var userRecipe = userRecipe();
    var recipes = Collections.singletonList(userRecipe);

    var result = recipeService.createRecipes(userId, recipes);
    var expectedRecipeId = userRecipe.getRecipeId();
    var expectedUserId = userRecipe.getUserId();
    var dbUserRecipe = dbMapper.toDbDto(userRecipe);

    var staticCreatedAt = Objects.requireNonNull(result.blockFirst()).getCreatedAt();

    dbUserRecipe
        .setRecipeId(expectedRecipeId.toString().toUpperCase())
        .setUserId(expectedUserId.toString().toUpperCase())
        .setCreatedAt(staticCreatedAt);

    StepVerifier.create(result)
        .expectNext(dbUserRecipe)
        .verifyComplete();
  }

  @Test
  void getRecipe_shouldReturnRecipeSuccessfully() {
    var dbUserRecipe = dbMapper.toDbDto(restUserRecipe());

    dbRecipeRepository.save(dbUserRecipe).block();

    StepVerifier.create(recipeService.getRecipe(dbUserRecipe.getRecipeId()))
        .expectNext(restMapper.toRestDto(dbUserRecipe))
        .verifyComplete();
  }

  @Test
  void getRecipe_shouldReturnEmptyWhenRecipeNotFound() {
    var recipeId = UUID.randomUUID().toString();

    StepVerifier.create(recipeService.getRecipe(recipeId))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  void getRecipeByName_shouldReturnRecipesSuccessfully() {
    var dbUserRecipe = dbMapper.toDbDto(testRecipe());

    dbRecipeRepository.save(dbUserRecipe).block();

    StepVerifier.create(dbRecipeRepository.findById(dbUserRecipe.getRecipeId()))
        .expectNext(dbUserRecipe)
        .verifyComplete();

    var result = recipeService.getRecipeByName(testRecipe().getTitle());

    var expectedRecipe = restMapper.toRestDto(dbUserRecipe);

    StepVerifier.create(result)
        .expectNext(expectedRecipe)
        .verifyComplete();
  }

  @Test
  void getRecipeByTags_shouldReturnRecipesSuccessfully() {
    var dbUserRecipe = dbMapper.toDbDto(testRecipe());

    dbRecipeRepository.save(dbUserRecipe).block();

    var result = recipeService.getRecipeByTags(List.of("tag3"));

    StepVerifier.create(result)
        .expectNext(restMapper.toRestDto(dbUserRecipe))
        .verifyComplete();
  }

  @Test
  void getRecipeByTags_shouldReturnEmptyWhenNoRecipesMatch() {
    var result = recipeService.getRecipeByTags(List.of("non-existent-tag"));

    StepVerifier.create(result)
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  void updateRecipe_shouldUpdateRecipeSuccessfully() {
    var dbUserRecipe = dbMapper.toDbDto(userRecipe());
    dbRecipeRepository.save(dbUserRecipe).block();

    userRecipe().setTitle("Updated Recipe Title");

    StepVerifier.create(recipeService.updateRecipe(restUserRecipe()))
        .expectNext(dbUserRecipe)
        .verifyComplete();
  }

  @Test
  void deleteRecipe_shouldDeleteRecipeSuccessfully() {
    var recipe = String.valueOf(recipeId);
    var user = String.valueOf(userId);

    var dbUserRecipe = dbMapper.toDbDto(userRecipe());

    dbRecipeRepository.save(dbUserRecipe).block();

    StepVerifier.create(recipeService.deleteRecipe(user, recipe))
        .verifyComplete();

    StepVerifier.create(dbRecipeRepository.findById(recipe))
        .verifyComplete();
  }

  @Test
  void deleteRecipe_shouldNotDeleteIfUserIdDoesNotMatch() {
    var recipe = String.valueOf(recipeId);
    var user = String.valueOf(userId);

    var dbUserRecipe = dbMapper.toDbDto(restUserRecipe());

    dbRecipeRepository.save(dbUserRecipe).block();

    StepVerifier.create(recipeService.deleteRecipe(user, recipe))
        .verifyComplete();
  }

  @Test
  void deleteRecipe_shouldNotDeleteIfRecipeDoesNotExist() {
    var recipe = (UUID.randomUUID().toString());
    var user = (String.valueOf(userId));

    StepVerifier.create(recipeService.deleteRecipe(user, recipe))
        .verifyComplete();
  }

  RestUserRecipe restUserRecipe() {
    return new RestUserRecipe()
        .setRecipeId(String.valueOf(userId).toUpperCase())
        .setUserId(String.valueOf(recipeId).toUpperCase())
        .setTitle("Sample Recipe Title")
        .setDescription("This is a sample recipe description.")
        .setIngredients(List.of("Ingredient 1", "Ingredient 2", "Ingredient 3"))
        .setInstructions(List.of("Step 1: Do this", "Step 2: Do that"))
        .setTags(List.of("tag1", "tag2"))
        .setImageUrl("http://example.com/image.jpg")
        .setImageUploadUrl("http://example.com/upload");
  }

  UserRecipe userRecipe() {
    return new UserRecipe()
        .setRecipeId(userId)
        .setUserId(recipeId)
        .setTitle("Sample Recipe Title")
        .setDescription("This is a sample recipe description.")
        .setIngredients(List.of("Ingredient 1", "Ingredient 2", "Ingredient 3"))
        .setInstructions(List.of("Step 1: Do this", "Step 2: Do that"))
        .setTags(List.of("tag1", "tag2"))
        .setImageUrl("http://example.com/image.jpg")
        .setImageUploadUrl("http://example.com/upload");
  }

  RestUserRecipe testRecipe() {
    return new RestUserRecipe()
        .setRecipeId(String.valueOf(recipeId).toUpperCase())
        .setUserId(String.valueOf(userId).toUpperCase())
        .setTitle("Test RecipeX")
        .setDescription("This is a test recipe description.")
        .setTags(Collections.singletonList("tag3"));
  }
}