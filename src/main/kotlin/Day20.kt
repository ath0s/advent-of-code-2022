import Day.Main
import kotlin.io.path.readLines

class Day20 : Day {
    override fun partOne(filename: String, verbose: Boolean): Long {
        val originalList = filename.asPath().readLines().map { it.toLong() }
        val decrypted = decrypt(originalList)
        val coordinates = decrypted.getCoordinates()
        return coordinates.toList().sum()
    }

    override fun partTwo(filename: String, verbose: Boolean): Long {
        val decryptionKey = 811589153
        val originalList = filename.asPath().readLines().map { it.toLong() * decryptionKey }
        val decrypted = decrypt(originalList, 10)
        val coordinates = decrypted.getCoordinates()
        return coordinates.toList().sum()
    }

    companion object : Main("Day20.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }

    private fun decrypt(originalList: List<Long>, mixCount: Int = 1): List<Pair<Int, Long>> {
        val decrypted = originalList.mapIndexedTo(mutableListOf()) { index, value -> index to value }

        repeat(mixCount) {
            for (index in originalList.indices) {
                val originalIndex = originalList[index]
                val pair = index to originalIndex
                var currentIndex = decrypted.indexOf(pair).toLong()
                decrypted -= pair
                currentIndex += originalIndex
                currentIndex = originalList % currentIndex
                decrypted.add(currentIndex.toInt(), pair)
            }
        }
        return decrypted
    }

    private operator fun List<Long>.rem(index: Long): Long {
        val denominator = size - 1
        var modulo = index % denominator
        if (modulo < 0) {
            modulo += denominator.toLong()
        }
        return modulo
    }

    private fun List<Pair<Int, Long>>.getCoordinates(): Triple<Long, Long, Long> {
        val indexOfZero = indexOfFirst { (_, value) -> value == 0L }
        return Triple(
            this[(indexOfZero + 1_000) % size].second,
            this[(indexOfZero + 2_000) % size].second,
            this[(indexOfZero + 3_000) % size].second
        )
    }
}
