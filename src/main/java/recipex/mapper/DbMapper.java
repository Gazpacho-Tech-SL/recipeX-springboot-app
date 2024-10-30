package recipex.mapper;


import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import recipex.db.DbReview;
import recipex.db.DbUser;
import recipex.db.DbUserRecipe;
import recipex.domain.Review;
import recipex.domain.UserRecipe;
import recipex.rest.RestReview;
import recipex.rest.RestUser;
import recipex.rest.RestUserRecipe;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface DbMapper extends UuidMapper {

  DbUser toDbDto(RestUser restUser);

  DbUserRecipe toDbDto(RestUserRecipe restUserRecipe);

  @Mapping(target = "reviewId", ignore = true)
  DbReview toDbDto(Review review);

  DbUserRecipe toDbDto(UserRecipe userRecipe);

  List<DbUserRecipe> toDbDto(List<RestUserRecipe> restUserRecipes);

  DbReview toDbDto(RestReview restReview);
}
