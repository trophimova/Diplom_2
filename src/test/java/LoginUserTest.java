import com.github.javafaker.Faker;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.client.StellarBurgersClient;
import ru.yandex.praktikum.model.User;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class LoginUserTest {

  private StellarBurgersClient client = new StellarBurgersClient();
  private User user;
  private String accessToken;
  private Faker faker;

  @Before
  public void setUp() {
    faker = new Faker();
    user = User.createValidUser();

    Response registerResponse = client.createUser(user);
    accessToken = registerResponse.then().extract().path("accessToken");
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
  public void shouldLoginWithExistingUser() {
    Response response = client.loginUser(user);

    response.then()
        .assertThat()
        .statusCode(200)
        .and()
        .body("success", equalTo(true))
        .body("accessToken", notNullValue())
        .body("refreshToken", notNullValue())
        .body("user.email", equalTo(user.getEmail()))
        .body("user.name", equalTo(user.getName()));
  }

  @Test
  public void shouldNotLoginWithInvalidPassword() {
    User invalidPasswordUser = new User(user.getEmail(), faker.internet().password(), user.getName());

    Response response = client.loginUser(invalidPasswordUser);

    response.then()
        .assertThat()
        .statusCode(401)
        .and()
        .body("success", equalTo(false))
        .body("message", equalTo("email or password are incorrect"));
  }

  @Test
  public void shouldNotLoginWithInvalidEmail() {
    User invalidEmailUser = new User(faker.internet().emailAddress(), user.getPassword(), user.getName());

    Response response = client.loginUser(invalidEmailUser);

    response.then()
        .assertThat()
        .statusCode(401)
        .and()
        .body("success", equalTo(false))
        .body("message", equalTo("email or password are incorrect"));
  }
}
