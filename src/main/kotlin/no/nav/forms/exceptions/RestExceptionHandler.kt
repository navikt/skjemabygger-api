package no.nav.forms.exceptions

import no.nav.forms.model.ErrorResponseDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class RestExceptionHandler {

	val logger: Logger = LoggerFactory.getLogger(javaClass)

	@ExceptionHandler
	fun handleResourceNotFound(exception: ResourceNotFoundException): ResponseEntity<ErrorResponseDto> {
		logger.info("${exception.message} (id=${exception.resourceId})", exception)
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponseDto(exception.message))
	}

	@ExceptionHandler
	fun handleGenericException(exception: Exception): ResponseEntity<ErrorResponseDto> {
		val responseErrorMessage = "Something went wrong"
		logger.warn("$responseErrorMessage: ${exception.message}", exception)
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponseDto(responseErrorMessage))
	}

}
