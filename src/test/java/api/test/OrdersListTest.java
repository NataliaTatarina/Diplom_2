package api.test;

import api.model.OrderResponse;
import api.model.OrdersList;
import io.qameta.allure.junit4.DisplayName;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static api.client.CreateOrderProc.createOrderResponse;
import static api.client.GetIngredientsProc.getIngredients;
import static api.client.GetOrdersListProc.getOrderList;
import static api.client.GetOrdersListProc.getOrderListResponse;
import static api.client.UserProc.createUserResponse;
import static api.client.UserProc.deleteUser;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.notNullValue;

public class OrdersListTest extends AbstractTest {
    private String token;

    private OrderResponse firstOrder;

    private OrderResponse secondOrder;


    @Before
    public void createUserAndCreateOrdersBeforeOrderListTest() {
        // Создать пользователя
        // Получить accessToken
        token = createUserResponse(requestSpec, userRegister).getAccessToken().replace("Bearer ", "");
        // Получить список ингредиентов
        ingredients = getIngredients(requestSpec);
        // Проверить, что список ингредиентов не нулевой, есть хотя бы 1 ингредиент
        Assert.assertNotNull("list og ingredients is empty", ingredients);
        // Поучить длину списка ингредиентов
        int size = ingredients.getData().size();
        // Создать два заказа
        String json1 = "{\"ingredients\":[\"" + ingredients.getData().get(0).get_id() + "\", " +
                "\"" + ingredients.getData().get(size - 1).get_id() + "\"" +
                "]}";
        firstOrder = createOrderResponse(requestSpec, token, json1);
        String json2 = "{\"ingredients\":[\"" + ingredients.getData().get(0).get_id() + "\", " +
                "\"" + ingredients.getData().get(size - 1).get_id() + "\"" +
                "]}";
        secondOrder = createOrderResponse(requestSpec, token, json2);
    }

    @After
    public void deleteUserAfterOrderListTest() {
        // Удалить пользователя
        deleteUser(requestSpec, userRegister, token);
    }

    // Получить список заказов для авторизированного пользователя - проверить статус
    @Test
    @DisplayName("Получение списка заказов для авторизированного пользователя - проверка статуса и ответа")
    public void getOrdersListForUserWithAuthStatusTest() {
        getOrderList(requestSpec, token, SC_OK, null, true);
      }

    @Test
    @DisplayName("Получение списка заказов для авторизированного пользователя")
    public void getOrdersListForUserWithAuthResponseTest() {
        OrdersList ordersList = getOrderListResponse(requestSpec, token);
        // Убедиться. что вернулся ожидаемый JSON
        MatcherAssert.assertThat(ordersList,
                notNullValue());
        // Убедиться, что в списке есть созданный пользователем заказ
        Assert.assertEquals(firstOrder.getOrder().getNumber(), ordersList.getOrders().get(0).getNumber());
        Assert.assertEquals(secondOrder.getOrder().getNumber(), ordersList.getOrders().get(1).getNumber());
    }

    // Получить список без авторизации
    @Test
    @DisplayName("Получение списка заказов для не авторизированного пользователя - проверка статуса и ответа")
    public void getOrdersListForUserWithoutAuthTest() {
        getOrderList(requestSpec, "", SC_UNAUTHORIZED, "You should be authorised", false);
    }
}
