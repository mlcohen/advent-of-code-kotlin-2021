package org.adventofcode

object SequenceDecoder {
    private fun resolveDecodingDigit(
        decodingDigit: DecodingDigit,
        encodedDigits: List<EncodedDigit>,
    ): DecodingDigit {
        val nextDecodingDigit = decodingDigit.resolveFor(encodedDigits)

        if (decodingDigit == nextDecodingDigit || nextDecodingDigit.complete()) {
            return nextDecodingDigit
        }

        val reducedDigits = encodedDigits
            .map { nextDecodingDigit.applyTo(it, strict = false) }
            .map { it.tryResolving() }
            .map { (_, digit) -> digit }

        return resolveDecodingDigit(nextDecodingDigit, reducedDigits)
    }

    fun tryResolvingDecodingDigit(encodedDigits: List<EncodedDigit>): DecodingDigit? {
        if (encodedDigits.size < 10) {
            throw error("Invalid number of encoded digits")
        }

        val primingEncodedDigits = encodedDigits.sortedBy { it.size }.filter {
            when (it.size) {
                2, 3, 4, 7 -> true
                else -> false
            }
        }

        if (primingEncodedDigits.size != 4) {
            throw error("Missing digits with 2, 3, 4 and 8 segments")
        }

        val primedEncodedDigits = encodedDigits.map { it.reduceFrom(primingEncodedDigits) }
        val decodingDigit = resolveDecodingDigit(DecodingDigit(), primedEncodedDigits)

        return if (decodingDigit.complete()) {
            decodingDigit
        } else {
            null
        }
    }
}