package dev.kosmx.pseudoSalesman.graphUtil

import dev.kosmx.pseudoSalesman.Loop

fun connectSets(node: GraphEntry<Loop>) {
    connectSetsInternal(node, null)
}

private fun connectSetsInternal(node: GraphEntry<Loop>, prev: GraphEntry<Loop>?) {

    for (edge in node.getNeighbours()) {
        if (edge === prev) continue
        connectLoops(node.t, edge.t)
        connectSetsInternal(edge, node)
    }
}

private fun connectLoops (a: Loop, b:Loop) {
    var closest = Pair(0, 0)
    var minDist = a.getPosList()[0] distance b.getPosList()[0]
    for (k in 1 until a.getPosList().size) {
        for (l in 0 until b.getPosList().size) {
            if (minDist > a.getPosList()[k] distance b.getPosList()[l]) {
                minDist = a.getPosList()[k] distance b.getPosList()[l]
                closest = Pair(k, l)
            }
        }
    }
    a[closest.first] = b.looper(closest.second)
}

