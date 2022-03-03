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
import kotlin.random.Random
import com.soywiz.klock.timesPerSecond

// Initial Stage Dimensions
val topBorder = 30.0
val leftBorder = 0.0
val rightBorder = 512.0
val bottomBorder = 512.0 + topBorder

var bulletFired = false

val topBar: SolidRect = SolidRect(leftBorder + rightBorder, topBorder, color = Colors.SLATEGRAY)
val basicText = Text("Placeholder", textSize = topBorder - 10.0, color = Colors.BLUEVIOLET)

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
	val player = player(initialPlayerX, initialPlayerY, initialPlayerAngle, playerPhysics)Random

	/*val asteroidPhysics = PhysicsComponent(10000.0, 10.0, 5.0, 100000.0)
	val asteroid = asteroid(30.0, 300.0, 300.0, asteroidPhysics)*/


	/*
	SET UP TOP BAR
	 */
	// We do this after other components so that the top bar is on top of everything in terms of layers
	solidRect(leftBorder + rightBorder, topBorder, color = Colors.SLATEGRAY)
	this.addChild(topBar)
	this.addChild(basicText)
	basicText.x = 5.0
	basicText.y = 5.0

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

	addFixedUpdater(timesPerSecond = 0.25.timesPerSecond) {
		randomlyAddAsteroid(player.x, player.y)
	}
}

fun Container.randomlyAddAsteroid(playerX: Double, playerY: Double) {
	// Items to be randomly generated: which of the four borders to spawn from, specific spawning location, initial velocity, radius
	// The spawning location and which of the four borders can be combined into the same random calculation.
	// An asteroid shouldn't spawn too close to the player
	val randomPos = Random.nextDouble(4.0) // the random number that will determine the spawning location of the
	val randomXVelocity = Random.nextDouble(5.0)
	val randomYVelocity = Random.nextDouble(5.0)
	val randomRadius = Random.nextDouble(20.0)
	if (randomPos < 1.0) {
		// Starting from left border
		var asteroidY = ((bottomBorder - topBorder) * randomPos) + topBorder
		val playerAsteroidDiff = playerY - asteroidY
		if (-150.0 < playerAsteroidDiff && playerAsteroidDiff < 150.0) {
			if (asteroidY > (bottomBorder - topBorder) / 2.0)
				asteroidY -= (bottomBorder - topBorder) / 2.0
			else
				asteroidY += (bottomBorder - topBorder) / 2.0
		}
		asteroid(
				randomRadius + 20.0,
				leftBorder - 30.0,
				asteroidY,
				PhysicsComponent((10000.0 * (randomRadius + 40.0) / 30.0), randomXVelocity * randomXVelocity, randomYVelocity * randomYVelocity))
	}
	else if (1.0 <= randomPos && randomPos < 2.0) {
		// Starting from top border
		var asteroidX = rightBorder * (randomPos - 1.0)
		val playerAsteroidDiff = playerX - asteroidX
		if (-150.0 < playerAsteroidDiff && playerAsteroidDiff < 150.0) {
			if (asteroidX > rightBorder / 2.0)
				asteroidX -= rightBorder / 2.0
			else
				asteroidX += rightBorder / 2.0
		}
		asteroid(
				randomRadius + 20.0,
				asteroidX,
				topBorder - 30.0,
				PhysicsComponent((10000.0 * (randomRadius + 40.0) / 30.0), randomXVelocity * randomXVelocity, randomYVelocity * randomYVelocity))
	}
	else if (2.0 <= randomPos && randomPos < 3.0) {
		// Starting from right border
		var asteroidY = (bottomBorder - topBorder) * (randomPos - 2.0) + topBorder
		val playerAsteroidDiff = playerY - asteroidY
		if (-150.0 < playerAsteroidDiff && playerAsteroidDiff < 150.0) {
			if (asteroidY > (bottomBorder - topBorder) / 2.0)
				asteroidY -= (bottomBorder - topBorder) / 2.0
			else
				asteroidY += (bottomBorder - topBorder) / 2.0
		}
		asteroid(
				randomRadius + 20.0,
				rightBorder + 30.0,
				asteroidY,
				PhysicsComponent((10000.0 * (randomRadius + 40.0) / 30.0), randomXVelocity * randomXVelocity, randomYVelocity * randomYVelocity))
	}
	else {
		// Starting from bottom border
		var asteroidX = rightBorder * (randomPos - 3.0)
		val playerAsteroidDiff = playerX - asteroidX
		if (-150.0 < playerAsteroidDiff && playerAsteroidDiff < 150.0) {
			if (asteroidX > rightBorder / 2.0)
				asteroidX -= rightBorder / 2.0
			else
				asteroidX += rightBorder / 2.0
		}
		asteroid(
				randomRadius + 20.0,
				rightBorder * (randomPos - 3.0),
				bottomBorder + 30.0,
				PhysicsComponent((10000.0 * (randomRadius + 40.0) / 30.0), randomXVelocity * randomXVelocity, randomYVelocity * randomYVelocity))
	}
}

// Custom DSL functions
fun Container.player(
		initX: Double,
		initY: Double,
		initAngle: Angle,
		physicsComponent: PhysicsComponent) = Player(initX, initY, initAngle, physicsComponent).addTo(this)

fun Container.asteroid(
		asteroidRadius: Double,
		initX: Double,
		initY: Double,
		physicsComponent: PhysicsComponent) = Asteroid(asteroidRadius, initX, initY, physicsComponent).addTo(this)

fun Container.bullet(
		initX: Double,
		initY: Double,
		initAngle: Angle,
		physicsComponent: PhysicsComponent) = Bullet(initX, initY, initAngle, physicsComponent).addTo(this)