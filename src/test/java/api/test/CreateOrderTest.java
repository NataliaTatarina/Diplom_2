package api.test;

import api.model.OrderResponse;
import api.model.OrdersList;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static api.client.CreateOrderProc.createOrder;
import static api.client.CreateOrderProc.createOrderResponse;
import static api.client.GetIngredientsProc.getIngredients;
import static api.client.GetOrdersListProc.getOrderListResponse;
import static api.client.UserProc.createUserResponse;
import static api.client.UserProc.deleteUser;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;

public class CreateOrderTest extends AbstractTest {

    private String token;

    @Before
    public void createUserBeforeCreateOrderTest() {
        // Создать пользователя
        // Получить accessToken
        token = createUserResponse(requestSpec, userRegister).getAccessToken().replace("Bearer ", "");
        // Получить список ингредиентов
        ingredients = getIngredients(requestSpec);
        Assert.assertNotNull("list og ingredients is empty", ingredients);
    }

    @After
    public void deleteUserAfterCreateOrderTest() {
        // Удалить пользователя
        deleteUser(requestSpec, userRegister, token);
    }

    // Создать заказ без авторизации и без ингредиентов
    @Test
    @DisplayName("Создание заказа без авторизации и без ингредиентов - проверка статуса и ответа")
    public void createOrderWithoutAuthWithoutIngredientsTest() {
        createOrder(requestSpec, "", "", SC_BAD_REQUEST, "Ingredient ids must be provided", false);
    }

    // Создать заказ без авторизации и c некорректным хэшем единственного ингредиента
    @Test
    @DisplayName("Создание заказа без авторизации и c некорректным хэшем единственного ингредиента - проверка статуса и ответа")
    public void createOrderWithoutAuthWithWrongHashForSingleIngredientResponseTest() {
        String json = "{\"ingredients\":[\"123456789012345678901234\"]}";
        createOrder(requestSpec, "", json, SC_BAD_REQUEST, "One or more ids provided are incorrect", false);
    }

    // Создать заказ без авторизации и c некорректным хэшем одного из двух ингредиентов
    // Проверить, что возвращается ожидаемый json

    @Test
    @DisplayName("Создание заказа без авторизации и c некорректным хэшем одного из двух ингредиентов")
    public void createOrderWithoutAuthWithWrongHashForOneOfTwoIngredientsResponseTest() {
        String json = "{\"ingredients\":[\"" + "123456789012345678901234" + "\", " +
                "\"" + ingredients.getData().get(1).get_id() + "\"" +
                "]}";
        // Создать заказ
        OrderResponse orderResponse = createOrderResponse(requestSpec, "", json);

    }

    // Создать заказ без авторизации и c некорректным хэшем одного из двух ингредиентов
    // Проверить, что возвращается ожидаемый статус
    @Test
    @DisplayName("Создание заказа без авторизации и c некорректным хэшем одного из двух ингредиентов - проверка статуса и ответа")
    public void createOrderWithoutAuthWithWrongHashForOneOfTwoIngredientsStatusTest() {
        String json = "{\"ingredients\":[\"" + "123456789012345678901234" + "\", " +
                "\"" + ingredients.getData().get(1).get_id() + "\"" +
                "]}";
        // Создать заказ
        createOrder(requestSpec, "", json, SC_OK, null, true);
    }

    // Создать заказ без авторизации и c корректным хэшем двух ингредиентов
    // Проверить, что возвращается ожидаемый json
    @Test
    @DisplayName("Создание заказа без авторизации и c корректным хэшем двух ингредиентов")
    public void createOrderWithoutAuthWithTwoCorrectIngredientsResponseTest() {
        String json = "{\"ingredients\":[\"" + ingredients.getData().get(2).get_id() + "\", " +
                "\"" + ingredients.getData().get(1).get_id() + "\"" +
                "]}";
        // Создать заказ
        OrderResponse orderResponse = createOrderResponse(requestSpec, "", json);
    }

    // Создать заказ без авторизации и c корректным хэшем двух ингредиентов
    // Проверить, что возвращается ожидаемый статус
    @Test
    @DisplayName("Создание заказа без авторизации и c корректным хэшем двух ингредиентов - проверка статуса и ответа")
    public void createOrderWithoutAuthWithTwoCorrectIngredientsStatusTest() {
        String json = "{\"ingredients\":[\"" + ingredients.getData().get(2).get_id() + "\", " +
                "\"" + ingredients.getData().get(1).get_id() + "\"" +
                "]}";
        // Создать заказ
        createOrder(requestSpec, "", json, SC_OK, null, true);
    }

    // Создать заказ с авторизацией и без ингредиентов
    @Test
    @DisplayName("Создание заказа с авторизацией и без ингредиентов - проверка статуса и ответа")
    public void createOrderWithAuthWithoutIngredientsResponseTest() {
        createOrder(requestSpec, token, "",
                SC_BAD_REQUEST, "Ingredient ids must be provided", false);
    }


    // Создать заказ c авторизацией и c некорректным хэшем единственного ингредиента
    @DisplayName("Создание заказа c авторизацией и c некорректным хэшем единственного ингредиента - проверка статуса и ответа")
    @Test
    public void createOrderWithAuthWithWrongHashForSingleIngredientResponseTest() {
        String json = "{\"ingredients\":[\"" + "123456789012345678901234" + "\"" + "]}";
        createOrder(requestSpec, token, json, SC_BAD_REQUEST,
                "One or more ids provided are incorrect", false);
    }

    // Создать заказ с авторизацией и c некорректным хэшем одного из двух ингредиентов - проверить статус
    @Test
    @DisplayName("Создание заказа с авторизацией и c некорректным хэшем одного из двух ингредиентов - проверка статуса и ответа")
    public void createOrderWithAuthWithWrongHashForOneOfTwoIngredientsStatusTest() {
        String json = "{\"ingredients\":[\"" + "123456789012345678901234" + "\", " +
                "\"" + ingredients.getData().get(1).get_id() + "\"" +
                "]}";
        // Создать заказ
        createOrder(requestSpec, token, json, SC_OK, null, true);
    }

    // Создать заказ с авторизацией и c некорректным хэшем одного из двух ингредиентов
    @Test
    @DisplayName("Создание заказа с авторизацией и c некорректным хэшем одного из двух ингредиентов")
    public void createOrderWithAuthWithWrongHashForOneOfTwoIngredientsResponseTest() {
        String json = "{\"ingredients\":[\"" + "123456789012345678901234" + "\", " +
                "\"" + ingredients.getData().get(1).get_id() + "\"" +
                "]}";
        // Создать заказ
        OrderResponse orderResponse = createOrderResponse(requestSpec, token, json);
        // Получить список заказов пользователя
        OrdersList ordersList = getOrderListResponse(requestSpec, token);
        // Убедиться, что в списке есть созданный пользователем заказ
        Assert.assertEquals(orderResponse.getOrder().getNumber(), ordersList.getOrders().get(0).getNumber());
    }

    // Создать заказ с авторизацией и c корректными хэшами двух ингредиентов - проверить статус
    @Test
    @DisplayName("Создание заказа с авторизацией и c корректными хэшами двух ингредиентов - проверка статуса и ответа")
    public void createOrderWithAuthWithTwoCorrectIngredientsStatusTest() {
        String json = "{\"ingredients\":[\"" + ingredients.getData().get(2).get_id() + "\", " +
                "\"" + ingredients.getData().get(1).get_id() + "\"" +
                "]}";
        createOrder(requestSpec, token, json, SC_OK, null, true);
    }

    // Создать заказ с авторизацией и c корректными хэшами двух ингредиентов
    @Test
    @DisplayName("Создание заказа с авторизацией и c корректными хэшами двух ингредиентов")
    public void createOrderWithAuthWithTwoCorrectIngredientsResponseTest() {
        String json = "{\"ingredients\":[\"" + ingredients.getData().get(2).get_id() + "\", " +
                "\"" + ingredients.getData().get(1).get_id() + "\"" +
                "]}";
        OrderResponse orderResponse = createOrderResponse(requestSpec, token, json);
        // Получить список заказов пользователя
        OrdersList ordersList = getOrderListResponse(requestSpec, token);
        // Убедиться, что в списке есть созданный пользователем заказ
        Assert.assertEquals(orderResponse.getOrder().getNumber(), ordersList.getOrders().get(0).getNumber());
    }
}
