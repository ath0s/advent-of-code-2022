import Day.Main
import kotlin.io.path.readLines

class Day16 : Day {
    override fun partOne(filename: String, verbose: Boolean): Int {
        val valves = filename.asPath().readLines().parseValves()

        if (verbose) {
            valves.values.forEach {
                println(
                    """Valve ${it.name} has flow rate=${it.flowRate}; tunnels lead to valves ${it.tunnels.joinToString(", ")}"""
                )
            }
        }

        val maxOpenedValves = valves.values.count { it.flowRate > 0 }

        val start = valves["AA"]!!
        val startPath = SoloPath(
            valves = listOf(start),
            opened = emptyMap(),
        )
        var allPaths = listOf(startPath)
        var bestPath = startPath

        var time = 1

        while (time < 30) {
            val newPaths = mutableListOf<SoloPath>()

            for (currentPath in allPaths) {
                if (currentPath.opened.size == maxOpenedValves) {
                    continue
                }

                val currentLast = currentPath.last()
                val currentValves = currentPath.valves

                // open valve
                if (currentLast.flowRate > 0 && !currentPath.opened.containsKey(currentLast)) {
                    val opened = currentPath.opened.toMutableMap()
                    opened[currentLast] = time
                    val possibleValves = currentValves + currentLast
                    val possibleOpenedPath = SoloPath(possibleValves, opened)
                    newPaths.add(possibleOpenedPath)
                }

                // move to valve
                val possiblePaths = currentLast.tunnels.map { lead ->
                    val possibleValve = valves[lead]!!
                    val possibleValves = currentValves + possibleValve
                    val possiblePath = SoloPath(possibleValves, currentPath.opened)
                    possiblePath
                }

                newPaths.addAll(possiblePaths)
            }

            allPaths = newPaths.sortedByDescending { it.total() }.take(10_000)

            if (allPaths.first().total() > bestPath.total()) {
                bestPath = allPaths.first()
            }

            time++
        }

        return bestPath.total()
    }

    override fun partTwo(filename: String, verbose: Boolean): Int {
        val valves = filename.asPath().readLines().parseValves()
        val maxOpenedValves = valves.values.count { it.flowRate > 0 }

        val start = valves["AA"]!!
        val startPath = DuoPath(
            valvesFirst = listOf(start),
            valvesSecond = listOf(start),
            opened = emptyMap(),
        )
        var allPaths = listOf(startPath)
        var bestPath = startPath

        var time = 1

        while (time < 26) {
            val newPaths = mutableListOf<DuoPath>()

            for (currentPath in allPaths) {
                if (currentPath.opened.size == maxOpenedValves) {
                    continue
                }

                val currentLastMe = currentPath.lastFirst()
                val currentLastElephant = currentPath.lastSecond()
                val currentValvesMe = currentPath.valvesFirst
                val currentValvesElephant = currentPath.valvesSecond

                val openMe = currentLastMe.flowRate > 0 && !currentPath.opened.containsKey(currentLastMe)
                val openElephant =
                    currentLastElephant.flowRate > 0 && !currentPath.opened.containsKey(currentLastElephant)

                // open both, mine or elephant's valve
                if (openMe || openElephant) {
                    val opened = currentPath.opened.toMutableMap()

                    val possibleValvesMes = if (openMe) {
                        opened[currentLastMe] = time
                        listOf(currentValvesMe + currentLastMe)
                    } else {
                        currentLastMe.tunnels.map { lead ->
                            // add possible path and move on
                            val possibleValve = valves[lead]!!
                            val possibleValves = currentValvesMe + possibleValve
                            possibleValves
                        }
                    }

                    val possibleValvesElephants = if (openElephant) {
                        opened[currentLastElephant] = time
                        listOf(currentValvesElephant + currentLastElephant)
                    } else {
                        currentLastElephant.tunnels.map { lead ->
                            // add possible path and move on
                            val possibleValve = valves[lead] ?: error("valve $lead not found")
                            val possibleValves = currentValvesElephant + possibleValve
                            possibleValves
                        }
                    }

                    for (possibleValvesMe in possibleValvesMes) {
                        for (possibleValvesElephant in possibleValvesElephants) {
                            val possibleOpenedPath = DuoPath(possibleValvesMe, possibleValvesElephant, opened)
                            newPaths.add(possibleOpenedPath)
                        }
                    }
                }

                // move to valves
                val combinedLeads = currentLastMe.tunnels.flatMap { leadMe ->
                    currentLastElephant.tunnels.map { leadElephant ->
                        leadMe to leadElephant
                    }
                }.filter { (a, b) -> a != b }

                val possiblePaths: List<DuoPath> = combinedLeads.map { (leadFirst, leadSecond) ->
                    val possibleValveMe = valves[leadFirst]!!
                    val possibleValvesMe = currentValvesMe + possibleValveMe
                    val possibleValveElephant = valves[leadSecond]!!
                    val possibleValvesElephant = currentValvesElephant + possibleValveElephant
                    val possiblePath = DuoPath(possibleValvesMe, possibleValvesElephant, currentPath.opened)
                    possiblePath
                }

                newPaths.addAll(possiblePaths)
            }

            allPaths = newPaths.sortedByDescending { it.total() }.take(100_000)

            if (allPaths.first().total() > bestPath.total()) {
                bestPath = allPaths.first()
            }

            time++
        }

        return bestPath.total()
    }

    companion object : Main("Day16.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}

private val flowRatePattern = Regex("""rate=(\d+)""")
private val tunnelPattern = Regex("""([A-Z]{2})""")

private fun List<String>.parseValves() =
    map { line ->
        val name = line.substring("Value ".length).take(2)
        val flowRate = flowRatePattern.find(line)!!.destructured.let { (flowRate) -> flowRate.toInt() }
        val tunnels = tunnelPattern.findAll(line.substringAfter("tunnels")).map { it.value }.toList()
        Valve(name, flowRate, tunnels)
    }
        .associateBy { it.name }

private data class Valve(
    val name: String,
    val flowRate: Int,
    val tunnels: List<String>
)

private data class SoloPath(val valves: List<Valve>, val opened: Map<Valve, Int>) {

    fun last(): Valve = valves.last()

    fun total(): Int = opened.map { (valve, time) -> (30 - time) * valve.flowRate }.sum()
}

private data class DuoPath(val valvesFirst: List<Valve>, val valvesSecond: List<Valve>, val opened: Map<Valve, Int>) {

    fun lastFirst(): Valve = valvesFirst.last()
    fun lastSecond(): Valve = valvesSecond.last()

    fun total(): Int = opened.map { (valve, time) -> (26 - time) * valve.flowRate }.sum()
}
