package com.cleancode;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DrinkMachineController.class)
class DrinkMachineControllerUnitTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private DrinkMachine drinkMachine;

  @Test
  void getDrinksReturnsOkWithDrinkList() throws Exception {
    Drink cappuccino = mock(Drink.class);
    when(cappuccino.getName()).thenReturn("Cappuccino");
    when(cappuccino.getCost()).thenReturn(3.0);
    when(cappuccino.getMakeable()).thenReturn(true);
    when(drinkMachine.getDrinkList()).thenReturn(List.of(cappuccino));

    mockMvc.perform(get("/api/drinks"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name", is("Cappuccino")))
        .andExpect(jsonPath("$[0].cost", is(3.0)))
        .andExpect(jsonPath("$[0].makeable", is(true)));
  }

  @Test
  void getDrinksReturnsEmptyListWhenNoDrinks() throws Exception {
    when(drinkMachine.getDrinkList()).thenReturn(List.of());

    mockMvc.perform(get("/api/drinks"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void getIngredientsReturnsOkWithIngredientList() throws Exception {
    Ingredient coffee = mock(Ingredient.class);
    when(coffee.getName()).thenReturn(IngredientName.COFFEE);
    when(coffee.getStock()).thenReturn(10);
    when(drinkMachine.getIngredientList()).thenReturn(List.of(coffee));

    mockMvc.perform(get("/api/ingredients"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name", is("Coffee")))
        .andExpect(jsonPath("$[0].stock", is(10)));
  }

  @Test
  void getIngredientsReturnsEmptyListWhenNoIngredients() throws Exception {
    when(drinkMachine.getIngredientList()).thenReturn(List.of());

    mockMvc.perform(get("/api/ingredients"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void makeDrinkReturnsNotFoundForUnknownDrink() throws Exception {
    when(drinkMachine.getDrinkList()).thenReturn(List.of());

    mockMvc.perform(post("/api/drinks/Unknown/make"))
        .andExpect(status().isNotFound());
  }

  @Test
  void makeDrinkReturnsOkAndDispensesWhenMakeable() throws Exception {
    Drink cappuccino = mock(Drink.class);
    when(cappuccino.getName()).thenReturn("Cappuccino");
    when(cappuccino.getCost()).thenReturn(3.0);
    when(cappuccino.getMakeable()).thenReturn(true);
    when(drinkMachine.getDrinkList()).thenReturn(List.of(cappuccino));
    when(drinkMachine.getIngredientList()).thenReturn(List.of());

    mockMvc.perform(post("/api/drinks/Cappuccino/make"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", is("Dispensing: Cappuccino")))
        .andExpect(jsonPath("$.drinks", hasSize(1)))
        .andExpect(jsonPath("$.ingredients", hasSize(0)));

    verify(drinkMachine).makeDrink(cappuccino);
  }

  @Test
  void makeDrinkReturnsConflictWhenNotMakeable() throws Exception {
    Drink coffee = mock(Drink.class);
    when(coffee.getName()).thenReturn("Coffee");
    when(coffee.getCost()).thenReturn(2.25);
    when(coffee.getMakeable()).thenReturn(false);
    when(drinkMachine.getDrinkList()).thenReturn(List.of(coffee));
    when(drinkMachine.getIngredientList()).thenReturn(List.of());

    mockMvc.perform(post("/api/drinks/Coffee/make"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message", is("Out of stock: Coffee")));

    verify(drinkMachine).makeDrink(coffee);
  }

  @Test
  void makeDrinkLookupIsCaseInsensitive() throws Exception {
    Drink cappuccino = mock(Drink.class);
    when(cappuccino.getName()).thenReturn("Cappuccino");
    when(cappuccino.getCost()).thenReturn(3.0);
    when(cappuccino.getMakeable()).thenReturn(true);
    when(drinkMachine.getDrinkList()).thenReturn(List.of(cappuccino));
    when(drinkMachine.getIngredientList()).thenReturn(List.of());

    mockMvc.perform(post("/api/drinks/cappuccino/make"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", is("Dispensing: Cappuccino")));
  }

  @Test
  void restockReturnsOkWithUpdatedStateAndCallsRestockIngredients() throws Exception {
    Drink cappuccino = mock(Drink.class);
    when(cappuccino.getName()).thenReturn("Cappuccino");
    when(cappuccino.getCost()).thenReturn(3.0);
    when(cappuccino.getMakeable()).thenReturn(true);
    Ingredient coffee = mock(Ingredient.class);
    when(coffee.getName()).thenReturn(IngredientName.COFFEE);
    when(coffee.getStock()).thenReturn(10);
    when(drinkMachine.getDrinkList()).thenReturn(List.of(cappuccino));
    when(drinkMachine.getIngredientList()).thenReturn(List.of(coffee));

    mockMvc.perform(post("/api/restock"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", is("Ingredients restocked")))
        .andExpect(jsonPath("$.drinks", hasSize(1)))
        .andExpect(jsonPath("$.ingredients", hasSize(1)));

    verify(drinkMachine).restockIngredients();
  }
}
