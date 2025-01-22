package no.nav.forms.forms

import no.nav.forms.ApplicationTest
import no.nav.forms.testutils.createMockToken
import no.nav.forms.testutils.FormsTestdata
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class EditFormsControllerTest : ApplicationTest() {

	@Test
	fun testCreateForm() {
		val authToken = mockOAuth2Server.createMockToken()
		val tema = "AAP"
		val innsending = "KUN_PAPIR"
		val request = FormsTestdata.newFormRequest(
			skjemanummer = "NAV 12-45.04B",
			properties = mapOf("tema" to tema, "innsending" to innsending),
			components = listOf(mapOf("type" to "panel"))
		)
		val newForm = testFormsApi.createForm(request, authToken)
			.assertSuccess()
			.body
		assertEquals(request.title, newForm.title)
		assertEquals("nav124504b", newForm.path)

		assertEquals(request.components.size, newForm.components.size)
		assertEquals("panel", newForm.components[0]["type"])
		assertEquals(tema, newForm.properties["tema"])
		assertEquals(innsending, newForm.properties["innsending"])
	}

	@Test
	fun testCreateFormFailureWhenMissingAuthToken() {
		val request = FormsTestdata.newFormRequest()
		val errorBody = testFormsApi.createForm(request, authToken = null)
			.assertHttpStatus(HttpStatus.UNAUTHORIZED)
			.errorBody
		assertEquals(errorBody.errorMessage, "Unauthorized")
	}

	@Test
	fun testUpdateForm() {
		val createRequest = FormsTestdata.newFormRequest(
			properties = mapOf("tema" to "BIL")
		)
		val authToken = mockOAuth2Server.createMockToken()
		val form = testFormsApi.createForm(createRequest, authToken)
			.assertSuccess()
			.body
		assertEquals(1, form.revision)
		assertEquals("BIL", form.properties["tema"])

		val updateRequest = FormsTestdata.updateFormRequest(
			title = form.title,
			components = form.components,
			properties = mapOf("tema" to "AAP")
		)
		val updatedForm = testFormsApi.updateForm(form.id, form.revision, updateRequest, authToken)
			.assertSuccess()
			.body
		assertEquals(2, updatedForm.revision)
		assertEquals("AAP", updatedForm.properties["tema"])
	}

	@Test
	fun testUpdateFormFailureWhenWrongRevision() {
		val createRequest = FormsTestdata.newFormRequest(
			properties = mapOf("tema" to "BIL")
		)
		val authToken = mockOAuth2Server.createMockToken()
		val newForm = testFormsApi.createForm(createRequest, authToken)
			.assertSuccess()
			.body
		assertEquals(1, newForm.revision)
		assertEquals("BIL", newForm.properties["tema"])

		val updateRequest1 = FormsTestdata.updateFormRequest(
			title = newForm.title,
			components = newForm.components,
			properties = mapOf("tema" to "AAP")
		)
		val updatedForm1 = testFormsApi.updateForm(newForm.id, newForm.revision, updateRequest1, authToken)
			.assertSuccess()
			.body
		assertEquals(2, updatedForm1.revision)
		assertEquals("AAP", updatedForm1.properties["tema"])

		val updateRequest2 = FormsTestdata.updateFormRequest(
			title = newForm.title,
			components = newForm.components,
			properties = mapOf("tema" to "PEN")
		)
		// Run second update with same revision as last update, which should fail with 409 Conflict
		val errorBody = testFormsApi.updateForm(newForm.id, newForm.revision, updateRequest2, authToken)
			.assertHttpStatus(HttpStatus.CONFLICT)
			.errorBody
		assertEquals("Conflict", errorBody.errorMessage)

		// Verify that the second update did not affect the form
		val currentForm = testFormsApi.getForm(newForm.id)
			.assertSuccess()
			.body
		assertEquals("AAP", currentForm.properties["tema"])
	}

	@Test
	fun testGetForm() {
		val authToken = mockOAuth2Server.createMockToken()
		val createRequest = FormsTestdata.newFormRequest()
		val newForm = testFormsApi.createForm(createRequest, authToken)
			.assertSuccess()
			.body
		val form = testFormsApi.getForm(newForm.id)
			.assertSuccess()
			.body
		assertEquals(newForm.title, form.title)
		assertEquals(newForm.components, form.components)
		assertEquals(newForm.properties, form.properties)
		assertEquals(newForm.revision, form.revision)
	}

}
