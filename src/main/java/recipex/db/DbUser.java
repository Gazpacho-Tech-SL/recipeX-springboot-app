package recipex.db;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import recipex.domain.Username;

@Data
@Accessors(chain = true)
@Document(collection = "users")
public class DbUser {

  @Id
  private String id;
  private Username username;
  private List<DbUserRecipe> recipes;
}