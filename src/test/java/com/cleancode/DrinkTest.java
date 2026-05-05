package com.cleancode;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.cleancode.IngredientName.COFFEE;
import static com.cleancode.IngredientName.SUGAR;
import static org.assertj.core.api.Assertions.assertThat;

class DrinkTest {

  private Drink buildDrink(String name) {
    Ingredient coffee = new Ingredient(COFFEE, 0.75);
    Ingredient sugar = new Ingredient(SUGAR, 0.25);
    Recipe recipe = new RecipeFactory(List.of(coffee, sugar)).create("Coffee", "Sugar");
    return new Drink(name, recipe);
  }

  @Test
  void getNameReturnsConstructorName() {
    Drink drink = buildDrink("Coffee");

    assertThat(drink.getName()).isEqualTo("Coffee");
  }

  @Test
  void setNameUpdatesDrinkName() {
    Drink drink = buildDrink("Coffee");

    drink.setName("Super Coffee");

    assertThat(drink.getName()).isEqualTo("Super Coffee");
  }

  @Test
  void getCostReturnsZeroByDefault() {
    Drink drink = buildDrink("Coffee");

    assertThat(drink.getCost()).isEqualTo(0.0);
  }

  @Test
  void setCostUpdatesDrinkCost() {
    Drink drink = buildDrink("Coffee");

    drink.setCost(1.25);

    assertThat(drink.getCost()).isEqualTo(1.25);
  }

  @Test
  void getMakeableReturnsFalseByDefault() {
    Drink drink = buildDrink("Coffee");

    assertThat(drink.getMakeable()).isFalse();
  }

  @Test
  void setMakeableUpdatesMakeableState() {
    Drink drink = buildDrink("Coffee");

    drink.setMakeable(true);

    assertThat(drink.getMakeable()).isTrue();
  }

  @Test
  void compareToReturnsNegativeWhenNameComesFirst() {
    Drink cappuccino = buildDrink("Cappuccino");
    Drink latte = buildDrink("Latte");

    assertThat(cappuccino.compareTo(latte)).isNegative();
  }

  @Test
  void compareToReturnsPositiveWhenNameComesLast() {
    Drink cappuccino = buildDrink("Cappuccino");
    Drink latte = buildDrink("Latte");

    assertThat(latte.compareTo(cappuccino)).isPositive();
  }

  @Test
  void compareToReturnsZeroForSameName() {
    Drink drink1 = buildDrink("Coffee");
    Drink drink2 = buildDrink("Coffee");

    assertThat(drink1.compareTo(drink2)).isZero();
  }

  @Test
  void getRecipeReturnsRecipeProvidedAtConstruction() {
    Ingredient coffee = new Ingredient(COFFEE, 0.75);
    Recipe recipe = new RecipeFactory(List.of(coffee)).create("Coffee");
    Drink drink = new Drink("Coffee", recipe);

    assertThat(drink.getRecipe()).isSameAs(recipe);
  }
}
