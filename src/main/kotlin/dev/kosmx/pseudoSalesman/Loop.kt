package dev.kosmx.pseudoSalesman

fun buildLoops(posSet: Collection<Pos>): List<Loop> {
    val positions: MutableSet<Pos> = posSet.toMutableSet()
    val maxDistance = connectDistance(posSet)

    val loops = mutableListOf<Loop>()
    while (positions.isNotEmpty()) {

        var pos = positions.iterator().next()
        val loop = Loop()
        loop += pos;
        positions.remove(pos)

        while (true) {
            var nextPos: Pos? = null
            for (candidate in positions) {
                if (candidate distance pos < maxDistance) {
                    nextPos = candidate
                    break
                }
            }

            if (nextPos != null) {
                pos = nextPos
                positions.remove(nextPos)
                continue
            }
            //TODO check if the loop is finished
            break
        }
        loops.add(loop)
    }
    return loops
}

fun connectDistance(positions: Collection<Pos>): Double {
    return 0.1 //TODO set it to a correct value
}

/**
 * Node loop for the solver
 */
open class Loop() {
    private val nodeList = mutableListOf<Any>()

    fun getPosList(): List<Pos> {
        return nodeList.filterIsInstance<Pos>()
    }

    operator fun plusAssign(pos: Pos) {
        this.nodeList.add(pos)
    }

    operator fun set(index: Int, v: Iterable<*>) {
        var entry = index
        var i = 0
        while (i++ < entry) {
            if (nodeList[i] !is Pos) entry++
        }

        nodeList.add(index, v)
    }

    fun size(): Int {
        return nodeList.size
    }

    fun looper(index: Int): Looper {
        var entry = index
        var i = 0
        while (i++ < entry) {
            if (nodeList[i] !is Pos) entry++
        }
        return Looper(this, entry)
    }

    operator fun get(index: Int): Any {
        return nodeList[index]
    }
}

class Looper(private val loop: Loop, private val entry:Int = 0): Iterable<Pos> {
    init {
        if (entry > loop.size()) throw IllegalArgumentException("Can not enter in higher index than the size of the loop")
    }

    override fun iterator(): Iterator<Pos> {
        return iterator {
            var pos = entry
            while (true) {
                val current = loop[pos]
                if (current is Iterable<*>) {
                    for (subEntry in current) {
                        yield(subEntry as Pos)
                    }
                }
                else if(current is Pos) {
                    yield(current)
                }
                pos += 1 % loop.size()
                if (pos == entry) break
            }
        }
    }
}
