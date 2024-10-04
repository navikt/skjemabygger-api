package no.nav.forms.recipients.repository

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DbJsonArrayConverterTest {

	private val dbJsonArrayConverter = DbJsonArrayConverter()

	@Test
	fun testToEntityWhenNull() {
		val entityAttr = dbJsonArrayConverter.convertToEntityAttribute(null)
		assertNull(entityAttr)
	}

	@Test
	fun testToEntityWhenOneElement() {
		val entityAttr = dbJsonArrayConverter.convertToEntityAttribute("[\"PEN\"]")
		assertEquals(true, entityAttr?.isArray)
		assertEquals(1, entityAttr?.toList()?.size)
	}

	@Test
	fun testToEntityWhenAmptyArray() {
		val entityAttr = dbJsonArrayConverter.convertToEntityAttribute("[]")
		assertEquals(true, entityAttr?.isArray)
		assertEquals(0, entityAttr?.toList()?.size)
	}

	@Test
	fun testToEntityWhenThreeElements() {
		val entityAttr = dbJsonArrayConverter.convertToEntityAttribute("[\"PEN\",\"BIL\",\"AAP\"]")
		assertEquals(true, entityAttr?.isArray)
		assertEquals(3, entityAttr?.toList()?.size)
	}

	@Test
	fun testToEntityWhenIllegalDbValue() {
		assertThrows(Exception::class.java) {
			dbJsonArrayConverter.convertToEntityAttribute("[")
		}
	}

	@Test
	fun testToDbColumnWhenNull() {
		val dbAttr = dbJsonArrayConverter.convertToDatabaseColumn(null)
		assertNull(dbAttr)
	}

	@Test
	fun testToDbColumnWhenEmptyList() {
		val dbAttr = dbJsonArrayConverter.convertToDatabaseColumn(createJsonNode(listOf()))
		assertEquals("[]", dbAttr)
	}

	@Test
	fun testToDbColumnWhenOneElement() {
		val dbAttr = dbJsonArrayConverter.convertToDatabaseColumn(createJsonNode(listOf("PEN")))
		assertEquals("[\"PEN\"]", dbAttr)
	}

	@Test
	fun testToDbColumnWhenThreeElements() {
		val dbAttr = dbJsonArrayConverter.convertToDatabaseColumn(createJsonNode(listOf("TAR","PEN","BIL")))
		assertEquals("[\"TAR\",\"PEN\",\"BIL\"]", dbAttr)
	}

	private fun createJsonNode(dbValues: List<String>): JsonNode {
		val arrayNode = ObjectMapper().createArrayNode()
		dbValues.forEach { arrayNode.add(it) }
		return arrayNode;
	}

}
