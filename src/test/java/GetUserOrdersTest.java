import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.client.StellarBurgersClient;
import ru.yandex.praktikum.model.AllOrders;
import ru.yandex.praktikum.model.Order;
import ru.yandex.praktikum.model.User;

import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GetUserOrdersTest {

  private StellarBurgersClient client = new StellarBurgersClient();
  private String accessToken;
  private List<String> expectedIngredientsInOrder1;
  private List<String> expectedIngredientsInOrder2;
  private List<String> expectedIngredientsInOrder3;

  @Before
  public void setUp() {
    User user = User.createValidUser();
    Response createUserResponse = client.createUser(user);
    accessToken = createUserResponse.then().extract().path("accessToken");

    expectedIngredientsInOrder1 = getIngredients();
    expectedIngredientsInOrder2 = getIngredients();
    expectedIngredientsInOrder3 = getIngredients();
    client.createOrder(accessToken, expectedIngredientsInOrder1);
    client.createOrder(accessToken, expectedIngredientsInOrder2);
    client.createOrder(accessToken, expectedIngredientsInOrder3);
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

  @Step("Получить список ингридиентов")
  private List<String> getIngredients() {
    Response ingredientsResponse = client.getIngredients();
    List<String> allIngredients = ingredientsResponse.jsonPath().getList("data._id");

    Collections.shuffle(allIngredients);

    Faker faker = new Faker();
    int randomCount = faker.number().numberBetween(1, allIngredients.size());

    return allIngredients.subList(0, randomCount);
  }

  @Test
  public void shouldGetOrdersForAuthorizedUser() {
    Response ordersResponse = client.getUserOrders(accessToken);
    AllOrders allOrders = ordersResponse.as(AllOrders.class);

    assertTrue("Ответ должен быть успешным", allOrders.isSuccess());
    assertNotNull("Список заказов не должен быть пустым", allOrders.getOrders());

    // Проверка общего количества заказов
    assertNotNull("Поле total не должно быть пустым", allOrders.getTotal());
    assertNotNull("Поле totalToday не должно быть пустым", allOrders.getTotalToday());

    Set<String> uniqueIds = new HashSet<>();
    Set<Integer> uniqueNumbers = new HashSet<>();

    // Проверяем каждый заказ по отдельности
    for (int i = 0; i < allOrders.getOrders().size(); i++) {
      Order order = allOrders.getOrders().get(i);

      // Проверка полей заказа
      assertNotNull("ID заказа не должен быть пустым", order.get_id());
      assertNotNull("Список ингредиентов не должен быть пустым", order.getIngredients());
      assertNotNull("Статус заказа не должен быть пустым", order.getStatus());
      assertNotNull("Номер заказа не должен быть пустым", order.getNumber());
      assertNotNull("Поле createdAt не должно быть пустым", order.getCreatedAt());
      assertNotNull("Поле updatedAt не должно быть пустым", order.getUpdatedAt());

      // Проверка уникальности id и номера заказов
      assertTrue("ID заказа должен быть уникальным", uniqueIds.add(order.get_id()));
      assertTrue("Номер заказа должен быть уникальным", uniqueNumbers.add(order.getNumber()));

      // Проверка ингредиентов в зависимости от номера заказа
      List<String> actualIngredients = order.getIngredients();
      if (i == 0) {
        assertThat("Ингредиенты первого заказа должны совпадать", actualIngredients, equalTo(expectedIngredientsInOrder1));
      } else if (i == 1) {
        assertThat("Ингредиенты второго заказа должны совпадать", actualIngredients, equalTo(expectedIngredientsInOrder2));
      } else if (i == 2) {
        assertThat("Ингредиенты третьего заказа должны совпадать", actualIngredients, equalTo(expectedIngredientsInOrder3));
      }
    }
  }

  @Test
  public void shouldNotGetOrdersForUnauthorizedUser() {
    Response ordersResponse = client.getUserOrdersWithoutAuth();

    ordersResponse.then()
        .assertThat()
        .statusCode(401)
        .and()
        .body("success", equalTo(false))
        .body("message", equalTo("You should be authorised"));
  }
}
