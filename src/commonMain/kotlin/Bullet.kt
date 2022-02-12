import com.soywiz.korge.view.Container
import com.soywiz.korge.view.ellipse
import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.Angle

class Bullet(
        private val initX: Double,
        private val initY: Double,
        private val initAngle: Angle): Container() {
            init {
                ellipse(3.0, 7.0, fill = Colors.GREEN) {
                    x = -1.5
                    y = -3.5
                }
                x = initX
                y = initY
                rotation = initAngle
            }
}