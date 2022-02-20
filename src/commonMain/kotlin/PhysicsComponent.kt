import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.cosine
import com.soywiz.korma.geom.sine
import kotlin.math.atan
import kotlin.math.sqrt

class PhysicsComponent(
        val massKg: Double = 5.0,
        var velocityXPpS: Double = 0.0,
        var velocityYPpS: Double = 0.0,
        var momentOfInertiaKgM2: Double = 100.0,
        var angularVelPosRpS: Double = 0.0,
        var angularVelNegRpS: Double = 0.0,
        var pixelToMeter: Double = 1.0,
        val forces: MutableList<Force> = mutableListOf(),
        val torques: MutableList<Torque> = mutableListOf()) {
    fun applyForce(force: Force) {
        forces.add(force)
    }

    fun removeForce(force: Force) {
        if (forces.contains(force))
            forces.remove(force)
    }

    fun applyTorque(torque: Torque) {
        torques.add(torque)
    }

    fun removeTorque(torque: Torque) {
        if (torques.contains(torque))
            torques.remove(torque)
    }

    fun getChangeInPositionXYCoord(dt: Double, globalDirection: Angle, updateVelocity: Boolean = true): XYCoord {
        var xAccel = 0.0
        var yAccel = 0.0
        forces.forEach {
            xAccel += it.getForceXGlobal(globalDirection) / massKg
            yAccel += it.getForceYGlobal(globalDirection) / massKg
        }
        val newX = ((velocityXPpS * dt) + (0.5 * xAccel * dt * dt))
        val newY = ((velocityYPpS * dt) + (0.5 * yAccel * dt * dt))
        if (updateVelocity) {
            velocityXPpS += xAccel * dt
            velocityYPpS += yAccel * dt
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
        velocityXPpS += xAccel * dt
        velocityYPpS += yAccel * dt
    }
}

class XYCoord(val x: Double = 0.0, val y: Double = 0.0)

class Force(
        val isForceLocal: Boolean = false,
        var magnitude: Double = 0.0,
        var direction: Angle = Angle(0.0)) {
    fun setForceByXandY(xForceKgPpSS: Double, yForceKgPpSS: Double) {
        magnitude = sqrt((xForceKgPpSS * xForceKgPpSS) + (yForceKgPpSS * yForceKgPpSS))
        direction = Angle(atan(yForceKgPpSS/xForceKgPpSS))
    }

    fun setForceByMagnitudeAndAngle(mag: Double, angle: Angle) {
        magnitude = mag
        direction = angle
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