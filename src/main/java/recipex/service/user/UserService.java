package recipex.service.user;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import recipex.domain.Username;
import recipex.exceptions.EmailAlreadyExistsException;
import recipex.mapper.DbMapper;
import recipex.mapper.RestMapper;
import recipex.mapper.UuidMapper;
import recipex.mongo.DbRecipeRepository;
import recipex.mongo.DbUserRepository;
import recipex.rest.RestUser;

@Slf4j
@Service
@AllArgsConstructor
public class UserService implements DefaultUserService {
  private static final String USER_DELETED_MESSAGE = "User {} deleted";
  private static final String RECIPE_DELETED_MESSAGE = "Recipe {} associated with user {} deleted";
  private static final String USER_NOT_DELETED_MESSAGE = "Error deleting user {}";
  private static final String RECIPE_NOT_DELETED_MESSAGE = "Error deleting recipe {}";
  private static final String USER_NOT_SAVED_MESSAGE = "Error saving user {}";
  private static final String USER_SAVED_MESSAGE = "User {} saved successfully";
  private static final String USER_RETRIEVED_MESSAGE = "User {} retrieved successfully";
  private static final String USER_NOT_FOUND_MESSAGE = "User {} not found";

  private final DbUserRepository dbUserRepository;
  private final DbRecipeRepository dbRecipeRepository;
  private final DbMapper dbMapper;
  private final UuidMapper uuidMapper;
  private final RestMapper restMapper;

  @Override
  public Mono<RestUser> createUser(Username username) {
    var user = new RestUser()
        .setId(UUID.randomUUID())
        .setUsername(username);

    log.info("Creating user with username: {}", username);

    return checkIfEmailExists(user.getUsername().getEmail())
        .flatMap(emailExists -> emailExists
            ? Mono.error(new EmailAlreadyExistsException(user.getUsername().getEmail()))
            : saveUser(user));
  }

  private Mono<RestUser> saveUser(RestUser user) {
    return Mono.just(user)
        .map(dbMapper::toDbDto)
        .flatMap(dbUserRepository::save)
        .doOnSuccess(savedUser -> log.info(USER_SAVED_MESSAGE, savedUser.getId()))
        .doOnError(error -> log.error(USER_NOT_SAVED_MESSAGE, user.getId()))
        .map(restMapper::toRestDto);
  }

  @Override
  public Mono<RestUser> getUser(UUID userId) {
    log.info("Retrieving user with ID: {}", userId);

    return dbUserRepository.findById(uuidMapper.toString(userId))
        .flatMap(dbUser -> dbRecipeRepository.findByUserId(dbUser.getId())
            .collectList()
            .doOnNext(dbUser::setRecipes)
            .thenReturn(dbUser))
        .map(restMapper::toRestDto)
        .doOnSuccess(user -> log.info(user != null ? USER_RETRIEVED_MESSAGE : USER_NOT_FOUND_MESSAGE, userId));
  }

  @Override
  public Mono<Void> deleteUser(UUID userId) {
    String user = uuidMapper.toString(userId);

    log.info("Deleting user with ID: {}", userId);

    return dbUserRepository.deleteById(user)
        .doOnSuccess(unused -> log.info(USER_DELETED_MESSAGE, user))
        .doOnError(error -> log.error(USER_NOT_DELETED_MESSAGE, user, error))
        .thenMany(dbRecipeRepository.findByUserId(user))
        .flatMap(recipe -> dbRecipeRepository.deleteById(recipe.getRecipeId())
            .doOnSuccess(unused -> log.info(RECIPE_DELETED_MESSAGE, recipe.getRecipeId(), user))
            .doOnError(error -> log.error(RECIPE_NOT_DELETED_MESSAGE, recipe.getRecipeId(), error))
        )
        .then();
  }

  private Mono<Boolean> checkIfEmailExists(String email) {
    log.info("Checking if email {} exists", email);

    return dbUserRepository.findByEmail(email)
        .map(existingUser -> true)
        .defaultIfEmpty(false);
  }
}
