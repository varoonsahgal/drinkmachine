package com.cleancode;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.cleancode.IngredientName.COFFEE;
import static com.cleancode.IngredientName.CREAM;
import static com.cleancode.IngredientName.SUGAR;
import static org.assertj.core.api.Assertions.assertThat;

class RecipeTest {

  @Test
  void hasIngredientReturnsTrueForIngredientInRecipe() {
    Ingredient coffee = new Ingredient(COFFEE, 0.75);
    Recipe recipe = new Recipe(coffee);

    assertThat(recipe.hasIngredient(coffee)).isTrue();
  }

  @Test
  void hasIngredientReturnsFalseForIngredientNotInRecipe() {
    Ingredient coffee = new Ingredient(COFFEE, 0.75);
    Ingredient sugar = new Ingredient(SUGAR, 0.25);
    Recipe recipe = new Recipe(coffee);

    assertThat(recipe.hasIngredient(sugar)).isFalse();
  }

  @Test
  void quantityNeededForReturnsSingleCountWhenIngredientAppearsOnce() {
    Ingredient coffee = new Ingredient(COFFEE, 0.75);
    Recipe recipe = new Recipe(coffee);

    assertThat(recipe.quantityNeededFor(coffee)).isEqualTo(1);
  }

  @Test
  void quantityNeededForReturnsCorrectCountWhenIngredientAppearsMultipleTimes() {
    Ingredient coffee = new Ingredient(COFFEE, 0.75);
    Recipe recipe = new Recipe(coffee, coffee, coffee);

    assertThat(recipe.quantityNeededFor(coffee)).isEqualTo(3);
  }

  @Test
  void ingredientsReturnsAllUniqueIngredientsInRecipe() {
    Ingredient coffee = new Ingredient(COFFEE, 0.75);
    Ingredient sugar = new Ingredient(SUGAR, 0.25);
    Ingredient cream = new Ingredient(CREAM, 0.25);
    Recipe recipe = new Recipe(coffee, coffee, sugar, cream);

    assertThat(recipe.ingredients()).hasSize(3);
  }

  @Test
  void recipeCreatedViaFactoryHasCorrectQuantities() {
    Ingredient coffee = new Ingredient(COFFEE, 0.75);
    Ingredient sugar = new Ingredient(SUGAR, 0.25);
    List<Ingredient> ingredientList = List.of(coffee, sugar);
    Recipe recipe = new RecipeFactory(ingredientList).create("Coffee", "Coffee", "Sugar");

    assertThat(recipe.hasIngredient(coffee)).isTrue();
    assertThat(recipe.quantityNeededFor(coffee)).isEqualTo(2);
    assertThat(recipe.hasIngredient(sugar)).isTrue();
    assertThat(recipe.quantityNeededFor(sugar)).isEqualTo(1);
  }
}
