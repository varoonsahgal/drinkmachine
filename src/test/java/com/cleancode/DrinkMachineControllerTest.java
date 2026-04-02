package com.cleancode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DrinkMachineController.class)
class DrinkMachineControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private DrinkMachine drinkMachine;

  private Drink cappuccino;
  private Drink coffee;

  @BeforeEach
  void setUp() {
    Ingredient espresso = new Ingredient(IngredientName.ESPRESSO, 1.10);
    Ingredient steamedMilk = new Ingredient(IngredientName.STEAMED_MILK, 0.35);
    Ingredient foamedMilk = new Ingredient(IngredientName.FOAMED_MILK, 0.35);
    Ingredient coffeeIngredient = new Ingredient(IngredientName.COFFEE, 0.75);
    Ingredient sugar = new Ingredient(IngredientName.SUGAR, 0.25);
    Ingredient cream = new Ingredient(IngredientName.CREAM, 0.25);

    cappuccino = new Drink("Cappuccino", new Recipe(espresso, espresso, steamedMilk, foamedMilk));
    cappuccino.setCost(2.15);
    cappuccino.setMakeable(true);

    coffee = new Drink("Coffee", new Recipe(coffeeIngredient, coffeeIngredient, coffeeIngredient, sugar, cream));
    coffee.setCost(2.75);
    coffee.setMakeable(true);

    when(drinkMachine.getDrinkList()).thenReturn(List.of(cappuccino, coffee));
    when(drinkMachine.getIngredientList()).thenReturn(List.of(espresso, steamedMilk, foamedMilk, coffeeIngredient, sugar, cream));
  }

  @Test
  void getDrinksReturnsDrinkList() throws Exception {
    mockMvc.perform(get("/api/drinks"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(greaterThan(0))))
        .andExpect(jsonPath("$[0].name").isString())
        .andExpect(jsonPath("$[0].cost").isNumber())
        .andExpect(jsonPath("$[0].makeable").isBoolean());
  }

  @Test
  void getIngredientsReturnsIngredientList() throws Exception {
    mockMvc.perform(get("/api/ingredients"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(greaterThan(0))))
        .andExpect(jsonPath("$[0].name").isString())
        .andExpect(jsonPath("$[0].stock").isNumber());
  }

  @Test
  void makeDrinkReturnsDrinkNotFound() throws Exception {
    mockMvc.perform(post("/api/drinks/NonExistentDrink/make"))
        .andExpect(status().isNotFound());
  }

  @Test
  void makeDrinkDispensesDrink() throws Exception {
    mockMvc.perform(post("/api/drinks/Cappuccino/make"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", is("Dispensing: Cappuccino")))
        .andExpect(jsonPath("$.drinks", hasSize(greaterThan(0))))
        .andExpect(jsonPath("$.ingredients", hasSize(greaterThan(0))));
  }

  @Test
  void makeDrinkReturnsConflictWhenOutOfStock() throws Exception {
    coffee.setMakeable(false);
    mockMvc.perform(post("/api/drinks/Coffee/make"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message", is("Out of stock: Coffee")));
  }

  @Test
  void restockReturnsUpdatedState() throws Exception {
    mockMvc.perform(post("/api/restock"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", is("Ingredients restocked")))
        .andExpect(jsonPath("$.drinks", hasSize(greaterThan(0))))
        .andExpect(jsonPath("$.ingredients", hasSize(greaterThan(0))));
  }
}

