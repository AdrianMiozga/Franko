package org.wentura.franko.map

import org.wentura.franko.Constants

class Speedometer {

    var speed: Double = 0.0
        get() {
            return field.times(Constants.MS_TO_KMH)
        }
}
