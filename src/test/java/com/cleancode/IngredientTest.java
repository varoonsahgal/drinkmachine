package com.cleancode;

import org.junit.jupiter.api.Test;

import static com.cleancode.IngredientName.COFFEE;
import static com.cleancode.IngredientName.SUGAR;
import static org.assertj.core.api.Assertions.assertThat;

class IngredientTest {

  @Test
  void constructorSetsNameCostAndDefaultStock() {
    Ingredient ingredient = new Ingredient(COFFEE, 0.75);

    assertThat(ingredient.getName()).isEqualTo(COFFEE);
    assertThat(ingredient.getCost()).isEqualTo(0.75);
    assertThat(ingredient.getStock()).isEqualTo(10);
  }

  @Test
  void setStockUpdatesStock() {
    Ingredient ingredient = new Ingredient(COFFEE, 0.75);

    ingredient.setStock(3);

    assertThat(ingredient.getStock()).isEqualTo(3);
  }

  @Test
  void compareToReturnsNegativeWhenNameComesFirst() {
    Ingredient coffee = new Ingredient(COFFEE, 0.75);
    Ingredient sugar = new Ingredient(SUGAR, 0.25);

    assertThat(coffee.compareTo(sugar)).isNegative();
  }

  @Test
  void compareToReturnsPositiveWhenNameComesLast() {
    Ingredient coffee = new Ingredient(COFFEE, 0.75);
    Ingredient sugar = new Ingredient(SUGAR, 0.25);

    assertThat(sugar.compareTo(coffee)).isPositive();
  }

  @Test
  void compareToReturnsZeroForSameName() {
    Ingredient coffee1 = new Ingredient(COFFEE, 0.75);
    Ingredient coffee2 = new Ingredient(COFFEE, 1.00);

    assertThat(coffee1.compareTo(coffee2)).isZero();
  }
}
