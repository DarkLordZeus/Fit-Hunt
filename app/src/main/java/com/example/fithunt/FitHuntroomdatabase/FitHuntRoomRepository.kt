package com.example.fithunt.FitHuntroomdatabase

import androidx.lifecycle.LiveData
import androidx.room.Query
import javax.inject.Inject

class FitHuntRoomRepository @Inject constructor(
    val fitHuntDao: FitHuntDao
) {
    suspend fun inserttask(fitHuntEntity: FitHuntEntity)
    {
        fitHuntDao.addtask(fitHuntEntity)
    }
    suspend fun deletetask(fitHuntEntity: FitHuntEntity)
    {
        fitHuntDao.deletetask(fitHuntEntity)
    }

    fun readallTracksSortedbyDate() = fitHuntDao.readallTracksSortedbyDate()

    fun readallTracksSortedbySpeed() = fitHuntDao.readallTracksSortedbySpeed()

    fun readallTracksSortedbyDistance() = fitHuntDao.readallTracksSortedbyDistance()

    fun readallTracksSortedbyDuration() = fitHuntDao.readallTracksSortedbyDuration()

    fun readallTracksSortedbyCalories() = fitHuntDao.readallTracksSortedbyCalories()

    fun sumoftimeofAllTracks() = fitHuntDao.sumoftimeofAllTracks()

    fun sumofcaloriesburntofAllTracks() = fitHuntDao.sumofcaloriesburntofAllTracks()

    fun sumofdistancesofAllTracks() = fitHuntDao.sumofdistancesofAllTracks()

    fun avgspeedofAllTracks() = fitHuntDao.avgspeedofAllTracks()

    fun todayscalorieburnt(string: String):LiveData<Long> {
        return fitHuntDao.todayscalorieburnt(string)
    }

    fun todaysworkoutduration(string: String):LiveData<Long> {
        return fitHuntDao.todayworkoutduration(string)
    }

    fun todayallworkout(string: String):LiveData<Long>{
        return fitHuntDao.todaystotalworkout(string)
    }

}