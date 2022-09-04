package api.client;

import api.model.OrderResponse;
import io.qameta.allure.Step;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateOrderProc {
    // Запрос на создание заказа пользователем
    @Step("Создание заказа пользователем - проверка статуса и ответа")
    public static void
    createOrder(RequestSpecification requestSpec, String token, String json,
                int status, String message, boolean success) {
        given()
                .spec(requestSpec)
                .and()
                .body(json)
                .auth().oauth2(token)
                .when()
                .post("orders")
                .then()
                .statusCode(status)
                .body("message",
                        equalTo(message))
                .and()
                .body("success",
                        equalTo(success));
    }

    @Step("Создание заказа пользователем")
    public static OrderResponse
    createOrderResponse(RequestSpecification requestSpec, String token, String json) {
        return given()
                .spec(requestSpec)
                .and()
                .body(json)
                .auth().oauth2(token)
                .when()
                .post("orders")
                .body()
                .as(OrderResponse.class);
    }
}