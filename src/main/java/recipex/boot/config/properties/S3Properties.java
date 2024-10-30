package recipex.boot.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "recipex.aws.s3")
public class S3Properties {

  private @NotBlank String accessKey;
  private @NotBlank String secretKey;
  private String region;
  private Long urlExpiration;
  private String bucket;
  private String folder;
}
