import Day.Main
import kotlin.io.path.useLines
import kotlin.math.max

class Day19 : Day {
    override fun partOne(filename: String, verbose: Boolean): Int =
        filename.asPath().useLines { lines ->
            lines.map { it.parseRobotCosts() }.foldIndexed(0) { index, quality, robotCosts ->
                val geodeSimulator = GeodeSimulator(robotCosts, 24)
                quality + (index + 1) * geodeSimulator.mostGeodes(oreRobots = 1)
            }
        }

    override fun partTwo(filename: String, verbose: Boolean): Int =
        filename.asPath().useLines { lines ->
            lines.take(3).map { it.parseRobotCosts() }.fold(1) { quality, robotCosts ->
                val geodeSimulator = GeodeSimulator(robotCosts, 32)
                quality * geodeSimulator.mostGeodes(oreRobots = 1)
            }
        }

    companion object : Main("Day19.txt") {

        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}

private val intPattern = Regex("(\\d+)")

private data class RobotCosts(
    val oreRobotOreCost: Int,
    val clayRobotOreCost: Int,
    val obsidianRobotOreCost: Int,
    val obsidianRobotClayCost: Int,
    val geodeRobotOreCost: Int,
    val geodeRobotObsidianCost: Int
)

private fun String.parseRobotCosts() =
    intPattern.findAll(substringAfter(":"))
        .map { it.value.toInt() }
        .toList()
        .let { (oreOre, clayOre, obsidianOre, obsidianClay, geodeOre, geodeObsidian) ->
            RobotCosts(
                oreOre,
                clayOre,
                obsidianOre,
                obsidianClay,
                geodeOre,
                geodeObsidian
            )
        }

private class GeodeSimulator(
    private val cost: RobotCosts,
    private val maxTime: Int,
) {
    private var geodeBest = 0

    fun mostGeodes(
        ore: Int = 0,
        clay: Int = 0,
        obsidian: Int = 0,
        geodes: Int = 0,
        oreRobots: Int = 0,
        clayRobots: Int = 0,
        obsidianRobots: Int = 0,
        geodeRobots: Int = 0,
        time: Int = 0
    ): Int {
        if (time == maxTime) {
            geodeBest = max(geodeBest, geodes)
            return geodes
        }

        val timeLeft = maxTime - time
        val maxGeodesPossible = geodes + (0 until timeLeft).sumOf { geodeRobots + it }
        if (maxGeodesPossible < geodeBest) {
            return 0
        }

        val no = ore + oreRobots
        val nc = clay + clayRobots
        val nob = obsidian + obsidianRobots
        val ng = geodes + geodeRobots

        if (ore >= cost.geodeRobotOreCost &&
            obsidian >= cost.geodeRobotObsidianCost
        ) {
            return mostGeodes(
                no - cost.geodeRobotOreCost,
                nc,
                nob - cost.geodeRobotObsidianCost,
                ng,
                oreRobots,
                clayRobots,
                obsidianRobots,
                geodeRobots + 1,
                time + 1
            )
        }
        if (clayRobots >= cost.obsidianRobotClayCost &&
            obsidianRobots < cost.geodeRobotObsidianCost &&
            ore >= cost.obsidianRobotOreCost &&
            clay >= cost.obsidianRobotClayCost
        ) {
            return mostGeodes(
                no - cost.obsidianRobotOreCost,
                nc - cost.obsidianRobotClayCost,
                nob,
                ng,
                oreRobots,
                clayRobots,
                obsidianRobots + 1,
                geodeRobots,
                time + 1
            )
        }

        var best = 0
        if (obsidianRobots < cost.geodeRobotObsidianCost &&
            ore >= cost.obsidianRobotOreCost &&
            clay >= cost.obsidianRobotClayCost
        ) {
            best = max(
                best,
                mostGeodes(
                    no - cost.obsidianRobotOreCost,
                    nc - cost.obsidianRobotClayCost,
                    nob,
                    ng,
                    oreRobots,
                    clayRobots,
                    obsidianRobots + 1,
                    geodeRobots,
                    time + 1
                )
            )
        }
        if (clayRobots < cost.obsidianRobotClayCost
            && ore >= cost.clayRobotOreCost
        ) {
            best = max(
                best,
                mostGeodes(
                    no - cost.clayRobotOreCost,
                    nc,
                    nob,
                    ng,
                    oreRobots,
                    clayRobots + 1,
                    obsidianRobots,
                    geodeRobots,
                    time + 1
                )
            )
        }
        if (oreRobots < 4 && ore >= cost.oreRobotOreCost) {
            best = max(
                best,
                mostGeodes(
                    no - cost.oreRobotOreCost,
                    nc,
                    nob,
                    ng,
                    oreRobots + 1,
                    clayRobots,
                    obsidianRobots,
                    geodeRobots,
                    time + 1
                )
            )
        }
        if (ore <= 4) {
            best = max(
                best,
                mostGeodes(
                    no,
                    nc,
                    nob,
                    ng,
                    oreRobots,
                    clayRobots,
                    obsidianRobots,
                    geodeRobots,
                    time + 1
                )
            )
        }
        return best
    }
}