package api.client;

import api.model.OrdersList;
import io.qameta.allure.Step;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;

import static io.restassured.RestAssured.given;

public class GetOrdersListProc {
    @Step("Получения списка заказов пользователя")
    public static OrdersList getOrderListResponse(RequestSpecification requestSpec, String token) {
        return given()
                .spec(requestSpec)
                .and()
                .auth().oauth2(token)
                .when()
                .get("orders")
                .body()
                .as(OrdersList.class);
    }

    @Step("Получения списка заказов пользователя - проверка статуса и ответа")
    public static void getOrderList(RequestSpecification requestSpec, String token,
                                    int status, String message, boolean success) {
        given()
                .spec(requestSpec)
                .and()
                .auth().oauth2(token)
                .when()
                .get("orders")
                .then()
                .statusCode(status)
                .body("message",
                        Matchers.equalTo(message))
                .and()
                .body("success",
                        Matchers.equalTo(success));
    }
}
