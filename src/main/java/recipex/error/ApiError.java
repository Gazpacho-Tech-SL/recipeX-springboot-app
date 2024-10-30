package recipex.error;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ApiError {
  public String error;
  public String message;
  public String path;
}
