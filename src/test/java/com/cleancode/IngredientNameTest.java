package com.cleancode;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IngredientNameTest {

  @Test
  void fromDisplayNameReturnsCorrectEnum() {
    assertThat(IngredientName.fromDisplayName("Coffee")).isEqualTo(IngredientName.COFFEE);
    assertThat(IngredientName.fromDisplayName("Sugar")).isEqualTo(IngredientName.SUGAR);
    assertThat(IngredientName.fromDisplayName("Cream")).isEqualTo(IngredientName.CREAM);
    assertThat(IngredientName.fromDisplayName("Decaf Coffee")).isEqualTo(IngredientName.DECAF_COFFEE);
    assertThat(IngredientName.fromDisplayName("Steamed Milk")).isEqualTo(IngredientName.STEAMED_MILK);
    assertThat(IngredientName.fromDisplayName("Foamed Milk")).isEqualTo(IngredientName.FOAMED_MILK);
    assertThat(IngredientName.fromDisplayName("Espresso")).isEqualTo(IngredientName.ESPRESSO);
    assertThat(IngredientName.fromDisplayName("Cocoa")).isEqualTo(IngredientName.COCOA);
    assertThat(IngredientName.fromDisplayName("Whipped Cream")).isEqualTo(IngredientName.WHIPPED_CREAM);
  }

  @Test
  void fromDisplayNameReturnsNullForUnknownName() {
    assertThat(IngredientName.fromDisplayName("Unknown")).isNull();
  }
}
