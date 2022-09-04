package api.test;

import api.util.UserLogin;
import api.util.UserRegister;
import api.model.UserRegisterResponse;
import io.qameta.allure.junit4.DisplayName;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static api.client.UserProc.*;
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
    @DisplayName("Изменение имени пользователя без авторизации - проверка статуса и ответа")
    public void updateUserNameWithoutAuthorizationFallsTest() {
        String json = "{\"name\": \"" + "Update" + testName + "\"}";
        updateUser(requestSpec, json, "", SC_UNAUTHORIZED, "You should be authorised", false);
    }

    @Test
    @DisplayName("Изменение email пользователя без авторизации - проверка статуса и ответа")
    public void updateUserEmailWithoutAuthorizationFallsTest() {
        String json = "{\"email\": \"" + "Update" + testEmail + "\"}";
        updateUser(requestSpec, json, "", SC_UNAUTHORIZED, "You should be authorised", false);
    }

    @Test
    @DisplayName("Изменение пароля пользователя без авторизации - проверка статуса и ответа")
    public void updateUserPasswordWithoutAuthorizationFallsTest() {
        String json = "{\"password\": \"" + "Update" + testPassword + "\"}";
        updateUser(requestSpec, json, "", SC_UNAUTHORIZED, "You should be authorised", false);
    }

    // Изменение данных пользователя с авторизацией - проверка возвращаемого статуса
    @Test
    @DisplayName("Изменение данных пользователя с авторизацией - проверка статуса и ответа")
    public void updateUserPasswordWithAuthorizationStatusTest() {
        String json = "{\"name\": \"" + "Update" + testName + "\", ";
        json = json + "\"email\": \"" + "Update" + testEmail + "\", ";
        json = json + "\"password\": \"" + "Update" + testPassword + "\"}";
        updateUser(requestSpec, json, token, SC_OK, null, true);
    }

    // Изменение данных пользователя с авторизацией - проверка возвращаемого ответа
    // Корректно изменить имя
    @Test
    @DisplayName("Изменение имени авторизированного пользователя")
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
    @DisplayName("Изменение пароля авторизированного пользователя")
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
    @DisplayName("Изменение email авторизированного пользователя")
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
    @DisplayName("Изменение пароля авторизированного пользователя - попытка дублирования паролей")
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
