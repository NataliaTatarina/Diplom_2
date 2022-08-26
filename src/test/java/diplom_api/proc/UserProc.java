package diplom_api.proc;

import io.restassured.specification.RequestSpecification;
import diplom_api.pojo.UserLogin;
import diplom_api.pojo.UserRegister;
import diplom_api.pojo.UserRegisterResponse;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class UserProc {

    // Запрос на создание пользователя без необходимых полей
    public static void createUserWithoutNecessaryField(RequestSpecification requestSpec, UserRegister userRegister) {
        given()
                .spec(requestSpec)
                .and()
                .body(userRegister)
                .when()
                .post("auth/register")
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("message",
                        equalTo("Email, password and name are required fields"))
                .and()
                .body("success",
                        equalTo(false));
    }


    // Запрос на создание пользователя
    public static UserRegisterResponse createUserResponse(RequestSpecification requestSpec, UserRegister userRegister) {
        return
                given()
                        .spec(requestSpec)
                        .and()
                        .body(userRegister)
                        .when()
                        .post("auth/register")
                        .body()
                        .as(UserRegisterResponse.class);
    }

    // Запрос на удаление пользователя
    // Проверяется, что возвращаемый статус - SC_ACCEPTED
    public static void deleteUser(RequestSpecification requestSpec, UserRegister userRegister, String token) {
        given()
                .spec(requestSpec)
                .and()
                .body(userRegister)
                .auth().oauth2(token)
                .when()
                .delete("auth/user")
                .then()
                .statusCode(SC_ACCEPTED);
    }

    // Заррос на авторизацию пользователя
    public static UserRegisterResponse loginUserResponse(RequestSpecification requestSpec, UserLogin userLogin) {
        return
                given()
                        .spec(requestSpec)
                        .and()
                        .body(userLogin)
                        .when()
                        .post("auth/login")
                        .body()
                        .as(UserRegisterResponse.class);
    }

    // Заррос на авторизацию пользователя - проверка статуса
    public static void loginUser(RequestSpecification requestSpec, UserLogin userLogin) {
                    given()
                        .spec(requestSpec)
                        .and()
                        .body(userLogin)
                        .when()
                        .post("auth/login")
                        .then()
                        .statusCode(SC_OK);
    }
    // Запрос на авторизацию пользователя без одного из необходимых полей
    // Проверяется, что возвращаемый статус - SC_UNAUTHORIZED
    public static void loginUserWithOneWrongField(RequestSpecification requestSpec, UserLogin userLogin) {
        given()
                .spec(requestSpec)
                .and()
                .body(userLogin)
                .when()
                .post("auth/login")
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .body("message",
                        equalTo("email or password are incorrect"))
                .and()
                .body("success",
                        equalTo(false));
    }

    // Запрос на измение полей учетной записи пользователя
    // Проверяется, что возвращаемый статус соответствует ожидаемому
    public static void updateUser(RequestSpecification requestSpec, String json, String token,
                                  int status, String message, boolean success) {
        given()
                .spec(requestSpec)
                .and()
                .body(json)
                .auth().oauth2(token)
                .when()
                .patch("auth/user")
                .then()
                .statusCode(status)
                .body("message",
                        equalTo(message))
                .and()
                .body("success",
                        equalTo(success));
    }


    // Запрос на измение полей учетной записи авторизированного пользователя
    public static UserRegisterResponse updateUserResponse(RequestSpecification requestSpec, String json, String token) {
        return
                given()
                        .spec(requestSpec)
                        .and()
                        .body(json)
                        .auth().oauth2(token)
                        .when()
                        .patch("auth/user")
                        .body()
                        .as(UserRegisterResponse.class);
    }

}