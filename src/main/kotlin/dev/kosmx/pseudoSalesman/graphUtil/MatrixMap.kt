package dev.kosmx.pseudoSalesman.graphUtil

import java.util.function.BiFunction

/**
 * Matrix for Kruskal
 *
 * @param R type of rows
 * @param C type of columns
 * @param V type of values
 */
open class MatrixMap<R, C, V>() : Iterable<Triple<R, C, V>> {
    private val mutableMap: MutableMap<R, MutableMap<C, V>> = mutableMapOf()


    operator fun get(r: R, c: C): V {
        try {
            return mutableMap[r]!![c]!!
        } catch (v: NullPointerException) {
            throw java.util.NoSuchElementException("MatrixMap has no element in [${r}, ${c}]")
        }
    }

    operator fun set(r: R, c:C, v:V): V? {
        if (mutableMap[r] == null) {
            mutableMap[r] = mutableMapOf()
        }

        return mutableMap[r]!!.put(c, v)
    }

    fun setEntries(function: BiFunction<R, C, V>) {
        for (r in mutableMap) {
            for (c in r.value) {
                this[r.key, c.key] = function.apply(r.key, c.key)
            }
        }
    }

    override fun iterator(): Iterator<Triple<R, C, V>> {
        return iterator {
            for (r in mutableMap) {
                for (c in r.value) {
                    yield(Triple(r.key, c.key, c.value))
                }
            }
        }
    }


    override fun equals(other: Any?): Boolean {
        return super.equals(other) && other is MatrixMap<*, *, *> && other.mutableMap == this.mutableMap
    }
    override fun hashCode(): Int {
        return mutableMap.hashCode()
    }
}