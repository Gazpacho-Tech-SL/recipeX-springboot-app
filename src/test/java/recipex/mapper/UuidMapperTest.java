package recipex.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class UuidMapperTest {

  private final UuidMapper uuidMapper = Mappers.getMapper(UuidMapper.class);
  private final String uuidStr = "f47ac10b-58cc-4372-a567-0e02b2c3d479";

  @Test
  void givenUuidString_whenConvertToUuid_thenReturnUUid() {
    // GIVEN
    var expectedUuid = UUID.fromString(uuidStr);
    // WHEN
    var resultUuid = uuidMapper.toUuid(uuidStr);
    // THEN
    assertThat(expectedUuid).isEqualTo(resultUuid);
  }

  @Test
  void givenNullUuidString_whenConvertToUuid_thenReturnNull() {
    // GIVEN
    // WHEN
    var resultUuid = uuidMapper.toUuid(null);
    // THEN
    assertThat(resultUuid).isNull();
  }

  @Test
  void givenUuid_whenConvertToString_thenReturnUuidString() {
    // GIVEN
    var uuid = UUID.fromString(uuidStr);
    // WHEN
    var resultUuidStr = uuidMapper.toString(uuid);
    // THEN
    assertThat(resultUuidStr).isEqualTo(uuid.toString().toUpperCase());
  }

  @Test
  void givenNullUuid_whenConvertToString_thenReturnNull() {
    // GIVEN
    // WHEN
    var resultUuidStr = uuidMapper.toString(null);
    // THEN
    assertThat(resultUuidStr).isNull();
  }
}