package no.nav.forms.utils.throwable

import no.nav.forms.exceptions.DuplicateResourceException
import org.apache.catalina.connector.ClientAbortException
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.springframework.web.context.request.async.AsyncRequestNotUsableException
import java.io.IOException
import kotlin.test.Test

class ThrowableExtensionsKtTest {

	@Test
	fun testThrowableHasCause() {
		val rootCause = ClientAbortException("client abort")
		val cause = IOException("io", rootCause)
		val exception = AsyncRequestNotUsableException("exception", cause)

		assertTrue(exception.hasCause(Throwable::class.java))
		assertTrue(exception.hasCause(Exception::class.java))
		assertTrue(exception.hasCause(AsyncRequestNotUsableException::class.java))
		assertTrue(exception.hasCause(IOException::class.java))
		assertTrue(exception.hasCause(ClientAbortException::class.java))
		assertFalse(exception.hasCause(DuplicateResourceException::class.java))
		assertFalse(exception.hasCause(IllegalArgumentException::class.java))
		assertFalse(exception.hasCause(Error::class.java))
	}

}
