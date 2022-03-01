import com.soywiz.klock.TimeSpan
import com.soywiz.korio.async.delay
import com.soywiz.korio.async.launch
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.cosine
import com.soywiz.korma.geom.sine
import kotlin.coroutines.CoroutineContext
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.sqrt

class PhysicsComponent(
        val massKg: Double = 5.0,
        var velocityXPpS: Double = 0.0,
        var velocityYPpS: Double = 0.0,
        var momentOfInertiaKgM2: Double = 100.0,
        var angularVelRpS: Double = 0.0,
        var pixelToMeter: Double = 1.0,
        val speedLimitPpS: Double = 1000.0,
        val rotationalSpeedLimitRpS: Double = 1000.0,
        val forces: MutableList<Force> = mutableListOf(),
        val torques: MutableList<Torque> = mutableListOf(),
        var requireUniqueForce: Boolean = true) {
    fun applyForce(force: Force) {
        if (requireUniqueForce) {
            if (!forces.contains(force))
                forces.add(force)
        }
        else
            forces.add(force)
    }

    fun applyForceForTime(context: CoroutineContext, force: Force, time: TimeSpan) {
        launch(context) {
            if (requireUniqueForce) {
                if (!forces.contains(force))
                    forces.add(force)
            }
            else
                forces.add(force)
            delay(time)
            forces.remove(force)
        }
    }

    fun removeForce(force: Force) {
        if (forces.contains(force))
            forces.remove(force)
    }

    fun applyTorque(torque: Torque) {
        if (requireUniqueForce) {
            if (!torques.contains(torque))
                torques.add(torque)
        }
        else
            torques.add(torque)
    }

    fun removeTorque(torque: Torque) {
        if (torques.contains(torque))
            torques.remove(torque)
    }

    fun applyTorqueForTime(context: CoroutineContext, torque: Torque, time: TimeSpan) {
        launch(context) {
            if (requireUniqueForce) {
                if (!torques.contains(torque))
                    torques.add(torque)
            }
            else
                torques.add(torque)
            delay(time)
            torques.remove(torque)
        }
    }

    fun getChangeInPositionXYCoord(dt: Double, globalDirection: Angle, updateVelocity: Boolean = true): XYCoord {
        var xAccel = 0.0
        var yAccel = 0.0
        forces.forEach {
            xAccel += it.getForceXGlobal(globalDirection) / massKg
            yAccel += it.getForceYGlobal(globalDirection) / massKg
        }
        var newXVelocity = velocityXPpS + xAccel * dt
        var newYVelocity = velocityYPpS + yAccel * dt
        if (sqrt(newXVelocity * newXVelocity + newYVelocity * newYVelocity) > speedLimitPpS){
            val accelDirectionRadians = correctForArcTan(xAccel, yAccel, atan(yAccel/xAccel))
            val velocityDirectionRadians = correctForArcTan(velocityXPpS, velocityYPpS, atan(velocityYPpS/velocityXPpS))
            val angleDifference = velocityDirectionRadians - accelDirectionRadians
            if (angleDifference != 0.0) {
                var angleAdjustment = PI / 2.0
                val accelMagnitude = sqrt((xAccel * xAccel) + (yAccel * yAccel))
                if (angleDifference <= (PI / -2.0)) {
                    // In this case, the acceleration direction is in the fourth quadrant and velocity direction is in the first quadrant.
                    // Accel angle needs to decrease by PI / 2 radians
                    angleAdjustment *= -1.0
                }
                else if (velocityDirectionRadians > accelDirectionRadians && angleDifference <= (PI / 2.0))
                    angleAdjustment *= -1.0
                xAccel = accelMagnitude * Angle(accelDirectionRadians + angleAdjustment).cosine
                yAccel = accelMagnitude * Angle(accelDirectionRadians + angleAdjustment).sine
                newXVelocity = velocityXPpS + xAccel * dt
                newYVelocity = velocityYPpS + yAccel * dt
            }
            else {
                // In this case, the user is just accelerating in the direction of the speed limit
                xAccel = 0.0
                yAccel = 0.0
                newXVelocity = velocityXPpS
                newYVelocity = velocityYPpS
            }
        }
        val newX = (velocityXPpS * dt) + 0.5 * xAccel * dt * dt
        val newY = (velocityYPpS * dt) + 0.5 * yAccel * dt * dt
        if (updateVelocity) {
            velocityXPpS = newXVelocity
            velocityYPpS = newYVelocity
        }
        return XYCoord(newX, newY)
    }

    fun updateVelocityXY(dt: Double, globalDirection: Angle) {
        var xAccel = 0.0
        var yAccel = 0.0
        forces.forEach {
            xAccel += it.getForceXGlobal(globalDirection) / massKg
            yAccel += it.getForceYGlobal(globalDirection) / massKg
        }
        var newXVelocity = velocityXPpS + xAccel * dt
        var newYVelocity = velocityYPpS + yAccel * dt
        if (sqrt(newXVelocity * newXVelocity + newYVelocity * newYVelocity) > speedLimitPpS){
            val accelDirectionRadians = correctForArcTan(xAccel, yAccel, atan(yAccel/xAccel))
            val velocityDirectionRadians = correctForArcTan(velocityXPpS, velocityYPpS, atan(velocityYPpS/velocityXPpS))
            val angleDifference = velocityDirectionRadians - accelDirectionRadians
            if (angleDifference != 0.0) {
                var angleAdjustment = PI / 2.0
                val accelMagnitude = sqrt((xAccel * xAccel) + (yAccel * yAccel))
                if (angleDifference <= (PI / -2.0)) {
                    // In this case, the acceleration direction is in the fourth quadrant and velocity direction is in the first quadrant.
                    // Accel angle needs to decrease by PI / 2 radians
                    angleAdjustment *= -1.0
                }
                else if (velocityDirectionRadians > accelDirectionRadians && angleDifference <= (PI / 2.0))
                    angleAdjustment *= -1.0
                xAccel = accelMagnitude * Angle(accelDirectionRadians + angleAdjustment).cosine
                yAccel = accelMagnitude * Angle(accelDirectionRadians + angleAdjustment).sine
                newXVelocity = velocityXPpS + xAccel * dt
                newYVelocity = velocityYPpS + yAccel * dt
            }
            else {
                // In this case, the user is just accelerating in the direction of the speed limit
                xAccel = 0.0
                yAccel = 0.0
                newXVelocity = velocityXPpS
                newYVelocity = velocityYPpS
            }
        }
        velocityXPpS = newXVelocity
        velocityYPpS = newYVelocity
    }

    fun getChangeInDirection(dt: Double, updateVelocity: Boolean = true): Angle {
        var rotationalAccel = 0.0
        torques.forEach {
            rotationalAccel += if (it.isPos)
                it.magnitudeKgPPpSS / momentOfInertiaKgM2
            else
                -1 * it.magnitudeKgPPpSS / momentOfInertiaKgM2
        }
        // We also factor in the torque due to forces on the object
        forces.forEach {
            rotationalAccel += if (it.localTorque.isPos)
                it.localTorque.magnitudeKgPPpSS / momentOfInertiaKgM2
            else
                -1 * it.localTorque.magnitudeKgPPpSS / momentOfInertiaKgM2
        }
        var newRadians = 0.0
        val newAngularVelocity = angularVelRpS + rotationalAccel * dt
        if ((-1 * rotationalSpeedLimitRpS) <= newAngularVelocity && newAngularVelocity <= rotationalSpeedLimitRpS) {
            newRadians = (angularVelRpS * dt) + 0.5 * rotationalAccel * dt * dt
            angularVelRpS = newAngularVelocity
        }
        else {
            newRadians = angularVelRpS * dt
        }
        return Angle(newRadians)
    }

    fun updateRotationalVelocity(dt: Double) {
        var rotationalAccel = 0.0
        torques.forEach {
            rotationalAccel += if (it.isPos)
                it.magnitudeKgPPpSS / momentOfInertiaKgM2
            else
                -1 * it.magnitudeKgPPpSS / momentOfInertiaKgM2
        }
        // We also factor in the torque due to forces on the object
        forces.forEach {
            rotationalAccel += if (it.localTorque.isPos)
                it.localTorque.magnitudeKgPPpSS / momentOfInertiaKgM2
            else
                -1 * it.localTorque.magnitudeKgPPpSS / momentOfInertiaKgM2
        }
        if ((-1.0 * rotationalSpeedLimitRpS) <= (angularVelRpS + rotationalAccel * dt) && (angularVelRpS + rotationalAccel * dt) <= rotationalSpeedLimitRpS)
            angularVelRpS += rotationalAccel * dt
    }
}

class XYCoord(val x: Double = 0.0, val y: Double = 0.0)

class Force(
        val isForceLocal: Boolean = false,
        var magnitude: Double = 0.0,
        var direction: Angle = Angle(0.0),
        var localX: Double = 0.0,
        var localY: Double = 0.0,) {
    var localTorque = Torque()

    fun setForceByXandY(xForceKgPpSS: Double, yForceKgPpSS: Double) {
        magnitude = sqrt((xForceKgPpSS * xForceKgPpSS) + (yForceKgPpSS * yForceKgPpSS))
        direction = Angle(correctForArcTan(xForceKgPpSS, yForceKgPpSS, atan(yForceKgPpSS/xForceKgPpSS)))
        // Adjust the torque due to the force as well if the force is offset
        if (localX != 0.0 && localY != 0.0)
            setLocalTorqueByXY(xForceKgPpSS, yForceKgPpSS)
    }

    fun setForceByMagnitudeAndAngle(mag: Double, angle: Angle) {
        magnitude = mag
        direction = angle
        // Adjust the torque due to the force as well if the force is offset
        if (localX != 0.0 && localY != 0.0) {
            setLocalTorqueByXY(getForceX(), getForceY())
        }
    }

    fun setLocalTorqueByXY(xForceKgPpSS: Double, yForceKgPpSS: Double) {
        localTorque.magnitudeKgPPpSS = (localX * yForceKgPpSS) + (-1.0 * localY * xForceKgPpSS)
        if (localTorque.magnitudeKgPPpSS < 0.0) {
            localTorque.magnitudeKgPPpSS *= -1.0
            localTorque.isPos = false
        }
        else
            localTorque.isPos = true
    }

    fun adjustMagnitude(increaseKgPpSS: Double) {
        magnitude += increaseKgPpSS
    }

    fun adjustAngle(adjustmentRadians: Double) {
        val currentAngle = direction.radians
        direction = Angle(currentAngle + adjustmentRadians)
    }

    fun adjustForceX(adjustmentKgPpSS: Double) {
        var xForce = getForceX()
        val yForce = getForceY()
        xForce += adjustmentKgPpSS
        setForceByXandY(xForce, yForce)
    }

    fun adjustForceY(adjustmentKgPpSS: Double) {
        val xForce = getForceX()
        var yForce = getForceY()
        yForce += adjustmentKgPpSS
        setForceByXandY(xForce, yForce)
    }

    fun getForceX(): Double = magnitude * direction.cosine

    fun getForceY(): Double = magnitude * direction.sine

    fun getForceXGlobal(globalAngle: Angle): Double {
        return if (isForceLocal) {
            // convert local x component to global
            // We do this by combining the local and global angles
            val totalAngle = Angle(direction.radians + globalAngle.radians)
            magnitude * totalAngle.cosine
        }
        else {
            // The Force is already global, so we just give the existing x component
            magnitude * direction.cosine
        }
    }

    fun getForceYGlobal(globalAngle: Angle): Double {
        return if (isForceLocal) {
            // convert local y component to global
            // We do this by combining the local and global angles
            val totalAngle = Angle(direction.radians + globalAngle.radians)
            magnitude * totalAngle.sine
        }
        else {
            // The Force is already global, so we just give the existing y component
            magnitude * direction.sine
        }
    }
}

class Torque(var magnitudeKgPPpSS: Double = 0.0, var isPos: Boolean = true) {
    fun setTorque(magKgPPpSS: Double, directionPos: Boolean) {
        magnitudeKgPPpSS = magKgPPpSS
        isPos = directionPos
    }

    fun adjustMagnitude(adjustmentKgPPpSS: Double) {
        magnitudeKgPPpSS += adjustmentKgPPpSS
    }

    fun setDirection(isPositiveDir: Boolean) {
        isPos = isPositiveDir
    }
}

fun correctForArcTan(x: Double, y: Double, angleRadians: Double): Double {
    if (y > 0.0) {
        if (x > 0.0) {
            // Positive X and Positive Y
            // This is the first quadrant from 0 to PIE/2. this case the angle from the atan Kotlin function will be accurate
            return angleRadians
        }
        else if (x < 0.0) {
            // Negative X and Positive Y
            // this is the second quadrant where the Kotlin atan function gives the angle going in a negative direction from PIE radians
            return PI + angleRadians
        }
        else {
            // Zero X and Positive Y
            // The angle is PIE/2 in this case
            return PI / 2.0
        }
    }
    else if (y < 0.0) {
        if (x < 0.0) {
            // Negative X and Negative Y
            // This is the third quadrant where the Kotlin atan function gives a positive angular distance from PIE
            return PI + angleRadians
        }
        else if (x > 0.0) {
            // Positive X and Negative Y
            // This is the fourth quadrant where the Kotlin atan function gives a negative angle representing the angular distance from 2 * PIE
            return 2.0 * PI + angleRadians
        }
        else {
            // Zero X and Negative Y
            // In this case, the angle is PIE * 1.5
            return PI * 1.5
        }
    }
    else {
        if (x < 0.0) {
            // Negative X and zero Y
            return PI
        }
        else if (x > 0.0) {
            // Positive X and zero Y
            return 0.0
        }
        else {
            // zero X and zero Y
            return 0.0
        }
    }
}