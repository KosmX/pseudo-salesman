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

        print("Building loop: ")

        while (true) {
            var nextPos: Pos? = null
            var nextDistance = Double.MAX_VALUE
            for (candidate in positions) {
                if (candidate distance pos < nextDistance) {
                    nextPos = candidate
                    nextDistance = candidate distance pos
                }
            }

            if (nextDistance < maxDistance && nextPos != null) {
                pos = nextPos
                positions.remove(nextPos)
                loop += nextPos
                print("$nextDistance; ")
                continue
            }
            //TODO check if the loop is finished
            break
        }
        loops.add(loop)
        print("\n")
    }
    return loops
}

/**
 * Build loops, with a different approach.
 * Don't need to re-order the original set, just find where it jumps
 */
fun buildLoopsV2(poseList: List<Pos>): List<Loop> {
    var prev: Pos = poseList[0]
    val loops = mutableListOf<Loop>()
    val maxDistnace = connectDistance(poseList)

    var currentLoop: Loop? = null

    for (pos in poseList) {
        if (currentLoop != null) {
            if (currentLoop.size() < 2 || (pos distance prev < maxDistnace + currentLoop.endJump())) {
                currentLoop += pos
            } else {
                println("Ending loop, big jump is required, distance: ${prev distance pos}")
                currentLoop = null
            }
        }

        if (currentLoop == null) {
            currentLoop = Loop()
            currentLoop += pos
            loops.add(currentLoop)
        }

        prev = pos
    }

    return loops
}

fun connectDistance(positions: Collection<Pos>): Double {
    return 2.0 //TODO set it to a correct value
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

    override fun toString(): String {
        return "Loop: $nodeList"
    }

    fun endJump(): Double {
        return getPosList().let { it[0] distance it[it.size-1] }
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
