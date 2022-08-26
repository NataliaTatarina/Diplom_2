package diplom_api.test;

import diplom_api.pojo.UserLogin;
import diplom_api.pojo.UserRegister;
import diplom_api.pojo.UserRegisterResponse;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static diplom_api.proc.UserProc.*;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UpdateUser extends AbstractTest {
    private String token;

    @Before
    public void createUserBeforeUpdateUserTest() {
        // Создать пользователя
        // Получить accessToken
        token = createUserResponse(requestSpec, userRegister).getAccessToken().replace("Bearer ", "");
    }

    @After
    public void deleteUserAfterUpdateUserTest() {
        // Удалить пользователя
        deleteUser(requestSpec, userRegister, token);
    }

    // Изменение данных пользователя без авторизации
    @Test
    public void updateUserNameWithoutAuthorizationFallsTest() {
        String json = "{\"name\": \"" + "Update" + testName + "\"}";
        updateUser(requestSpec, json, "", SC_UNAUTHORIZED, "You should be authorised", false);
    }

    @Test
    public void updateUserEmailWithoutAuthorizationFallsTest() {
        String json = "{\"email\": \"" + "Update" + testEmail + "\"}";
        updateUser(requestSpec, json, "", SC_UNAUTHORIZED, "You should be authorised", false);
    }

    @Test
    public void updateUserPasswordWithoutAuthorizationFallsTest() {
        String json = "{\"password\": \"" + "Update" + testPassword + "\"}";
        updateUser(requestSpec, json, "", SC_UNAUTHORIZED, "You should be authorised", false);
    }

    // Изменение данных пользователя с авторизацией - проверка возвращаемого статуса
    @Test
    public void updateUserPasswordWithAuthorizationStatusTest() {
        String json = "{\"name\": \"" + "Update" + testName + "\", ";
        json = json + "\"email\": \"" + "Update" + testEmail + "\", ";
        json = json + "\"password\": \"" + "Update" + testPassword + "\"}";
        updateUser(requestSpec, json, token, SC_OK, null, true);
    }

    // Изменение данных пользователя с авторизацией - проверка возвращаемого ответа
    // Корректно изменить имя
    @Test
    public void updateCorrectUserNameResponseTest() {
        String json = "{\"name\": \"" + "Update" + testName + "\"}";
        UserRegisterResponse userUpdateName =
                updateUserResponse(requestSpec, json, token);
        MatcherAssert.assertThat(userUpdateName,
                notNullValue());
        // Авторизироваться, из ответа получть name, сравнить с обновленным занчением
        UserRegisterResponse loginUserRespone = loginUserResponse(requestSpec, userLogin);
        MatcherAssert.assertThat(loginUserRespone.getUser().getName(), equalTo("Update" + testName));
    }

    // Корректно изменить пароль
    @Test
    public void updateCorrectUserPasswordResponseTest() {
        String json = "{\"password\": \"" + "Update" + testPassword + "\"}";
        UserRegisterResponse userUpdatePassword =
                updateUserResponse(requestSpec, json, token);
        MatcherAssert.assertThat(userUpdatePassword,
                notNullValue());
        // Авторизироваться под новым паролем
        UserLogin newUserLogin = new UserLogin(testEmail, "Update" + testPassword );
        loginUser(requestSpec, newUserLogin);
    }

    // Корректно изменить email
    @Test
    public void updateCorrectUserEmailResponseTest() {
        String json = "{\"email\": \"" + "Update" + testEmail + "\"}";
        UserRegisterResponse userUpdateEmail =
                updateUserResponse(requestSpec, json, token);
        MatcherAssert.assertThat(userUpdateEmail,
                notNullValue());
        // Авторизироваться под новым email
        UserLogin newUserLogin = new UserLogin("Update" +testEmail,  testPassword );
        loginUser(requestSpec, newUserLogin);
    }

    // Некорректно изменить email авторизированного пользователя
    // Попытаться установить ему email, который уже иcпользуется -
    // первым пользователем, созданным в Before
    @Test
    public void updateUserDuplicateEmailFallsStatusTest() {
        String json = "{\"email\": \"" + testEmail + "\"}";
        // Создать второго пользователя
        UserRegister secondUser = new UserRegister("Update" + testEmail, testPassword, testName);
        String secondToken = createUserResponse(requestSpec, secondUser).
                getAccessToken().replace("Bearer ", "");
        // Попытаться обновить email второму пользователю
        given()
                .spec(requestSpec)
                .and()
                .body(json)
                .auth().oauth2(secondToken)
                .when()
                .patch("auth/user")
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("message",
                        equalTo("User with such email already exists"))
                .and()
                .body("success",
                        equalTo(false));
        // Удалить второго пользователя
        deleteUser(requestSpec, userRegister, secondToken);
    }
}
