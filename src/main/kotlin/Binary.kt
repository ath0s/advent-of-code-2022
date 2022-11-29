import java.nio.file.Path
import kotlin.io.path.readLines

internal class Binary(
    private val bits: Array<Bit>
) : Iterable<Bit> {

    override fun toString() =
        bits.joinToString("")

    fun toInt() =
        toString().toInt(2)

    fun toLong() =
        toString().toLong(2)

    operator fun get(index: Int) =
        bits[index]

    operator fun not() =
        Binary(bits.map { !it }.toTypedArray())

    override fun iterator() =
        bits.iterator()
}

internal fun Collection<Bit>.toBinary() =
    Binary(toTypedArray())

internal fun String.toBinary() =
    Binary(map { it.toBit() }.toTypedArray())

internal fun Path.readBinaries() =
    readLines().map { it.toBinary() }