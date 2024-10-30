package recipex.service.review;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;
import recipex.DefaultSpringBootTest;
import recipex.db.DbUserRecipe;
import recipex.domain.Review;
import recipex.mapper.DbMapper;
import recipex.mongo.DbRecipeRepository;

class ReviewServiceTest extends DefaultSpringBootTest {
  @Autowired
  DefaultReviewService reviewService;
  @Autowired
  DbRecipeRepository recipeRepository;

  private final String recipeId = "123e4567-e89b-12d3-a456-426614174000";

  private final DbMapper dbMapper = Mappers.getMapper(DbMapper.class);

  @Test
  void testCreateReview_AddsANewReviewToTheRecipeSuccessfully() {
    var recipe = createSampleRecipe();
    recipeRepository.save(recipe).block();

    var newReview = createSampleReview(5, "Great recipe!");

    StepVerifier.create(reviewService.createReview(recipeId, newReview))
        .expectNextMatches(restReview -> {
          assertNotNull(restReview);
          assertEquals(newReview.getComment(), restReview.getComment());
          return true;
        })
        .verifyComplete();

    var updatedRecipe = recipeRepository.findById(recipeId).block();

    assertNotNull(updatedRecipe);
    assertEquals(1, updatedRecipe.getReviews().size());
    assertEquals(newReview.getComment(), updatedRecipe.getReviews().get(0).getComment());
  }

  @Test
  void testCreateReview_UpdatesAnExistingReviewWhenUserRevisesTheirComment() {
    var recipe = createSampleRecipe();
    recipeRepository.save(recipe).block();

    var existingReview = createSampleReview(4, "Old comment");
    recipe.getReviews().add(dbMapper.toDbDto(existingReview));
    recipeRepository.save(recipe).block();

    var updatedReview = createSampleReview(5, "Updated comment");

    StepVerifier.create(reviewService.createReview(recipeId, updatedReview))
        .expectNextMatches(restReview -> {
          assertNotNull(restReview);
          assertEquals(updatedReview.getComment(), restReview.getComment());
          return true;
        })
        .verifyComplete();

    var updatedRecipe = recipeRepository.findById(recipeId).block();

    assertNotNull(updatedRecipe);
    assertEquals(1, updatedRecipe.getReviews().size());
    assertEquals("Updated comment", updatedRecipe.getReviews().get(0).getComment());
  }

  @Test
  void testCreateReview_ThrowsExceptionWhenRecipeIdDoesNotExist() {
    var review = createSampleReview(5, "Some comment");

    StepVerifier.create(reviewService.createReview("nonExistentRecipeId", review))
        .expectErrorMatches(throwable ->
            throwable instanceof RuntimeException &&
                throwable.getMessage().equals("Recipe not found with ID: nonExistentRecipeId"))
        .verify();
  }

  @Test
  void testGetReviews_ReturnsAllReviewsForTheGivenRecipeId() {
    var recipe = createSampleRecipe();
    recipeRepository.save(recipe).block();

    var review1 = createSampleReview(5, "Great recipe!");
    recipe.getReviews().add(dbMapper.toDbDto(review1));
    recipeRepository.save(recipe).block();

    StepVerifier.create(reviewService.getReviews(recipeId))
        .expectNextMatches(restReview -> {
          assertNotNull(restReview);
          assertEquals(review1.getComment(), restReview.getComment());
          return true;
        })
        .verifyComplete();

    var updatedRecipe = recipeRepository.findById(recipeId).block();

    assertNotNull(updatedRecipe);
    assertEquals(1, updatedRecipe.getReviews().size());
  }

  @Test
  void testGetReviews_IndicatesNoReviewsExistWhenRecipeHasNone() {
    var unchangedRecipe = createSampleRecipe();
    recipeRepository.save(unchangedRecipe).block();

    StepVerifier.create(reviewService.getReviews(recipeId))
        .expectComplete()
        .verify();

    var updatedRecipe = recipeRepository.findById(recipeId).block();

    assertNotNull(updatedRecipe);
    assertEquals(0, updatedRecipe.getReviews().size());
  }

  private DbUserRecipe createSampleRecipe() {
    return new DbUserRecipe()
        .setRecipeId(recipeId)
        .setReviews(new ArrayList<>());
  }

  private Review createSampleReview(int rating, String comment) {
    String userId = "987e6543-e21b-43d2-bc65-123456789abc";
    return new Review()
        .setUserId(userId)
        .setRating(rating)
        .setComment(comment);
  }
}