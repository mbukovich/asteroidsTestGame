import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.Angle
import com.soywiz.klock.timesPerSecond
import com.soywiz.korge.view.*

class Bullet(
        private val initX: Double,
        private val initY: Double,
        private val initAngle: Angle,
        val physicsComponent: PhysicsComponent = PhysicsComponent()): Container() {
            init {
                val bulletView = ellipse(7.0, 3.0, fill = Colors.GREEN) {
                    x = -3.5
                    y = -1.5
                }
                /*bulletView.onCollisionShape (filter = {it != this && it != this.parent && it != topBar && it != basicText}) {
                    it.removeFromParent()
                    this.removeFromParent()
                }*/
                x = initX
                y = initY
                rotation = initAngle
                addFixedUpdater(timesPerSecond = 60.timesPerSecond) {
                    if (y < (topBorder - 30))
                        this.removeFromParent()
                    if (y > (bottomBorder + 30.0))
                        this.removeFromParent()
                    if (x < (leftBorder - 30))
                        this.removeFromParent()
                    if (x > (rightBorder + 30.0))
                        this.removeFromParent()
                    val updatedPosition = physicsComponent.getChangeInPositionXYCoord((1.0 / 60.0), rotation)
                    x += updatedPosition.x
                    y += updatedPosition.y
                }
            }
}