package diplom_api.test;

import diplom_api.pojo.UserRegister;
import diplom_api.pojo.UserRegisterResponse;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static diplom_api.proc.UserProc.*;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateUserTest extends AbstractTest {

    // Создание уникального пользователя
    // Проверить возвращаемый статус
    @Test
    public void createCorrectUserStatusTest() {
        // Создать пользователя и проверить возвращаемый статус
        given()
                .spec(requestSpec)
                .and()
                .body(userRegister)
                .when()
                .post("auth/register")
                .then()
                .statusCode(SC_OK);
        // Убедиться, что пользователь может авторизироваться и получить accessToken
        UserRegisterResponse userLoginResponse =
                loginUserResponse(requestSpec, userLogin);
        // Удалить пользователя
        deleteUser(requestSpec, userRegister,
                userLoginResponse.getAccessToken().replace("Bearer ", ""));
    }

    // Проверить возвращаемый response
    @Test
    public void createCorrectUserResponseTest() {
        // Создать пользователя
        UserRegisterResponse UserRegisterResponse =
                createUserResponse(requestSpec, userRegister);
        // Убедиться. что вернулся ожидаемый JSON
        MatcherAssert.assertThat(UserRegisterResponse,
                notNullValue());
        // Убедиться, что пользователь может авторизироваться
        UserRegisterResponse userLoginResponse =
                loginUserResponse(requestSpec, userLogin);
        // Удалить пользователя
        deleteUser(requestSpec, userRegister,
                userLoginResponse.getAccessToken().replace("Bearer ", ""));
    }

    // Нельзя создать 2 одинаковых пользователей
    // Проверить возвращаемый статус
    @Test
    public void createTwoEqualUsersFallsTest() {
        // Создать первого  пользователя
        UserRegisterResponse userRegisterResponse =
                createUserResponse(requestSpec, userRegister);
        // Попытаться создать второго пользователя с теми же параметрами
        // и убедиться, что вернулся верный статус
        given()
                .spec(requestSpec)
                .and()
                .body(userRegister)
                .when()
                .post("auth/register")
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("message",
                        equalTo("User already exists"))
                .and()
                .body("success",
                        equalTo(false));
        // Удалить первого пользователя
        deleteUser(requestSpec, userRegister,
                userRegisterResponse.getAccessToken().replace("Bearer ", ""));
    }

    // Нельзя создать пользователя, если не указано одно из обязательных полей
    // Попытка создать пользователя без указания email
    @Test
    public void createUserWithoutEmailFallsTest() {
        UserRegister userWithoutEmail = new UserRegister(null, testPassword, testName);
        createUserWithoutNecessaryField(requestSpec, userWithoutEmail);
    }

    // Попытка создать пользователя без указания пароля
    @Test
    public void createUserWithoutPasswordFallsTest() {
        UserRegister userWithoutPassword = new UserRegister(testEmail, null, testName);
        createUserWithoutNecessaryField(requestSpec, userWithoutPassword);
    }

    // Попытка создать пользователя без указания имени
    @Test
    public void createUserWithoutNameFallsTest() {
        UserRegister userWithoutName = new UserRegister(testEmail, testPassword, null);
        createUserWithoutNecessaryField(requestSpec, userWithoutName);
    }


}
