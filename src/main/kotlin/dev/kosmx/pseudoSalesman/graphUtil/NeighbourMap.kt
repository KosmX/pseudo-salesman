package dev.kosmx.pseudoSalesman.graphUtil

import java.util.function.BiFunction
import java.util.function.Supplier

class NeighbourMap<K, V>(collection: Collection<K>, defaultSupplier: (K, K) -> V): MatrixMap<K, K, V>() {
    init {
        for (r in collection) {
            for (c in collection) {
                this[r, c] = defaultSupplier.invoke(r, c)
            }
        }
    }
}