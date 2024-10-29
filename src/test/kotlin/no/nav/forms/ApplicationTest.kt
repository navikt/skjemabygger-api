package no.nav.forms

import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
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
@EnableMockOAuth2Server
abstract class ApplicationTest {

	@Autowired
	lateinit var restTemplate: TestRestTemplate

	@Autowired
	lateinit var mockOAuth2Server: MockOAuth2Server

	@Autowired
	private lateinit var flyway: Flyway

	final val baseUrl = "http://localhost:8082"

	@BeforeEach
	fun setup() {
		flyway.clean()
		flyway.migrate()
	}

}
