import com.soywiz.korge.view.Container
import com.soywiz.korge.view.addFixedUpdater
import com.soywiz.korge.view.solidRect
import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.Angle
import com.soywiz.klock.timesPerSecond

class Player(
        private val initX: Double,
        private val initY: Double,
        private val initAngle: Angle,
        val physicsComponent: PhysicsComponent = PhysicsComponent()): Container() {
    init {
        solidRect(10, 30, color = Colors.DEEPSKYBLUE) {
            x = -20.0
            y = -15.0
        }
        solidRect(30, 10, color = Colors.DEEPSKYBLUE) {
            x = -10.0
            y = -5.0
        }
        x = initX
        y = initY
        rotation = initAngle
        addFixedUpdater(timesPerSecond = 60.timesPerSecond) {
            var xWarp: Double = 0.0
            var yWarp: Double = 0.0
            if (x <= (leftBorder - 30.0)) {
                xWarp = rightBorder + 60
            }
            if (x >= (rightBorder + 30.0)) {
                xWarp = -rightBorder - 60
            }
            if (y <= (topBorder - 30.0)) {
                yWarp = bottomBorder - topBorder + 60
            }
            if (y >= (bottomBorder + 30.0)) {
                yWarp = -bottomBorder - 60 + topBorder
            }
            // x += (velocityXPpS / 60.0) + xWarp
            // y += (velocityYPpS / 60.0) + yWarp
            val changeInPosition =  physicsComponent.getChangeInPositionXYCoord((1.0 / 60.0), rotation)
            x += changeInPosition.x + xWarp
            y += changeInPosition.y + yWarp

            val updatedRotationalVelocity = rotation.radians + (physicsComponent.angularVelRpS * (1.0 / 6.0))
            rotation = Angle(updatedRotationalVelocity)
        }
    }
    val thrusterForce = Force(true, 100000.0)

    fun accelerateDown() {
        physicsComponent.applyForce(thrusterForce)
    }

    fun accelerateUp() {
        physicsComponent.removeForce(thrusterForce)
    }

    fun turnLeftDown() {
        physicsComponent.angularVelRpS = -0.5
    }

    fun turnLeftUp() {
        physicsComponent.angularVelRpS = 0.0
    }

    fun turnRightDown() {
        physicsComponent.angularVelRpS = 0.5
    }

    fun turnRightUp() {
        physicsComponent.angularVelRpS = 0.0
    }
}