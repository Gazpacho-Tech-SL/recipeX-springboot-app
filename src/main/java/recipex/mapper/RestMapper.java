package recipex.mapper;


import recipex.db.DbReview;
import recipex.db.DbUser;
import recipex.db.DbUserRecipe;
import recipex.rest.RestReview;
import recipex.rest.RestUser;
import recipex.rest.RestUserRecipe;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface RestMapper extends UuidMapper {

  RestUser toRestDto(DbUser dbUser);

  RestUserRecipe toRestDto(DbUserRecipe dbUserRecipe);

  List<RestUserRecipe> toRestDto(List<DbUserRecipe> dbUserRecipes);

  RestReview toRestDto(DbReview dbReview);
}
