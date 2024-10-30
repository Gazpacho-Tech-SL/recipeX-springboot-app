package recipex.service.external;

import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import recipex.boot.config.properties.S3Properties;
import recipex.mapper.DbMapper;
import recipex.mapper.RestMapper;
import recipex.mongo.DbRecipeRepository;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Slf4j
@Service
@AllArgsConstructor
public class S3ExternalService implements DefaultS3ExternalService {

  private final S3Presigner s3Presigner;
  private final S3Properties s3Properties;
  private final DbRecipeRepository dbRecipeRepository;
  private final DbMapper dbMapper;
  private final RestMapper restMapper;


  @Override
  public Mono<String> uploadImage(String recipeId) {
    log.info("Starting image upload process for recipeId: {}", recipeId);

    var putObjectRequest = PutObjectRequest.builder()
        .bucket(s3Properties.getBucket())
        .key(s3Properties.getFolder() + recipeId)
        .contentType("image/jpeg")
        .build();

    var presignedRequest = s3Presigner.presignPutObject(r -> r
        .putObjectRequest(putObjectRequest)
        .signatureDuration((Duration.ofMinutes(s3Properties.getUrlExpiration()))));

    var imageUploadUrl = presignedRequest.url().toString();
    log.info("Presigned URL for image upload generated for recipeId: {}. URL: {}", recipeId, imageUploadUrl);

    return dbRecipeRepository.findById(recipeId)
        .map(restMapper::toRestDto)
        .doOnNext(recipe -> recipe.setImageUploadUrl(imageUploadUrl))
        .flatMap(recipe -> dbRecipeRepository.save(dbMapper.toDbDto(recipe)))
        .doOnSuccess(recipe -> log.info("Recipe updated successfully in the database for recipeId: {}", recipeId))
        .doOnError(error -> log.error("Error while updating recipe for recipeId: {}", recipeId, error))
        .thenReturn(imageUploadUrl);
  }

  @Override
  public Mono<String> getImage(String recipeId) {
    log.info("Starting image retrieval process for recipeId: {}", recipeId);

    var getObjectRequest = GetObjectRequest.builder()
        .bucket(s3Properties.getBucket())
        .key(s3Properties.getFolder() + recipeId)
        .build();

    var presignedRequest = s3Presigner.presignGetObject(r -> r
        .getObjectRequest(getObjectRequest)
        .signatureDuration((Duration.ofMinutes(s3Properties.getUrlExpiration()))));

    var imageGetUrl = presignedRequest.url().toString();
    log.info("Presigned URL for image retrieval generated for recipeId: {}. URL: {}", recipeId, imageGetUrl);

    return dbRecipeRepository.findById(recipeId)
        .map(restMapper::toRestDto)
        .doOnNext(recipe -> recipe.setImageUrl(imageGetUrl))
        .flatMap(recipe -> dbRecipeRepository.save(dbMapper.toDbDto(recipe)))
        .thenReturn(imageGetUrl);
  }
}
