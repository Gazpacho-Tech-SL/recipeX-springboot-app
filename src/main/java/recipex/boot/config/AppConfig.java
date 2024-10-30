package recipex.boot.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import recipex.boot.config.properties.S3Properties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@EnableConfigurationProperties({
    S3Properties.class
})
public class AppConfig {
  @Bean
  public S3AsyncClient s3AsyncClient(
      StaticCredentialsProvider staticCredentialsProvider, S3Properties s3Properties) {
    return S3AsyncClient.builder()
        .region(Region.of(s3Properties.getRegion()))
        .credentialsProvider(staticCredentialsProvider)
        .build();
  }

  @Bean
  public StaticCredentialsProvider staticCredentialsProvider(AwsBasicCredentials awsBasicCredentials) {
    return StaticCredentialsProvider.create(awsBasicCredentials);
  }

  @Bean
  public AwsBasicCredentials awsBasicCredentials(S3Properties s3Properties) {
    return AwsBasicCredentials.create(s3Properties.getAccessKey(), s3Properties.getSecretKey());
  }

  @Bean
  public S3Presigner s3Presigner(StaticCredentialsProvider staticCredentialsProvider, S3Properties s3Properties) {
    return S3Presigner.builder()
        .region(Region.of(s3Properties.getRegion()))
        .credentialsProvider(staticCredentialsProvider)
        .build();
  }
}
