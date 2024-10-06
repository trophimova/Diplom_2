package ru.yandex.praktikum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class Order {
  private String _id;
  private List<String> ingredients;
  private String status;
  private int number;
  private String createdAt;
  private String updatedAt;
}
