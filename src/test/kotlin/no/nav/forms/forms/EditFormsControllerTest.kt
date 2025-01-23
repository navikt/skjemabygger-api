package no.nav.forms.forms

import no.nav.forms.ApplicationTest
import no.nav.forms.model.FormDto
import no.nav.forms.testutils.createMockToken
import no.nav.forms.testutils.FormsTestdata
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import kotlin.reflect.full.memberProperties

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

		assertEquals(request.components.size, newForm.components?.size)
		assertEquals("panel", newForm.components?.get(0)["type"])
		assertEquals(tema, newForm.properties?.get("tema"))
		assertEquals(innsending, newForm.properties?.get("innsending"))
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
		assertEquals("BIL", form.properties?.get("tema"))

		val updateRequest = FormsTestdata.updateFormRequest(
			title = form.title!!,
			components = form.components!!,
			properties = mapOf("tema" to "AAP")
		)
		val updatedForm = testFormsApi.updateForm(form.id, form.revision!!, updateRequest, authToken)
			.assertSuccess()
			.body
		assertEquals(2, updatedForm.revision)
		assertEquals("AAP", updatedForm.properties?.get("tema"))
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
		assertEquals("BIL", newForm.properties?.get("tema"))

		val newFormTitle = newForm.title!!
		val newFormComponents = newForm.components!!
		val newFormInitialRevision = newForm.revision!!

		val updateRequest1 = FormsTestdata.updateFormRequest(
			title = newFormTitle,
			components = newFormComponents,
			properties = mapOf("tema" to "AAP")
		)
		val updatedForm1 = testFormsApi.updateForm(newForm.id, newFormInitialRevision, updateRequest1, authToken)
			.assertSuccess()
			.body
		assertEquals(2, updatedForm1.revision)
		assertEquals("AAP", updatedForm1.properties?.get("tema"))

		val updateRequest2 = FormsTestdata.updateFormRequest(
			title = newFormTitle,
			components = newFormComponents,
			properties = mapOf("tema" to "PEN")
		)
		// Run second update with same revision as last update, which should fail with 409 Conflict
		val errorBody = testFormsApi.updateForm(newForm.id, newFormInitialRevision, updateRequest2, authToken)
			.assertHttpStatus(HttpStatus.CONFLICT)
			.errorBody
		assertEquals("Conflict", errorBody.errorMessage)

		// Verify that the second update did not affect the form
		val currentForm = testFormsApi.getForm(newForm.id)
			.assertSuccess()
			.body
		assertEquals("AAP", currentForm.properties?.get("tema"))
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

	@Test
	fun testGetAllForms() {
		val authToken = mockOAuth2Server.createMockToken()
		testFormsApi.createForm(FormsTestdata.newFormRequest(skjemanummer = "TEST1"), authToken)
			.assertSuccess()
		testFormsApi.createForm(FormsTestdata.newFormRequest(skjemanummer = "TEST2"), authToken)
			.assertSuccess()
		testFormsApi.createForm(FormsTestdata.newFormRequest(skjemanummer = "TEST3"), authToken)
			.assertSuccess()
		testFormsApi.createForm(FormsTestdata.newFormRequest(skjemanummer = "TEST4"), authToken)
			.assertSuccess()
		testFormsApi.createForm(FormsTestdata.newFormRequest(skjemanummer = "TEST5"), authToken)
			.assertSuccess()
		testFormsApi.createForm(FormsTestdata.newFormRequest(skjemanummer = "TEST6"), authToken)
			.assertSuccess()
		val forms = testFormsApi.getForms().assertSuccess().body
		assertEquals(6, forms.size)
	}

	fun assertPrecenceOfProps(formDto: FormDto, select: List<String>) {
		for (prop in FormDto::class.memberProperties) {
			// id should always be present
			if (prop.name == "id" || select.contains(prop.name)) {
				assertNotNull(prop.getter.call(formDto), "Expected ${prop.name} not to be null")
			} else {
				assertNull(prop.getter.call(formDto), "Expected ${prop.name} to be null")
			}
		}
	}

	@Test
	fun testGetAllFormsWithSelect() {
		val authToken = mockOAuth2Server.createMockToken()
		testFormsApi.createForm(FormsTestdata.newFormRequest(), authToken)
			.assertSuccess().body

		val select1 = "properties"
		assertPrecenceOfProps(
			testFormsApi.getForms(select1).assertSuccess().body.first(),
			select1.split(",")
		)

		val select2 = "title,skjemanummer,revision"
		assertPrecenceOfProps(
			testFormsApi.getForms(select2).assertSuccess().body.first(),
			select2.split(",")
		)
	}

}
