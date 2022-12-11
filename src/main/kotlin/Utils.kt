import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.toPath
import kotlin.reflect.KClass
import kotlin.reflect.KFunction


/**
 * Converts string to md5 hash.
 */
internal fun String.md5(): String =
    BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)

/**
 * As a path on the classpath
 */
internal fun String.asPath() =
    asResourceUrl()!!.toURI().toPath()

internal fun <T> KClass<*>.newInstance(): T =
    constructors.filterIsInstance<KFunction<T>>().first().call()

fun <T, R> Iterable<T>.mapToSet(transform: (T) -> R): Set<R> =
    mapTo(mutableSetOf(), transform)

inline fun <T, R> Iterable<T>.flatMapToSet(transform: (T) -> Iterable<R>): Set<R> =
    flatMapTo(mutableSetOf(), transform)

fun <T> Iterable<Iterable<T>>.flattenToSet(): Set<T> =
    flatMapToSet { it }

private fun String.asResourceUrl() =
    Thread.currentThread().contextClassLoader.getResource(this)


fun Iterable<Long>.lcm(): Long {
    var multiplier = min()
    while (true) {
        if (all { multiplier % it == 0L }) {
            return multiplier
        }
        multiplier++
    }
}

operator fun <T> List<T>.component6(): T {
    return get(5)
}
