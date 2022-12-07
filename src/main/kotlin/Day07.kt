import kotlin.io.path.readLines

class Day07 : Day {
    override fun partOne(filename: String, verbose: Boolean): Int {
        val tree = parseTree(filename)
        if (verbose) {
            tree.print()
        }
        return tree.allSubDirectories.map { it.size }.filter { it < 100_000 }.sum()
    }

    override fun partTwo(filename: String, verbose: Boolean): Any {
        val tree = parseTree(filename)
        val usedSpace = tree.size
        val unusedSpace = 70_000_000 - usedSpace
        val neededSpace = 30_000_000 - unusedSpace
        if (verbose) {
            println("Used space:\t\t$usedSpace")
            println("Unused space:\t$unusedSpace")
            println("Needed space:\t$neededSpace")
        }
        return tree.allSubDirectories.map { it.size }.filter { it >= neededSpace }.min()
    }

    private fun parseTree(filename: String): Directory {
        val lines = filename.asPath().readLines()
        val tree = Directory(null, "/")
        var currentDirectory = tree
        var listing = false
        lines.forEach { line ->
            when {
                line.startsWith("$ cd") -> {
                    val (_, _, argument) = line.split(" ", limit = 3).takeIf { it.size == 3 }
                        ?: throw IllegalArgumentException("Cannot parse $line")
                    currentDirectory = when (argument) {
                        ".." -> currentDirectory.parent ?: currentDirectory
                        "/" -> tree
                        else -> currentDirectory.subDirectories[argument]
                            ?: throw IllegalArgumentException("No subdirectory $argument")
                    }
                }

                line == "$ ls" -> listing = true
                else -> if (listing) {
                    currentDirectory += line.parseFileType(currentDirectory)
                        ?: throw IllegalArgumentException("Expected file listing")
                } else {
                    throw IllegalStateException("Unexpected line $line")
                }
            }

        }
        return tree
    }

    companion object : Day.Main("Day07.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }

}

private sealed interface FileType {
    val parent: Directory?
    val name: String
    val size: Int

    fun print(indentation: Int = 0)
}

private fun String.parseFileType(parent: Directory): FileType? =
    when {
        startsWith("$") -> null
        startsWith("dir") -> split(" ").let { (_, name) -> Directory(parent, name) }
        else -> split(" ").let { (size, name) -> File(parent, name, size.toInt()) }
    }

private data class Directory(override val parent: Directory?, override val name: String) : FileType {

    override val size
        get() =
            children.sumOf { it.size }

    override fun toString() = "$name (dir)"

    private val children = mutableListOf<FileType>()

    operator fun plusAssign(child: FileType) {
        children += child
    }

    val subDirectories
        get() =
            children.filterIsInstance<Directory>()

    val allSubDirectories
        get() : List<Directory> =
            subDirectories.flatMap { listOf(it) + it.allSubDirectories }

    override fun print(indentation: Int) {
        println(" ".repeat(indentation) + "- $this")
        children.forEach {
            it.print(indentation + 2)
        }
    }
}

private data class File(override val parent: Directory, override val name: String, override val size: Int) : FileType {
    override fun toString() = "$name (file, size=$size)"

    override fun print(indentation: Int) {
        println(" ".repeat(indentation) + "- $this")
    }
}

private operator fun <T : FileType> Iterable<T>.get(name: String) =
    firstOrNull { it.name == name }
