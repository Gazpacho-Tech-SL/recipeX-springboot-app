package recipex.db;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DbReview {

  private String reviewId;
  private String recipeId;
  private String userId;
  private int rating;
  private String comment;
  private LocalDateTime createdAt;
}
