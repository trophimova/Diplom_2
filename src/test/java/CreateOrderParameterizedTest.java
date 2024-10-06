import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.praktikum.client.StellarBurgersClient;
import ru.yandex.praktikum.model.User;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.runners.Parameterized.*;

@RunWith(Parameterized.class)
public class CreateOrderParameterizedTest {

  private StellarBurgersClient client = new StellarBurgersClient();
  private String accessToken;
  private final List<String> ingredients;
  private final String expectedOrderName;

  public CreateOrderParameterizedTest(List<String> ingredients, String expectedOrderName) {
    this.ingredients = ingredients;
    this.expectedOrderName = expectedOrderName;
  }

  @Parameters
  public static Collection<Object[]> testData() {
    StellarBurgersClient client = new StellarBurgersClient();

    Response ingredientsResponse = client.getIngredients();
    List<String> ingredientIds = ingredientsResponse.jsonPath().getList("data._id");

    return Arrays.asList(new Object[][]{
        {ingredientIds.subList(0, 2), "Бессмертный флюоресцентный бургер"},
        {ingredientIds.subList(2, 4), "Био-марсианский метеоритный бургер"},
        {ingredientIds.subList(4, 15), "Экзо-плантаго альфа-сахаридный минеральный space традиционный-галактический фалленианский люминесцентный краторный антарианский spicy астероидный бургер"},
    });
  }

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
  public void shouldCreateOrderWithAuth() {
    Response createOrderResponse = client.createOrder(accessToken, ingredients);

    createOrderResponse.then()
        .assertThat()
        .statusCode(200)
        .and()
        .body("success", equalTo(true))
        .body("order.number", notNullValue())
        .body("name", equalTo(expectedOrderName));
  }

  @Test
  public void shouldCreateOrderWithoutAuth() {
    Response createOrderResponse = client.createOrderWithoutAuth(ingredients);

    createOrderResponse.then()
        .assertThat()
        .statusCode(200)
        .and()
        .body("success", equalTo(true))
        .body("order.number", notNullValue())
        .body("name", equalTo(expectedOrderName));
  }
}
