package no.nav.forms

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.forms.model.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.util.MultiValueMap

data class FormsApiResponse<T>(
	val statusCode: HttpStatusCode,
	val body: T? = null,
) {
	fun <B> successBody(): B {
		assertTrue(statusCode.is2xxSuccessful, "Expected successful response code")
		return body!! as B
	}

	fun isSuccess(): FormsApiResponse<T> {
		assertTrue(statusCode.is2xxSuccessful, "Expected successful response code")
		return this
	}
}

private const val formsapiEntityRevisionHeaderName = "Formsapi-Entity-Revision"

class TestFormsApi(
	private val baseUrl: String,
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
		val headers = mapOf(formsapiEntityRevisionHeaderName to revision.toString())
		val response = restTemplate.exchange(
			"$globalTranslationBaseUrl/$id",
			HttpMethod.PUT,
			HttpEntity(request, httpHeaders(authToken, headers.plus(additionalHeaders))),
			String::class.java
		)
		val body: Any = readGlobalTranslationBody(response)
		return FormsApiResponse(response.statusCode, body)
	}

	fun deleteGlobalTranslation(
		id: Long,
		authToken: String? = null,
	): FormsApiResponse<Any> {
		val response = restTemplate.exchange(
			"$globalTranslationBaseUrl/$id",
			HttpMethod.DELETE,
			HttpEntity(null, httpHeaders(authToken)),
			String::class.java
		)
		val body: ErrorResponseDto? = if (!response.statusCode.is2xxSuccessful) readErrorBody(response) else null
		return FormsApiResponse(response.statusCode, body)
	}

	fun getGlobalTranslations(): FormsApiResponse<List<GlobalTranslationDto>> {
		val responseType = object : ParameterizedTypeReference<List<GlobalTranslationDto>>() {}
		val response = restTemplate.exchange(globalTranslationBaseUrl, HttpMethod.GET, null, responseType)
		return FormsApiResponse(response.statusCode, response.body!!)
	}

	private fun readGlobalTranslationBody(response: ResponseEntity<String>): Any {
		val body: Any = if (response.statusCode.is2xxSuccessful) objectMapper.readValue(
			response.body,
			GlobalTranslationDto::class.java
		) else objectMapper.readValue(response.body, ErrorResponseDto::class.java)
		return body
	}

	private fun readErrorBody(response: ResponseEntity<String>): ErrorResponseDto {
		return objectMapper.readValue(response.body, ErrorResponseDto::class.java)
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

	fun createFormTranslation(
		formPath: String,
		request: NewFormTranslationRequestDto,
		authToken: String
	): FormsApiResponse<Any> {
		val response = restTemplate.exchange(
			"$baseUrl/v1/forms/$formPath/translations",
			HttpMethod.POST,
			HttpEntity(request, httpHeaders(authToken)),
			String::class.java
		)
		val body: Any = readFormTranslationBody(response)
		return FormsApiResponse(response.statusCode, body)
	}

	fun updateFormTranslation(
		formPath: String,
		formTranslationId: Long,
		revision: Int,
		request: UpdateFormTranslationRequest,
		authToken: String,
	): FormsApiResponse<Any> {
		val headers = mapOf(formsapiEntityRevisionHeaderName to revision.toString())
		val response = restTemplate.exchange(
			"$baseUrl/v1/forms/$formPath/translations/$formTranslationId",
			HttpMethod.PUT,
			HttpEntity(request, httpHeaders(authToken, headers)),
			String::class.java
		)
		val body: Any = readFormTranslationBody(response)
		return FormsApiResponse(response.statusCode, body)
	}

	private fun readFormTranslationBody(response: ResponseEntity<String>): Any {
		val body: Any = if (response.statusCode.is2xxSuccessful) objectMapper.readValue(
			response.body,
			FormTranslationDto::class.java
		) else objectMapper.readValue(response.body, ErrorResponseDto::class.java)
		return body
	}

	fun getFormTranslations(formPath: String): FormsApiResponse<Any> {
		val responseType = object : ParameterizedTypeReference<List<FormTranslationDto>>() {}
		val response = restTemplate.exchange("$baseUrl/v1/forms/$formPath/translations", HttpMethod.GET, null, responseType)
		return FormsApiResponse(response.statusCode, response.body!!)
	}

	fun publishGlobalTranslations(authToken: String?): FormsApiResponse<Any> {
		val response = restTemplate.exchange(
			"$baseUrl/v1/global-translations/publish",
			HttpMethod.POST,
			HttpEntity(null, httpHeaders(authToken)),
			String::class.java
		)
		val body: ErrorResponseDto? = if (!response.statusCode.is2xxSuccessful) readErrorBody(response) else null
		return FormsApiResponse(response.statusCode, body)
	}

	fun getPublishedGlobalTranslations(languageCodeValue: String): FormsApiResponse<Map<String, String>> {
		val responseType = object : ParameterizedTypeReference<Map<String, String>>() {}
		val response = restTemplate.exchange("$baseUrl/v1/published-global-translations/$languageCodeValue", HttpMethod.GET, null, responseType)
		return FormsApiResponse(response.statusCode, response.body!!)
	}

	fun getGlobalTranslationPublication(languageCodeValues: List<String>? = emptyList()): FormsApiResponse<Any> {
		val queryString = if (languageCodeValues != null && !languageCodeValues.isEmpty()) "?languageCodes=${languageCodeValues.joinToString(",")}" else ""
		val response = restTemplate.exchange(
			"$baseUrl/v1/published-global-translations$queryString",
			HttpMethod.GET,
			HttpEntity(null, httpHeaders(null)),
			String::class.java
		)
		val body: Any = parsePublishedGlobalTranslationsResponse(response)
		return FormsApiResponse(response.statusCode, body)
	}

	private fun parsePublishedGlobalTranslationsResponse(response: ResponseEntity<String>): Any {
		val body: Any = if (response.statusCode.is2xxSuccessful) objectMapper.readValue(
			response.body,
			PublishedGlobalTranslationsDto::class.java
		) else objectMapper.readValue(response.body, ErrorResponseDto::class.java)
		return body
	}

	fun deleteFormTranslation(formPath: String, formTranslationId: Long, authToken: String? = null): FormsApiResponse<Any> {
		val response = restTemplate.exchange(
			"$baseUrl/v1/forms/$formPath/translations/$formTranslationId",
			HttpMethod.DELETE,
			HttpEntity(null, httpHeaders(authToken)),
			String::class.java
		)
		val body: ErrorResponseDto? = if (!response.statusCode.is2xxSuccessful) readErrorBody(response) else null
		return FormsApiResponse(response.statusCode, body)
	}

}
