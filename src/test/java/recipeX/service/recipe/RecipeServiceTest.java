package recipeX.service.recipe;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;
import recipeX.DefaultSpringBootTest;
import recipeX.domain.Ids;
import recipeX.domain.UserRecipe;
import recipeX.mapper.DbMapper;
import recipeX.mapper.RestMapper;
import recipeX.mongo.DbRecipeRepository;
import recipeX.rest.RestUserRecipe;

class RecipeServiceTest extends DefaultSpringBootTest {
  private final UUID userId = UUID.fromString("f9b3b0ec-8fbb-4b91-9ff1-5b45c6b0e05a");
  private final UUID recipeId = UUID.fromString("7f2d50f9-6a41-47f1-937b-c91d3f0fd8f1");

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
    var dbUserRecipe2 = dbMapper.toDbDto(testRecipe2());

    dbRecipeRepository.save(dbUserRecipe).block();
    dbRecipeRepository.save(dbUserRecipe2).block();

    var result = recipeService.getRecipeByTags(List.of("tag3"));

    StepVerifier.create(result)
        .expectNextCount(2)
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

    StepVerifier.create(recipeService.updateRecipe(userRecipe()))
        .expectNext(restMapper.toRestDto(dbUserRecipe))
        .verifyComplete();
  }

  @Test
  void deleteRecipe_shouldDeleteRecipeSuccessfully() {

    var ids = new Ids()
        .setRecipeId(String.valueOf(recipeId))
        .setUserId(String.valueOf(userId));

    var dbUserRecipe = dbMapper.toDbDto(userRecipe());

    dbRecipeRepository.save(dbUserRecipe).block();

    StepVerifier.create(recipeService.deleteRecipe(ids))
        .verifyComplete();

    StepVerifier.create(dbRecipeRepository.findById(ids.getRecipeId()))
        .verifyComplete();
  }

  @Test
  void deleteRecipe_shouldNotDeleteIfUserIdDoesNotMatch() {
    var ids = new Ids()
        .setRecipeId(String.valueOf(recipeId))
        .setUserId(String.valueOf(userId));

    var dbUserRecipe = dbMapper.toDbDto(restUserRecipe());

    dbRecipeRepository.save(dbUserRecipe).block();

    StepVerifier.create(recipeService.deleteRecipe(ids))
        .verifyComplete();
  }

  @Test
  void deleteRecipe_shouldNotDeleteIfRecipeDoesNotExist() {
    var ids = new Ids()
        .setRecipeId(UUID.randomUUID().toString())
        .setUserId(String.valueOf(userId));

    StepVerifier.create(recipeService.deleteRecipe(ids))
        .verifyComplete();
  }

  RestUserRecipe restUserRecipe() {
    return new RestUserRecipe()
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
        .setRecipeId(UUID.fromString("7f2d50f9-6a41-47f1-937b-c91d3f0fd8f1"))
        .setUserId(UUID.fromString("f9b3b0ec-8fbb-4b91-9ff1-5b45c6b0e05a"))
        .setTitle("Test RecipeX")
        .setDescription("This is a test recipe description.")
        .setTags(Collections.singletonList("tag3"));
  }

  RestUserRecipe testRecipe2() {
    return new RestUserRecipe()
        .setRecipeId(UUID.fromString("7f2d50f9-6a41-47f1-937b-c91d3f0fd8f2"))
        .setUserId(UUID.fromString("f9b3b0ec-8fbb-4b91-9ff1-5b45c6b0e05a"))
        .setTitle("Test RecipeX 2")
        .setDescription("This is a test recipe description 2.")
        .setTags(Collections.singletonList("tag3"));
  }
}