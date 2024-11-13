package no.nav.forms.exceptions.db

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.orm.jpa.JpaSystemException

class DbExceptionHelpersKtTest {
	@Test
	fun itExtractsDbErrorIdFromExceptionMessage() {
		val expectedDbError = DbError.FORMSAPI_001
		val rt = RuntimeException("could not execute statement [ERROR: ${expectedDbError.id}\n")
		val actualDbError = JpaSystemException(rt).getFormsApiDbError()
		assertEquals(expectedDbError, actualDbError)
	}
}
