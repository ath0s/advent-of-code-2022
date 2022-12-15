import org.intellij.lang.annotations.Language
import java.lang.reflect.ParameterizedType
import kotlin.test.Test
import kotlin.test.assertEquals

abstract class DayTest<D : Day>(
    @Language("file-reference") protected val filename: String? = null
) {

    open val partOneExpected: Any? = null
    open val partTwoExpected: Any? = null

    protected val target: D = ((javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<*>).kotlin.newInstance()

    @Test
    open fun `Part One`() {
        assumeNotNull(filename)
        assumeNotNull(partOneExpected)

        val result = target.partOne(filename, true)

        assertEquals(partOneExpected, result)
    }

    @Test
    open fun `Part Two`() {
        assumeNotNull(filename)
        assumeNotNull(partTwoExpected)

        val result = target.partTwo(filename, true)

        assertEquals(partTwoExpected, result)
    }
}
