package dev.kosmx.lowPassSimulator

import dev.kosmx.pseudoSalesman.Pos

/**
 * @param poses The ESP's pre-programmed positions
 * @param simStep The simulation time step
 * @param tau The time constant of the low-pass filter
 * @param stepT The ESP unit's position change speed
 */
fun eulerSim(poses: List<Pos>, simStep: Double = 0.002, tau: Double = 5.0, stepT: Double = 10.0): Sequence<Pos> {
    if (simStep > stepT) throw IllegalArgumentException("That will not work, signal step must be larger than the simulation step")
    return sequence {
        var stateVar = poses[0]

        for (pose in poses) {
            var timeUntilNextStep = stepT
            while (timeUntilNextStep > 0){
                timeUntilNextStep -= simStep
                stateVar += (pose - stateVar) / tau * simStep
                yield(stateVar)
            }
        }

    }
}