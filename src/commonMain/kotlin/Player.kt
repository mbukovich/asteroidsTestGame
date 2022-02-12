import com.soywiz.korge.view.Container
import com.soywiz.korge.view.solidRect
import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.Angle

class Player(
        private val initX: Double,
        private val initY: Double,
        private val initAngle: Angle): Container() {
    init {
        solidRect(30, 10, color = Colors.DEEPSKYBLUE) {
            x = -15.0
            y = 10.0
        }
        solidRect(10, 30, color = Colors.DEEPSKYBLUE) {
            x = -5.0
            y = -15.0
        }
        x = initX
        y = initY
        rotation = initAngle
    }
}