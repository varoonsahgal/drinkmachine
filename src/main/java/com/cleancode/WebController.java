package com.cleancode;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

  private final DrinkMachine drinkMachine;

  public WebController(DrinkMachine drinkMachine) {
    this.drinkMachine = drinkMachine;
  }

  @GetMapping("/")
  public String index(Model model) {
    model.addAttribute("drinks", drinkMachine.getDrinkList());
    model.addAttribute("ingredients", drinkMachine.getIngredientList());
    return "index";
  }
}
