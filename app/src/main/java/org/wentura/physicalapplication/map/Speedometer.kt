package org.wentura.physicalapplication.map

import org.wentura.physicalapplication.Constants

class Speedometer {
   
    var miles = false

    var speed: Double = 0.0
        get() {
            return if (miles) {
                field.times(Constants.MPH)
            } else {
                field.times(Constants.KMH)
            }
        }
}
