package chalmers.tda367.testing.coffe;

import java.util.HashMap;

/**
 * @brief Inventory of ingredients.
 */
public class Inventory {
    private HashMap<Ingredient, Integer> ingredients;

    private int money;

    /**
     * @brief Creates a new inventory.
     */
    public Inventory() {
        this.ingredients = new HashMap<Ingredient, Integer>();
        this.money = 0;
    }

    /**
     * @brief Gets the amount of a specific ingredient.
     * 
     * @param ingredient The ingredient to get the amount of, 0 if the ingredient is not in the inventory.
     * @return The amount of the ingredient.
     */
    public int getAmount(Ingredient ingredient) {
        if (this.ingredients.containsKey(ingredient)) {
            return this.ingredients.get(ingredient);
        } else {
            return 0;
        }
    }

    /**
     * @brief Gets the amount of money in the inventory.
     * 
     * @return The amount of money.
     */
    public int getMoney() {
        return this.money;
    }

    /**
     * @brief Earns money.
     * 
     * @param amount The amount of money to earn.
     */
    public void earnMoney(int amount) {
        this.money += amount;
    }

    /**
     * @brief Spends money.
     * 
     * @param amount The amount of money to spend.
     */
    public void spendMoney(int amount) {
        this.money -= amount;
    }

    /**
     * @brief Adds an ingredient to the inventory.
     * 
     * @param ingredient The ingredient to add.
     * @param amount The amount of the ingredient to add.
     */
    public void addIngredient(Ingredient ingredient, int amount) {
        if (this.ingredients.containsKey(ingredient)) {
            this.ingredients.put(ingredient, this.ingredients.get(ingredient) + amount);
        } else {
            this.ingredients.put(ingredient, amount);
        }
    }
    
}
