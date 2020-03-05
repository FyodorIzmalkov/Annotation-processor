package example;

public class MealFactory {
    public Meal create(String mealName){

        if (mealName == null){
            throw new IllegalArgumentException("Name is null!");
        }

        if ("Margharita".equals(mealName)){
            return new MargharitaPizza();
        }

        if ("Calzeone".equals(mealName)){
            return new CalzzeonePizza();
        }

        if ("Tiramisu".equals(mealName)){
            return new Tiramisu();
        }

        throw new IllegalArgumentException("UNKNOWN MEAL + " + mealName);
    }
}
