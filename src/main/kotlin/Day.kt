import org.intellij.lang.annotations.Language
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

interface Day {

    fun partOne(@Language("file-reference") filename: String, verbose: Boolean = false): Number

    fun partTwo(@Language("file-reference") filename: String, verbose: Boolean = false): Number

    abstract class Main(@Language("file-reference") private val filename: String) {

        @OptIn(ExperimentalTime::class)
        fun main() {
            val day = Day()

            println(day::class.simpleName)

            measureTimedValue { day.partOne(filename) }.run {
                println("Part One: $value\t($duration)")
            }

            measureTimedValue { day.partTwo(filename) }.run {
                println("Part Two: $value\t($duration)")
            }

        }

        private fun Day() =
            javaClass.declaringClass.kotlin.newInstance<Day>()
    }
}

