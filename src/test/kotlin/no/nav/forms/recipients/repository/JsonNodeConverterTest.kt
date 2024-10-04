package no.nav.forms.recipients.repository

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class JsonNodeConverterTest {

	private val jsonNodeConverter = JsonNodeConverter()

	@Test
	fun testToEntityWhenNull() {
		val entityAttr = jsonNodeConverter.convertToEntityAttribute(null)
		assertNull(entityAttr)
	}

	@Test
	fun testToEntityWhenOneElement() {
		val entityAttr = jsonNodeConverter.convertToEntityAttribute("[\"PEN\"]")
		assertEquals(true, entityAttr?.isArray)
		assertEquals(1, entityAttr?.toList()?.size)
	}

	@Test
	fun testToEntityWhenAmptyArray() {
		val entityAttr = jsonNodeConverter.convertToEntityAttribute("[]")
		assertEquals(true, entityAttr?.isArray)
		assertEquals(0, entityAttr?.toList()?.size)
	}

	@Test
	fun testToEntityWhenThreeElements() {
		val entityAttr = jsonNodeConverter.convertToEntityAttribute("[\"PEN\",\"BIL\",\"AAP\"]")
		assertEquals(true, entityAttr?.isArray)
		assertEquals(3, entityAttr?.toList()?.size)
	}

	@Test
	fun testToEntityWhenIllegalDbValue() {
		assertThrows(Exception::class.java) {
			jsonNodeConverter.convertToEntityAttribute("[")
		}
	}

	@Test
	fun testToDbColumnWhenNull() {
		val dbAttr = jsonNodeConverter.convertToDatabaseColumn(null)
		assertNull(dbAttr)
	}

	@Test
	fun testToDbColumnWhenEmptyList() {
		val dbAttr = jsonNodeConverter.convertToDatabaseColumn(createJsonNode(listOf()))
		assertEquals("[]", dbAttr)
	}

	@Test
	fun testToDbColumnWhenOneElement() {
		val dbAttr = jsonNodeConverter.convertToDatabaseColumn(createJsonNode(listOf("PEN")))
		assertEquals("[\"PEN\"]", dbAttr)
	}

	@Test
	fun testToDbColumnWhenThreeElements() {
		val dbAttr = jsonNodeConverter.convertToDatabaseColumn(createJsonNode(listOf("TAR","PEN","BIL")))
		assertEquals("[\"TAR\",\"PEN\",\"BIL\"]", dbAttr)
	}

	private fun createJsonNode(dbValues: List<String>): JsonNode {
		val arrayNode = ObjectMapper().createArrayNode()
		dbValues.forEach { arrayNode.add(it) }
		return arrayNode;
	}

}
