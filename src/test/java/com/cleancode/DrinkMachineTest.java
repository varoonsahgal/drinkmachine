package com.cleancode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DrinkMachineTest {

  private DrinkMachine drinkMachine;

  @BeforeEach
  void setUp() {
    drinkMachine = new DrinkMachine();
    drinkMachine.restockIngredients();
  }

  @Test
  void getDrinkListReturnsSixDrinks() {
    assertThat(drinkMachine.getDrinkList()).hasSize(6);
  }

  @Test
  void getDrinkListIsUnmodifiable() {
    List<Drink> drinks = drinkMachine.getDrinkList();

    org.junit.jupiter.api.Assertions.assertThrows(
        UnsupportedOperationException.class,
        () -> drinks.add(null)
    );
  }

  @Test
  void getIngredientListReturnsNineIngredients() {
    assertThat(drinkMachine.getIngredientList()).hasSize(9);
  }

  @Test
  void getIngredientListIsUnmodifiable() {
    List<Ingredient> ingredients = drinkMachine.getIngredientList();

    org.junit.jupiter.api.Assertions.assertThrows(
        UnsupportedOperationException.class,
        () -> ingredients.add(null)
    );
  }

  @Test
  void allDrinksAreMakeableAfterRestock() {
    assertThat(drinkMachine.getDrinkList())
        .allMatch(Drink::getMakeable);
  }

  @Test
  void makeDrinkReducesIngredientStock() {
    Drink coffee = drinkMachine.getDrinkList().stream()
        .filter(d -> d.getName().equals("Coffee"))
        .findFirst()
        .orElseThrow();
    int stockBefore = drinkMachine.getIngredientList().stream()
        .filter(i -> i.getName() == IngredientName.COFFEE)
        .findFirst()
        .orElseThrow()
        .getStock();

    drinkMachine.makeDrink(coffee);

    int stockAfter = drinkMachine.getIngredientList().stream()
        .filter(i -> i.getName() == IngredientName.COFFEE)
        .findFirst()
        .orElseThrow()
        .getStock();
    // Coffee recipe uses 3 units of Coffee
    assertThat(stockAfter).isEqualTo(stockBefore - 3);
  }

  @Test
  void makeDrinkDoesNotReduceStockWhenNotMakeable() {
    Drink coffee = drinkMachine.getDrinkList().stream()
        .filter(d -> d.getName().equals("Coffee"))
        .findFirst()
        .orElseThrow();
    // Drain until out of stock
    while (coffee.getMakeable()) {
      drinkMachine.makeDrink(coffee);
    }

    int stockBefore = drinkMachine.getIngredientList().stream()
        .filter(i -> i.getName() == IngredientName.COFFEE)
        .findFirst()
        .orElseThrow()
        .getStock();

    drinkMachine.makeDrink(coffee);

    int stockAfter = drinkMachine.getIngredientList().stream()
        .filter(i -> i.getName() == IngredientName.COFFEE)
        .findFirst()
        .orElseThrow()
        .getStock();
    assertThat(stockAfter).isEqualTo(stockBefore);
  }

  @Test
  void restockIngredientsSetsAllStockToTen() {
    Drink coffee = drinkMachine.getDrinkList().stream()
        .filter(d -> d.getName().equals("Coffee"))
        .findFirst()
        .orElseThrow();
    drinkMachine.makeDrink(coffee);

    drinkMachine.restockIngredients();

    assertThat(drinkMachine.getIngredientList())
        .allMatch(i -> i.getStock() == 10);
  }

  @Test
  void restockIngredientsMakesDrinksAvailableAgain() {
    Drink coffee = drinkMachine.getDrinkList().stream()
        .filter(d -> d.getName().equals("Coffee"))
        .findFirst()
        .orElseThrow();
    while (coffee.getMakeable()) {
      drinkMachine.makeDrink(coffee);
    }
    assertThat(coffee.getMakeable()).isFalse();

    drinkMachine.restockIngredients();

    assertThat(coffee.getMakeable()).isTrue();
  }

  @Test
  void drinkListIsSortedAlphabetically() {
    List<Drink> drinks = drinkMachine.getDrinkList();

    for (int i = 0; i < drinks.size() - 1; i++) {
      assertThat(drinks.get(i).getName())
          .isLessThanOrEqualTo(drinks.get(i + 1).getName());
    }
  }

  @Test
  void ingredientListIsSortedAlphabetically() {
    List<Ingredient> ingredients = drinkMachine.getIngredientList();

    for (int i = 0; i < ingredients.size() - 1; i++) {
      assertThat(ingredients.get(i).compareTo(ingredients.get(i + 1)))
          .isLessThanOrEqualTo(0);
    }
  }
}
