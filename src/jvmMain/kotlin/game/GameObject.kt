package game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.openrndr.math.Vector2
import org.openrndr.math.mod
import utils.angle

abstract class ShipData : GameObject() {
    override val size: Double = 40.0
    open var visualAngle: Double = 0.0   // for pointing the head of spaceship and redirecting the bullets

    fun fire(game: Game) {
        val ship = this
        game.gameObjects.add(fireArm(ship))
    }

    abstract fun fireArm(shipData: ShipData): GameObject
}

class EnemyShipData : ShipData() {
    override val size: Double = 60.0
    override var visualAngle: Double = 0.0

    override fun fireArm(shipData: ShipData): GameObject {
        return EnemyBulletData(shipData.speed * 2.0, shipData.visualAngle, shipData.position)
    }
}

class UserShipData : ShipData() {
    override val size: Double = 40.0
    override var visualAngle: Double = 270.0

    override fun fireArm(shipData: ShipData): GameObject {
        return BulletData(80.0, shipData.visualAngle, shipData.position)
    }
}

class EnemyBulletData(speed: Double = 0.0, angle: Double = 0.0, position: Vector2 = Vector2.ZERO) :
    GameObject(speed, angle, position) {
    override val size: Double = 40.0
    var word: String = ""
    var isUnderAttack = false
}

class BulletData(speed: Double = 0.0, angle: Double = 0.0, position: Vector2 = Vector2.ZERO) :
    GameObject(speed, angle, position) {
    override val size: Double = 4.0
}

sealed class GameObject(speed: Double = 0.0, angle: Double = 0.0, position: Vector2 = Vector2.ZERO) {
    var speed by mutableStateOf(speed)
    var angle by mutableStateOf(angle)
    var position by mutableStateOf(position)
    var movementVector
        get() = (Vector2.UNIT_X * speed).rotate(angle)
        set(value) {
            speed = value.length
            angle = value.angle()
        }
    abstract val size: Double // Diameter

    open fun update(realDelta: Float, game: Game) {
        val obj = this
        val velocity = movementVector * realDelta.toDouble()
        obj.position += velocity
    }

    fun overlapsWith(other: GameObject): Boolean {
        // Overlap means the center of the game objects are closer together than the sum of their radii
        return this.position.distanceTo(other.position) < ((this.size + other.size) / 2)
    }
}
