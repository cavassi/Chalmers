package chalmers.tda367.testing.coffe;

import java.util.HashMap;

/**
 * @brief A recipe which contains the ingredients and the amount of each ingredient.
 * 
 * This class contains both correct and incorrect methods.
 */
public class Recipe {
    private HashMap<Ingredient, Integer> ingredients;

    private String name;

    /**
     * @brief Creates a new recipe.
     * 
     * @param name The name of the recipe.
     */
    public Recipe(String name) {
        this.name = name;
    }

    /**
     * @brief Gets the name of the recipe.
     * 
     * @return The name of the recipe.
     */
    public String name() {
        return this.name;
    }

    /**
     * @brief Gets the amount of a specific ingredient.
     * 
     * @param ingredient The ingredient to get the amount of, 0 if the ingredient is not in the recipe.
     * @return The amount of the ingredient.
     */
    public int getAmount(Ingredient ingredient) {
        return this.ingredients.get(ingredient);
    }

    /**
     * @brief Sets the amount of a specific ingredient.
     * 
     * @param ingredient The ingredient to set the amount of.
     * @param amount The amount of the ingredient.
     */
    public void setAmount(Ingredient ingredient, int amount) {
        this.ingredients.put(ingredient, amount);
    }

    /**
     * @brief Adds to a ingredient to the recipe.
     * 
     * @param ingredient The ingredient to add to.
     * @param amount The amount of the ingredient.
     */
    public void addIngredient(Ingredient ingredient, int amount) {
        if (this.ingredients.containsKey(ingredient)) {
            this.ingredients.put(ingredient, this.ingredients.get(ingredient) + amount);
        }
        
        this.ingredients.put(ingredient, amount);
    }

    /**
     * @brief Removes an ingredient from the recipe.
     * 
     * @param ingredient The ingredient to remove.
     */
    public void removeIngredient(Ingredient ingredient) {
        this.ingredients.remove(ingredient);
    }

    /**
     * @brief Gets the ingredients in the recipe.
     * 
     * @return The ingredients in the recipe.
     */
    public Ingredient[] ingredients() {
        Ingredient[] ingredients = new Ingredient[this.ingredients.size()];

        int i = 0;
        
        for (Ingredient ingredient : ingredients) {
            ingredients[i] = ingredient;
            i++;
        }

        return ingredients;
    }
}
