package org.adventofcode

class Solution {
    companion object {
        fun solve (values: List<Int>): Int {
            return values.foldIndexed(0) {  idx, acc, value ->
                when (idx) {
                    0 -> acc
                    else -> if (value > values[idx - 1]) acc + 1 else acc
                }
            }
        }
    }
}