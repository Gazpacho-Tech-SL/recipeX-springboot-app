package recipex.mapper;

import java.util.Optional;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UuidMapper {

  /**
   * Converts a string to a UUID.
   *
   * @param str the string to convert
   * @return the UUID
   */
  default UUID toUuid(String str) {
    return Optional.ofNullable(str)
        .map(UUID::fromString)
        .orElse(null);
  }

  /**
   * Converts a UUID to a string.
   *
   * @param uuid the UUID to convert
   * @return the string
   */
  default String toString(UUID uuid) {
    return Optional.ofNullable(uuid)
        .map(UUID::toString)
        .map(String::toUpperCase)
        .orElse(null);
  }
}
