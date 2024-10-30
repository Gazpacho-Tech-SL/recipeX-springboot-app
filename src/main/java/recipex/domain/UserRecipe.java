package recipex.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(Include.NON_NULL)
public class UserRecipe {

  private UUID recipeId;
  private UUID userId;

  @NotBlank(message = "Title cannot be blank")
  @Size(min = 3, max = 50, message = "Title must be between 3 and 50 characters")
  private String title;

  @NotBlank(message = "Description cannot be blank")
  @Size(max = 500, message = "Description cannot exceed 500 characters")
  private String description;

  @NotEmpty(message = "Ingredients cannot be empty")
  private List<String> ingredients;

  @NotEmpty(message = "Ingredients cannot be empty")
  private List<String> instructions;

  private List<String> tags;
  private String imageUrl;
  private String imageUploadUrl;
  private LocalDateTime createdAt;
  private List<Review> reviews;
  private Double averageRating;
}
