package dev.kosmx.pseudoSalesman.graphUtil

open class GraphEntry<T>(val t:T) {
    private val neighbours: MutableSet<GraphEntry<T>> = mutableSetOf()

    fun setNeighbour(other: GraphEntry<T>): Boolean {
        if (other === this) throw IllegalArgumentException("Graph entry can not be neighbour with itself")

        if (neighbours.contains(other)) return true
        neighbours.add(other)
        other.neighbours.add(this)
        return false
    }

    infix fun isNeighbour (other: GraphEntry<T>) = neighbours.contains(other)

    infix fun isPathTo (other: GraphEntry<T>): Boolean {
        return recursiveDFSSearch(other, mutableSetOf())
    }

    private fun recursiveDFSSearch(other: GraphEntry<T>, touched: MutableSet<GraphEntry<T>>): Boolean {
        touched.add(this)
        for (neighbour in neighbours) {
            if (other == neighbour) return true
            if (neighbour !in touched) {
                if (neighbour.recursiveDFSSearch(other, touched)) return true
            }
        }
        return false
    }

    fun getNeighbours() = neighbours.toSet()

}