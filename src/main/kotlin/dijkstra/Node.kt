package dijkstra

data class Node<T>(val value: T) {
    var shortestPath = emptyList<Node<T>>()
    var distance = Int.MAX_VALUE
    val adjacentNodes = mutableMapOf<Node<T>, Int>()
}