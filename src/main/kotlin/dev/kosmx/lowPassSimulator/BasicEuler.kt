package dev.kosmx.lowPassSimulator

import dev.kosmx.pseudoSalesman.Pos

/**
 * @param poses The ESP's pre-programmed positions
 * @param simStep The simulation time step
 * @param tau The time constant of the low-pass filter
 * @param stepT The ESP unit's position change speed
 * @param tWarmup Time before start drawing. makes the loop more accurate
 */
fun eulerSim(poses: List<Pos>, simStep: Double = 0.008, tau: Double = 5.0, stepT: Double = 10.0, tWarmup: Double = 100.0): Sequence<Pos> {
    if (simStep > stepT) throw IllegalArgumentException("That will not work, signal step must be larger than the simulation step")
    return sequence {
        var stateVar = poses[0]

        var time = 0.0
        val maxTime = stepT * poses.size
        val exitTime = maxTime + tWarmup


        while (time < exitTime) {
            time += simStep
            val pose = poses[(time.mod(maxTime) / stepT).toInt()]

            stateVar += (pose - stateVar) / tau * simStep
            if (time >= tWarmup) {
                yield(stateVar)
            }
        }

    }
}