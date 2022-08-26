package diplom_api.test;

import diplom_api.pojo.OrderResponse;
import diplom_api.pojo.OrdersList;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static diplom_api.proc.CreateOrderProc.createOrderResponse;
import static diplom_api.proc.GetIngredientsProc.getIngredients;
import static diplom_api.proc.GetOrdersListProc.getOrderList;
import static diplom_api.proc.GetOrdersListProc.getOrderListResponse;
import static diplom_api.proc.UserProc.createUserResponse;
import static diplom_api.proc.UserProc.deleteUser;
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
    public void getOrdersListForUserWithAuthStatusTest() {
        getOrderList(requestSpec, token, SC_OK, null, true);
      }

    @Test
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
    public void getOrdersListForUserWithoutAuthTest() {
        getOrderList(requestSpec, "", SC_UNAUTHORIZED, "You should be authorised", false);
    }
}
