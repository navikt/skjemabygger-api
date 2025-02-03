package no.nav.forms.forms

import no.nav.forms.ApplicationTest
import no.nav.forms.model.NewFormTranslationRequestDto
import no.nav.forms.testutils.createMockToken
import no.nav.forms.testutils.FormsTestdata
import no.nav.forms.translations.testdata.GlobalTranslationsTestdata
import no.nav.forms.utils.LanguageCode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class FormPublicationsControllerTest : ApplicationTest() {

	@BeforeEach
	fun createGlobalTranslationsForTest() {
		val authToken = mockOAuth2Server.createMockToken()
		GlobalTranslationsTestdata.translations.values.forEach {
			testFormsApi.createGlobalTranslation(it, authToken).assertSuccess()
		}
		testFormsApi.publishGlobalTranslations(authToken).assertSuccess()
	}

	@Test
	fun testPublishForm() {
		val authToken = mockOAuth2Server.createMockToken()
		val createRequest = FormsTestdata.newFormRequest()
		val newForm = testFormsApi.createForm(createRequest, authToken)
			.assertSuccess()
			.body
		val formPath = newForm.path!!
		val formRevision = newForm.revision!!

		val formBeforePublication = testFormsApi.getForm(formPath)
			.assertSuccess()
			.body
		assertNull(formBeforePublication.publishedAt)
		assertNull(formBeforePublication.publishedBy)

		val publishedForm = testFormsApi.publishForm(formPath, formRevision, authToken)
			.assertSuccess()
			.body

		assertEquals(formPath, publishedForm.path)
		assertEquals(formRevision, publishedForm.revision)
		assertNotNull(publishedForm.publishedAt)
		assertNotNull(publishedForm.publishedBy)

		val formAfterPublication = testFormsApi.getForm(formPath)
			.assertSuccess()
			.body
		assertNotNull(formAfterPublication.publishedAt)
		assertNotNull(formAfterPublication.publishedBy)
	}

	@Test
	fun testGetPublishedFormTranslationsReturnsPublishedLanguagesByDefault() {
		val authToken = mockOAuth2Server.createMockToken()
		val createRequest = FormsTestdata.newFormRequest()
		val newForm = testFormsApi.createForm(createRequest, authToken)
			.assertSuccess()
			.body
		val formPath = newForm.path!!
		val formRevision = newForm.revision!!

		val translationKey = "Sannheten"
		testFormsApi.createFormTranslation(
			formPath,
			NewFormTranslationRequestDto(key = translationKey, nb = "Sannheten", nn = "Sanninga", en = "The truth"),
			authToken
		).assertSuccess()

		testFormsApi.publishForm(formPath, formRevision, authToken, listOf(LanguageCode.NB, LanguageCode.NN))
			.assertSuccess()
		testFormsApi.getPublishedFormTranslations(formPath)
			.assertSuccess()
			.body.let {
				assertEquals(setOf("nb", "nn"), it.translations?.keys)
			}
	}

	@Test
	fun testGetPublishedFormTranslationsReturnsOnlyRequestedLanguages() {
		val authToken = mockOAuth2Server.createMockToken()
		val createRequest = FormsTestdata.newFormRequest()
		val newForm = testFormsApi.createForm(createRequest, authToken)
			.assertSuccess()
			.body
		val formPath = newForm.path!!
		val formRevision = newForm.revision!!

		val translationKey = "Sannheten"
		testFormsApi.createFormTranslation(
			formPath,
			NewFormTranslationRequestDto(key = translationKey, nb = "Sannheten", nn = "Sanninga", en = "The truth"),
			authToken
		).assertSuccess()

		// Publish all languages
		testFormsApi.publishForm(formPath, formRevision, authToken, LanguageCode.entries)
			.assertSuccess()

		testFormsApi.getPublishedFormTranslations(formPath, listOf(LanguageCode.NB, LanguageCode.NN))
			.assertSuccess()
			.body.let {
				assertEquals(setOf("nb", "nn"), it.translations?.keys)
			}
		testFormsApi.getPublishedFormTranslations(formPath, listOf(LanguageCode.EN))
			.assertSuccess()
			.body.let {
				assertEquals(setOf("en"), it.translations?.keys)
			}
		testFormsApi.getPublishedFormTranslations(formPath, listOf(LanguageCode.NN))
			.assertSuccess()
			.body.let {
				assertEquals(setOf("nn"), it.translations?.keys)
			}
	}

	@Test
	fun testPublishFormAndTranslationsWithDifferentLanguagesEnabled() {
		val authToken = mockOAuth2Server.createMockToken()
		val createRequest = FormsTestdata.newFormRequest()
		val newForm = testFormsApi.createForm(createRequest, authToken)
			.assertSuccess()
			.body
		val formPath = newForm.path!!
		val formRevision = newForm.revision!!

		val translationKey1 = "Tester"
		testFormsApi.createFormTranslation(
			formPath,
			NewFormTranslationRequestDto(key = translationKey1, nb = "Tester", nn = "Testar", en = "Testing"),
			authToken
		).assertSuccess()

		val translationKey2 = "Sykdom"
		testFormsApi.createFormTranslation(
			formPath,
			NewFormTranslationRequestDto(key = translationKey2, nb = "Sykdom", nn = "Sjukdom", en = "Illness"),
			authToken
		).assertSuccess()

		// Default: only 'nb' is published
		testFormsApi.publishForm(formPath, formRevision, authToken)
			.assertSuccess()
			.body
		testFormsApi.getPublishedFormTranslations(formPath)
			.assertSuccess()
			.body.let {
				assertEquals(setOf("nb"), it.translations?.keys)
				assertEquals("Tester", it.translations?.get("nb")?.get(translationKey1))
				assertEquals("Sykdom", it.translations?.get("nb")?.get(translationKey2))
			}

		// Publish "nb" and "nn"
		testFormsApi.publishForm(formPath, formRevision, authToken, listOf(LanguageCode.NB, LanguageCode.NN))
			.assertSuccess()
			.body
		testFormsApi.getPublishedFormTranslations(formPath)
			.assertSuccess()
			.body.let {
				assertEquals(setOf("nb", "nn"), it.translations?.keys)
				assertEquals("Tester", it.translations?.get("nb")?.get(translationKey1))
				assertEquals("Sykdom", it.translations?.get("nb")?.get(translationKey2))
				assertEquals("Testar", it.translations?.get("nn")?.get(translationKey1))
				assertEquals("Sjukdom", it.translations?.get("nn")?.get(translationKey2))
			}

		// Publish "nb" and "en"
		testFormsApi.publishForm(formPath, formRevision, authToken, listOf(LanguageCode.NB, LanguageCode.EN))
			.assertSuccess()
			.body
		testFormsApi.getPublishedFormTranslations(formPath)
			.assertSuccess()
			.body.let {
				assertEquals(setOf("nb", "en"), it.translations?.keys)
				assertEquals("Tester", it.translations?.get("nb")?.get(translationKey1))
				assertEquals("Sykdom", it.translations?.get("nb")?.get(translationKey2))
				assertEquals("Testing", it.translations?.get("en")?.get(translationKey1))
				assertEquals("Illness", it.translations?.get("en")?.get(translationKey2))
			}

		// Publish "nb", "en" and "nn"
		testFormsApi.publishForm(formPath, formRevision, authToken, LanguageCode.entries)
			.assertSuccess()
			.body
		testFormsApi.getPublishedFormTranslations(formPath)
			.assertSuccess()
			.body.let {
				assertEquals(setOf("nb", "en", "nn"), it.translations?.keys)
				assertEquals("Tester", it.translations?.get("nb")?.get(translationKey1))
				assertEquals("Sykdom", it.translations?.get("nb")?.get(translationKey2))
				assertEquals("Testar", it.translations?.get("nn")?.get(translationKey1))
				assertEquals("Sjukdom", it.translations?.get("nn")?.get(translationKey2))
				assertEquals("Testing", it.translations?.get("en")?.get(translationKey1))
				assertEquals("Illness", it.translations?.get("en")?.get(translationKey2))
			}
	}

	@Test
	fun testPublishFormFailureWhenWrongRevision() {
		val authToken = mockOAuth2Server.createMockToken()
		val createRequest = FormsTestdata.newFormRequest()
		val newForm = testFormsApi.createForm(createRequest, authToken)
			.assertSuccess()
			.body

		val formPath = newForm.path!!
		val wrongRevision = newForm.revision!! + 1
		val errorBody = testFormsApi.publishForm(formPath, wrongRevision, authToken)
			.assertHttpStatus(HttpStatus.CONFLICT)
			.errorBody

		assertEquals("Conflict", errorBody.errorMessage)
	}

	@Test
	fun testEditingFormAfterPublication() {
		val authToken = mockOAuth2Server.createMockToken()
		val createRequest = FormsTestdata.newFormRequest(title = "Original title")
		val newForm = testFormsApi.createForm(createRequest, authToken)
			.assertSuccess()
			.body
		assertEquals("Original title", newForm.title)
		val formPath = newForm.path!!
		val formRevision = newForm.revision!!
		val originalTitle = newForm.title!!

		testFormsApi.publishForm(formPath, formRevision, authToken)
			.assertSuccess()

		val updateRequest = FormsTestdata.updateFormRequest(
			title = "Updated title",
			components = newForm.components!!,
			properties = newForm.properties!!
		)
		val updatedForm = testFormsApi.updateForm(formPath, formRevision, updateRequest, authToken)
			.assertSuccess()
			.body
		assertEquals("Updated title", updatedForm.title)

		val publishedForm = testFormsApi.getPublishedForm(formPath)
			.assertSuccess()
			.body
		assertEquals(originalTitle, publishedForm.title)
	}

	@Test
	fun testGetPublishedFormWhenNotPublished() {
		val authToken = mockOAuth2Server.createMockToken()
		val createRequest = FormsTestdata.newFormRequest()
		val newForm = testFormsApi.createForm(createRequest, authToken)
			.assertSuccess()
			.body
		val formPath = newForm.path!!

		val errorBody = testFormsApi.getPublishedForm(formPath)
			.assertHttpStatus(HttpStatus.NOT_FOUND)
			.errorBody

		assertEquals("Form not published", errorBody.errorMessage)
	}

	@Test
	fun testGetPublishedFormWithInvalidPath() {
		val invalidFormPath = "invalidpath"

		val errorBody = testFormsApi.getPublishedForm(invalidFormPath)
			.assertHttpStatus(HttpStatus.NOT_FOUND)
			.errorBody

		assertEquals("Form not found", errorBody.errorMessage)
	}

	@Test
	fun testPublishFormWithInvalidPath() {
		val authToken = mockOAuth2Server.createMockToken()
		val invalidFormPath = "invalidpath"
		val formRevision = 1

		val errorBody = testFormsApi.publishForm(invalidFormPath, formRevision, authToken)
			.assertHttpStatus(HttpStatus.BAD_REQUEST)
			.errorBody

		assertEquals("Invalid form path: $invalidFormPath", errorBody.errorMessage)
	}

	@Test
	fun testPublishFormWithoutAuthentication() {
		val authToken = mockOAuth2Server.createMockToken()
		val createRequest = FormsTestdata.newFormRequest()
		val newForm = testFormsApi.createForm(createRequest, authToken)
			.assertSuccess()
			.body
		val formPath = newForm.path!!
		val formRevision = newForm.revision!!

		val publishAuthToken = null
		val errorBody = testFormsApi.publishForm(formPath, formRevision, publishAuthToken)
			.assertHttpStatus(HttpStatus.UNAUTHORIZED)
			.errorBody

		assertEquals("Unauthorized", errorBody.errorMessage)
	}

	@Test
	fun testGetPublishedFormsWhenNoFormsHaveBeenPublished() {
		val publishedForms = testFormsApi.getPublishedForms()
			.assertSuccess()
			.body

		assertEquals(0, publishedForms.size)
	}

	@Test
	fun testGetPublishedForms() {
		val authToken = mockOAuth2Server.createMockToken()
		val createRequest1 = FormsTestdata.newFormRequest(skjemanummer = "NAV 24-12.03")
		val newForm1 = testFormsApi.createForm(createRequest1, authToken)
			.assertSuccess()
			.body
		val formPath1 = newForm1.path!!
		val formRevision1 = newForm1.revision!!
		testFormsApi.publishForm(formPath1, formRevision1, authToken)
			.assertSuccess()

		val createRequest2 = FormsTestdata.newFormRequest(skjemanummer = "NAV 12-45.04B")
		val newForm2 = testFormsApi.createForm(createRequest2, authToken)
			.assertSuccess()
			.body
		val formPath2 = newForm2.path!!
		val formRevision2 = newForm2.revision!!
		testFormsApi.publishForm(formPath2, formRevision2, authToken)
			.assertSuccess()

		val publishedForms = testFormsApi.getPublishedForms()
			.assertSuccess()
			.body

		assertEquals(2, publishedForms.size)
	}

	@Test
	fun testFetchingFormAfterPublishAndUpdate() {
		val authToken = mockOAuth2Server.createMockToken()
		val form = testFormsApi.createForm(FormsTestdata.newFormRequest(), authToken)
			.assertSuccess().body

		val formPath = form.path!!
		testFormsApi.publishForm(formPath, form.revision!!, authToken, LanguageCode.entries)
			.assertSuccess()

		val titleUpdated = "Oppdatert skjematittel"
		val updateFormResponse = testFormsApi.updateForm(formPath, 1, FormsTestdata.updateFormRequest(titleUpdated), authToken)
			.assertSuccess()
		assertEquals(titleUpdated, updateFormResponse.body.title)
		assertNotNull(updateFormResponse.body.publishedAt)

		val getFormResponse = testFormsApi.getForm(formPath).assertSuccess()
		assertEquals(titleUpdated, getFormResponse.body.title)
		assertNotNull(getFormResponse.body.publishedAt)

		val getFormsResponse = testFormsApi.getForms().assertSuccess()
		val correctForm = getFormsResponse.body.find { it.path == formPath }
		assertEquals(titleUpdated, correctForm?.title)
		assertNotNull(correctForm?.publishedAt)

	}

}
