package api.client;

import api.model.Ingredients;
import io.qameta.allure.Step;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class GetIngredientsProc {
    // Получение списка ингредиентов
    @Step("Получение списка ингредиентов")
    public static Ingredients getIngredients(RequestSpecification requestSpec) {
        return
                given()
                        .spec(requestSpec)
                        .and()
                        .when()
                        .get("ingredients")
                        .body()
                        .as(Ingredients.class);
    }
}
