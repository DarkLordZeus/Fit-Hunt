package com.example.fithunt.FitHuntroomdatabase

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "FitHuntTable")
data class FitHuntEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    var bitmap:Bitmap? = null,
    var timestamp: String ,   //date it was started
    var avgspeedinKMH:Float,
    var distanceinMeters:Int,
    var timeinmills:Long,
    var caloriesburnt:Long,
    var excercisetype:String
)
