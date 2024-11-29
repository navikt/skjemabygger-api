package no.nav.forms.translations

import no.nav.forms.ApplicationTest
import no.nav.forms.model.GlobalTranslationDto
import no.nav.forms.model.NewGlobalTranslationRequest
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
		val bokmal = publishedGlobalTranslationsBokmal.body as Map<String, String>
		assertEquals(1, bokmal.keys.size)
		assertEquals(
			mapOf(
				tRequired.key to tRequired.nb
			),
			bokmal
		)
		bokmal.keys.none { it == tFornavn.key }
		bokmal.keys.find { it == tRequired.key }.also { assertEquals(tRequired.nb, bokmal[it])}

		val publishedGlobalTranslationsNynorsk = testFormsApi.getPublishedGlobalTranslations(LanguageCode.NN.value)
		val nynorsk = publishedGlobalTranslationsNynorsk.body as Map<String, String>
		assertEquals(
			mapOf(
				tRequired.key to tRequired.nn,
				tFornavn.key to tFornavn.nn
			),
			nynorsk
		)

		val publishedGlobalTranslationsEnglish = testFormsApi.getPublishedGlobalTranslations(LanguageCode.EN.value)
		val english = publishedGlobalTranslationsEnglish.body as Map<String, String>
		assertEquals(
			mapOf(
				tRequired.key to tRequired.en,
				tFornavn.key to tFornavn.en
			),
			english
		)
	}

	@Test
	fun testChangeAndPublish() {
		val authToken = mockOAuth2Server.createMockToken()
		val globalTranslations = testFormsApi.getGlobalTranslations().body as List<GlobalTranslationDto>
		val tFornavn = globalTranslations.find { it.key == "Fornavn" }!!
		val tRequired = translations.values.find { it.key == "required" }!!

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
		val english = publishedGlobalTranslationsEnglish.body as Map<String,String>
		assertEquals(2, english.keys.size)
		assertEquals(
			mapOf(
				tRequired.key to tRequired.en,
				tFornavn.key to updatedEnTranslation
			),
			english
		)
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

}
