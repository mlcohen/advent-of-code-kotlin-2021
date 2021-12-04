package org.adventofcode

data class BingoCardCell(
    val number: Int,
    val index: Int,
    val row: Int,
    val column: Int,
)

class BingoCard(
    val id: Int,
    val numbers: List<Int>,
    val columnSize: Int,
) {
    private var rowSize: Int = 0
    private val cells: MutableMap<Int, BingoCardCell> = mutableMapOf()
    private val markedRowCellLists: MutableList<MutableList<BingoCardCell>> = mutableListOf()
    private val markedColumnCellLists: MutableList<MutableList<BingoCardCell>> = mutableListOf()
    private val markedNumbers: MutableSet<Int> = mutableSetOf()

    val unmarkedNumbers: List<Int>
        get() = numbers.filter { !markedNumbers.contains(it) }

    val winningRowCellList: List<BingoCardCell>?
        get() = markedRowCellLists.find { it.size == rowSize }

    val winningColumnCellList: List<BingoCardCell>?
        get() = markedColumnCellLists.find { it.size == columnSize}

    val hasWinningRow: Boolean
        get() = winningRowCellList != null

    val hasWinningColumn: Boolean
        get() = winningColumnCellList != null

    val isWinner: Boolean
        get() = hasWinningRow || hasWinningColumn

    val winningNumbers: List<Int>?
        get() {
            if (hasWinningRow) {
                return winningRowCellList!!.sortedBy { it.column }.map { it.number }
            }
            if (hasWinningColumn) {
                return winningColumnCellList!!.sortedBy { it.row }.map { it.number }
            }
            return null
        }

    init {
        rowSize = numbers.size / columnSize
        numbers.forEachIndexed { idx, num ->
            val row = idx / rowSize
            val column = idx % columnSize
            val cell = BingoCardCell(
                number = num,
                index = idx,
                row = row,
                column = column,
            )
            cells[cell.number] = cell
        }
        (0 until rowSize).forEach { _ -> markedRowCellLists.add(mutableListOf()) }
        (0 until columnSize).forEach { _ -> markedColumnCellLists.add(mutableListOf()) }
    }

    fun mark(number: Int) {
        val cell = cells[number]
        val markedNumber = markedNumbers.contains(number)

        if (cell == null || markedNumber) {
            return
        }

        markedNumbers.add(number)
        markedRowCellLists[cell.row] += cell
        markedColumnCellLists[cell.column] += cell
    }

    override fun toString(): String {
        return "BingoCard(id=$id, numbers=${numbers.chunked(columnSize)})"
    }

    companion object {
        fun create(numbers: List<Int>, id: Int, columnSize: Int = 5): BingoCard {
            return BingoCard(
                id = id,
                numbers = numbers,
                columnSize = columnSize,
            )
        }

        fun create(numbers: List<List<Int>>, id: Int): BingoCard {
            return BingoCard(
                id = id,
                numbers = numbers.flatten(),
                columnSize = numbers[0].size,
            )
        }
    }
}

class BingoCardCollection {
    private val registeredbingoCards: MutableList<BingoCard> = mutableListOf()

    constructor(bingoCards: List<BingoCard>) {
        bingoCards.forEach { this.add(it) }
    }

    val size: Int
        get() = registeredbingoCards.size

    operator fun get(index: Int): BingoCard {
        return registeredbingoCards[index]
    }

    val winningBingoCard: BingoCard?
        get() = registeredbingoCards.find { it.isWinner }

    val hasWinningBingoCard: Boolean
        get() = winningBingoCard != null

    fun add(card: BingoCard) {
        registeredbingoCards.add(card)
    }

    fun mark(number: Int) {
        registeredbingoCards.forEach { it.mark(number) }
    }

}