import org.junit.jupiter.api.Assumptions.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
internal fun assumeNotNull(value: Any?) {
    contract {
        returns() implies (value != null)
    }
    assumeTrue(value != null)
}