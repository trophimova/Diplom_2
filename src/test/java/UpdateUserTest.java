import com.github.javafaker.Faker;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.client.StellarBurgersClient;
import ru.yandex.praktikum.model.User;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;

public class UpdateUserTest {

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
  public void shouldUpdateUserNameWithAuth() {
    String newName = faker.name().firstName();

    Map<String, String> fieldsToUpdate = new HashMap<>();
    fieldsToUpdate.put("name", newName);

    Response updateResponse = client.updateUser(accessToken, fieldsToUpdate);

    updateResponse.then()
        .assertThat()
        .statusCode(200)
        .and()
        .body("success", equalTo(true))
        .body("user.name", equalTo(newName))
        .body("user.email", equalTo(user.getEmail()));
  }

  @Test
  public void shouldUpdateUserEmailWithAuth() {
    String newEmail = faker.internet().emailAddress();

    Map<String, String> fieldsToUpdate = new HashMap<>();
    fieldsToUpdate.put("email", newEmail);

    Response updateResponse = client.updateUser(accessToken, fieldsToUpdate);

    updateResponse.then()
        .assertThat()
        .statusCode(200)
        .and()
        .body("success", equalTo(true))
        .body("user.email", equalTo(newEmail))
        .body("user.name", equalTo(user.getName()));
  }

  @Test
  public void shouldReturnErrorWhenUpdatingUserNameWithoutAuth() {
    String newName = faker.name().firstName();

    Map<String, String> fieldsToUpdate = new HashMap<>();
    fieldsToUpdate.put("name", newName);

    Response updateResponse = client.updateUserWithoutAuth(fieldsToUpdate);

    updateResponse.then()
        .assertThat()
        .statusCode(401)
        .and()
        .body("success", equalTo(false))
        .body("message", equalTo("You should be authorised"));
  }

  @Test
  public void shouldReturnErrorWhenUpdatingUserEmailWithoutAuth() {
    String newEmail = faker.internet().emailAddress();

    Map<String, String> fieldsToUpdate = new HashMap<>();
    fieldsToUpdate.put("email", newEmail);

    Response updateResponse = client.updateUserWithoutAuth(fieldsToUpdate);

    updateResponse.then()
        .assertThat()
        .statusCode(401)
        .and()
        .body("success", equalTo(false))
        .body("message", equalTo("You should be authorised"));
  }
}
