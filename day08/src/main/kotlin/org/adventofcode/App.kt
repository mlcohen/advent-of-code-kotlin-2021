package org.adventofcode


fun main() {
    val encodedSignalEntries = EncodedSignalEntryListFactory.fromFile("day08/src/main/resources/sampleInput.txt")
    val decodedSignalEntries = encodedSignalEntries.map { it.tryDecoding() }
    val decodedOutputSteams = decodedSignalEntries.mapNotNull { it!!.outputStream to it!!.outputValue }

    decodedOutputSteams.forEach { (stream, value) ->
        val result = stream.joinToString(" ") { (digit) -> digit.charsToString() }
        println("$result: ${value}")
    }

    val total = decodedOutputSteams.sumOf { (_, value) -> value }
    println("total: $total")
}
