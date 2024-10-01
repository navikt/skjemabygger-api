package no.nav.forms.exceptions

import no.nav.forms.model.ErrorResponseDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class RestExceptionHandler {

	@ExceptionHandler
	fun handleResourceNotFound(exception: ResourceNotFoundException): ResponseEntity<ErrorResponseDto> {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponseDto(exception.message))
	}

}
