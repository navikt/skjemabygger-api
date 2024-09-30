package no.nav.forms

import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
abstract class ApplicationTest {

	@Autowired
	lateinit var restTemplate: TestRestTemplate

	@Autowired
	private lateinit var flyway: Flyway

	val baseUrl = "http://localhost:8082"

	@BeforeEach
	fun setup() {
		flyway.clean()
		flyway.migrate()
	}

}
