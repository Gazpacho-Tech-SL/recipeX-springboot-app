package recipex.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(Include.NON_NULL)
public class RestUserRecipe {

  @NotBlank
  private String recipeId;

  @NotBlank
  private String userId;

  private String title;
  private String description;
  private List<String> ingredients;
  private List<String> instructions;
  private List<String> tags;
  private String imageUrl;
  private String imageUploadUrl;
  private LocalDateTime createdAt;
  private List<RestReview> reviews;
  private Double averageRating;
}
