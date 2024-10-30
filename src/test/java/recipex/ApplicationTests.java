package recipex;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ApplicationTests extends DefaultSpringBootTest {

  @Test
  void contextLoads() {
    assertThat(webTestClient).isNotNull();
  }
}
