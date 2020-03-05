package example;

import javax.swing.*;

public class PizzaStore {
    MealFactory mealFactory = new MealFactory();

    public Meal order(String mealName){
        return mealFactory.create(mealName);
    }

    public static void main(String[] args) {
        PizzaStore pizzaStore = new PizzaStore();
        Meal meal = pizzaStore.order("Margharita");
        System.out.println("Bill: $" + meal.getPrice());
    }
}
