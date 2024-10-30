package recipex.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import recipex.db.DbUser;
import recipex.db.DbUserRecipe;
import recipex.domain.Username;

class RestMapperTest {
  private final LocalDateTime createdAt = LocalDateTime.now();
  private final String recipeId = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
  private final String userId = "3bf739a1-f9e7-4d49-8e8d-865e4d581b3e";
  private final String title = "recipe name ";
  private final String description = "recipe description";
  private final String imageUrl = "http://example.com/image.jpg";
  private final RestMapper restMapper = Mappers.getMapper(RestMapper.class);


  @Test
  void givenDbRecipeXUser_whenMapToRestDto_thenReturnRestRecipeXUser() {
    // GIVEN
    var dbRecipeXUser = getDbRecipeXUser();
    // WHEN
    var result = restMapper.toRestDto(dbRecipeXUser);

    // THEN
    assertThat(result.getId()).isEqualTo(UUID.fromString(dbRecipeXUser.getId()));
    assertThat(result.getUsername().getName()).isEqualTo(dbRecipeXUser.getUsername().getName());
    assertThat(result.getUsername().getSurname()).isEqualTo(dbRecipeXUser.getUsername().getSurname());
    assertThat(result.getRecipes()).hasSize(dbRecipeXUser.getRecipes().size());
  }

  @Test
  void givenDbUserRecipe_whenMapToRestDto_thenReturnRestUserRecipe() {
    // GIVEN
    // WHEN
    var result = restMapper.toRestDto(dbUserRecipe1());

    // THEN
    assertThat(result.getRecipeId()).isEqualTo(dbUserRecipe1().getRecipeId());
    assertThat(result.getUserId()).isEqualTo(dbUserRecipe1().getUserId());
    assertThat(result.getTitle()).isEqualTo(dbUserRecipe1().getTitle());
    assertThat(result.getDescription()).isEqualTo(dbUserRecipe1().getDescription());
    assertThat(result.getIngredients()).containsExactlyElementsOf(dbUserRecipe1().getIngredients());
    assertThat(result.getInstructions()).containsExactlyElementsOf(dbUserRecipe1().getInstructions());
    assertThat(result.getTags()).containsExactlyElementsOf(dbUserRecipe1().getTags());
    assertThat(result.getImageUrl()).isEqualTo(dbUserRecipe1().getImageUrl());
    assertThat(result.getCreatedAt()).isEqualTo(dbUserRecipe1().getCreatedAt());
  }

  @Test
  void givenListOfDbUserRecipe_whenMapToRestDto_thenReturnListOfRestUserRecipe() {
    // GIVEN
    // WHEN
    var result = restMapper.toRestDto(dbUserRecipeList());

    // THEN
    assertThat(result).hasSize(dbUserRecipeList().size());
  }

  @Test
  void givenEmptyListOfDbUserRecipe_whenMapToRestDto_thenReturnEmptyListOfRestUserRecipe() {
    // GIVEN
    List<DbUserRecipe> dbUserRecipeList = Collections.emptyList();
    // WHEN
    var result = restMapper.toRestDto(dbUserRecipeList);

    // THEN
    assertThat(result).isEmpty();
  }

  @Test
  void givenDbUserRecipeWithNullFields_whenMapToRestDto_thenIgnoreNullFields() {
    // GIVEN
    DbUserRecipe dbUserRecipe = new DbUserRecipe()
        .setRecipeId(recipeId)
        .setUserId(userId)
        .setTitle(title);
    // WHEN
    var result = restMapper.toRestDto(dbUserRecipe);

    // THEN
    assertThat(result.getRecipeId()).isEqualTo(dbUserRecipe.getRecipeId());
    assertThat(result.getUserId()).isEqualTo(dbUserRecipe.getUserId());
    assertThat(result.getTitle()).isEqualTo(dbUserRecipe.getTitle());
    assertThat(result.getDescription()).isNull();
    assertThat(result.getIngredients()).isNull();
    assertThat(result.getInstructions()).isNull();
    assertThat(result.getTags()).isNull();
    assertThat(result.getImageUrl()).isNull();
    assertThat(result.getCreatedAt()).isNull();
  }

  private DbUser getDbRecipeXUser() {
    return new DbUser()
        .setId(userId)
        .setUsername(getUsername())
        .setRecipes(dbUserRecipeList());
  }

  private Username getUsername() {
    return new Username()
        .setName("user")
        .setSurname("surname");
  }

  private DbUserRecipe dbUserRecipe1() {
    return new DbUserRecipe()
        .setRecipeId(recipeId)
        .setUserId(userId)
        .setTitle(title)
        .setDescription(description)
        .setIngredients(getIngredients())
        .setInstructions(getInstructions())
        .setTags(getTags())
        .setImageUrl(imageUrl)
        .setCreatedAt(createdAt);
  }

  private DbUserRecipe dbUserRecipe2() {
    return new DbUserRecipe()
        .setRecipeId(recipeId)
        .setUserId(userId)
        .setTitle(title)
        .setDescription(description)
        .setIngredients(getIngredients())
        .setInstructions(getInstructions())
        .setTags(getTags())
        .setImageUrl(imageUrl)
        .setCreatedAt(createdAt);
  }

  private List<DbUserRecipe> dbUserRecipeList() {

    return List.of(dbUserRecipe1(), dbUserRecipe2());
  }

  private List<String> getIngredients() {
    return Arrays.asList("Ingredient 1", "Ingredient 2", "Ingredient 3");
  }

  private List<String> getTags() {
    return Arrays.asList("Tag1", "Tag2", "Tag3");
  }

  private List<String> getInstructions() {
    return Arrays.asList("Step 1", "Step 2", "Step 3");
  }
}