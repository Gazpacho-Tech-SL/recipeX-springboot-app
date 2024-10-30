package recipex.service.user;

import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;
import recipex.DefaultSpringBootTest;
import recipex.domain.Username;
import recipex.exceptions.EmailAlreadyExistsException;
import recipex.mongo.DbUserRepository;
import recipex.rest.RestUser;


class UserServiceTest extends DefaultSpringBootTest {

  @Autowired
  DefaultUserService userService;
  @Autowired
  DbUserRepository dbUserRepository;

  @Test
  void createUser_shouldCreateUserSuccessfully() {
    var testUsername = username().setEmail(generateUniqueEmail());
    var result = userService.createUser(testUsername);

    StepVerifier.create(result)
        .assertNext(createdUser -> {
          assert createdUser.getUsername().getEmail().equals(testUsername.getEmail());
          assert createdUser.getId() != null;
        })
        .verifyComplete();

    StepVerifier.create(dbUserRepository.findByEmail(testUsername.getEmail()))
        .assertNext(dbUser -> {
          assert dbUser.getId() != null;
          assert dbUser.getUsername().getEmail().equals(testUsername.getEmail());
        })
        .verifyComplete();
  }

  @Test
  void createUser_shouldThrowUserAlreadyExistsException() {
    var existingUsername = username().setEmail(generateUniqueEmail());

    userService.createUser(existingUsername).block();

    StepVerifier.create(userService.createUser(existingUsername))
        .expectError(EmailAlreadyExistsException.class)
        .verify();

    StepVerifier.create(dbUserRepository.findByEmail(existingUsername.getEmail()))
        .assertNext(dbUser -> {
          assert dbUser.getUsername().getEmail().equals(existingUsername.getEmail());
        })
        .verifyComplete();
  }

  @Test
  void getUser_shouldRetrieveUserSuccessfully() {
    var testUsername = username();
    var createdUser = userService.createUser(testUsername).block();

    assert createdUser != null;
    var expectedUser = new RestUser()
        .setId(createdUser.getId())
        .setUsername(testUsername)
        .setRecipes(Collections.emptyList());

    var result = userService.getUser(createdUser.getId());

    StepVerifier.create(result)
        .expectNext(expectedUser)
        .verifyComplete();
  }

  @Test
  void deleteUser_shouldDeleteUserSuccessfully() {
    var userToDelete = username().setEmail(generateUniqueEmail());
    var createdUser = userService.createUser(userToDelete).block();

    assert createdUser != null;
    StepVerifier.create(userService.deleteUser(createdUser.getId()))
        .verifyComplete();

    StepVerifier.create(dbUserRepository.findById(String.valueOf(createdUser.getId())))
        .expectNextCount(0)
        .verifyComplete();
  }

  Username username() {
    return new Username()
        .setName("name")
        .setSurname("surname")
        .setEmail(generateUniqueEmail());
  }

  private String generateUniqueEmail() {
    return UUID.randomUUID() + "@example.com";
  }
}