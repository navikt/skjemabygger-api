package no.nav.forms.translations

import no.nav.forms.ApplicationTest
import no.nav.forms.model.GlobalTranslationDto
import no.nav.forms.model.NewGlobalTranslationRequest
import no.nav.forms.model.PublishedGlobalTranslationDto
import no.nav.forms.model.UpdateGlobalTranslationRequest
import no.nav.forms.testutils.MOCK_USER_GROUP_ID
import no.nav.forms.testutils.createMockToken
import no.nav.forms.utils.LanguageCode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PublishGlobalTranslationsControllerTest : ApplicationTest() {

	private val translations: Map<String, NewGlobalTranslationRequest> = mapOf(
		"Fornavn" to NewGlobalTranslationRequest(
			key = "Fornavn",
			tag = "skjematekster",
			nb = null,
			nn = "Fornamn",
			en = "Given name",
		),
		"required" to NewGlobalTranslationRequest(
			key = "required",
			tag = "validering",
			nb = "Du må fylle ut: {{field}}",
			nn = "Du må fylle ut: {{field}}",
			en = "You must fill in: {{field}}",
		),
	)

	@BeforeEach
	fun createGlobalTranslationsForTest() {
		val authToken = mockOAuth2Server.createMockToken()
		translations.values.forEach {
			val response = testFormsApi.createGlobalTranslation(it, authToken)
			assertTrue(response.statusCode.is2xxSuccessful)
		}
		val publishResponse = testFormsApi.publishGlobalTranslations(authToken)
		assertTrue(publishResponse.statusCode.is2xxSuccessful)
	}

	@Test
	fun testPublish() {
		val tFornavn = translations.values.find { it.key == "Fornavn" }!!
		val tRequired = translations.values.find { it.key == "required" }!!

		val publishedGlobalTranslationsBokmal = testFormsApi.getPublishedGlobalTranslations(LanguageCode.NB.value)
		val bokmal = publishedGlobalTranslationsBokmal.body as List<PublishedGlobalTranslationDto>
		assertEquals(1, bokmal.size)
		bokmal.none { it.key == tFornavn.key }
		bokmal.find { it.key == tRequired.key }.also { assertEquals(tRequired.nb, it?.value) }

		val publishedGlobalTranslationsNynorsk = testFormsApi.getPublishedGlobalTranslations(LanguageCode.NN.value)
		val nynorsk = publishedGlobalTranslationsNynorsk.body as List<PublishedGlobalTranslationDto>
		assertEquals(1, bokmal.size)
		nynorsk.find { it.key == tFornavn.key }.also { assertEquals(tFornavn.nn, it?.value) }
		nynorsk.find { it.key == tRequired.key }.also { assertEquals(tRequired.nn, it?.value) }

		val publishedGlobalTranslationsEnglish = testFormsApi.getPublishedGlobalTranslations(LanguageCode.EN.value)
		val english = publishedGlobalTranslationsEnglish.body as List<PublishedGlobalTranslationDto>
		assertEquals(2, english.size)
		english.find { it.key == tFornavn.key }.also { assertEquals(tFornavn.en, it?.value) }
		english.find { it.key == tRequired.key }.also { assertEquals(tRequired.en, it?.value) }
	}

	@Test
	fun testChangeAndPublish() {
		val authToken = mockOAuth2Server.createMockToken()
		val globalTranslations = testFormsApi.getGlobalTranslations().body as List<GlobalTranslationDto>
		val tFornavn = globalTranslations.find { it.key == "Fornavn" }!!

		val updatedEnTranslation = "${tFornavn.en}postfix"
		val putResponse = testFormsApi.putGlobalTranslation(
			tFornavn.id,
			tFornavn.revision!!,
			UpdateGlobalTranslationRequest(
				nb = tFornavn.nb,
				en = updatedEnTranslation,
				nn = tFornavn.nn,
			),
			authToken,
		)
		assertTrue(putResponse.statusCode.is2xxSuccessful)

		val publishResponse = testFormsApi.publishGlobalTranslations(authToken)
		assertTrue(publishResponse.statusCode.is2xxSuccessful)

		val publishedGlobalTranslationsEnglish = testFormsApi.getPublishedGlobalTranslations(LanguageCode.EN.value)
		val english = publishedGlobalTranslationsEnglish.body as List<PublishedGlobalTranslationDto>
		assertEquals(2, english.size)
		english.find { it.key == tFornavn.key }.also { assertEquals(updatedEnTranslation, it?.value) }
	}

	@Test
	fun testPublishWhenNotAuthenticated() {
		val authToken = null
		val publishResponse = testFormsApi.publishGlobalTranslations(authToken)
		assertTrue(publishResponse.statusCode.is4xxClientError)
	}

	@Test
	fun testPublishWhenNotAdmin() {
		val authToken = mockOAuth2Server.createMockToken(groups = listOf(MOCK_USER_GROUP_ID))
		val publishResponse = testFormsApi.publishGlobalTranslations(authToken)
		assertTrue(publishResponse.statusCode.is4xxClientError)
	}

	private fun create(
		key: String,
		tag: String,
		nb: String?,
		nn: String?,
		en: String?,
		authToken: String
	): GlobalTranslationDto {
		val createResponse = testFormsApi.createGlobalTranslation(
			NewGlobalTranslationRequest(
				key = key,
				tag = tag,
				nb = nb,
				nn = nn,
				en = en,
			),
			authToken,
		)
		assertTrue(createResponse.statusCode.is2xxSuccessful)
		createResponse.body as GlobalTranslationDto
		return createResponse.body
	}

}
