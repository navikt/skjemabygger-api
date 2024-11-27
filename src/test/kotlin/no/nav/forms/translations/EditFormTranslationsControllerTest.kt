package no.nav.forms.translations

import no.nav.forms.ApplicationTest
import no.nav.forms.exceptions.db.DbError
import no.nav.forms.model.*
import no.nav.forms.testutils.createMockToken
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class EditFormTranslationsControllerTest : ApplicationTest() {

	@Test
	fun testChangeOfFormTranslation() {
		val formPath = "nav123456"
		val authToken = mockOAuth2Server.createMockToken()

		val createRequest = NewFormTranslationRequestDto(
			key = "Opplysninger om søker",
			nb = "Opplysninger om søker",
			nn = "Opppplysningar om søkjaren",
		)
		val createResponse = testFormsApi.createFormTranslation(formPath, createRequest, authToken)
		assertTrue(createResponse.statusCode.is2xxSuccessful)
		createResponse.body as FormTranslationDto

		assertEquals(createRequest.key, createResponse.body.key)
		assertEquals(1, createResponse.body.revision)
		assertEquals(createRequest.nb, createResponse.body.nb)
		assertEquals(createRequest.nn, createResponse.body.nn)
		assertNull(createResponse.body.en)

		val updateRequest = UpdateFormTranslationRequest(
			nb = createResponse.body.nb,
			nn = "Opplysningar om søkjaren",
			en = "Information about the applicant",
		)
		val updateResponse = testFormsApi.updateFormTranslation(
			formPath,
			createResponse.body.id,
			createResponse.body.revision!!,
			updateRequest,
			authToken,
		)
		assertTrue(updateResponse.statusCode.is2xxSuccessful)
		updateResponse.body as FormTranslationDto

		assertEquals(createRequest.key, updateResponse.body.key)
		assertEquals(2, updateResponse.body.revision)
		assertEquals(updateRequest.nb, updateResponse.body.nb)
		assertEquals(updateRequest.nn, updateResponse.body.nn)
		assertEquals(updateRequest.en, updateResponse.body.en)
	}

	@Test
	fun failsOnCreateDuplicateFormTranslation() {
		val formPath = "nav123456"
		val authToken = mockOAuth2Server.createMockToken()

		val createRequest = NewFormTranslationRequestDto(
			key = "Opplysninger om søker",
			nb = "Opplysninger om søker",
			nn = "Opppplysningar om søkjaren",
		)
		val createResponse1 = testFormsApi.createFormTranslation(formPath, createRequest, authToken)
		assertTrue(createResponse1.statusCode.is2xxSuccessful)

		val createResponse2 = testFormsApi.createFormTranslation(formPath, createRequest, authToken)
		assertEquals(HttpStatus.CONFLICT.value(), createResponse2.statusCode.value())
	}

	@Test
	fun failsOnEditAlreadyEditedFormTranslation() {
		val formPath = "nav123456"
		val authToken = mockOAuth2Server.createMockToken()

		val createRequest = NewFormTranslationRequestDto(
			key = "Tester",
			nb = "Tester",
		)
		val createResponse = testFormsApi.createFormTranslation(formPath, createRequest, authToken)
		assertTrue(createResponse.statusCode.is2xxSuccessful)
		createResponse.body as FormTranslationDto
		val formTranslationId = createResponse.body.id

		assertEquals(1, createResponse.body.revision)

		val updateRequest = UpdateFormTranslationRequest(
			nb = createResponse.body.nb,
			nn = "Testar",
		)
		val updateResponse1 = testFormsApi.updateFormTranslation(
			formPath,
			formTranslationId,
			1,
			updateRequest,
			authToken,
		)
		assertTrue(updateResponse1.statusCode.is2xxSuccessful)

		val updateResponse2 = testFormsApi.updateFormTranslation(
			formPath,
			formTranslationId,
			1,
			updateRequest,
			authToken,
		)
		assertEquals(HttpStatus.CONFLICT.value(), updateResponse2.statusCode.value())
	}

	@Test
	fun failsDueToFormPathMismatchOnUpdate() {
		val nav111111 = "nav111111"
		val nav222222 = "nav222222"
		val authToken = mockOAuth2Server.createMockToken()

		val createRequest = NewFormTranslationRequestDto(
			key = "Tester",
			nb = "Tester",
		)
		val createResponse1 = testFormsApi.createFormTranslation(nav111111, createRequest, authToken)
		assertTrue(createResponse1.statusCode.is2xxSuccessful)
		createResponse1.body as FormTranslationDto

		val createResponse2 = testFormsApi.createFormTranslation(nav222222, createRequest, authToken)
		assertTrue(createResponse2.statusCode.is2xxSuccessful)
		createResponse2.body as FormTranslationDto

		val updateRequest = UpdateFormTranslationRequest(
			nb = createResponse1.body.nb,
			nn = "Testar",
		)
		val updateResponse1 = testFormsApi.updateFormTranslation(
			nav111111,
			createResponse2.body.id,
			1,
			updateRequest,
			authToken,
		)
		assertEquals(HttpStatus.BAD_REQUEST.value(), updateResponse1.statusCode.value())
	}

	@Test
	fun testGetAllFormTranslations() {
		val formPath = "nav123456"
		val authToken = mockOAuth2Server.createMockToken()

		val createResponse1 = testFormsApi.createFormTranslation(
			formPath,
			NewFormTranslationRequestDto(
				key = "Foo",
				nb = "Foo",
			),
			authToken
		)
		assertTrue(createResponse1.statusCode.is2xxSuccessful)
		createResponse1.body as FormTranslationDto

		val createResponse2 = testFormsApi.createFormTranslation(
			formPath,
			NewFormTranslationRequestDto(
				key = "Bar",
				nb = "Bar",
			),
			authToken
		)
		assertTrue(createResponse2.statusCode.is2xxSuccessful)

		val createResponse3 = testFormsApi.createFormTranslation(
			formPath,
			NewFormTranslationRequestDto(
				key = "Foobar",
				nb = "Foobar",
			),
			authToken
		)
		assertTrue(createResponse3.statusCode.is2xxSuccessful)

		val putResponse1 = testFormsApi.updateFormTranslation(
			formPath,
			createResponse1.body.id,
			createResponse1.body.revision!!,
			UpdateFormTranslationRequest(
				nb = createResponse1.body.nb,
				nn = "Nynorskfoo"
			),
			authToken
		)
		assertTrue(putResponse1.statusCode.is2xxSuccessful)

		val response = testFormsApi.getFormTranslations(formPath)
		assertTrue(response.statusCode.is2xxSuccessful)
		response.body as List<*>

		assertEquals(3, response.body.size)
	}

	@Test
	fun testCreateFormTranslationWithGlobalValue() {
		val formPath = "nav123456"
		val authToken = mockOAuth2Server.createMockToken()

		val globalTranslationResponse = testFormsApi.createGlobalTranslation(
			NewGlobalTranslationRequest("Ja", "skjematekster", "Ja", "Ja", "Yes"),
			authToken
		)
		assertTrue(globalTranslationResponse.statusCode.is2xxSuccessful)
		globalTranslationResponse.body as GlobalTranslationDto
		assertNotNull(globalTranslationResponse.body.id)

		val createResponse1 = testFormsApi.createFormTranslation(
			formPath,
			NewFormTranslationRequestDto(
				key = "Ja",
				globalTranslationId = globalTranslationResponse.body.id,
			),
			authToken
		)
		assertTrue(createResponse1.statusCode.is2xxSuccessful)
		createResponse1.body as FormTranslationDto

		val response = testFormsApi.getFormTranslations(formPath)
		assertTrue(response.statusCode.is2xxSuccessful)
		response.body as List<*>
		assertEquals(1, response.body.size)
		val formTranslation = response.body.firstOrNull() as FormTranslationDto
		assertEquals(globalTranslationResponse.body.id, formTranslation.globalTranslationId)
		assertEquals(globalTranslationResponse.body.nb, formTranslation.nb)
		assertEquals(globalTranslationResponse.body.nn, formTranslation.nn)
		assertEquals(globalTranslationResponse.body.en, formTranslation.en)
	}

	@Test
	fun failsOnCreateWhenGlobalTranslationDoesNotExist() {
		val formPath = "nav123456"
		val authToken = mockOAuth2Server.createMockToken()

		val createResponse = testFormsApi.createFormTranslation(
			formPath,
			NewFormTranslationRequestDto(
				key = "Ja",
				globalTranslationId = 37,
			),
			authToken
		)
		assertTrue(createResponse.statusCode.is4xxClientError)
		createResponse.body as ErrorResponseDto

		assertEquals(HttpStatus.BAD_REQUEST.value(), createResponse.statusCode.value())
		assertEquals("Global translation not found", createResponse.body.errorMessage)
	}

	@Test
	fun failsOnCreateWhenBothGlobalTranslationIdAndTranslationExistInRequest() {
		val formPath = "nav123456"
		val authToken = mockOAuth2Server.createMockToken()

		val globalTranslationResponse = testFormsApi.createGlobalTranslation(
			NewGlobalTranslationRequest("Ja", "skjematekster", "Ja", "Ja", "Yes"),
			authToken
		)
		assertTrue(globalTranslationResponse.statusCode.is2xxSuccessful)
		globalTranslationResponse.body as GlobalTranslationDto
		assertNotNull(globalTranslationResponse.body.id)

		val createResponse = testFormsApi.createFormTranslation(
			formPath,
			NewFormTranslationRequestDto(
				key = "Ja",
				globalTranslationId = globalTranslationResponse.body.id,
				nb = "Ja",
				nn = "Ja",
				en = "Yes",
			),
			authToken
		)
		assertTrue(createResponse.statusCode.is4xxClientError)
		createResponse.body as ErrorResponseDto

		assertEquals(DbError.FORMSAPI_001.httpStatus.value(), createResponse.statusCode.value())
		assertEquals(
			DbError.FORMSAPI_001.message,
			createResponse.body.errorMessage
		)
	}

	@Test
	fun failsOnUpdateWhenBothGlobalTranslationAndLocalTranslationsAreProvided() {
		val formPath = "nav123456"
		val authToken = mockOAuth2Server.createMockToken()

		val createRequest = NewFormTranslationRequestDto(
			key = "Ja",
			nb = "Ja",
			nn = "Ja",
		)
		val createResponse = testFormsApi.createFormTranslation(formPath, createRequest, authToken)
		assertTrue(createResponse.statusCode.is2xxSuccessful)
		createResponse.body as FormTranslationDto

		val globalTranslationResponse = testFormsApi.createGlobalTranslation(
			NewGlobalTranslationRequest("Ja", "skjematekster", "Ja", "Ja", "Yes"),
			authToken
		)
		assertTrue(globalTranslationResponse.statusCode.is2xxSuccessful)
		globalTranslationResponse.body as GlobalTranslationDto
		assertNotNull(globalTranslationResponse.body.id)

		val updateRequest = UpdateFormTranslationRequest(
			globalTranslationId = globalTranslationResponse.body.id,
			nb = createResponse.body.nb,
			nn = "Jauda",
			en = "Yes",
		)
		val updateResponse = testFormsApi.updateFormTranslation(
			formPath,
			createResponse.body.id,
			createResponse.body.revision!!,
			updateRequest,
			authToken,
		)
		assertTrue(updateResponse.statusCode.is4xxClientError)
		updateResponse.body as ErrorResponseDto

		assertEquals(DbError.FORMSAPI_001.httpStatus.value(), updateResponse.statusCode.value())
		assertEquals(
			DbError.FORMSAPI_001.message,
			updateResponse.body.errorMessage
		)
	}

	@Test
	fun testUpdateFormTranslationAndLinkToGlobalTranslation() {
		val formPath = "nav123456"
		val authToken = mockOAuth2Server.createMockToken()

		val createRequest = NewFormTranslationRequestDto(key = "Nei", nb = "Nei", nn = "Nei")
		val createResponse = testFormsApi.createFormTranslation(formPath, createRequest, authToken)
		assertTrue(createResponse.statusCode.is2xxSuccessful)
		createResponse.body as FormTranslationDto

		val globalTranslationResponse = testFormsApi.createGlobalTranslation(
			NewGlobalTranslationRequest(key = "Ja", tag = "skjematekster", nb = "Ja", nn = "Ja", en = "Yes"),
			authToken
		)
		assertTrue(globalTranslationResponse.statusCode.is2xxSuccessful)
		globalTranslationResponse.body as GlobalTranslationDto
		assertNotNull(globalTranslationResponse.body.id)

		val updateRequest = UpdateFormTranslationRequest(
			globalTranslationId = globalTranslationResponse.body.id,
		)
		val updateResponse = testFormsApi.updateFormTranslation(
			formPath,
			createResponse.body.id,
			createResponse.body.revision!!,
			updateRequest,
			authToken,
		)
		assertTrue(updateResponse.statusCode.is2xxSuccessful)
		updateResponse.body as FormTranslationDto

		assertEquals(globalTranslationResponse.body.nb, updateResponse.body.nb)
		assertEquals(globalTranslationResponse.body.nn, updateResponse.body.nn)
		assertEquals(globalTranslationResponse.body.en, updateResponse.body.en)
	}

	@Test
	fun testDeleteFormTranslationWithoutAuthToken() {
		val formPath = "nav123456"
		val authToken = mockOAuth2Server.createMockToken()

		val createRequest = NewFormTranslationRequestDto(key = "Nei", nb = "Nei")
		val createResponse = testFormsApi.createFormTranslation(formPath, createRequest, authToken)
		assertTrue(createResponse.statusCode.is2xxSuccessful)
		createResponse.body as FormTranslationDto

		val deleteResponse = testFormsApi.deleteFormTranslation(formPath, createResponse.body.id)
		assertEquals(HttpStatus.UNAUTHORIZED.value(), deleteResponse.statusCode.value())
	}

	@Test
	fun testDeleteNonExistingFormTranslation() {
		val formPath = "nav123456"
		val authToken = mockOAuth2Server.createMockToken()

		val deleteResponse = testFormsApi.deleteFormTranslation(formPath, 123L, authToken)
		assertEquals(HttpStatus.NOT_FOUND.value(), deleteResponse.statusCode.value())
	}

	@Test
	fun testDeleteFormTranslation() {
		val formPath = "nav123456"
		val authToken = mockOAuth2Server.createMockToken()

		val createRequest = NewFormTranslationRequestDto(key = "Nei", nb = "Nei")
		val createResponse = testFormsApi.createFormTranslation(formPath, createRequest, authToken)
		assertTrue(createResponse.statusCode.is2xxSuccessful)
		createResponse.body as FormTranslationDto

		val deleteResponse = testFormsApi.deleteFormTranslation(formPath, createResponse.body.id, authToken)
		assertTrue(deleteResponse.statusCode.is2xxSuccessful)

		val formTranslationsResponse = testFormsApi.getFormTranslations(formPath)
		assertTrue(formTranslationsResponse.statusCode.is2xxSuccessful)
		formTranslationsResponse.body as List<*>
		assertEquals(0, formTranslationsResponse.body.size)
	}

	@Test
	fun testRecreateFormTranslationAfterDelete() {
		val formPath = "nav123456"
		val authToken = mockOAuth2Server.createMockToken()

		val createRequest1 = NewFormTranslationRequestDto(key = "Nei", nb = "Nei")
		val createResponse1 = testFormsApi.createFormTranslation(formPath, createRequest1, authToken)
		assertTrue(createResponse1.statusCode.is2xxSuccessful)
		createResponse1.body as FormTranslationDto

		val deleteResponse = testFormsApi.deleteFormTranslation(formPath, createResponse1.body.id, authToken)
		assertTrue(deleteResponse.statusCode.is2xxSuccessful)

		val createRequest2 = NewFormTranslationRequestDto(key = "Nei", nb = "Nei", en = "No")
		val createResponse2 = testFormsApi.createFormTranslation(formPath, createRequest2, authToken)
		assertTrue(createResponse2.statusCode.is2xxSuccessful)
		createResponse2.body as FormTranslationDto

		val formTranslationsResponse = testFormsApi.getFormTranslations(formPath)
		assertTrue(formTranslationsResponse.statusCode.is2xxSuccessful)
		formTranslationsResponse.body as List<*>
		assertEquals(1, formTranslationsResponse.body.size)
		val formTranslation = formTranslationsResponse.body[0] as FormTranslationDto
		assertEquals(2, formTranslation.revision)
		assertEquals(createRequest2.key, formTranslation.key)
		assertEquals(createRequest2.nb, formTranslation.nb)
		assertEquals(createRequest2.nn, formTranslation.nn)
		assertEquals(createRequest2.en, formTranslation.en)
	}

}
