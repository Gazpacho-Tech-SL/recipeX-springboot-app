package recipex.service.external;

import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class S3ExternalServiceTest {
  @Mock
  S3ExternalService s3ExternalService;

  @Test
  void testUploadImage_Success() {
    var recipeId = UUID.randomUUID();
    String expectedUrl = "http://example.com/image-upload-url";

    when(s3ExternalService.uploadImage(recipeId.toString())).thenReturn(Mono.just(expectedUrl));

    Mono<String> result = s3ExternalService.uploadImage(recipeId.toString());

    StepVerifier.create(result)
        .expectNext(expectedUrl)
        .verifyComplete();
  }

  @Test
  void testGetImage_Success() {
    var recipeId = UUID.randomUUID();
    String expectedUrl = "http://example.com/image-get-url";

    when(s3ExternalService.getImage(recipeId.toString())).thenReturn(Mono.just(expectedUrl));

    var result = s3ExternalService.getImage(recipeId.toString());

    StepVerifier.create(result)
        .expectNext(expectedUrl)
        .verifyComplete();
  }

  @Test
  void testGetImage_RecipeNotFound() {
    var recipeId = UUID.randomUUID();

    when(s3ExternalService.getImage(recipeId.toString())).thenReturn(Mono.empty());

    var result = s3ExternalService.getImage(recipeId.toString());

    StepVerifier.create(result)
        .expectComplete()
        .verify();
  }

  @Test
  void testUploadImage_Failure() {
    var recipeId = UUID.randomUUID();
    when(s3ExternalService.uploadImage(recipeId.toString())).thenReturn(Mono.error(new RuntimeException("Upload failed")));

    var result = s3ExternalService.uploadImage(recipeId.toString());

    StepVerifier.create(result)
        .expectErrorMessage("Upload failed")
        .verify();
  }

  @Test
  void testGetImage_RetrievalFailure() {
    UUID recipeId = UUID.randomUUID();
    when(s3ExternalService.getImage(recipeId.toString())).thenReturn(Mono.error(new RuntimeException("Retrieval failed")));

    var result = s3ExternalService.getImage(recipeId.toString());

    StepVerifier.create(result)
        .expectErrorMessage("Retrieval failed")
        .verify();
  }

  @Test
  void testImageUrlIsUpdated() {
    var recipeId = UUID.randomUUID();
    String oldUrl = "http://example.com/old-image-url";
    String newUrl = "http://example.com/new-image-url";

    when(s3ExternalService.getImage(recipeId.toString())).thenReturn(Mono.just(oldUrl));

    Mono<String> initialResult = s3ExternalService.getImage(recipeId.toString());

    StepVerifier.create(initialResult)
        .expectNext(oldUrl)
        .verifyComplete();

    when(s3ExternalService.uploadImage(recipeId.toString())).thenReturn(Mono.just(newUrl));

    var uploadResult = s3ExternalService.uploadImage(recipeId.toString());

    StepVerifier.create(uploadResult)
        .expectNext(newUrl)
        .verifyComplete();

    when(s3ExternalService.getImage(recipeId.toString())).thenReturn(Mono.just(newUrl));

    var finalResult = s3ExternalService.getImage(recipeId.toString());

    StepVerifier.create(finalResult)
        .expectNext(newUrl)
        .verifyComplete();
  }
}