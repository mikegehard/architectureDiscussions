
import com.example.Application
import com.jayway.restassured.RestAssured
import com.jayway.restassured.RestAssured.given
import org.hamcrest.Matchers.equalTo
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringApplicationConfiguration
import org.springframework.boot.test.context.SpringApplicationTest
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner::class)
@SpringApplicationConfiguration(Application::class)
@SpringApplicationTest(webEnvironment = SpringApplicationTest.WebEnvironment.DEFINED_PORT)
class IntegrationTests {

    companion object {
        @BeforeClass
        fun configureRestAssured() {
            RestAssured.port = 8080
        }
    }

    @Test
    fun successfulCreate() {
        val payload = """
        {
            "name":"goodName"
        }
        """

        val expected = """
        {
            "id": 1,
            "createdAt":"2016-02-02"
        }
        """.replace(" ", "").replace("\n", "")

        given()
                .contentType("application/json")
                .body(payload)
                .`when`()
                .post("/kotlinWhen")
                .then()
                .statusCode(201)
                .body(equalTo(expected))
    }

    @Test
    fun creationErrors() {
        val payload = """
        {
            "name":"badName"
        }
        """

        val expected = """
        {
            "errors": ["InvalidName"]
        }
        """.replace(" ", "").replace("\n", "")

        given()
                .contentType("application/json")
                .body(payload)
                .`when`()
                .post("/kotlinWhen")
                .then()
                .statusCode(422)
                .body(equalTo(expected))
    }
}
