package ru.yandex.praktikum.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.praktikum.model.User;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class StellarBurgersClient {

  private static final String BASE_URL = "https://stellarburgers.nomoreparties.site/";

  @Step("Регистрация пользователя {user.email}")
  public Response createUser(User user) {
    return given().baseUri(BASE_URL)
        .header("Content-Type", "application/json")
        .body(user)
        .post("/api/auth/register");
  }

  @Step("Удаление пользователя с токеном {accessToken}")
  public Response deleteUser(String accessToken) {
    return given().baseUri(BASE_URL)
        .header("Authorization", accessToken)
        .delete("/api/auth/user");
  }

  @Step("Авторизация пользователя {user.email}")
  public Response loginUser(User user) {
    return given().baseUri(BASE_URL)
        .header("Content-Type", "application/json")
        .body(user)
        .post("/api/auth/login");
  }

  @Step("Обновление данных пользователя с токеном {accessToken}")
  public Response updateUser(String accessToken, Map<String, String> fieldsToUpdate) {
    return given()
        .baseUri(BASE_URL)
        .header("Authorization", accessToken)
        .header("Content-Type", "application/json")
        .body(fieldsToUpdate)
        .patch("/api/auth/user");
  }

  @Step("Обновление данных пользователя без авторизации")
  public Response updateUserWithoutAuth(Map<String, String> fieldsToUpdate) {
    return given()
        .baseUri(BASE_URL)
        .header("Content-Type", "application/json")
        .body(fieldsToUpdate)
        .patch("/api/auth/user");
  }

  @Step("Создание заказа с токеном {accessToken}")
  public Response createOrder(String accessToken, List<String> ingredients) {
    return given()
        .baseUri(BASE_URL)
        .header("Authorization", accessToken)
        .header("Content-Type", "application/json")
        .body(Map.of("ingredients", ingredients))
        .post("/api/orders");
  }

  @Step("Создание заказа без авторизации")
  public Response createOrderWithoutAuth(List<String> ingredients) {
    return given()
        .baseUri(BASE_URL)
        .header("Content-Type", "application/json")
        .body(Map.of("ingredients", ingredients))
        .post("/api/orders");
  }

  @Step("Получение ингредиентов")
  public Response getIngredients() {
    return given().baseUri(BASE_URL)
        .header("Content-Type", "application/json")
        .get("/api/ingredients");
  }

  @Step("Получение заказов пользователя с токеном {accessToken}")
  public Response getUserOrders(String accessToken) {
    return given()
        .baseUri(BASE_URL)
        .header("Authorization", accessToken)
        .header("Content-Type", "application/json")
        .get("/api/orders");
  }

  @Step("Получение заказов пользователя без авторизации")
  public Response getUserOrdersWithoutAuth() {
    return given()
        .baseUri(BASE_URL)
        .header("Content-Type", "application/json")
        .get("/api/orders");
  }
}
