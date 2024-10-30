package no.nav.forms

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.forms.model.ErrorResponseDto
import no.nav.forms.model.GlobalTranslation
import no.nav.forms.model.NewGlobalTranslationRequest
import no.nav.forms.model.UpdateGlobalTranslationRequest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.util.MultiValueMap

data class FormsApiResponse<T>(
	val statusCode: HttpStatusCode,
	val body: T,
)

class TestFormsApi(
	baseUrl: String,
	private val restTemplate: TestRestTemplate,
	private val objectMapper: ObjectMapper,
) {

	private val globalTranslationBaseUrl = "$baseUrl/v1/global-translations"

	fun createGlobalTranslation(
		request: NewGlobalTranslationRequest,
		authToken: String? = null,
		additionalHeaders: Map<String, String> = emptyMap()
	): FormsApiResponse<Any> {
		val response = restTemplate.exchange(
			globalTranslationBaseUrl,
			HttpMethod.POST,
			HttpEntity(request, httpHeaders(authToken, additionalHeaders)),
			String::class.java
		)
		val body: Any = readGlobalTranslationBody(response)
		return FormsApiResponse(response.statusCode, body)
	}

	fun putGlobalTranslation(
		id: Long,
		revision: Int,
		request: UpdateGlobalTranslationRequest,
		authToken: String? = null,
		additionalHeaders: Map<String, String> = emptyMap()
	): FormsApiResponse<Any> {
		val headers = mapOf("Formsapi-Entity-Revision" to revision.toString())
		val response = restTemplate.exchange(
			"$globalTranslationBaseUrl/$id",
			HttpMethod.PUT,
			HttpEntity(request, httpHeaders(authToken, headers.plus(additionalHeaders))),
			String::class.java
		)
		val body: Any = readGlobalTranslationBody(response)
		return FormsApiResponse(response.statusCode, body)
	}

	fun getGlobalTranslations(): FormsApiResponse<List<GlobalTranslation>> {
		val responseType = object : ParameterizedTypeReference<List<GlobalTranslation>>() {}
		val response = restTemplate.exchange(globalTranslationBaseUrl, HttpMethod.GET, null, responseType)
			return FormsApiResponse(response.statusCode, response.body!!)
	}

	private fun readGlobalTranslationBody(response: ResponseEntity<String>): Any {
		val body: Any = if (response.statusCode.is2xxSuccessful) objectMapper.readValue(
			response.body,
			GlobalTranslation::class.java
		) else objectMapper.readValue(response.body, ErrorResponseDto::class.java)
		return body
	}

	private fun httpHeaders(
		token: String?,
		additionalHeaders: Map<String, String>? = emptyMap()
	): MultiValueMap<String, String> {
		val headers = HttpHeaders()
		if (token != null) {
			headers.add("Authorization", "Bearer $token")
		}
		additionalHeaders?.forEach { headers.add(it.key, it.value) }
		return headers
	}

}
