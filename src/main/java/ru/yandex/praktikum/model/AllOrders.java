package ru.yandex.praktikum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AllOrders {
  private boolean success;
  private List<Order> orders;
  private int total;
  private int totalToday;
}
