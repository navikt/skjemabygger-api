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
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.util.MultiValueMap


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
		val response = sendNewGlobalTranslationRequest(request, authToken)
		assertTrue(response.statusCode.is2xxSuccessful)
		assertEquals(request.key, response.body?.key)
		assertEquals(request.nb, response.body?.nb)
		assertEquals(request.nn, response.body?.nn)
		assertNull(response.body?.en)
		assertEquals(1, response.body?.revision)
	}

	@Test
	fun onlyAdminUsersAreAllowedToCreateGlobalTranslation() {
		val tokenNotAdmin = mockOAuth2Server.createMockToken(groups = listOf(MOCK_USER_GROUP_ID))
		val request = NewGlobalTranslationRequest(
			key = "Fornavn",
			tag = "skjematekster",
			nb = "Fornavn",
			nn = "Fornamn"
		)
		val response = restTemplate.exchange(
			globalTranslationBaseUrl,
			HttpMethod.POST,
			HttpEntity(
				request,
				httpHeaders(tokenNotAdmin)
			),
			String::class.java
		)
		assertEquals(HttpStatusCode.valueOf(403), response.statusCode)
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
		val postResponse = sendNewGlobalTranslationRequest(createRequest, adminToken)
		assertTrue(postResponse.statusCode.is2xxSuccessful)

		val translationBody = postResponse.body!!
		assertEquals(1, translationBody.revision)

		val putRequest = UpdateGlobalTranslationRequest(
			nb = "Fornavn",
			nn = "Fornamn",
			en = "Surname"
		)
		val putResponse = sendUpdateGlobalTranslationRequest(translationBody.id, translationBody.revision!!, putRequest, adminToken)
		val updatedTranslation = putResponse.body!! as GlobalTranslation
		assertTrue(putResponse.statusCode.is2xxSuccessful)
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
		val postResponse = sendNewGlobalTranslationRequest(createRequest, adminToken)
		assertTrue(postResponse.statusCode.is2xxSuccessful)

		val translationBody = postResponse.body!!
		val revision1 = translationBody.revision!!
		assertEquals(1, revision1)

		val firstPutResponse = sendUpdateGlobalTranslationRequest(
			translationBody.id, revision1, UpdateGlobalTranslationRequest(
				nb = "Fornavn",
				nn = "Fornamn",
				en = "Surname"
			), adminToken
		)
		assertTrue(firstPutResponse.statusCode.is2xxSuccessful)
		val updatedTranslation = firstPutResponse.body!! as GlobalTranslation
		assertEquals(2, updatedTranslation.revision)

		val secondUpdateResponse = sendUpdateGlobalTranslationRequest(
			translationBody.id, revision1, UpdateGlobalTranslationRequest(
				nb = "Fornavn",
				nn = "Feil oversettelse",
				en = "Wrong translation"
			), adminToken, expectFailure = true
		)
		assertTrue(secondUpdateResponse.statusCode.is4xxClientError)
		val errorResponseDto = secondUpdateResponse.body!! as ErrorResponseDto
		assertEquals("Conflict", errorResponseDto.errorMessage)

		val latestRevisionsResponse = fetchGlobalTranslations()
		assertEquals(1, latestRevisionsResponse.body?.size)

		val body = latestRevisionsResponse.body!!
		assertEquals("Fornamn", body[0].nn)
		assertEquals("Surname", body[0].en)
	}

	@Test
	fun testGetLatestRevisions() {
		val authToken = mockOAuth2Server.createMockToken()

		val createFornavnResponse = sendNewGlobalTranslationRequest(
			NewGlobalTranslationRequest(
				key = "Fornavn",
				tag = "skjematekster",
				nb = "Fornavn"
			), authToken
		)
		assertTrue(createFornavnResponse.statusCode.is2xxSuccessful)

		val createEtternavnResponse = sendNewGlobalTranslationRequest(
			NewGlobalTranslationRequest(
				key = "Etternavn",
				tag = "skjematekster",
				nb = "Etternavn"
			), authToken
		)
		assertTrue(createEtternavnResponse.statusCode.is2xxSuccessful)

		val updateFornavnResponse = sendUpdateGlobalTranslationRequest(
			createFornavnResponse.body?.id!!,
			createFornavnResponse.body?.revision!!,
			UpdateGlobalTranslationRequest(
				nb = "Fornavn",
				nn = "Fornamn",
				en = "Given name"
			), authToken
		)
		assertTrue(updateFornavnResponse.statusCode.is2xxSuccessful)

		val updateEtternavnResponse = sendUpdateGlobalTranslationRequest(
			createEtternavnResponse.body?.id!!,
			createEtternavnResponse.body?.revision!!,
			UpdateGlobalTranslationRequest(
				nb = "Etternavn",
				nn = "Etternamn",
				en = "Surname"
			), authToken
		)
		assertTrue(updateEtternavnResponse.statusCode.is2xxSuccessful)

		val latestRevisionsResponse = fetchGlobalTranslations()
		assertEquals(2, latestRevisionsResponse.body?.size)

		val givenNameLatestRevision =
			latestRevisionsResponse.body?.firstOrNull { it.key == createFornavnResponse.body?.key }
		val surnameLatestRevision =
			latestRevisionsResponse.body?.firstOrNull { it.key == createEtternavnResponse.body?.key }

		assertNotNull(givenNameLatestRevision)
		assertNotNull(surnameLatestRevision)

		assertEquals("Given name", givenNameLatestRevision!!.en)
		assertEquals("Surname", surnameLatestRevision!!.en)
	}

	private fun fetchGlobalTranslations(): ResponseEntity<List<GlobalTranslation>> {
		val responseType = object : ParameterizedTypeReference<List<GlobalTranslation>>() {}
		val latestRevisionsResponse = restTemplate.exchange(globalTranslationBaseUrl, HttpMethod.GET, null, responseType)
		return latestRevisionsResponse
	}

	private fun sendNewGlobalTranslationRequest(
		request: NewGlobalTranslationRequest,
		authToken: String,
	): ResponseEntity<GlobalTranslation> {
		val response = restTemplate.exchange(
			globalTranslationBaseUrl,
			HttpMethod.POST,
			HttpEntity(request, httpHeaders(authToken)),
			GlobalTranslation::class.java
		)
		return response
	}

	private fun sendUpdateGlobalTranslationRequest(
		id: Long,
		revision: Int,
		request: UpdateGlobalTranslationRequest,
		authToken: String,
		expectFailure: Boolean = false
	): ResponseEntity<out Any> {
		val headers = mapOf("Formsapi-Entity-Revision" to revision.toString())
		val response = restTemplate.exchange(
			"$globalTranslationBaseUrl/$id",
			HttpMethod.PUT,
			HttpEntity(request, httpHeaders(authToken, headers)),
			if (expectFailure) ErrorResponseDto::class.java else GlobalTranslation::class.java
		)
		return response
	}

	private fun httpHeaders(token: String, additionalHeaders: Map<String, String> = emptyMap()): MultiValueMap<String, String> {
		val headers = HttpHeaders()
		headers.add("Authorization", "Bearer $token")
		additionalHeaders.forEach { headers.add(it.key, it.value) }
		return headers
	}

}
