package org.adventofcode

import java.io.File

typealias HexString = String

val HexString.bitLength: Int get() = this.length * 4

fun HexString.toUInt(): UInt {
    if (this.length > 8) throw error ("Cannot covert to integer. Max 8 hex characters (32 bits)")
    return this.fold(0u) { sum, c -> (sum shl 4) or c.digitToInt(16).toUInt() }
}

fun HexString.toBinary(separator: String = "", fromIndex: Int = 0): String {
    return this.toList().joinToString(separator) {
        it.digitToInt(16).toString(2).padStart(4, '0')
    }
}

fun HexString.subbitsToUInt(startIndex: Int, endIndex: Int): UInt {
    val strBitLength = this.length * 4
    val subbitsLength = endIndex - startIndex

    if (
        startIndex < 0 ||
        endIndex < 0 ||
        startIndex >= strBitLength ||
        endIndex > strBitLength ||
        subbitsLength < 1 ||
        subbitsLength >= 32
    ) {
        throw error("Out of bounds: start index $startIndex, end index $endIndex, length $subbitsLength")
    }

    val startCharIdx = startIndex / 4
    val endCharIdx = ((endIndex - 1) / 4) + 1
    val chars: HexString = this.substring(startCharIdx, endCharIdx)
    val hexBitStartIdx = startIndex - (startCharIdx * 4)
    val hexBitEndIdx = endIndex - ((endCharIdx - 1) * 4)
    val bitOffset = 4 - hexBitEndIdx
    val chunkBitLength = chars.length * 4
    val chunkBitStartIdx = (4 - hexBitStartIdx) + chunkBitLength - 4 - bitOffset
    val bitFilter = (UInt.MAX_VALUE shl chunkBitStartIdx).inv()
    return (chars.toUInt() shr bitOffset) and bitFilter
}

fun HexString.subbitsToInt(startIndex: Int, endIndex: Int): Int {
    return subbitsToUInt(startIndex, endIndex).toInt()
}

object PacketHeaderUtils {
    object BitSize {
        const val VERSION_FIELD = 3
        const val TYPE_FIELD = 3
        const val HEADER = VERSION_FIELD + TYPE_FIELD
    }

    object Offset {
        const val VERSION_FIELD = 0
        const val TYPE_FIELD = VERSION_FIELD + BitSize.VERSION_FIELD
    }

    fun versionFrom(data: HexString, packetStartIndex: Int = 0): Int {
        val baseOffset = packetStartIndex + Offset.VERSION_FIELD
        return data.subbitsToInt(baseOffset, baseOffset + BitSize.VERSION_FIELD)
    }

    fun typeFrom(data: HexString, packetStartIndex: Int = 0): Int {
        val baseOffset = packetStartIndex + Offset.TYPE_FIELD
        return data.subbitsToInt(baseOffset, baseOffset + BitSize.TYPE_FIELD)
    }
}

object LiteralPacketUtils {
    object BitSize {
        const val IS_LAST_DIGIT_FIELD = 1
        const val DIGIT_VALUE_FIELD = 4
        const val DIGIT_BODY = IS_LAST_DIGIT_FIELD + DIGIT_VALUE_FIELD
    }

    object Offset {
        const val IS_LAST_DIGIT_FIELD = 0
        const val DIGIT_VALUE_FIELD = IS_LAST_DIGIT_FIELD + BitSize.IS_LAST_DIGIT_FIELD
    }

    fun digitFromByIndex(data: HexString, packetStartIndex: Int, digitIndex: Int): Pair<UInt, Boolean> {
        val baseOffset = packetStartIndex + PacketHeaderUtils.BitSize.HEADER + (digitIndex * BitSize.DIGIT_BODY)

        val isLastDigitOffset = baseOffset + Offset.IS_LAST_DIGIT_FIELD
        val lastDigit = data.subbitsToInt(isLastDigitOffset, isLastDigitOffset + BitSize.IS_LAST_DIGIT_FIELD) == 0

        val digitValueOffset = baseOffset + Offset.DIGIT_VALUE_FIELD
        val digitValue = data.subbitsToUInt(digitValueOffset, digitValueOffset + BitSize.DIGIT_VALUE_FIELD)

        return digitValue to lastDigit
    }

    fun digitsFrom(data: HexString, packetStartIndex: Int): List<UInt> {
        val sequence = generateSequence(Triple(0u, 0, false)) { (_, digitIndex, done) ->
            if (!done) {
                val (digit, last) = digitFromByIndex(data, packetStartIndex, digitIndex)
                Triple(digit, digitIndex + 1, last)
            } else null
        }

        return sequence
            .drop(1)
            .map { (digit) -> digit }
            .toList()
    }

    fun valueFrom(data: HexString, packetStartIndex: Int): UInt {
        return digitsFrom(data, packetStartIndex).fold(0u) { sum, digit -> (sum shl 4) or digit }
    }
}

enum class OperatorPacketLengthType(val value: Int) {
    SUBPACKETS_TOTAL_BITS(0),
    SUBPACKETS_COUNT(1),
}

object OperatorPacketUtils {
    object BitSize {
        const val LENGTH_TYPE_FIELD = 1
        const val SUBPACKETS_TOTAL_LENGTH_FIELD = 15
        const val SUBPACKETS_COUNT_LENGTH_FIELD = 11
    }

    object Offset {
        const val LENGTH_TYPE_FIELD = 0
        const val SUBPACKETS_LENGTH_FIELD = LENGTH_TYPE_FIELD + BitSize.LENGTH_TYPE_FIELD
    }

    fun lengthTypeFrom(data: HexString, packetStartIndex: Int): OperatorPacketLengthType {
        val baseOffset = packetStartIndex + PacketHeaderUtils.BitSize.HEADER + Offset.LENGTH_TYPE_FIELD
        val lengthType = data.subbitsToInt(baseOffset, baseOffset + BitSize.LENGTH_TYPE_FIELD)

        return when (lengthType) {
            0 -> OperatorPacketLengthType.SUBPACKETS_TOTAL_BITS
            1 -> OperatorPacketLengthType.SUBPACKETS_COUNT
            else -> throw error("Invalid operator length type $lengthType")
        }
    }

    fun subpacketsCountFrom(data: HexString, packetStartIndex: Int): Int {
        val baseOffset = packetStartIndex + PacketHeaderUtils.BitSize.HEADER + Offset.SUBPACKETS_LENGTH_FIELD
        return data.subbitsToInt(baseOffset, baseOffset + BitSize.SUBPACKETS_COUNT_LENGTH_FIELD)
    }

    fun subpacketsTotalBitsFrom(data: HexString, packetStartIndex: Int): Int {
        val baseOffset = packetStartIndex + PacketHeaderUtils.BitSize.HEADER + Offset.SUBPACKETS_LENGTH_FIELD
        return data.subbitsToInt(baseOffset, baseOffset + BitSize.SUBPACKETS_TOTAL_LENGTH_FIELD)
    }
}

sealed class Packet {
    abstract val data: HexString;
    abstract val startIndex: Int;
    abstract val size: Int;

    val type: Int by lazy { PacketHeaderUtils.typeFrom(data, startIndex) }
    val version: Int by lazy { PacketHeaderUtils.versionFrom(data, startIndex) }

    data class Literal(
        override val data: HexString,
        override val startIndex: Int = 0,
    ) : Packet() {
        val value: UInt by lazy { LiteralPacketUtils.valueFrom(data, startIndex) }
        override val size: Int by lazy {
            val body = LiteralPacketUtils.digitsFrom(data, startIndex).size * LiteralPacketUtils.BitSize.DIGIT_BODY
            val header = PacketHeaderUtils.BitSize.HEADER
            header + body
        }
    }

    data class Operator(
        override val data: HexString,
        override val startIndex: Int = 0,
        val subpackets: List<Packet>,
    ) : Packet() {
        val lengthType: OperatorPacketLengthType by lazy { OperatorPacketUtils.lengthTypeFrom(data, startIndex) }
        val length: Int by lazy { when (lengthType) {
            OperatorPacketLengthType.SUBPACKETS_COUNT -> OperatorPacketUtils.subpacketsCountFrom(data, startIndex)
            OperatorPacketLengthType.SUBPACKETS_TOTAL_BITS -> OperatorPacketUtils.subpacketsTotalBitsFrom(data, startIndex)
        } }
        override val size: Int by lazy {
            val subpacketsLengthFieldSize = when (lengthType) {
                OperatorPacketLengthType.SUBPACKETS_COUNT -> {
                    OperatorPacketUtils.BitSize.SUBPACKETS_COUNT_LENGTH_FIELD
                }
                OperatorPacketLengthType.SUBPACKETS_TOTAL_BITS -> {
                    OperatorPacketUtils.BitSize.SUBPACKETS_TOTAL_LENGTH_FIELD
                }
            }
            val body = (
                OperatorPacketUtils.BitSize.LENGTH_TYPE_FIELD
                + subpacketsLengthFieldSize
                + subpackets.sumOf { it.size }
            )
            val header = PacketHeaderUtils.BitSize.HEADER
            header + body
        }
    }
}

fun <T> Packet.fold(value: T, fn: ((value: T, packet: Packet) -> T)): T {
    return when (this) {
        is Packet.Literal -> fn(value, this)
        is Packet.Operator -> {
            val nextValue = fn(value, this)
            this.subpackets.fold(nextValue) { acc, subpacket -> subpacket.fold(acc, fn) }
        }
    }
}

fun Packet.walk(depth: Int = 0, fn: ((packet: Packet, depth: Int) -> Unit)) {
    return when (this) {
        is Packet.Literal -> fn(this, depth)
        is Packet.Operator -> {
            fn(this, depth)
            this.subpackets.forEach { it.walk(depth + 1, fn) }
        }
    }
}

fun Packet.prettyPrintTree() {
    this.walk { packet, depth ->
        val indent = " ".repeat(depth * 4)
        val generalProps = listOf(
            "version" to packet.version,
            "type" to packet.type,
            "size" to packet.size,
        )
        val detailedProps = when (packet) {
            is Packet.Literal -> listOf("kind" to "LITERAL", "value" to packet.value)
            is Packet.Operator -> listOf(
                "kind" to "OPERATOR",
                "length type" to packet.lengthType,
                "length" to packet.length
            )
        }
        println("${indent}Packet ${generalProps.joinToString(", ") { "${it.first}: ${it.second}" }}")
        detailedProps.forEach { println("${indent}- ${it.first}: ${it.second}") }
    }
}

interface PacketProcessor<T : Packet> {
    fun process(data: HexString, startIndex: Int): T;
}

object LiteralPacketProcessor : PacketProcessor<Packet.Literal> {
    override fun process(data: HexString, startIndex: Int): Packet.Literal {
        return Packet.Literal(data, startIndex)
    }
}

class SubpacketsTotalBitsOperatorPacketProcessor(
    private val subpacketPacketProcessor: PacketProcessor<*>,
) : PacketProcessor<Packet.Operator> {
    override fun process(data: HexString, startIndex: Int): Packet.Operator {
        val subpacketsBitLength = OperatorPacketUtils.subpacketsTotalBitsFrom(data, startIndex)
        val initSubpacketStartIndex = (
            startIndex
            + PacketHeaderUtils.BitSize.HEADER
            + OperatorPacketUtils.BitSize.LENGTH_TYPE_FIELD
            + OperatorPacketUtils.BitSize.SUBPACKETS_TOTAL_LENGTH_FIELD
        )
        val endOfPacketIndex = initSubpacketStartIndex + subpacketsBitLength
        val initSequenceState = Triple(null as Packet?, initSubpacketStartIndex, false)
        val sequence = generateSequence(initSequenceState) { (_, subpacketStartIndex, done) ->
            if (!done) {
                val subpacket = subpacketPacketProcessor.process(data, subpacketStartIndex)
                val nextSubpacketStartIndex = subpacketStartIndex + subpacket.size
                val shouldFinish = (subpacketStartIndex + subpacket.size) >= endOfPacketIndex
                Triple(subpacket, nextSubpacketStartIndex, shouldFinish)
            } else null
        }

        val subpackets = sequence.drop(1).mapNotNull { (subpacket) -> subpacket }.toList()
        return Packet.Operator(data, startIndex, subpackets)
    }
}

class SubpacketsCountOperatorPacketProcessor(
    private val subpacketPacketProcessor: PacketProcessor<*>,
): PacketProcessor<Packet.Operator> {
    override fun process(data: HexString, startIndex: Int): Packet.Operator {
        val subpacketsCount = OperatorPacketUtils.subpacketsCountFrom(data, startIndex)
        val initSubpacketStartIndex = (
            startIndex
            + PacketHeaderUtils.BitSize.HEADER
            + OperatorPacketUtils.BitSize.LENGTH_TYPE_FIELD
            + OperatorPacketUtils.BitSize.SUBPACKETS_COUNT_LENGTH_FIELD
        )

        val foldInitState = listOf<Packet>() to initSubpacketStartIndex
        val (subpackets) = (0 until subpacketsCount).fold(foldInitState) { (subpackets, subpacketStartIndex), _ ->
            val subpacket = subpacketPacketProcessor.process(data, subpacketStartIndex)
            (subpackets + subpacket) to (subpacketStartIndex + subpacket.size)
        }

        return Packet.Operator(data, startIndex, subpackets)
    }
}

class RootOperatorPacketProcessor(
    private val subpacketsPacketProcessors: List<Pair<OperatorPacketLengthType, PacketProcessor<Packet.Operator>>>
): PacketProcessor<Packet.Operator> {
    override fun process(data: HexString, startIndex: Int): Packet.Operator {
        val lengthType = OperatorPacketUtils.lengthTypeFrom(data, startIndex)
        return subpacketsPacketProcessors
                .toMap()[lengthType]?.process(data, startIndex) ?: throw error ("No packet processor for $lengthType")
    }
}

const val PACKET_TYPE_LITERAL = 4

class ProcessPacketController : PacketProcessor<Packet> {
    private val operatorPacketProcessor: PacketProcessor<*>
    private val literalPacketProcessor: PacketProcessor<*>

    init {
        literalPacketProcessor = LiteralPacketProcessor
        val subpacketCountPacketProcessor = SubpacketsCountOperatorPacketProcessor(this)
        val subpacketTotalBitsPacketProcessor = SubpacketsTotalBitsOperatorPacketProcessor(this)
        operatorPacketProcessor = RootOperatorPacketProcessor(listOf(
            OperatorPacketLengthType.SUBPACKETS_COUNT to subpacketCountPacketProcessor,
            OperatorPacketLengthType.SUBPACKETS_TOTAL_BITS to subpacketTotalBitsPacketProcessor,
        ))
    }

    override fun process(data: HexString, startIndex: Int): Packet {
        return when (PacketHeaderUtils.typeFrom(data, startIndex)) {
            PACKET_TYPE_LITERAL -> literalPacketProcessor
            else -> operatorPacketProcessor
        }.process(data, startIndex)
    }

    fun process(data: HexString): Packet {
        return process(data, 0)
    }

}


fun main() {
    val data = File("day16/src/main/resources/puzzleInput.txt").readLines().first()
    val controller = ProcessPacketController()
    val packet = controller.process(data)

    packet.prettyPrintTree()

    val summedVersion = packet.fold(0) { sum, packet -> sum + packet.version }
    println()
    println("Summed version: $summedVersion")
}
