import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.client.StellarBurgersClient;
import ru.yandex.praktikum.model.User;

import static org.hamcrest.CoreMatchers.*;

public class CreateUserTest {

  private StellarBurgersClient client = new StellarBurgersClient();
  private User user;
  private String accessToken;

  @Before
  public void setUp() {
    user = User.createValidUser();
  }

  @After
  public void tearDown() {
    if (accessToken != null) {
      Response response = client.deleteUser(accessToken);

      response.then()
          .assertThat()
          .statusCode(202);
    }
  }

  @Test
  public void shouldCreateUniqueUser() {
    Response response = client.createUser(user);

    response.then()
        .assertThat()
        .statusCode(200)
        .and()
        .body("success", equalTo(true))
        .body("user.email", equalTo(user.getEmail()))
        .body("user.name", equalTo(user.getName()))
        .body("accessToken", notNullValue())
        .body("accessToken", startsWith("Bearer "))
        .body("refreshToken", notNullValue());

    accessToken = response.then().extract().path("accessToken");
  }

  @Test
  public void shouldNotCreateUserWithExistingEmail() {
    Response responseFirstUser = client.createUser(user);
    accessToken = responseFirstUser.then().extract().path("accessToken");

    Response responseSecondUser = client.createUser(user);

    responseSecondUser.then()
        .assertThat()
        .statusCode(403)
        .and()
        .body("success", equalTo(false))
        .body("message", equalTo("User already exists"));
  }

  @Test
  public void shouldReturnErrorIfNoEmail() {
    User noEmailUser = new User(null, user.getPassword(), user.getName());
    Response response = client.createUser(noEmailUser);

    response.then()
        .assertThat()
        .statusCode(403)
        .and()
        .body("success", equalTo(false))
        .body("message", equalTo("Email, password and name are required fields"));
  }

  @Test
  public void shouldReturnErrorIfNoPassword() {
    User noPasswordUser = new User(user.getEmail(), null, user.getName());
    Response response = client.createUser(noPasswordUser);

    response.then()
        .assertThat()
        .statusCode(403)
        .and()
        .body("success", equalTo(false))
        .body("message", equalTo("Email, password and name are required fields"));
  }

  @Test
  public void shouldReturnErrorIfNoName() {
    User noNameUser = new User(user.getEmail(), user.getPassword(), null);
    Response response = client.createUser(noNameUser);

    response.then()
        .assertThat()
        .statusCode(403)
        .and()
        .body("success", equalTo(false))
        .body("message", equalTo("Email, password and name are required fields"));
  }
}
