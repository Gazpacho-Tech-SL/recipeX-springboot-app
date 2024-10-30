
package recipex.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(Include.NON_NULL)
public class RestReview {

  private UUID reviewId;
  private String recipeId;
  private String userId;

  @Min(value = 1, message = "Rating must be at least 1")
  @Max(value = 5, message = "Rating cannot be more than 5")
  private int rating;

  @Size(max = 500, message = "Review comment cannot exceed 500 characters")
  private String comment;

  private LocalDateTime createdAt;
}
