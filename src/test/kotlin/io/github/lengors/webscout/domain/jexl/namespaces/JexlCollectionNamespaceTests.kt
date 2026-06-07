package io.github.lengors.webscout.domain.jexl.namespaces

import org.junit.jupiter.api.Test
import java.util.Map.entry

class JexlCollectionNamespaceTests {
    @Test
    fun `should correctly concatenate streams`() {
        val stream1 = listOf(1, 2, 3)
        val stream2 = listOf(4, 5, 6)
        val concatenatedStream =
            JexlCollectionNamespace
                .concat(stream1.stream(), stream2.stream())
                .toList()
        assert(concatenatedStream == listOf(1, 2, 3, 4, 5, 6))
    }

    @Test
    fun `should correctly enumerate iterable`() {
        val iterable = listOf(1, 2, 3)
        val enumeratedIterable = JexlCollectionNamespace.enumerate(iterable)
        assert(
            enumeratedIterable ==
                listOf(
                    entry(0, 1),
                    entry(1, 2),
                    entry(2, 3),
                ),
        )
    }

    @Test
    fun `should correctly create list`() {
        val list = listOf(1, 2, 3)
        val createdList = JexlCollectionNamespace.listOf(*list.toTypedArray())

        println(createdList)
        assert(createdList == list)
    }

    @Test
    fun `should correctly join string`() {
        val strings = listOf("a", "b", "c")
        val joinedString = JexlCollectionNamespace.join(strings, " ")
        assert(joinedString == "a b c")
    }

    @Test
    fun `should correctly join string transforming values`() {
        val strings = listOf("a", "b", "c")
        val joinedString = JexlCollectionNamespace.join(strings, " ") { it.uppercase() }
        assert(joinedString == "A B C")
    }

    @Test
    fun `should correctly apply mapping function`() {
        val strings = listOf("a", "b", "c")
        val mappedStrings = JexlCollectionNamespace.map(strings) { it.uppercase() }
        assert(mappedStrings == listOf("A", "B", "C"))
    }

    @Test
    fun `should provide appropriate map collector`() {
        val collector = JexlCollectionNamespace.mapCollector<String, String>()
        val list =
            listOf(
                entry("a", "A"),
                entry("b", "B"),
                entry("c", "C"),
            )

        val map = list.stream().collect(collector)
        assert(map == mapOf("a" to "A", "b" to "B", "c" to "C"))
    }

    @Test
    fun `should create stream from iterable`() {
        val iterable = listOf(1, 2, 3)
        val stream = JexlCollectionNamespace.stream(iterable)
        assert(stream.toList() == iterable)
    }

    @Test
    fun `should create stream from non-null value`() {
        val result = JexlCollectionNamespace.streamOf(1)
        assert(result.toList() == listOf(1))
    }

    @Test
    fun `should create stream from null value`() {
        val result = JexlCollectionNamespace.streamOf<Int>(null)
        assert(result.toList() == emptyList<Int>())
    }
}
