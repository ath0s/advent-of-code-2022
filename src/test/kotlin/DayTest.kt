import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assumptions.assumeFalse
import java.lang.reflect.ParameterizedType
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.test.Test
import kotlin.test.assertEquals

abstract class DayTest<D : Day>(
    @Language("file-reference") private val filename: String? = null
) {

    open val partOneExpected: Number = -1
    open val partTwoExpected: Number = -1

    protected val target: D = ((javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<*>).kotlin.newInstance()

    @Test
    fun `Part One`() {
        assumeNotNull(filename)
        assumeFalse(partOneExpected.toInt() < 0)

        val result = target.partOne(filename, true)

        assertEquals(partOneExpected, result)
    }

    @Test
    fun `Part Two`() {
        assumeNotNull(filename)
        assumeFalse(partTwoExpected.toInt() < 0)

        val result = target.partTwo(filename, true)

        assertEquals(partTwoExpected, result)
    }

    @OptIn(ExperimentalContracts::class)
    private fun assumeNotNull(value: Any?) {
        contract {
            returns() implies (value != null)
        }
        assumeFalse(value == null)
    }

}