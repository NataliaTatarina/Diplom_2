package diplom_api.test;

import diplom_api.pojo.Ingredients;
import diplom_api.pojo.UserLogin;
import diplom_api.pojo.UserRegister;
import io.restassured.RestAssured;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.RandomStringUtils;

public class AbstractTest {

    protected RequestSpecification requestSpec = RestAssured.given()
            .baseUri("https://stellarburgers.nomoreparties.site/api/")
            .header("Content-type", "application/json");
         //   .filter(new ResponseLoggingFilter());
    protected String testEmail = RandomStringUtils.randomAlphabetic(5) + "@yandex.ru";
    protected String testPassword = RandomStringUtils.randomAlphabetic(5)+"TestPassword";
    protected String testName = RandomStringUtils.randomAlphabetic(5) + "TestName";
    protected UserRegister userRegister = new UserRegister (testEmail, testPassword, testName);
    protected UserLogin userLogin = new UserLogin (testEmail, testPassword);

    protected Ingredients ingredients;

}
