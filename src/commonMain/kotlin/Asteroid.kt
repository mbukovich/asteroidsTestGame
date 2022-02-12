import com.soywiz.korge.view.Container
import com.soywiz.korge.view.circle
import com.soywiz.korim.color.Colors

class Asteroid(private val initX: Double, private val initY: Double): Container() {
    init {
        circle(30.0, fill = Colors.DARKGRAY) {
            x = -30.0
            y = -30.0
        }
        x = initX
        y = initY
    }
}