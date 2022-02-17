import com.soywiz.klock.seconds
import com.soywiz.korev.Key
import com.soywiz.korge.*
import com.soywiz.korge.input.keys
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.degrees
import com.soywiz.korma.interpolation.Easing

// Initial Stage Dimensions
val topBorder = 30.0
val leftBorder = 0.0
val rightBorder = 512.0
val bottomBorder = 512.0 + topBorder

suspend fun main() = Korge(
		width = (leftBorder + rightBorder).toInt(),
		height = bottomBorder.toInt(),
		bgcolor = Colors["#2b2b2b"]) {
	val initialPlayerX = 100.0 + leftBorder
	val initialPlayerY = 100.0 + topBorder
	val initialPlayerAngle = Angle(0.0)


	/*
	SET UP GAME AREA
	 */
	val player = player(initialPlayerX, initialPlayerY, initialPlayerAngle)

	val asteroid = asteroid(300.0, 300.0)

	val bullet = bullet(100.0, 40.0, initialPlayerAngle)


	/*
	SET UP TOP BAR
	 */
	// We do this after other components so that the top bar is on top of everything in terms of layers
	solidRect(leftBorder + rightBorder, topBorder, color = Colors.SLATEGRAY)
	val basicText = text("Placeholder", textSize = topBorder - 10.0, color = Colors.BLUEVIOLET) {
		x = 5.0
		y = 5.0
	}

	/*
	SET UP INPUT
	 */
	keys {
		down {
			when (it.key) {
				Key.UP, Key.W -> basicText.text = "UP pressed"
				Key.DOWN, Key.S -> basicText.text = "DOWN pressed"
				Key.LEFT, Key.A -> basicText.text = "LEFT pressed"
				Key.RIGHT, Key.D -> basicText.text = "RIGHT pressed"
				Key.SPACE -> basicText.text = "SPACE pressed"
			}
		}
		up {
			when (it.key) {
				Key.UP, Key.W -> basicText.text = "UP released"
				Key.DOWN, Key.S -> basicText.text = "DOWN released"
				Key.LEFT, Key.A -> basicText.text = "LEFT released"
				Key.RIGHT, Key.D -> basicText.text = "RIGHT released"
				Key.SPACE -> basicText.text = "SPACE released"
			}
		}
	}
}

// Custom DSL functions
fun Container.player(
		initX: Double,
		initY: Double,
		initAngle: Angle) = Player(initX, initY, initAngle).addTo(this)

fun Container.asteroid(initX: Double, initY: Double) = Asteroid(initX, initY).addTo(this)

fun Container.bullet(
		initX: Double,
		initY: Double,
		initAngle: Angle) = Bullet(initX, initY, initAngle).addTo(this)