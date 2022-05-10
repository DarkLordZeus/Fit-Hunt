package com.example.fithunt.FitHuntroomdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fithunt.Util.Converter

@Database(entities=[FitHuntEntity::class],version=1,exportSchema=false)

@TypeConverters(Converter::class)
abstract class FitHuntDatabase: RoomDatabase(){

    abstract fun FitHuntDao():FitHuntDao

//    companion object{
//        @Volatile
//        var INSTANCE:FitHuntDatabase?=null
//
//        fun getDatabase(context: Context):FitHuntDatabase{
//            val tempInstance=INSTANCE
//            if(tempInstance!=null){
//                return tempInstance
//            }
//            synchronized(this){
//                val instance= Room.databaseBuilder(
//                    context.applicationContext,
//                    FitHuntDatabase::class.java,
//                    "user_database"
//                ).allowMainThreadQueries()
//                    .build()
//                INSTANCE=instance
//                return instance
//
//            }
//        }
//    }

}
