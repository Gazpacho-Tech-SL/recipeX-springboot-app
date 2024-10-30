package recipex.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(Include.NON_NULL)
public class Username {

  @Size(min = 3, max = 20, message = "name must be between 3 and 20 characters")
  @NotBlank(message = "Username cannot be blank")
  private String name;

  @Size(min = 3, max = 20, message = "surname must be between 3 and 20 characters")
  private String surname;

  @Email(message = "Email should be valid")
  @NotBlank(message = "Email cannot be blank")
  private String email;

  @Size(min = 8, message = "Password must be at least 8 characters long")
  @Pattern(
      regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*.-]).{8,}$",
      message = "Password must contain at least one uppercase letter, one lowercase letter," +
          " and one number and a special character."
  )
  @NotBlank(message = "Password cannot be blank")
  private String password;

}
