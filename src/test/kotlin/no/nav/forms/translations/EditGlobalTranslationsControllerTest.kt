package no.nav.forms.translations

import no.nav.forms.ApplicationTest
import no.nav.forms.model.*
import no.nav.forms.testutils.MOCK_USER_GROUP_ID
import no.nav.forms.testutils.createMockToken
import no.nav.forms.utils.LanguageCode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode


class EditGlobalTranslationsControllerTest : ApplicationTest() {

	@Test
	fun testPostGlobalTranslation() {
		val authToken = mockOAuth2Server.createMockToken()
		val request = NewGlobalTranslationRequest(
			key = "Fornavn",
			tag = "skjematekster",
			nb = "Fornavn",
			nn = "Fornamn"
		)
		val response = testFormsApi.createGlobalTranslation(request, authToken)
		assertTrue(response.statusCode.is2xxSuccessful)
		response.body as GlobalTranslationDto
		assertEquals(request.key, response.body.key)
		assertEquals(request.nb, response.body.nb)
		assertEquals(request.nn, response.body.nn)
		assertNull(response.body.en)
		assertEquals(1, response.body.revision)
	}

	@Test
	fun testDuplicatePostGlobalTranslationIsRejected() {
		val authToken = mockOAuth2Server.createMockToken()
		val request = NewGlobalTranslationRequest(
			key = "Fornavn",
			tag = "skjematekster",
			nb = "Fornavn",
			nn = "Fornamn"
		)
		val response = testFormsApi.createGlobalTranslation(request, authToken)
		assertTrue(response.statusCode.is2xxSuccessful)
		val responseDuplicate = testFormsApi.createGlobalTranslation(request, authToken)
		assertTrue(responseDuplicate.statusCode.is4xxClientError)
	}

	@Test
	fun onlyAdminUsersAreAllowedToCreateGlobalTranslation() {
		val tokenNotAdmin = mockOAuth2Server.createMockToken(groups = listOf(MOCK_USER_GROUP_ID))
		val response = testFormsApi.createGlobalTranslation(
			NewGlobalTranslationRequest(
				key = "Fornavn",
				tag = "skjematekster",
				nb = "Fornavn"
			),
			tokenNotAdmin
		)
		assertEquals(HttpStatusCode.valueOf(403), response.statusCode)
		response.body as ErrorResponseDto
		assertEquals("Forbidden", response.body.errorMessage)
	}

	@Test
	fun testChangeOfGlobalTranslation() {
		val adminToken = mockOAuth2Server.createMockToken()

		val createRequest = NewGlobalTranslationRequest(
			key = "Fornavn",
			tag = "skjematekster",
			nb = "Fornavn",
			nn = "Fornamn"
		)
		val postResponse = testFormsApi.createGlobalTranslation(createRequest, adminToken)
		assertTrue(postResponse.statusCode.is2xxSuccessful)
		postResponse.body as GlobalTranslationDto

		val translationBody = postResponse.body
		assertEquals(1, translationBody.revision)

		val putRequest = UpdateGlobalTranslationRequest(
			nb = "Fornavn",
			nn = "Fornamn",
			en = "Surname"
		)
		val putResponse =
			testFormsApi.putGlobalTranslation(translationBody.id, translationBody.revision!!, putRequest, adminToken)
		assertTrue(putResponse.statusCode.is2xxSuccessful)
		val updatedTranslation = putResponse.body as GlobalTranslationDto
		assertEquals(2, updatedTranslation.revision)
	}

	@Test
	fun testConflictWhenUpdatingGlobalTranslationOnSameRevision() {
		val adminToken = mockOAuth2Server.createMockToken()

		val createRequest = NewGlobalTranslationRequest(
			key = "Fornavn",
			tag = "skjematekster",
			nb = "Fornavn",
			nn = "Fornamn"
		)
		val postResponse = testFormsApi.createGlobalTranslation(createRequest, adminToken)
		assertTrue(postResponse.statusCode.is2xxSuccessful)
		postResponse.body as GlobalTranslationDto

		val translationBody = postResponse.body
		val revision1 = translationBody.revision!!
		assertEquals(1, revision1)

		val firstPutResponse = testFormsApi.putGlobalTranslation(
			translationBody.id, revision1, UpdateGlobalTranslationRequest(
				nb = "Fornavn",
				nn = "Fornamn",
				en = "Surname"
			), adminToken
		)
		assertTrue(firstPutResponse.statusCode.is2xxSuccessful)
		val updatedTranslation = firstPutResponse.body as GlobalTranslationDto
		assertEquals(2, updatedTranslation.revision)

		val secondUpdateResponse = testFormsApi.putGlobalTranslation(
			translationBody.id, revision1, UpdateGlobalTranslationRequest(
				nb = "Fornavn",
				nn = "Feil oversettelse",
				en = "Wrong translation"
			), adminToken
		)
		assertTrue(secondUpdateResponse.statusCode.is4xxClientError)
		val errorResponseDto = secondUpdateResponse.body as ErrorResponseDto
		assertEquals("Conflict", errorResponseDto.errorMessage)

		val latestRevisionsResponse = testFormsApi.getGlobalTranslations()
		assertEquals(1, latestRevisionsResponse.body!!.size)

		val body = latestRevisionsResponse.body
		assertEquals("Fornamn", body[0].nn)
		assertEquals("Surname", body[0].en)
	}

	@Test
	fun testUpdatingGlobalTranslationOnErroneousRevision() {
		val adminToken = mockOAuth2Server.createMockToken()

		val postResponse = testFormsApi.createGlobalTranslation(
			NewGlobalTranslationRequest(
				key = "Ja",
				tag = "skjematekster",
				nb = "Ja",
			),
			adminToken
		)
		assertTrue(postResponse.statusCode.is2xxSuccessful)
		postResponse.body as GlobalTranslationDto
		assertEquals(1, postResponse.body.revision)

		val firstPutResponse = testFormsApi.putGlobalTranslation(
			postResponse.body.id, 2,
			UpdateGlobalTranslationRequest(
				nb = "Ja",
				en = "Yes"
			),
			adminToken
		)
		assertTrue(firstPutResponse.statusCode.is4xxClientError)
		firstPutResponse.body as ErrorResponseDto
		assertEquals("Invalid revision: 2", firstPutResponse.body.errorMessage)

		val globalTranslationsResponse = testFormsApi.getGlobalTranslations()
		assertTrue(globalTranslationsResponse.statusCode.is2xxSuccessful)
		assertEquals(1, globalTranslationsResponse.body!!.size)
		assertEquals("Ja", globalTranslationsResponse.body[0].nb)
		assertNull(globalTranslationsResponse.body[0].en)
	}

	@Test
	fun testGetLatestRevisions() {
		val authToken = mockOAuth2Server.createMockToken()

		val createFornavnResponse = testFormsApi.createGlobalTranslation(
			NewGlobalTranslationRequest(
				key = "Fornavn",
				tag = "skjematekster",
				nb = "Fornavn"
			), authToken
		)
		assertTrue(createFornavnResponse.statusCode.is2xxSuccessful)
		createFornavnResponse.body as GlobalTranslationDto

		val createEtternavnResponse = testFormsApi.createGlobalTranslation(
			NewGlobalTranslationRequest(
				key = "Etternavn",
				tag = "skjematekster",
				nb = "Etternavn"
			), authToken
		)
		assertTrue(createEtternavnResponse.statusCode.is2xxSuccessful)
		createEtternavnResponse.body as GlobalTranslationDto

		val updateFornavnResponse = testFormsApi.putGlobalTranslation(
			createFornavnResponse.body.id,
			createFornavnResponse.body.revision!!,
			UpdateGlobalTranslationRequest(
				nb = "Fornavn",
				nn = "Fornamn",
				en = "Given name"
			), authToken
		)
		assertTrue(updateFornavnResponse.statusCode.is2xxSuccessful)

		val updateEtternavnResponse = testFormsApi.putGlobalTranslation(
			createEtternavnResponse.body.id,
			createEtternavnResponse.body.revision!!,
			UpdateGlobalTranslationRequest(
				nb = "Etternavn",
				nn = "Etternamn",
				en = "Surname"
			), authToken
		)
		assertTrue(updateEtternavnResponse.statusCode.is2xxSuccessful)

		val latestRevisionsResponse = testFormsApi.getGlobalTranslations()
		assertEquals(2, latestRevisionsResponse.body!!.size)

		val givenNameLatestRevision =
			latestRevisionsResponse.body.firstOrNull { it.key == createFornavnResponse.body.key }
		val surnameLatestRevision =
			latestRevisionsResponse.body.firstOrNull { it.key == createEtternavnResponse.body.key }

		assertNotNull(givenNameLatestRevision)
		assertNotNull(surnameLatestRevision)

		assertEquals("Given name", givenNameLatestRevision!!.en)
		assertEquals("Surname", surnameLatestRevision!!.en)
	}

	@Test
	fun testDeleteOk() {
		val authToken = mockOAuth2Server.createMockToken()
		val createRequest = NewGlobalTranslationRequest(
			key = "Fornavn",
			tag = "skjematekster",
		)
		val createResponse = testFormsApi.createGlobalTranslation(createRequest, authToken)
		assertTrue(createResponse.statusCode.is2xxSuccessful)
		createResponse.body as GlobalTranslationDto

		val deleteResponse = testFormsApi.deleteGlobalTranslation(createResponse.body.id, authToken)
		assertTrue(deleteResponse.statusCode.is2xxSuccessful)

		val getResponse = testFormsApi.getGlobalTranslations()
		assertTrue(getResponse.statusCode.is2xxSuccessful)
		val translation = getResponse.body!!.firstOrNull { it.key == createResponse.body.key }
		assertNull(translation)
		assertEquals(0, getResponse.body.size)
	}

	@Test
	fun testDeleteFailsWhenNoToken() {
		val authToken = mockOAuth2Server.createMockToken()
		val request = NewGlobalTranslationRequest(
			key = "Fornavn",
			tag = "skjematekster",
		)
		val createResponse = testFormsApi.createGlobalTranslation(request, authToken)
		assertTrue(createResponse.statusCode.is2xxSuccessful)
		createResponse.body as GlobalTranslationDto

		val deleteResponse = testFormsApi.deleteGlobalTranslation(createResponse.body.id)
		assertEquals(HttpStatus.UNAUTHORIZED.value(), deleteResponse.statusCode.value())
	}

	@Test
	fun testDeleteFailsWhenNoAdminToken() {
		val adminAuthToken = mockOAuth2Server.createMockToken()
		val userAuthToken = mockOAuth2Server.createMockToken(groups = listOf(MOCK_USER_GROUP_ID))
		val request = NewGlobalTranslationRequest(
			key = "Fornavn",
			tag = "skjematekster",
		)
		val createResponse = testFormsApi.createGlobalTranslation(request, adminAuthToken)
		assertTrue(createResponse.statusCode.is2xxSuccessful)
		createResponse.body as GlobalTranslationDto

		val deleteResponse = testFormsApi.deleteGlobalTranslation(createResponse.body.id, userAuthToken)
		assertEquals(HttpStatus.FORBIDDEN.value(), deleteResponse.statusCode.value())
	}

	@Test
	fun testDeleteFailsWhenFormTranslationReferencesIt() {
		val authToken = mockOAuth2Server.createMockToken()
		val request = NewGlobalTranslationRequest(
			key = "Fornavn",
			tag = "skjematekster",
		)
		val createResponse = testFormsApi.createGlobalTranslation(request, authToken)
		assertTrue(createResponse.statusCode.is2xxSuccessful)
		createResponse.body as GlobalTranslationDto

		val createFormTranslationResponse = testFormsApi.createFormTranslation(
			"nav123456",
			NewFormTranslationRequestDto(
				"Fornavn",
				globalTranslationId = createResponse.body.id
			),
			authToken
		)
		assertTrue(createFormTranslationResponse.statusCode.is2xxSuccessful)

		val deleteResponse = testFormsApi.deleteGlobalTranslation(createResponse.body.id, authToken)
		assertFalse(deleteResponse.statusCode.is2xxSuccessful)
		assertTrue(deleteResponse.statusCode.is4xxClientError)
	}

	@Test
	fun testDeleteDoesNotFailWhenFormTranslationOnlyReferencedItInThePast() {
		val authToken = mockOAuth2Server.createMockToken()
		val request = NewGlobalTranslationRequest(
			key = "Fornavn",
			tag = "skjematekster",
		)
		val createResponse = testFormsApi.createGlobalTranslation(request, authToken)
		assertTrue(createResponse.statusCode.is2xxSuccessful)
		createResponse.body as GlobalTranslationDto

		val createFormTranslationResponse = testFormsApi.createFormTranslation(
			"nav123456",
			NewFormTranslationRequestDto(
				"Fornavn",
				globalTranslationId = createResponse.body.id
			),
			authToken
		)
		assertTrue(createFormTranslationResponse.statusCode.is2xxSuccessful)
		createFormTranslationResponse.body as FormTranslationDto
		val updateFormTranslationResponse = testFormsApi.updateFormTranslation(
			"nav123456",
			createFormTranslationResponse.body.id,
			createFormTranslationResponse.body.revision!!,
			UpdateFormTranslationRequest(
				globalTranslationId = null,
				nb = "Fornavn",
				nn = "Fornamn",
				en = "Given name"
			),
			authToken
		)
		assertTrue(updateFormTranslationResponse.statusCode.is2xxSuccessful)

		val deleteResponse = testFormsApi.deleteGlobalTranslation(createResponse.body.id, authToken)
		assertTrue(deleteResponse.statusCode.is2xxSuccessful)
	}

	@Test
	fun testUpdateOfDeletedTranslation() {
		val adminToken = mockOAuth2Server.createMockToken()

		val createRequest = NewGlobalTranslationRequest(
			key = "Fornavn",
			tag = "skjematekster",
			nb = "Fornavn",
			nn = "Fornamn"
		)
		val postResponse = testFormsApi.createGlobalTranslation(createRequest, adminToken)
		assertTrue(postResponse.statusCode.is2xxSuccessful)
		postResponse.body as GlobalTranslationDto

		val translationBody = postResponse.body
		assertEquals(1, translationBody.revision)

		val deleteResponse = testFormsApi.deleteGlobalTranslation(postResponse.body.id, adminToken)
		assertTrue(deleteResponse.statusCode.is2xxSuccessful)

		val putRequest = UpdateGlobalTranslationRequest(
			nb = "Fornavn",
			nn = "Fornamn",
			en = "Surname"
		)
		val putResponse =
			testFormsApi.putGlobalTranslation(translationBody.id, translationBody.revision!!, putRequest, adminToken)
		assertEquals(HttpStatus.NOT_FOUND.value(), putResponse.statusCode.value())
	}

	@Test
	fun testRecreateAfterDelete() {
		val authToken = mockOAuth2Server.createMockToken()
		val createRequest = NewGlobalTranslationRequest(
			key = "Fornavn",
			tag = "skjematekster",
			en = "Given name"
		)
		val createResponse = testFormsApi.createGlobalTranslation(createRequest, authToken)
		assertTrue(createResponse.statusCode.is2xxSuccessful)
		createResponse.body as GlobalTranslationDto
		assertEquals(1, createResponse.body.revision)
		assertEquals(createRequest.tag, createResponse.body.tag)
		assertEquals(createRequest.nb, createResponse.body.nb)
		assertEquals(createRequest.nn, createResponse.body.nn)
		assertEquals(createRequest.en, createResponse.body.en)

		testFormsApi.publishGlobalTranslations(authToken).isSuccess()

		val deleteResponse = testFormsApi.deleteGlobalTranslation(createResponse.body.id, authToken)
		assertTrue(deleteResponse.statusCode.is2xxSuccessful)

		val recreateRequest = createRequest.copy(nb = "Fornavn på bokmål", tag = "grensesnitt", en = null, nn = null)
		val recreateResponse = testFormsApi.createGlobalTranslation(recreateRequest, authToken)
		assertTrue(recreateResponse.statusCode.is2xxSuccessful)
		recreateResponse.body as GlobalTranslationDto
		assertEquals(2, recreateResponse.body.revision)
		assertEquals(recreateRequest.tag, recreateResponse.body.tag)
		assertEquals(recreateRequest.nb, recreateResponse.body.nb)
		assertEquals(recreateRequest.nn, recreateResponse.body.nn)
		assertEquals(recreateRequest.en, recreateResponse.body.en)
		assertNotNull(recreateResponse.body.publishedAt)
		assertNotNull(recreateResponse.body.publishedBy)

		val getResponse = testFormsApi.getGlobalTranslations()
		assertTrue(getResponse.statusCode.is2xxSuccessful)
		val translation = getResponse.body!!.firstOrNull { it.key == createResponse.body.key }
		assertNotNull(translation)
		assertEquals(recreateRequest.nb, translation!!.nb)
		assertEquals(recreateRequest.nn, translation.nn)
		assertEquals(recreateRequest.en, translation.en)
		assertEquals(recreateRequest.tag, translation.tag)
		assertNotNull(translation.publishedAt)
		assertNotNull(translation.publishedBy)
	}

	@Test
	fun testPublishedAtIsCorrectAfterUpdate() {
		val authToken = mockOAuth2Server.createMockToken()
		val createRequest = NewGlobalTranslationRequest(
			key = "Fornavn",
			tag = "skjematekster",
			en = "Given name"
		)
		val createResponse = testFormsApi.createGlobalTranslation(createRequest, authToken)
		assertTrue(createResponse.statusCode.is2xxSuccessful)
		createResponse.body as GlobalTranslationDto

		// First publish
		testFormsApi.publishGlobalTranslations(authToken).isSuccess()
		val publication1 = testFormsApi.getGlobalTranslationPublication(listOf(LanguageCode.EN.value))
			.successBody<PublishedGlobalTranslationsDto>()

		// Update the translation after publish
		val putRequest1 = UpdateGlobalTranslationRequest(
			nb = "Fornavn",
			nn = "Fornamn",
			en = "Given name with change"
		)
		val putResponse1 = testFormsApi
			.putGlobalTranslation(createResponse.body.id, createResponse.body.revision!!, putRequest1, authToken)
			.successBody<GlobalTranslationDto>()
		assertEquals(publication1.publishedAt, putResponse1.publishedAt)

		val allGlobalTranslations = fetchGlobalTranslations()

		// Verify updated translation data with published information
		val globalTranslation = allGlobalTranslations.firstOrNull()
		assertNotNull(globalTranslation)
		assertEquals(publication1.publishedAt, globalTranslation?.publishedAt)
		assertNotNull(globalTranslation?.publishedBy)
		assertTrue(
			globalTranslation?.changedAt?.isAfter(publication1.publishedAt) == true,
			"ChangedAt timestamp should be after publishedAt"
		)

		// Update the translation after first publish
		val putRequest2 = UpdateGlobalTranslationRequest(
			nb = "Fornavn",
			nn = "Fornamn",
			en = "Given name with change 2"
		)
		val putResponse2 = testFormsApi
			.putGlobalTranslation(putResponse1.id, putResponse1.revision!!, putRequest2, authToken)
			.successBody<GlobalTranslationDto>()
		assertEquals(publication1.publishedAt, putResponse2.publishedAt)

		// Second publish
		testFormsApi.publishGlobalTranslations(authToken).isSuccess()
		val publication2 = testFormsApi.getGlobalTranslationPublication(listOf(LanguageCode.EN.value))
			.successBody<PublishedGlobalTranslationsDto>()

		val allGlobalTranslationsFinal = fetchGlobalTranslations()
		val globalTranslationFinal = allGlobalTranslationsFinal.firstOrNull()
		assertNotNull(globalTranslationFinal)
		assertEquals(publication2.publishedAt, globalTranslationFinal?.publishedAt)
	}

	@Test
	fun testPublishedAtIsCorrectAfterTwoPublications() {
		val authToken = mockOAuth2Server.createMockToken()
		val createRequest = NewGlobalTranslationRequest(
			key = "Fornavn",
			tag = "skjematekster",
			en = "Given name"
		)
		val createResponse = testFormsApi.createGlobalTranslation(createRequest, authToken)
		assertTrue(createResponse.statusCode.is2xxSuccessful)
		createResponse.body as GlobalTranslationDto

		testFormsApi.publishGlobalTranslations(authToken).isSuccess()

		val publication1 = testFormsApi.getGlobalTranslationPublication().successBody<PublishedGlobalTranslationsDto>()

		val allGlobalTranslations1 = fetchGlobalTranslations()
		val globalTranslationAfterFirstPublication = allGlobalTranslations1.firstOrNull()
		assertNotNull(globalTranslationAfterFirstPublication)
		assertEquals(
			publication1.publishedAt,
			globalTranslationAfterFirstPublication?.publishedAt,
			"Should still show published timestamp"
		)

		val publish2 = testFormsApi.publishGlobalTranslations(authToken)
		assertTrue(publish2.statusCode.is2xxSuccessful)

		val publication2 = testFormsApi.getGlobalTranslationPublication().successBody<PublishedGlobalTranslationsDto>()

		val allGlobalTranslations2 = fetchGlobalTranslations()
		val globalTranslationAfterSecondPublication = allGlobalTranslations2.firstOrNull()
		assertNotNull(globalTranslationAfterSecondPublication)
		assertEquals(
			publication2.publishedAt,
			globalTranslationAfterSecondPublication?.publishedAt,
			"Should still show published timestamp"
		)
	}

	private fun fetchGlobalTranslations(): List<GlobalTranslationDto> {
		val globalTranslationsResponse = testFormsApi.getGlobalTranslations()
		assertTrue(globalTranslationsResponse.statusCode.is2xxSuccessful)
		return globalTranslationsResponse.body as List<GlobalTranslationDto>
	}
}
