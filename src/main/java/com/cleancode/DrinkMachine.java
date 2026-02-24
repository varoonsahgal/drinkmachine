package com.cleancode;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.cleancode.IngredientName.COCOA;
import static com.cleancode.IngredientName.COFFEE;
import static com.cleancode.IngredientName.CREAM;
import static com.cleancode.IngredientName.DECAF_COFFEE;
import static com.cleancode.IngredientName.ESPRESSO;
import static com.cleancode.IngredientName.FOAMED_MILK;
import static com.cleancode.IngredientName.STEAMED_MILK;
import static com.cleancode.IngredientName.SUGAR;
import static com.cleancode.IngredientName.WHIPPED_CREAM;

@Component
public class DrinkMachine {

  private final List<Drink> drinkList = new ArrayList<>();
  private final List<Ingredient> ingredientList = new ArrayList<>();

  public DrinkMachine() {
    ingredientList.add(new Ingredient(COFFEE, 0.75));
    ingredientList.add(new Ingredient(DECAF_COFFEE, 0.75));
    ingredientList.add(new Ingredient(SUGAR, 0.25));
    ingredientList.add(new Ingredient(CREAM, 0.25));
    ingredientList.add(new Ingredient(STEAMED_MILK, 0.35));
    ingredientList.add(new Ingredient(FOAMED_MILK, 0.35));
    ingredientList.add(new Ingredient(ESPRESSO, 1.10));
    ingredientList.add(new Ingredient(COCOA, 0.90));
    ingredientList.add(new Ingredient(WHIPPED_CREAM, 1.00));

    Collections.sort(ingredientList);

    RecipeFactory recipeFactory = new RecipeFactory(ingredientList);
    drinkList.add(new Drink("Coffee", recipeFactory.create("Coffee", "Coffee", "Coffee", "Sugar", "Cream")));
    drinkList.add(new Drink("Decaf Coffee", recipeFactory.create("Decaf Coffee", "Decaf Coffee", "Decaf Coffee", "Sugar", "Cream")));
    drinkList.add(new Drink("Caffe Latte", recipeFactory.create("Espresso", "Espresso", "Steamed Milk")));
    drinkList.add(new Drink("Caffe Americano", recipeFactory.create("Espresso", "Espresso", "Espresso")));
    drinkList.add(new Drink("Caffe Mocha", recipeFactory.create("Espresso", "Cocoa", "Steamed Milk", "Whipped Cream")));
    drinkList.add(new Drink("Cappuccino", recipeFactory.create("Espresso", "Espresso", "Steamed Milk", "Foamed Milk")));

    Collections.sort(drinkList);

    for (Drink drink : drinkList) {
      double cost = 0;
      Recipe recipe = drink.getRecipe();
      for (Ingredient ingredient : ingredientList) {
        if (recipe.hasIngredient(ingredient)) {
          cost += ingredient.getCost() * recipe.quantityNeededFor(ingredient);
        }
      }
      drink.setCost(cost);
    }
    updateMakeable();
  }

  public List<Drink> getDrinkList() {
    return Collections.unmodifiableList(drinkList);
  }

  public List<Ingredient> getIngredientList() {
    return Collections.unmodifiableList(ingredientList);
  }

  public void makeDrink(Drink drink) {
    if (drink.getMakeable()) {
      for (Ingredient ingredient : ingredientList) {
        Recipe recipe = drink.getRecipe();
        if (recipe.hasIngredient(ingredient)) {
          ingredient.setStock(ingredient.getStock() - recipe.quantityNeededFor(ingredient));
        }
      }
    }
    updateMakeable();
  }

  public void restockIngredients() {
    for (Ingredient ingredient : ingredientList) {
      ingredient.setStock(10);
    }
    updateMakeable();
  }

  private void updateMakeable() {
    for (Drink drink : drinkList) {
      drink.updateDrinkState();
    }
  }

}