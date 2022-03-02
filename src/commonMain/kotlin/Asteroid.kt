import com.soywiz.klock.milliseconds
import com.soywiz.klock.timesPerSecond
import com.soywiz.korge.tween.tween
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors

class Asteroid(
        private val initX: Double,
        private val initY: Double,
        val asteroidPhysics: PhysicsComponent = PhysicsComponent()
): Container() {
    init {
        val asteroidcircle = circle(30.0, fill = Colors.DARKGRAY) {
            x = -30.0
            y = -30.0
        }
        x = initX
        y = initY
        asteroidcircle.onCollision (filter = {it != this && it != this.parent && it != topBar && it != basicText}) {
            it.removeFromParent()
            this.removeFromParent()
        }
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
            val changeInPosition =  asteroidPhysics.getChangeInPositionXYCoord((1.0 / 60.0), rotation)
            x += changeInPosition.x + xWarp
            y += changeInPosition.y + yWarp
        }
    }
}