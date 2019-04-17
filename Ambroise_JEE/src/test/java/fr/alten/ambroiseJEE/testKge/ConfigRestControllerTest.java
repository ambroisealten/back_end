package fr.alten.ambroiseJEE.testKge;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Headers;

import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.test.context.support.WithMockUser;

import fr.alten.ambroiseJEE.AmbroiseJeeApplication;
import fr.alten.ambroiseJEE.security.UserRole;

/**
 * 
 * @author Kylian Gehier
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AmbroiseJeeApplication.class)
@TestPropertySource(value = { "classpath:application.properties" })
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class ConfigRestControllerTest {

	@Value("${local.server.port}")
	int port;

	@Before
	public void setBaseUri() {
		RestAssured.port = port;
		RestAssured.baseURI = "http://localhost"; // replace as appropriate
	}

	@Test
	public void createUserTest() {
		// when().get("/test/api/tdd/truc")
		// .then().assertThat().body("data", equalTo("truc"));
		// System.out.println("\n\n\nTADA
		// :"+when().get("/test/configRouting/Skills").asString()+"\n\n\n");

		Header mailHeader = new Header("mail", "abc@gmail.com");
		Header roleHeader = new Header("role", UserRole.MANAGER_ADMIN.toString());
		Headers header = new Headers(mailHeader, roleHeader);
		System.out.println(given().headers(header)
				.when().get("\n\n\nTADA :" + "/test/configRouting/Skills")
				.asString() + "\n\n\n");

	}

}