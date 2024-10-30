package recipex.service.recipe;

import java.util.List;
import java.util.UUID;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import recipex.db.DbUserRecipe;
import recipex.domain.UserRecipe;
import recipex.rest.RestUserRecipe;

public interface DefaultRecipeService {
  Flux<DbUserRecipe> createRecipes(UUID userId, List<UserRecipe> recipes);

  Mono<RestUserRecipe> getRecipe(String recipeId);

  Flux<RestUserRecipe> getRecipeByName(String name);

  Flux<RestUserRecipe> getRecipeByTags(List<String> tags);

  Mono<DbUserRecipe> updateRecipe(RestUserRecipe recipe);

  Mono<Void> deleteRecipe(String userId, String recipeId);
}
