import Bit.*

@Suppress("EnumEntryName")
internal enum class Bit {
    `0`,
    `1`;

    operator fun not() = when (this) {
        `0` -> `1`
        `1` -> `0`
    }

    fun toInt() = when (this) {
        `0` -> 0
        `1` -> 1
    }

}

internal fun Char.toBit() =
    when (this) {
        '0' -> `0`
        '1' -> `1`
        else -> throw IllegalArgumentException("$this is neither 0 nor 1")
    }