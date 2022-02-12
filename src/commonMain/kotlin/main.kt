import com.soywiz.klock.seconds
import com.soywiz.korge.*
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
	val initialPlayerX = 100.0
	val initialPlayerY = 100.0
	val initialPlayerAngle = Angle(0.0)

	/*
	SET UP TOP BAR
	 */
	solidRect(leftBorder + rightBorder, topBorder, color = Colors.SLATEGRAY)
	val basicText = text("Placeholder", textSize = topBorder - 10.0, color = Colors.BLUEVIOLET) {
		x = 5.0
		y = 5.0
	}

	val player = player(initialPlayerX, initialPlayerY, initialPlayerAngle)

	val asteroid = asteroid(300.0, 300.0)

	val bullet = bullet(100.0, 10.0, initialPlayerAngle)
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