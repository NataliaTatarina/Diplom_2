package api.test;

import api.util.UserLogin;
import api.model.UserRegisterResponse;
import io.qameta.allure.junit4.DisplayName;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static api.client.UserProc.*;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.notNullValue;

public class LoginUserTest extends AbstractTest {
    private String token;

    @Before
    public void createUserBeforeLoginUserTest() {
        // Создать пользователя
        // Получить accessToken
        token = createUserResponse(requestSpec, userRegister).getAccessToken().replace("Bearer ", "");
    }

    @After
    public void deleteUserAfterLoginUserTest() {
        // Удалить пользователя
        deleteUser(requestSpec, userRegister, token);
    }

    // Успешная авторизация под существующим паролем
    // Проверить статус ответа
    @Test
    @DisplayName("Успешная авторизация под существующим паролем - проверка статуса")
    public void loginCorrectUserStatusTest() {
        given()
                .spec(requestSpec)
                .and()
                .body(userLogin)
                .when()
                .post("auth/login")
                .then()
                .statusCode(SC_OK);
    }

    // Проверить возвращаемый response
    @Test
    @DisplayName("Успешная авторизация под существующим паролем")
    public void loginCorrectUserResponseTest() {
        UserRegisterResponse userLoginResponse =
                loginUserResponse(requestSpec, userLogin);
        // Убедиться. что вернулся ожидаемый JSON
        MatcherAssert.assertThat(userLoginResponse,
                notNullValue());
        // Убедиться, что можно разлогиниться - получть refreshToken из ответа
        // Сделать logout
        String json = "{\"token\": \"" + userLoginResponse.getRefreshToken() + "\"}";
        given()
                .spec(requestSpec)
                .and()
                .body(json)
                .when()
                .post("auth/logout")
                .then()
                .statusCode(SC_OK);
    }

    // Авторизация с неверным логином и паролем
    // Пользователь существует, email некорректный
    @Test
    @DisplayName("Авторизация с неверным email")
    public void loginUserWithWrongEmailFallsTest() {
        UserLogin userLoginWithWrongEmail =
                new UserLogin(testEmail + testEmail, testPassword);
        loginUserWithOneWrongField(requestSpec, userLoginWithWrongEmail);
    }

    // Пользователь существует, пароль некорректный
    @Test
    @DisplayName("Авторизация с неверным паролем")
    public void loginUserWithWrongPasswordFallsTest() {
        UserLogin userLoginWithWrongPassword = new UserLogin(testEmail, testPassword + testPassword);
        loginUserWithOneWrongField(requestSpec, userLoginWithWrongPassword);
    }

}