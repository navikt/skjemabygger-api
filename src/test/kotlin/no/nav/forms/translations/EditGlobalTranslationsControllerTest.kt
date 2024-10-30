package no.nav.forms.translations

import no.nav.forms.ApplicationTest
import no.nav.forms.model.ErrorResponseDto
import no.nav.forms.model.GlobalTranslation
import no.nav.forms.model.NewGlobalTranslationRequest
import no.nav.forms.model.UpdateGlobalTranslationRequest
import no.nav.forms.testutils.MOCK_USER_GROUP_ID
import no.nav.forms.testutils.createMockToken
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatusCode


class EditGlobalTranslationsControllerTest : ApplicationTest() {

	val globalTranslationBaseUrl = "$baseUrl/v1/global-translations"

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
		response.body as GlobalTranslation
		assertEquals(request.key, response.body.key)
		assertEquals(request.nb, response.body.nb)
		assertEquals(request.nn, response.body.nn)
		assertNull(response.body.en)
		assertEquals(1, response.body.revision)
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
		val key = "Fornavn"

		val createRequest = NewGlobalTranslationRequest(
			key = key,
			tag = "skjematekster",
			nb = "Fornavn",
			nn = "Fornamn"
		)
		val postResponse = testFormsApi.createGlobalTranslation(createRequest, adminToken)
		assertTrue(postResponse.statusCode.is2xxSuccessful)
		postResponse.body as GlobalTranslation

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
		val updatedTranslation = putResponse.body as GlobalTranslation
		assertEquals(2, updatedTranslation.revision)
	}

	@Test
	fun testConflictWhenUpdatingGlobalTranslationOnSameRevision() {
		val adminToken = mockOAuth2Server.createMockToken()
		val key = "Fornavn"

		val createRequest = NewGlobalTranslationRequest(
			key = key,
			tag = "skjematekster",
			nb = "Fornavn",
			nn = "Fornamn"
		)
		val postResponse = testFormsApi.createGlobalTranslation(createRequest, adminToken)
		assertTrue(postResponse.statusCode.is2xxSuccessful)
		postResponse.body as GlobalTranslation

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
		val updatedTranslation = firstPutResponse.body as GlobalTranslation
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
		assertEquals(1, latestRevisionsResponse.body.size)

		val body = latestRevisionsResponse.body
		assertEquals("Fornamn", body[0].nn)
		assertEquals("Surname", body[0].en)
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
		createFornavnResponse.body as GlobalTranslation

		val createEtternavnResponse = testFormsApi.createGlobalTranslation(
			NewGlobalTranslationRequest(
				key = "Etternavn",
				tag = "skjematekster",
				nb = "Etternavn"
			), authToken
		)
		assertTrue(createEtternavnResponse.statusCode.is2xxSuccessful)
		createEtternavnResponse.body as GlobalTranslation

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
		assertEquals(2, latestRevisionsResponse.body.size)

		val givenNameLatestRevision =
			latestRevisionsResponse.body.firstOrNull { it.key == createFornavnResponse.body.key }
		val surnameLatestRevision =
			latestRevisionsResponse.body.firstOrNull { it.key == createEtternavnResponse.body.key }

		assertNotNull(givenNameLatestRevision)
		assertNotNull(surnameLatestRevision)

		assertEquals("Given name", givenNameLatestRevision!!.en)
		assertEquals("Surname", surnameLatestRevision!!.en)
	}

}
