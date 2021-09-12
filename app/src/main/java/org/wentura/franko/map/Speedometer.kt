package org.wentura.franko.map

import org.wentura.franko.Constants

class Speedometer {

    var unitsOfMeasure = Constants.METRIC

    var speed: Double = 0.0
        get() {
            return if (unitsOfMeasure == Constants.IMPERIAL) {
                field.times(Constants.MPH)
            } else {
                field.times(Constants.KMH)
            }
        }
}
