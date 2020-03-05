package example;

@Factory(
        id="Tiramisu",
        type = Meal.class
)
public class CalzzeonePizza implements Meal{

    @Override
    public float getPrice() {
        return 15.0f;
    }
}
