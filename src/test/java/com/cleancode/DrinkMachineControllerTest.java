package com.cleancode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DrinkMachineControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  void restock() throws Exception {
    mockMvc.perform(post("/api/restock"));
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
    // Coffee recipe uses 3 units of coffee; with stock of 10, it runs out after 3 orders
    for (int i = 0; i < 3; i++) {
      mockMvc.perform(post("/api/drinks/Coffee/make")).andExpect(status().isOk());
    }
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

