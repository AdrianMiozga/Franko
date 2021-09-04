package org.wentura.physicalapplication.map

class Speedometer(speed: Double = 0.0) {
    var speed: Double = speed
        get() {
            // MP/H
//            return field.times(2.23694)
            // KM/H
            return field.times(3.6)
        }
}