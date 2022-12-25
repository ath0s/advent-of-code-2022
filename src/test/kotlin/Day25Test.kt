import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals

class Day25Test : DayTest<Day25>("Day25_test.txt") {
    override val partOneExpected = "2=-1=0"

    @ParameterizedTest
    @CsvSource(
        "1,1",
        "2,2",
        "3,1=",
        "4,1-",
        "5,10",
        "6,11",
        "7,12",
        "8,2=",
        "9,2-",
        "10,20",
        "15,1=0",
        "20,1-0",
        "2022,1=11-2",
        "12345,1-0---0",
        "314159265,1121-1110-1=0",
    )
    fun `Decimal to Snafu`(decimal: Long, snafu: String) {
        assertEquals(snafu, Snafu(decimal).toString())
    }

    @ParameterizedTest
    @CsvSource(
        "1=-0-2,1747",
        "12111,906",
        "2=0=,198",
        "21,11",
        "2=01,201",
        "111,31",
        "20012,1257",
        "112,32",
        "1=-1=,353",
        "1-12,107",
        "12,7",
        "1=,3",
        "122,37",
    )
    fun `Snafu to Decimal`(snafu: String, decimal: Long) {
        assertEquals(decimal, Snafu(snafu).value)
    }
}