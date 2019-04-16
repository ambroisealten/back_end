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
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import fr.alten.ambroiseJEE.AmbroiseJeeApplication;

/**
 * 
 * @author Kylian Gehier
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AmbroiseJeeApplication.class)
@TestPropertySource(value = { "classpath:application.properties" })
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class UserAdminRestControllerTest {
	@Value("${local.server.port}")
	int port;

	@Before
	public void setBaseUri() {
		RestAssured.port = port;
		RestAssured.baseURI = "http://localhost"; // replace as appropriate
	}
	

	@Test
	public void createUserTest() {
		//when().get("/test/api/tdd/truc")
		//	.then().assertThat().body("data", equalTo("truc"));
		System.out.println("\n\n\nTADA :"+when().get("/test/api/tdd/truc").asString()+"\n\n\n");
	}
}