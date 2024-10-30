package recipex.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import recipex.DefaultSpringBootTest;
import recipex.db.DbUserRecipe;
import recipex.domain.Review;
import recipex.domain.UserRecipe;
import recipex.domain.Username;
import recipex.rest.RestReview;
import recipex.rest.RestUser;
import recipex.rest.RestUserRecipe;

class RecipeXControllerTest extends DefaultSpringBootTest {

  private UUID createdUserId;
  private UUID createdRecipeId;


  @BeforeEach
  void initializeData() {
    var username = createSampleUsername();
    createdUserId = Objects.requireNonNull(webTestClient.post()
            .uri("/register")
            .bodyValue(username)
            .exchange()
            .expectStatus().isOk()
            .expectBody(RestUser.class)
            .returnResult()
            .getResponseBody())
        .getId();

    var recipes = List.of(createSampleUserRecipe().setUserId(createdUserId));
    createdRecipeId = UUID.fromString(Objects.requireNonNull(webTestClient.post()
            .uri("/recipes/{userId}", createdUserId)
            .bodyValue(recipes)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(DbUserRecipe.class)
            .returnResult()
            .getResponseBody())
        .get(0)
        .getRecipeId());
  }

  @AfterEach
  void cleanupData() {
    if (createdRecipeId != null) {
      webTestClient.delete()
          .uri("/recipe/{userId}/{recipeId}", createdUserId.toString().toUpperCase(), createdRecipeId.toString().toUpperCase())
          .exchange()
          .expectStatus().isOk();
    }

    if (createdUserId != null) {
      webTestClient.delete()
          .uri("/user/{userId}", createdUserId.toString().toUpperCase())
          .exchange()
          .expectStatus().isOk();
    }
  }

  @Test
  void testCreateUser() {
    var username = createSampleUsername();

    webTestClient.post()
        .uri("/register")
        .bodyValue(username)
        .exchange()
        .expectStatus().isOk()
        .expectBody(RestUser.class)
        .value(user -> assertNotNull(user.getId(), "User ID should be generated"));
  }

  @Test
  void testGetUser() {
    webTestClient.get()
        .uri("/user/{userId}", createdUserId)
        .exchange()
        .expectStatus().isOk()
        .expectBody(RestUser.class)
        .value(user -> assertNotNull(user, "User should exist"));
  }

  @Test
  void testCreateUserRecipes() {
    var recipes = List.of(createSampleUserRecipe());

    webTestClient.post()
        .uri("/recipes/{userId}", createdRecipeId.toString().toUpperCase())
        .bodyValue(recipes)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(DbUserRecipe.class)
        .hasSize(1);
  }

  @Test
  void testGetRecipe() {
    webTestClient.get()
        .uri("/recipe/{recipeId}", createdRecipeId)
        .exchange()
        .expectStatus().isOk()
        .expectBody(RestUserRecipe.class)
        .value(recipe -> assertNotNull(recipe, "Recipe should be returned"));
  }

  @Test
  void testGetRecipeByName() {
    var recipeName = "no-way-you-get-this-name";
    webTestClient.get()
        .uri("/recipes/by-title/{name}", recipeName)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(RestUserRecipe.class)
        .hasSize(1);
  }

  @Test
  void testGetRecipeByTags() {
    var tag = "no-way-you-get-this-tag";
    webTestClient.get()
        .uri("/recipes/by-tags?tags={tags}", tag)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(RestUserRecipe.class)
        .hasSize(1);
  }

  @Test
  void testUploadImage() {
    webTestClient.post()
        .uri("/image/{recipeId}", createdRecipeId)
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class)
        .value(url -> assertNotNull(url, "Image upload URL should be generated"));
  }

  @Test
  void testGetImage() {
    webTestClient.get()
        .uri("/image/{recipeId}", createdRecipeId)
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class)
        .value(url -> assertNotNull(url, "Image URL should be returned"));
  }

  @Test
  void testUpdateRecipe() {
    var updatedRecipe = createAnotherSampleUserRecipe();

    webTestClient.put()
        .uri("/recipe")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(updatedRecipe)
        .exchange()
        .expectStatus().isOk()
        .expectBody(RestUserRecipe.class)
        .consumeWith(result -> {
          var recipe = result.getResponseBody();
          System.out.println("Received recipe: " + recipe);
          assertNotNull(recipe, "Recipe should not be null");
        });
  }


  @Test
  void testDeleteUser() {
    webTestClient.delete()
        .uri("/user/{userId}", createdUserId)
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  void testDeleteRecipe() {
    webTestClient.delete()
        .uri("/recipe/{userId}/{recipeId}", createdRecipeId, createdRecipeId)
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  void testCreateRecipeReview() {
    var review = createSampleReview();

    webTestClient.post()
        .uri("/{recipeId}/reviews", createdRecipeId.toString().toUpperCase())
        .bodyValue(review)
        .exchange()
        .expectStatus().isOk()
        .expectBody(RestReview.class)
        .value(createdReview -> assertNotNull(createdReview, "Review should be created"));
  }

  @Test
  void testGetRecipeReviews() {
    webTestClient.get()
        .uri("/{recipeId}/reviews", createdRecipeId.toString().toUpperCase())
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(RestReview.class)
        .hasSize(1);
  }

  private Username createSampleUsername() {
    return new Username()
        .setName("John")
        .setSurname("Doe")
        .setEmail(generateUniqueEmail())
        .setPassword("Password@123");
  }

  private UserRecipe createSampleUserRecipe() {
    var ingredients = Arrays.asList("1 cup flour", "2 eggs", "1/2 cup milk");
    var instructions = Arrays.asList("Mix ingredients.", "Bake at 350°F for 20 minutes.");
    var tags = Arrays.asList("breakfast", "quick", "no-way-you-get-this-tag");

    return new UserRecipe()
        .setUserId(createdUserId)
        .setRecipeId(createdRecipeId)
        .setTitle("no-way-you-get-this-name")
        .setDescription("This is a sample description for a recipe.")
        .setIngredients(ingredients)
        .setInstructions(instructions)
        .setTags(tags)
        .setImageUrl("http://example.com/image.jpg")
        .setImageUploadUrl("http://example.com/upload")
        .setCreatedAt(LocalDateTime.now())
        .setReviews(Collections.singletonList(createSampleReview()))
        .setAverageRating(4.5);
  }

  private RestUserRecipe createAnotherSampleUserRecipe() {
    var ingredients = Arrays.asList("2 cups rice", "1 cup chicken", "1/2 cup peas", "2 tablespoons soy sauce");
    var instructions = Arrays.asList("Cook rice according to package instructions.",
        "In a separate pan, sauté chicken until cooked through.",
        "Add peas and soy sauce to chicken, and stir-fry for 5 minutes.",
        "Combine with rice and serve warm.");
    var tags = Arrays.asList("lunch", "easy");

    return new RestUserRecipe()
        .setRecipeId(String.valueOf(createdRecipeId).toUpperCase())
        .setUserId(String.valueOf(createdUserId))
        .setTitle("Chicken Fried Rice")
        .setDescription("A quick and easy chicken fried rice recipe that is perfect for lunch.")
        .setIngredients(ingredients)
        .setInstructions(instructions)
        .setTags(tags)
        .setImageUrl("http://example.com/chicken-fried-rice.jpg")
        .setImageUploadUrl("http://example.com/upload-chicken-fried-rice")
        .setCreatedAt(LocalDateTime.now())
        .setAverageRating(4.7);
  }

  Review createSampleReview() {
    return new Review()
        .setRecipeId("sampleRecipeId")
        .setUserId("sampleUserId")
        .setRating(5)
        .setComment("This is a sample review comment.")
        .setCreatedAt(LocalDateTime.now());
  }

  private String generateUniqueEmail() {
    return UUID.randomUUID() + "@example.com";
  }
}