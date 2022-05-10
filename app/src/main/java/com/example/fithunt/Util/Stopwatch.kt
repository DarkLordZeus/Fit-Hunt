package com.example.fithunt.Util

import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.location.Location
import com.example.fithunt.Polyline
import java.util.concurrent.TimeUnit

object Stopwatch {
    fun getFormattedStopWatchTime(ms: Long, includeMillis: Boolean = false): String {

        val f: NumberFormat = DecimalFormat("00")

        var milliseconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        milliseconds -= TimeUnit.SECONDS.toMillis(seconds)
        milliseconds /= 10
        if(!includeMillis) {
            return "${f.format(hours)}:${f.format(minutes)}:${f.format(seconds)}"
        }
        else
            return "${f.format(hours)}:${f.format(minutes)}:${f.format(seconds)}:${f.format(milliseconds)}"

    }

    fun calculatepolylineDistance(polyline: Polyline): Float {
        var distance = 0f
        for(i in 0..polyline.size-2){

            val pos1 = polyline[i]
            val pos2 = polyline[i+1]
            val result = FloatArray(1)
            Location.distanceBetween(
                pos1.latitude,pos1.longitude
            ,pos2.latitude,pos2.longitude,
                result
            )
            distance += result[0]
        }
        return distance
    }
}