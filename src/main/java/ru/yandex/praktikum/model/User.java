package ru.yandex.praktikum.model;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class User {

  private final String email;
  private final String password;
  private final String name;

  private static Faker faker = new Faker();

  public static User createValidUser() {
    String email = faker.internet().emailAddress();
    String password = faker.internet().password();
    String name = faker.name().firstName();
    return new User(email, password, name);
  }
}
