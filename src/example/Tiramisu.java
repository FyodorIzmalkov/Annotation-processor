package example;

@Factory(
        id="Calzeone",
        type = Meal.class
)
public class Tiramisu implements Meal {

    @Override
    public float getPrice() {
        return 20.0f;
    }
}
