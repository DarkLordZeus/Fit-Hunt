package com.example.fithunt.FitHuntroomdatabase

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FitHuntRoomViewmodel @Inject constructor(val repository:FitHuntRoomRepository):ViewModel() {

    fun inserttrack(fitHuntEntity: FitHuntEntity){
        viewModelScope.launch(Dispatchers.IO){
            repository.inserttask(fitHuntEntity)
        }
    }
    fun deletetrack(fitHuntEntity: FitHuntEntity){
        viewModelScope.launch(Dispatchers.IO){
            repository.deletetask(fitHuntEntity)
        }
    }
    val readallTracksSortedbyDate = repository.readallTracksSortedbyDate()

    val readallTracksSortedbySpeed = repository.readallTracksSortedbySpeed()

    val readallTracksSortedbyDistance = repository.readallTracksSortedbyDistance()

    val readallTracksSortedbyDuration = repository.readallTracksSortedbyDuration()

    val readallTracksSortedbyCalories = repository.readallTracksSortedbyCalories()

    val sumoftimeofAllTracks = repository.sumoftimeofAllTracks()

    val sumofcaloriesburntofAllTracks = repository.sumofcaloriesburntofAllTracks()

    val sumofdistancesofAllTracks = repository.sumofdistancesofAllTracks()

    val avgspeedofAllTracks = repository.avgspeedofAllTracks()

    fun todaycalorieburnt(string: String): LiveData<Long> {
        return repository.todayscalorieburnt(string)
    }
    fun todayworkoutduration(string: String): LiveData<Long> {
        return repository.todaysworkoutduration(string)
    }
    fun todaytotalworkouts(string: String):LiveData<Long>{
        return repository.todayallworkout(string)
    }
}