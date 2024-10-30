package recipex.boot.config;

import recipex.mapper.DbMapper;
import recipex.mapper.DomainMapper;
import recipex.mapper.RestMapper;
import recipex.mapper.UuidMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MappersConfig {

  @Bean
  RestMapper restMapper() {
    return Mappers.getMapper(RestMapper.class);
  }

  @Bean
  UuidMapper uuidMapper() {
    return Mappers.getMapper(UuidMapper.class);
  }

  @Bean
  DbMapper dbMapper() {
    return Mappers.getMapper(DbMapper.class);
  }

  @Bean
  DomainMapper domainMapper() {
    return Mappers.getMapper(DomainMapper.class);
  }
}
