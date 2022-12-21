import kotlin.io.path.readLines


class Day21 : Day {
    override fun partOne(filename: String, verbose: Boolean): Long {
        val variables = filename.asPath().readLines().parseExpressions()
        if(verbose) {
            variables.forEach { (name, expression) ->
                println("$name: $expression")
            }
        }

        val root = variables["root"] ?: throw IllegalArgumentException("No root!")
        return root()
    }

    override fun partTwo(filename: String, verbose: Boolean): Long {
        val variables = filename.asPath().readLines().parseExpressions()

        val root = variables["root"] ?: throw IllegalArgumentException("No root!")
        return isolate("humn", root as BinaryExpression)()
    }

    companion object : Day.Main("Day21.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }

    private sealed interface Expression {
        val name: String?

        operator fun invoke(): Long

        override fun toString(): String

        operator fun contains(other: String): Boolean
    }

    private class Literal(override val name: String, val value: Long): Expression {
        override fun invoke() = value

        override fun toString() = "$value"

        override fun contains(other: String) =
            other == name
    }

    private sealed class BinaryExpression(
        override val name: String? = null,
        val leftName: String?,
        val rightName: String?
    ) : Expression {
        lateinit var left: Expression
        lateinit var right: Expression

        constructor(left: Expression, right: Expression) : this(null, left.name, right.name) {
            this.left = left
            this.right = right
        }

        override fun contains(other: String) =
            other == name || other in left || other in right
    }

    private class Addition : BinaryExpression {
        constructor(name: String, leftName: String, rightName: String) : super(name, leftName, rightName)
        constructor(left: Expression, right: Expression): super(left, right)
        override fun invoke() =
            left() + right()

        override fun toString() = "$left + $right"
    }

    private class Subtraction : BinaryExpression {
        constructor(name: String, leftName: String, rightName: String) : super(name, leftName, rightName)
        constructor(left: Expression, right: Expression): super(left, right)
        override fun invoke() =
            left() - right()

        override fun toString() = "$left - $right"
    }

    private class Multiplication : BinaryExpression {
        constructor(name: String, leftName: String, rightName: String) : super(name, leftName, rightName)
        constructor(left: Expression, right: Expression): super(left, right)

        override fun invoke() =
            left() * right()

        override fun toString() = "$left * $right"
    }

    private class Division : BinaryExpression {
        constructor(name: String, leftName: String, rightName: String) : super(name, leftName, rightName)
        constructor(left: Expression, right: Expression): super(left, right)

        override fun invoke() =
            left() / right()

        override fun toString() = "$left / $right"
    }

    private class Equals(left: Expression, right: Expression) : BinaryExpression(left, right) {

        override fun invoke(): Long {
            throw IllegalStateException("Should not get invoked")
        }

        override fun toString() = "$left = $right"

    }

    private fun List<String>.parseExpressions() =
        map { it.parseExpression() }.associateBy { it.name }.also {
            it.values.filterIsInstance<BinaryExpression>().forEach { expression ->
                expression.left = it[expression.leftName]!!
                expression.right = it[expression.rightName]!!
            }
        }

    private fun String.parseExpression(): Expression =
        split(':').let { (name, expression) ->
            expression.trim().run {
                when {
                    contains('+') -> {
                        val (left, right) = split('+')
                        Addition(name, left.trim(), right.trim())
                    }

                    contains('-') -> {
                        val (left, right) = split('-')
                        Subtraction(name, left.trim(), right.trim())
                    }

                    contains('*') -> {
                        val (left, right) = split('*')
                        Multiplication(name, left.trim(), right.trim())
                    }

                    contains('/') -> {
                        val (left, right) = split('/')
                        Division(name, left.trim(), right.trim())
                    }

                    else -> Literal(name, trim().toLong())
                }
            }
        }


    private fun isolate(
        name: String,
        expression: BinaryExpression
    ): Expression {
             fun solve(
                a: Expression,
                b: Expression
            ): Expression {
                val ee = Equals(a, b)
                return isolate(name, ee)
            }

            fun solve(): Expression {
                when (name) {
                    expression.leftName -> return expression.right
                    expression.rightName -> return expression.left
                }

                val x = if (name in expression.left) {
                    expression.left
                } else {
                    expression.right
                }
                if(x !is BinaryExpression) {
                    throw IllegalStateException("Unexpected expression '$x'")
                }

                return if (name in x.left) {
                    when (x) {
                        is Addition -> solve(x.left, Subtraction(expression.right, x.right))
                        is Subtraction -> solve(x.left, Addition(expression.right, x.right))
                        is Multiplication -> solve(x.left, Division(expression.right, x.right))
                        is Division -> solve(x.left, Multiplication(expression.right, x.right))
                        else -> throw IllegalStateException("Unexpected binary expression: ${x::class.simpleName}")
                    }
                } else {
                    when (x) {
                        is Addition -> solve(x.right, Subtraction(expression.right, x.left))
                        is Subtraction -> solve(x.right, Subtraction(x.left, expression.right))
                        is Multiplication -> solve(x.right, Division(expression.right, x.left))
                        is Division -> solve(x.right, Division(x.left, expression.right))
                        else -> throw IllegalStateException("Unexpected binary expression: ${x::class.simpleName}")
                    }
                }
            }
        return solve()
    }
}

