package recipex.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import recipex.db.DbUserRecipe;
import recipex.domain.Review;
import recipex.domain.UserRecipe;
import recipex.domain.Username;
import recipex.rest.RestReview;
import recipex.rest.RestUser;
import recipex.rest.RestUserRecipe;

public interface RecipeXApi {

  @Operation(summary = "Create a new user",
      description = "Create a user by providing a username.",
      tags = {"user"},
      responses = {
          @ApiResponse(description = "User successfully created",
              responseCode = "200",
              content = @Content(schema = @Schema(implementation = RestUser.class))),
          @ApiResponse(description = "Invalid input", responseCode = "400")
      })
  @PostMapping("/register")
  Mono<RestUser> createUser(@Valid @RequestBody Username username);

  @Operation(summary = "Create recipes for a user",
      description = "Provide a list of recipes to be associated with the specified user.",
      tags = {"recipe", "user"},
      responses = {
          @ApiResponse(description = "Recipes successfully created",
              responseCode = "200",
              content = @Content(schema = @Schema(implementation = DbUserRecipe.class))),
          @ApiResponse(description = "User not found", responseCode = "404")
      })
  @PostMapping("/recipes/{userId}")
  Flux<DbUserRecipe> createUserRecipes(@PathVariable("userId") UUID userId,
                                       @Valid @RequestBody List<UserRecipe> recipes);

  @Operation(summary = "Get user by ID",
      description = "Retrieve user details using the unique user ID.",
      tags = {"user"},
      responses = {
          @ApiResponse(description = "User details retrieved",
              responseCode = "200",
              content = @Content(schema = @Schema(implementation = RestUser.class))),
          @ApiResponse(description = "User not found", responseCode = "404")
      })
  @GetMapping("/user/{userId}")
  Mono<RestUser> getUser(@PathVariable("userId") UUID userId);

  @Operation(summary = "Get recipe by ID",
      description = "Fetch details of a specific recipe using its unique ID.",
      tags = {"recipe"},
      responses = {
          @ApiResponse(description = "Recipe details retrieved",
              responseCode = "200",
              content = @Content(schema = @Schema(implementation = RestUserRecipe.class))),
          @ApiResponse(description = "Recipe not found", responseCode = "404")
      })
  @GetMapping("/recipe/{recipeId}")
  Mono<RestUserRecipe> getRecipe(@PathVariable("recipeId") String recipeId);

  @Operation(summary = "Get recipes by title",
      description = "Search and retrieve recipes based on their title.",
      tags = {"recipe"},
      responses = {
          @ApiResponse(description = "Recipes retrieved",
              responseCode = "200",
              content = @Content(schema = @Schema(implementation = RestUserRecipe.class))),
          @ApiResponse(description = "No recipes found with the specified title", responseCode = "404")
      })
  @GetMapping("/recipes/by-title/{title}")
  Flux<RestUserRecipe> getRecipeByName(@PathVariable("title") String title);

  @Operation(summary = "Get recipes by tags",
      description = "Search and retrieve recipes based on a list of tags.",
      tags = {"recipe"},
      responses = {
          @ApiResponse(description = "Recipes retrieved",
              responseCode = "200",
              content = @Content(schema = @Schema(implementation = RestUserRecipe.class))),
          @ApiResponse(description = "No recipes found for the specified tags", responseCode = "404")
      })
  @GetMapping("/recipes/by-tags")
  Flux<RestUserRecipe> getRecipeByTags(@RequestParam List<String> tags);

  @Operation(summary = "Update an existing recipe",
      description = "Update the details of a specific recipe.",
      tags = {"recipe"},
      responses = {
          @ApiResponse(description = "Recipe successfully updated",
              responseCode = "200",
              content = @Content(schema = @Schema(implementation = RestUserRecipe.class))),
          @ApiResponse(description = "Invalid input or recipe not found", responseCode = "400")
      })
  @PutMapping("/recipe")
  Mono<DbUserRecipe> updateRecipe(@Valid @RequestBody RestUserRecipe recipe);

  @Operation(summary = "Delete a recipe",
      description = "Remove a recipe using its ID.",
      tags = {"recipe"},
      responses = {
          @ApiResponse(description = "Recipe successfully deleted", responseCode = "200"),
          @ApiResponse(description = "Recipe not found", responseCode = "404")
      })
  @DeleteMapping("/recipe/{userId}/{recipeId}")
  Mono<Void> deleteRecipe(@PathVariable @NotBlank String userId,
                          @PathVariable @NotBlank String recipeId);

  @Operation(summary = "Upload an image for a recipe",
      description = "Upload an image for the specified recipe.",
      tags = {"image"},
      responses = {
          @ApiResponse(description = "Image uploaded successfully",
              responseCode = "200"),
          @ApiResponse(description = "Recipe not found", responseCode = "404")
      })
  @PostMapping("/image/{recipeId}")
  Mono<String> uploadImage(@PathVariable("recipeId") String recipeId);

  @Operation(summary = "Get the image of a recipe",
      description = "Retrieve the image associated with the specified recipe.",
      tags = {"image"},
      responses = {
          @ApiResponse(description = "Image retrieved successfully",
              responseCode = "200"),
          @ApiResponse(description = "Image or recipe not found", responseCode = "404")
      })
  @GetMapping("/image/{recipeId}")
  Mono<String> getImage(@PathVariable("recipeId") String recipeId);

  @Operation(summary = "Delete a user",
      description = "Delete the user associated with the specified user ID.",
      tags = {"user"},
      responses = {
          @ApiResponse(description = "User successfully deleted",
              responseCode = "200"),
          @ApiResponse(description = "User not found", responseCode = "404")
      })
  @DeleteMapping("/user/{userId}")
  Mono<Void> deleteUser(@PathVariable("userId") UUID userId);

  @Operation(summary = "Create a recipe review",
      description = "Submit a review for the specified recipe.",
      tags = {"review"},
      responses = {
          @ApiResponse(description = "Review successfully created",
              responseCode = "200",
              content = @Content(schema = @Schema(implementation = RestReview.class))),
          @ApiResponse(description = "Recipe not found", responseCode = "404")
      })
  @PostMapping("/{recipeId}/reviews")
  Mono<RestReview> createRecipeReview(@PathVariable("recipeId") String recipeId,
                                      @Valid @RequestBody Review review);

  @Operation(summary = "Get reviews for a recipe",
      description = "Retrieve all reviews associated with the specified recipe.",
      tags = {"review"},
      responses = {
          @ApiResponse(description = "Reviews retrieved successfully",
              responseCode = "200",
              content = @Content(schema = @Schema(implementation = RestReview.class))),
          @ApiResponse(description = "Recipe not found", responseCode = "404")
      })
  @GetMapping("/{recipeId}/reviews")
  Flux<RestReview> getRecipeReviews(@PathVariable("recipeId") String recipeId);

}