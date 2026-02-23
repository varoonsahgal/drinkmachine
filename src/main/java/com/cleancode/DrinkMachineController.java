package com.cleancode;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class DrinkMachineController {

  private final DrinkMachine drinkMachine;

  public DrinkMachineController(DrinkMachine drinkMachine) {
    this.drinkMachine = drinkMachine;
  }

  @GetMapping("/drinks")
  public List<Map<String, Object>> getDrinks() {
    return drinkMachine.getDrinkList().stream()
        .map(this::drinkToMap)
        .collect(Collectors.toList());
  }

  @GetMapping("/ingredients")
  public List<Map<String, Object>> getIngredients() {
    return drinkMachine.getIngredientList().stream()
        .map(this::ingredientToMap)
        .collect(Collectors.toList());
  }

  @PostMapping("/drinks/{name}/make")
  public ResponseEntity<Map<String, Object>> makeDrink(@PathVariable String name) {
    Drink drink = drinkMachine.getDrinkList().stream()
        .filter(d -> d.getName().equalsIgnoreCase(name))
        .findFirst()
        .orElse(null);

    if (drink == null) {
      return ResponseEntity.notFound().build();
    }

    boolean wasMakeable = drink.getMakeable();
    drinkMachine.makeDrink(drink);

    Map<String, Object> response = new LinkedHashMap<>();
    response.put("message", wasMakeable ? "Dispensing: " + drink.getName() : "Out of stock: " + drink.getName());
    response.put("drinks", getDrinks());
    response.put("ingredients", getIngredients());
    return wasMakeable ? ResponseEntity.ok(response) : ResponseEntity.status(409).body(response);
  }

  @PostMapping("/restock")
  public Map<String, Object> restock() {
    drinkMachine.restockIngredients();
    Map<String, Object> response = new LinkedHashMap<>();
    response.put("message", "Ingredients restocked");
    response.put("drinks", getDrinks());
    response.put("ingredients", getIngredients());
    return response;
  }

  private Map<String, Object> drinkToMap(Drink drink) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("name", drink.getName());
    map.put("cost", drink.getCost());
    map.put("makeable", drink.getMakeable());
    return map;
  }

  private Map<String, Object> ingredientToMap(Ingredient ingredient) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("name", ingredient.getName().displayName());
    map.put("stock", ingredient.getStock());
    return map;
  }
}
