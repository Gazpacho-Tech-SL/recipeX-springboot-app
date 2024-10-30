package recipex.api;

import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;
import recipex.DefaultSpringBootTest;
import recipex.db.DbUserRecipe;
import recipex.rest.RestReview;
import recipex.rest.RestUser;


class RecipeXApiTest extends DefaultSpringBootTest {
  static String createdRecipeId;

  private static Stream<Arguments> provideUserJson() {
    return Stream.of(
        Arguments.of("{\"name\":\"Alice\"}", false),
        Arguments.of("{\"name\":\"Alice\",\"surname\":\"Smith\"}", false),
        Arguments.of("{\"name\":\"Alice\",\"surname\":\"Smith\",\"email\":\"alice@example.com\"}", false),
        Arguments.of("{\"name\":\"Alice\",\"surname\":\"Smith\",\"email\":\"alice@example.com\",\"password\":\"short\"}", false),
        Arguments.of("{\"name\":\"Alice\",\"surname\":\"Smith\",\"email\":\"alice@example.com\",\"password\":\"Password1!\"}", true),
        Arguments.of("{\"name\":\"Bob\",\"surname\":\"Brown\",\"email\":\"bob@example.com\",\"password\":\"Password1!\"}", true),
        Arguments.of("{\"name\":\"Bo\",\"surname\":\"Smith\",\"email\":\"bo@example.com\",\"password\":\"Password1!\"}", false),
        Arguments.of("{\"name\":\"Charlie\",\"surname\":\"Johnson\",\"email\":\"invalid-email\",\"password\":\"Password1!\"}", false),
        Arguments.of("{\"name\":\"Diana\",\"surname\":\"Green\",\"email\":\"diana@example.com\",\"password\":\"Password1!\"}", true)
    );
  }

  @ParameterizedTest
  @MethodSource("provideUserJson")
  @DisplayName("Test user creation with progressively valid JSON username inputs")
  void testCreateUser(String usernameJson, boolean expectSuccess) {
    var response = webTestClient.post()
        .uri("/register")
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(usernameJson), String.class)
        .exchange();
    if (expectSuccess) {
      response.expectStatus().isOk()
          .expectBody(RestUser.class);
    } else {
      response.expectStatus().isBadRequest();
    }
  }

  private static Stream<Arguments> provideUserRecipesJson() {

    var usrId = String.valueOf(UUID.randomUUID()).toLowerCase();
    return Stream.of(
        Arguments.of(usrId, "[{\"title\":\"Guacamole\"}]", false),
        Arguments.of(usrId, "[{\"title\":\"Guacamole\", \"description\":\"A traditional Mexican dip made from ripe avocados, lime juice, cilantro, onions, and jalapeños.\"}]", false),
        Arguments.of(usrId, "[{\"title\":\"Guacamole\", \"description\":\"A traditional Mexican dip made from ripe avocados, lime juice, cilantro, onions, and jalapeños.\", \"ingredients\":[\"Ripe avocados\"]}]", false),
        Arguments.of(usrId, "[{\"title\":\"Guacamole\", \"description\":\"A traditional Mexican dip made from ripe avocados, lime juice, cilantro, onions, and jalapeños.\", \"ingredients\":[\"Ripe avocados\", \"Lime juice\"]}]", false),
        Arguments.of(usrId, "[{\"title\":\"Guacamole\", \"description\":\"A traditional Mexican dip made from ripe avocados, lime juice, cilantro, onions, and jalapeños.\", \"ingredients\":[\"Ripe avocados\", \"Lime juice\"], \"instructions\":[\"Mash the avocados in a bowl.\"]}]", true),
        Arguments.of(usrId, "[{\"title\":\"Guacamole\", \"description\":\"A traditional Mexican dip made from ripe avocados, lime juice, cilantro, onions, and jalapeños.\", \"ingredients\":[\"Ripe avocados\", \"Lime juice\", \"Fresh cilantro\", \"Red onion\", \"Jalapeño peppers\", \"Salt\"], \"instructions\":[\"Mash the avocados in a bowl.\", \"Add lime juice, cilantro, red onion, and finely chopped jalapeños.\", \"Mix well and season with salt to taste.\", \"Serve with tortilla chips or as a side dish.\"], \"tags\":[\"Mexican\", \"Dip\", \"Vegan\"]}]", true),
        Arguments.of(usrId, "[{\"title\":\"Guacamole\", \"description\":\"A traditional Mexican dip made from ripe avocados, lime juice, cilantro, onions, and jalapeños.\", \"ingredients\":[\"Ripe avocados\", \"Lime juice\", \"Fresh cilantro\", \"Red onion\", \"Jalapeño peppers\", \"Salt\"], \"instructions\":[\"Mash the avocados in a bowl.\", \"Add lime juice, cilantro, red onion, and finely chopped jalapeños.\", \"Mix well and season with salt to taste.\", \"Serve with tortilla chips or as a side dish.\"], \"tags\":[\"Mexican\", \"Dip\", \"Vegan\"]}, {\"title\":\"Caprese Salad\"}]", false),
        Arguments.of(usrId, "[{\"title\":\"Guacamole\", \"description\":\"A traditional Mexican dip made from ripe avocados, lime juice, cilantro, onions, and jalapeños.\", \"ingredients\":[\"Ripe avocados\", \"Lime juice\", \"Fresh cilantro\", \"Red onion\", \"Jalapeño peppers\", \"Salt\"], \"instructions\":[\"Mash the avocados in a bowl.\", \"Add lime juice, cilantro, red onion, and finely chopped jalapeños.\", \"Mix well and season with salt to taste.\", \"Serve with tortilla chips or as a side dish.\"], \"tags\":[\"Mexican\", \"Dip\", \"Vegan\"]}, {\"title\":\"Caprese Salad\", \"description\":\"A simple Italian salad made with fresh tomatoes, mozzarella cheese, and basil.\"}]", false),
        Arguments.of(usrId, "[{\"title\":\"Guacamole\", \"description\":\"A traditional Mexican dip made from ripe avocados, lime juice, cilantro, onions, and jalapeños.\", \"ingredients\":[\"Ripe avocados\", \"Lime juice\", \"Fresh cilantro\", \"Red onion\", \"Jalapeño peppers\", \"Salt\"], \"instructions\":[\"Mash the avocados in a bowl.\", \"Add lime juice, cilantro, red onion, and finely chopped jalapeños.\", \"Mix well and season with salt to taste.\", \"Serve with tortilla chips or as a side dish.\"], \"tags\":[\"Mexican\", \"Dip\", \"Vegan\"]}, {\"title\":\"Caprese Salad\", \"description\":\"A simple Italian salad made with fresh tomatoes, mozzarella cheese, and basil.\", \"ingredients\":[\"Fresh mozzarella\", \"Tomatoes\", \"Basil\"]}]", false),
        Arguments.of(usrId, "[{\"title\":\"Guacamole\", \"description\":\"A traditional Mexican dip made from ripe avocados, lime juice, cilantro, onions, and jalapeños.\", \"ingredients\":[\"Ripe avocados\", \"Lime juice\", \"Fresh cilantro\", \"Red onion\", \"Jalapeño peppers\", \"Salt\"], \"instructions\":[\"Mash the avocados in a bowl.\", \"Add lime juice, cilantro, red onion, and finely chopped jalapeños.\", \"Mix well and season with salt to taste.\", \"Serve with tortilla chips or as a side dish.\"], \"tags\":[\"Mexican\", \"Dip\", \"Vegan\"]}, {\"title\":\"Caprese Salad\", \"description\":\"A simple Italian salad made with fresh tomatoes, mozzarella cheese, and basil.\", \"ingredients\":[\"Fresh mozzarella\", \"Tomatoes\", \"Basil\", \"Olive oil\", \"Salt\", \"Pepper\"], \"instructions\":[\"Slice the mozzarella and tomatoes.\", \"Layer mozzarella, tomatoes, and basil.\", \"Drizzle with olive oil and season with salt and pepper.\"], \"tags\":[\"Italian\", \"Salad\", \"Vegetarian\"]}]", true)
    );
  }

  @ParameterizedTest
  @MethodSource("provideUserRecipesJson")
  @DisplayName("Test user recipes creation with progressively valid JSON inputs 2")
  void testCreateUserRecipes2(String usrId, String recipesJson, boolean shouldBeSuccessful) {
    var response = webTestClient.post()
        .uri("/recipes/{userId}", usrId)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(recipesJson), String.class)
        .exchange();

    if (shouldBeSuccessful) {
      var createdRecipes = response.expectStatus().isOk()
          .expectBodyList(DbUserRecipe.class)
          .returnResult()
          .getResponseBody();

      if (createdRecipes != null) {
        createdRecipeId = createdRecipes.get(0).getRecipeId();
      }
    } else {
      response.expectStatus().is4xxClientError();
    }
  }

  private static Stream<Arguments> provideReviewData() {
    var recipeId = createdRecipeId.toUpperCase();
    return Stream.of(
        Arguments.of(recipeId, "{\"recipeId\":\"validRecipeId\",\"userId\":\"user123\",\"rating\":0,\"comment\":\"Too low rating\"}", false),
        Arguments.of(recipeId, "{\"recipeId\":\"validRecipeId\",\"userId\":\"user123\",\"rating\":6,\"comment\":\"Too high rating\"}", false),
        Arguments.of(recipeId, "{\"recipeId\":\"validRecipeId\",\"userId\":\"user123\",\"rating\":1,\"comment\":\"Minimum rating\"}", true),
        Arguments.of(recipeId, "{\"recipeId\":\"validRecipeId\",\"userId\":\"user123\",\"rating\":5,\"comment\":\"Maximum rating\"}", true),
        Arguments.of(recipeId, "{\"recipeId\":\"validRecipeId\",\"userId\":\"user123\",\"rating\":3,\"comment\":\"This is a valid review comment within the allowed character limit.\"}", true),
        Arguments.of(recipeId, "{\"recipeId\":\"validRecipeId\",\"userId\":\"user123\",\"rating\":4,\"comment\":\"" + "A".repeat(501) + "\"}", false)
    );
  }

  @ParameterizedTest
  @MethodSource("provideReviewData")
  @DisplayName("Test creating recipe reviews with various input scenarios")
  void testCreateRecipeReview(String recipeId, String reviewJson, boolean shouldBeSuccessful) {
    var response = webTestClient.post()
        .uri("/{recipeId}/reviews", recipeId)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(reviewJson), String.class)
        .exchange();

    if (shouldBeSuccessful) {
      response.expectStatus().isOk()
          .expectBody(RestReview.class);
    } else {
      response.expectStatus().is4xxClientError();
    }
  }
}
