import com.example.Application
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringApplicationConfiguration
import org.springframework.boot.test.context.web.WebIntegrationTest
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner::class)
@SpringApplicationConfiguration(Application::class)
@WebIntegrationTest
class IntegrationTests {

    @Test
    fun successfulCreate() {
        /*
        {
            foo:
                {id: <theIdOfTheSavedObject>}
        }
         */
        assert(true)
    }

    @Test
    fun creationErrors() {
        /*
        {errors:["e1", "e2"]}
         */
        assert(true)
    }
}