package com.ttpsc.dynamics365fieldService.helpers.models

class Vector3 {
    companion object {
        const val HASH_CODE_BASE = 31
    }

    var x: Float
    var y: Float
    var z: Float

    constructor() {
        this.x = 0f
        this.y = 0f
        this.z = 0f
    }

    constructor(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    operator fun plus(other: Vector3): Vector3 {
        return Vector3(x + other.x, y + other.y, z + other.z)
    }

    operator fun times(scalar: Float): Vector3 {
        return Vector3(x * scalar, y * scalar, z * scalar)
    }

    operator fun times(scalar: Vector3): Vector3 {
        return Vector3(x * scalar.z, y * scalar.y, z * scalar.z)
    }

    override fun equals(other: Any?): Boolean {
        if (other is Vector3) {
            return other.x == x && other.y == y && other.z == z
        }
        return false
    }

    override fun toString(): String {
        return "($x, $y, $z)"
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = HASH_CODE_BASE * result + y.hashCode()
        result = HASH_CODE_BASE * result + z.hashCode()
        return result
    }
}