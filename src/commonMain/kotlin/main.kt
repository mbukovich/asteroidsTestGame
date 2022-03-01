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
import com.soywiz.korma.geom.cosine
import com.soywiz.korma.geom.degrees
import com.soywiz.korma.geom.sine
import com.soywiz.korma.interpolation.Easing
import kotlin.math.atan

// Initial Stage Dimensions
val topBorder = 30.0
val leftBorder = 0.0
val rightBorder = 512.0
val bottomBorder = 512.0 + topBorder

var bulletFired = false

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
	val playerPhysics = PhysicsComponent(massKg = 1000.0, momentOfInertiaKgM2 = 10000.0, speedLimitPpS = 200.0)
	val player = player(initialPlayerX, initialPlayerY, initialPlayerAngle, playerPhysics)

	val asteroidPhysics = PhysicsComponent(10000.0, 10.0, 5.0, 100000.0)
	val asteroid = asteroid(300.0, 300.0, asteroidPhysics)


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
				Key.UP, Key.W -> {
					basicText.text = "UP pressed"
					player.accelerateDown()
				}
				Key.DOWN, Key.S -> basicText.text = "DOWN pressed"
				Key.LEFT, Key.A -> {
					basicText.text = "LEFT pressed"
					player.turnLeftDown()
				}
				Key.RIGHT, Key.D -> {
					basicText.text = "RIGHT pressed"
					player.turnRightDown()
				}
				Key.SPACE -> {
					basicText.text = "SPACE pressed"
					if (!bulletFired) {
						bulletFired = true
						val initialX = player.x + (23.5 * player.rotation.cosine)
						val initialY = player.y + (23.5 * player.rotation.sine)
						val bulletPhysicsComponent = PhysicsComponent(
								massKg = 100.0,
								velocityXPpS = (550.0 * player.rotation.cosine),
								velocityYPpS = (550.0 * player.rotation.sine))
						bullet(initX = initialX, initY = initialY, initAngle = player.rotation, physicsComponent = bulletPhysicsComponent)
					}
				}
			}
		}
		up {
			when (it.key) {
				Key.UP, Key.W -> {
					basicText.text = "UP released"
					player.accelerateUp()
				}
				Key.DOWN, Key.S -> basicText.text = "DOWN released"
				Key.LEFT, Key.A -> {
					basicText.text = "LEFT released. Radians: " + atan(player.rotation.sine/player.rotation.cosine).toString()
					player.turnLeftUp()
				}
				Key.RIGHT, Key.D -> {
					basicText.text = "RIGHT released. Radians: " + atan(player.rotation.sine/player.rotation.cosine).toString()
					player.turnRightUp()
				}
				Key.SPACE -> {
					basicText.text = "SPACE released"
					bulletFired = false
				}
			}
		}
	}
}

// Custom DSL functions
fun Container.player(
		initX: Double,
		initY: Double,
		initAngle: Angle,
		physicsComponent: PhysicsComponent) = Player(initX, initY, initAngle, physicsComponent).addTo(this)

fun Container.asteroid(
		initX: Double,
		initY: Double,
		physicsComponent: PhysicsComponent) = Asteroid(initX, initY, physicsComponent).addTo(this)

fun Container.bullet(
		initX: Double,
		initY: Double,
		initAngle: Angle,
		physicsComponent: PhysicsComponent) = Bullet(initX, initY, initAngle, physicsComponent).addTo(this)