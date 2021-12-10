package org.adventofcode

import java.io.File

data class DecodedSignalEntry(
    val inputStream: List<Pair<EncodedDigit, Int>>,
    val outputStream: List<Pair<EncodedDigit, Int>>,
    val outputValue: Int,
)

data class EncodedSignalEntry(
    val inputStream: List<EncodedDigit>,
    val outputStream: List<EncodedDigit>,
)

fun EncodedSignalEntry.tryDecoding(): DecodedSignalEntry? {
    val decodingDigit = SequenceDecoder.tryResolvingDecodingDigit(this.inputStream) ?: return null

    val decodedInput = decodingDigit!!
        .applyTo(this.inputStream)
        .map { it.tryResolving() }
        .map { (_, digit, value) -> digit to value }
    val decodedOutput = decodingDigit!!
        .applyTo(this.outputStream)
        .map { it.tryResolving() }
        .map { (_, digit, value) -> digit to value }
    val outputValue = decodedOutput
        .joinToString("") { (_, value) -> value.toString() }
        .toInt()

    return DecodedSignalEntry(
        inputStream = decodedInput,
        outputStream = decodedOutput,
        outputValue = outputValue,
    )
}

typealias EncodedSignalEntryList = List<EncodedSignalEntry>

fun EncodedSignalEntryList.countOutputDigitsWithUniqueNumberOfSegments(): Int {
    return this.fold(0) { total, entry ->
        total + entry.outputStream.map { it.chars.size }.filter { it < 5 || it > 6 }.size
    }
}

object EncodedSignalEntryListFactory {
    fun fromStrings(input: List<String>): EncodedSignalEntryList {
        return input.map { it.split('|') }.map {
            val encodedInputStrings = it[0].trim().split(' ')
            val encodedOutputStrings = it[1].trim().split(' ')
            EncodedSignalEntry(
                inputStream = EncodedDigitListFactory.fromStrings(encodedInputStrings),
                outputStream = EncodedDigitListFactory.fromStrings(encodedOutputStrings),
            )
        }
    }

    fun fromFile(pathname: String): EncodedSignalEntryList {
        val file = File(pathname)
        return fromStrings(file.readLines())
    }
}