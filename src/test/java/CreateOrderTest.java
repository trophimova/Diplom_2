import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.client.StellarBurgersClient;
import ru.yandex.praktikum.model.User;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateOrderTest {

  private StellarBurgersClient client = new StellarBurgersClient();
  private String accessToken;

  @Before
  public void setUp() {
    User user = User.createValidUser();
    Response createUserResponse = client.createUser(user);
    accessToken = createUserResponse.then().extract().path("accessToken");
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
  public void shouldReturnErrorWhenNoIngredientsProvidedWithoutAuth() {
    Response createOrderResponse = client.createOrderWithoutAuth(Arrays.asList());

    createOrderResponse.then()
        .assertThat()
        .statusCode(400)
        .and()
        .body("success", equalTo(false))
        .body("message", equalTo("Ingredient ids must be provided"));
  }

  @Test
  public void shouldReturnErrorWhenNoIngredientsProvidedWithAuth() {
    Response createOrderResponse = client.createOrder(accessToken, Arrays.asList());

    createOrderResponse.then()
        .assertThat()
        .statusCode(400)
        .and()
        .body("success", equalTo(false))
        .body("message", equalTo("Ingredient ids must be provided"));
  }

  @Test
  public void shouldReturnErrorWhenInvalidIngredientHashProvidedWithAuth() {
    List<String> invalidIngredients = Arrays.asList("invalid1234567890abcdef12", "abcdef1234567890invalid34");
    Response createOrderResponse = client.createOrder(accessToken, invalidIngredients);

    createOrderResponse.then()
        .assertThat()
        .statusCode(500)
        .and()
        .body(containsString("Internal Server Error"));
  }

  @Test
  public void shouldReturnErrorWhenInvalidIngredientHashProvidedWithoutAuth() {
    List<String> invalidIngredients = Arrays.asList("invalid1234567890abcdef12", "abcdef1234567890invalid34");
    Response createOrderResponse = client.createOrderWithoutAuth(invalidIngredients);

    createOrderResponse.then()
        .assertThat()
        .statusCode(500)
        .and()
        .body(containsString("Internal Server Error"));
  }
}
