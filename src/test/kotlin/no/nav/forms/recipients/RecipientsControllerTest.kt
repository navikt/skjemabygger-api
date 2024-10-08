package no.nav.forms.recipients

import no.nav.forms.ApplicationTest
import no.nav.forms.model.NewRecipientRequest
import no.nav.forms.model.RecipientDto
import no.nav.forms.model.UpdateRecipientRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.web.util.UriComponentsBuilder

class RecipientsControllerTest : ApplicationTest() {

	@Test
	fun testGetRecipients() {
		val uri = UriComponentsBuilder.fromHttpUrl("$baseUrl/v1/recipients")
			.build()
			.toUri()
		val responseType = object : ParameterizedTypeReference<List<RecipientDto>>() {}
		val response = restTemplate.exchange(
			uri,
			HttpMethod.GET,
			HttpEntity(responseType),
			responseType
		)
		assertEquals(2, response.body?.size)
	}

	@Test
	fun testPostRecipient() {
		val uri = UriComponentsBuilder.fromHttpUrl("$baseUrl/v1/recipients")
			.build()
			.toUri()
		val responseType = object : ParameterizedTypeReference<RecipientDto>() {}
		val requestBody = NewRecipientRequest(
			"NAV Skanning",
			"Postboks 1",
			"0591",
			"Oslo",
		)
		val response = restTemplate.exchange(
			uri,
			HttpMethod.POST,
			HttpEntity(requestBody),
			responseType
		)
		assertNotNull(response.body?.recipientId)
		assertEquals(requestBody.name, response.body?.name)
	}

	@Test
	fun testPutRecipient() {
		val recipientId = "1"
		val uri = UriComponentsBuilder.fromHttpUrl("$baseUrl/v1/recipients/$recipientId")
			.build()
			.toUri()
		val responseType = object : ParameterizedTypeReference<RecipientDto>() {}
		val requestBody = UpdateRecipientRequest(
			"NAV Nytt navn",
			"Postboks 99",
			"6425",
			"Molde",
		)
		val response = restTemplate.exchange(
			uri,
			HttpMethod.PUT,
			HttpEntity(requestBody),
			responseType
		)
		assertNotNull(response.body?.recipientId)
		assertEquals(requestBody.name, response.body?.name)
		assertEquals(requestBody.poBoxAddress, response.body?.poBoxAddress)
		assertEquals(requestBody.postalCode, response.body?.postalCode)
		assertEquals(requestBody.postalName, response.body?.postalName)
	}
}
