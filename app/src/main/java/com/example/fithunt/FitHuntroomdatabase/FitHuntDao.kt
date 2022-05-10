package com.example.fithunt.FitHuntroomdatabase

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FitHuntDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addtask(fitHuntEntity: FitHuntEntity)

    @Delete
    suspend fun deletetask(fitHuntEntity: FitHuntEntity)

    @Query("SELECT * FROM FitHuntTable ORDER BY timestamp DESC")
    fun readallTracksSortedbyDate():LiveData<List<FitHuntEntity>>

    @Query("SELECT * FROM FitHuntTable ORDER BY avgspeedinKMH DESC")
    fun readallTracksSortedbySpeed():LiveData<List<FitHuntEntity>>

    @Query("SELECT * FROM FitHuntTable ORDER BY distanceinMeters DESC")
    fun readallTracksSortedbyDistance():LiveData<List<FitHuntEntity>>

    @Query("SELECT * FROM FitHuntTable ORDER BY timeinmills DESC")
    fun readallTracksSortedbyDuration():LiveData<List<FitHuntEntity>>

    @Query("SELECT * FROM FitHuntTable ORDER BY caloriesburnt DESC")
    fun readallTracksSortedbyCalories():LiveData<List<FitHuntEntity>>

    @Query ("SELECT SUM(timeinmills) FROM fithunttable")
    fun sumoftimeofAllTracks():LiveData<Long>

    @Query ("SELECT SUM(caloriesburnt) FROM fithunttable")
    fun sumofcaloriesburntofAllTracks():LiveData<Long>

    @Query ("SELECT SUM(distanceinMeters) FROM fithunttable")
    fun sumofdistancesofAllTracks():LiveData<Long>

    @Query ("SELECT AVG(avgspeedinKMH) FROM fithunttable")
    fun avgspeedofAllTracks():LiveData<Float>

    @Query ( "SELECT SUM(caloriesburnt) FROM fithunttable WHERE timestamp=:string")
    fun todayscalorieburnt(string: String):LiveData<Long>

    @Query ( "SELECT SUM(timeinmills) FROM fithunttable WHERE timestamp=:string")
    fun todayworkoutduration(string: String):LiveData<Long>
    //@Query("SELECT SUM()")
    @Query("SELECT COUNT(timestamp) FROM fithunttable WHERE timestamp=:string ")
    fun todaystotalworkout(string: String):LiveData<Long>
}