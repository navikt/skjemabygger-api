package no.nav.forms.forms

import no.nav.forms.api.EditFormsApi
import no.nav.forms.config.AzureAdConfig
import no.nav.forms.model.FormDto
import no.nav.forms.model.NewFormRequest
import no.nav.forms.model.UpdateFormRequest
import no.nav.forms.security.SecurityContextHolder
import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
@ProtectedWithClaims(issuer = AzureAdConfig.ISSUER, claimMap = ["${AzureAdConfig.CLAIM_NAV_IDENT}=*"])
class EditFormsController(
	private val editFormsService: EditFormsService,
	private val securityContextHolder: SecurityContextHolder,
) : EditFormsApi {

	override fun createForm(newFormRequest: NewFormRequest): ResponseEntity<FormDto> {
		securityContextHolder.requireValidUser()
		val userId = securityContextHolder.getUserName()
		val newForm = editFormsService.createForm(
			newFormRequest.skjemanummer,
			newFormRequest.title,
			newFormRequest.components,
			newFormRequest.properties,
			userId,
		)
		return ResponseEntity.status(HttpStatus.CREATED).body(newForm)
	}

	@Unprotected
	override fun getForm(id: Long): ResponseEntity<FormDto> {
		val form = editFormsService.getForm(id)
		return ResponseEntity.ok(form)
	}

	override fun updateForm(
		id: Long,
		formsapiEntityRevision: Int,
		updateFormRequest: UpdateFormRequest
	): ResponseEntity<FormDto> {
		securityContextHolder.requireValidUser()
		val userId = securityContextHolder.getUserName()
		val form = editFormsService.updateForm(id, formsapiEntityRevision, updateFormRequest.title, updateFormRequest.components, updateFormRequest.properties, userId)
		return ResponseEntity.ok(form)
	}

	@Unprotected
	override fun getForms(): ResponseEntity<List<FormDto>> {
		val forms = editFormsService.getForms()
		return ResponseEntity.ok(forms)
	}

}
