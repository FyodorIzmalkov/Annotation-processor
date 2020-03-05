package example;

@Factory(
        id="Margharita",
        type = Meal.class
)
public class MargharitaPizza implements Meal {
    @Override
    public float getPrice() {
        return 6.0f;
    }
}
