package chalmers.tda367.testing.coffe;

import java.util.HashMap;

/**
 * @brief A shop where ingredients and recipes can be bought.
 */
public class Shop {

    private HashMap<Ingredient, Integer> ingredientPrices;

    /**
     * @brief Creates a new shop.
     */
    public Shop() {
        this.ingredientPrices = new HashMap<Ingredient, Integer>();
    }

    /**
     * @brief Gets the price of a specific ingredient.
     * 
     * @param ingredient The ingredient to get the price of, 0 if the ingredient is not in the shop.
     * @return The price of the ingredient.
     */
    public int getPrice(Ingredient ingredient) {
        if (this.ingredientPrices.containsKey(ingredient)) {
            return this.ingredientPrices.get(ingredient);
        } else {
            return 0;
        }
    }

    /**
     * @brief Sets the price of a specific ingredient.
     * 
     * @param ingredient The ingredient to set the price of.
     * @param price The price of the ingredient.
     */
    public void setPrice(Ingredient ingredient, int price) {
        this.ingredientPrices.put(ingredient, price);
    }

    /**
     * @brief Buys an ingredient.
     * 
     * @param ingredient The ingredient to buy.
     * @param amount The amount of the ingredient to buy.
     * @param inventory The inventory to buy the ingredient to.
     * @return True if the ingredient was bought, false otherwise.
     */
    public boolean buyIngredient(Ingredient ingredient, int amount, Inventory inventory) {
        int price = this.getPrice(ingredient);
        int totalCost = price * amount;

        if (inventory.getMoney() >= totalCost) {
            inventory.spendMoney(totalCost);
            inventory.addIngredient(ingredient, amount);
        }

        return inventory.getMoney() >= totalCost;
    }

    /**
     * @brief Get the price of a recipe.
     * 
     * @param recipe The recipe to get the price of.
     * @return The price of the recipe.
     */
    public int getPrice(Recipe recipe) {
        int price = 0;

        for (Ingredient ingredient : recipe.ingredients()) {
            price += this.getPrice(ingredient) * recipe.getAmount(ingredient);
        }

        return price;
    }


}
