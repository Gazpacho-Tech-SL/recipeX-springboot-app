package recipex.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.experimental.Accessors;
import recipex.domain.Username;

@Data
@Accessors(chain = true)
@JsonInclude(Include.NON_NULL)
public class RestUser {

  private UUID id;
  private Username username;
  private List<RestUserRecipe> recipes;
}