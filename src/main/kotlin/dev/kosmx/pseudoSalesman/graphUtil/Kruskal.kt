package dev.kosmx.pseudoSalesman.graphUtil

import dev.kosmx.pseudoSalesman.Loop

fun buildMatrix(loops: Collection<Loop>): NeighbourMap<GraphEntry<Loop>, Double> {
    return NeighbourMap(loops.map { GraphEntry(it) }) { a, b ->
        var min = a.t.getPosList()[0] distance b.t.getPosList()[0]

        for (varA in a.t.getPosList()) {
            for(varB in b.t.getPosList()) {
                if (varA distance varB < min) {
                    min = varA distance varB
                }
            }
        }

        min
    }
}


fun kruskal (loops: Collection<Loop>): GraphEntry<Loop> {
    val matrix = buildMatrix(loops)

    val edges = mutableListOf<Triple<GraphEntry<Loop>, GraphEntry<Loop>, Double>>()

    for (edge in matrix) {
        if (edge.first === edge.second) continue
        edges.add(edge)
    }
    edges.sortBy { it.third }

    for (edge in edges) {
        if (!(edge.first isPathTo edge.second)) {
            edge.first.setNeighbour(edge.second)
        }
    }
    return edges.iterator().next().first
}
