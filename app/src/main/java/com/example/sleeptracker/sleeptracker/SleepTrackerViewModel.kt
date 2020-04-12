package com.example.sleeptracker.sleeptracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.sleeptracker.database.SleepDatabaseDao
import com.example.sleeptracker.database.SleepNight
import com.example.sleeptracker.formatNights
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
    val database: SleepDatabaseDao,
    application: Application
) : AndroidViewModel(application) {
    //TODO:Define viewModelJob and assign it an instance of Job
    private var viewModelJob = Job()

    private val _navigateToSleepQuality = MutableLiveData<SleepNight>()

    val navigateToSleepQuality: LiveData<SleepNight>
        get() = _navigateToSleepQuality

   //TODO: Override onCleared() and cancel all coroutines.
    override fun onCleared() {
        super.onCleared()
       viewModelJob.cancel()
    }
    //TODO: Define a uiScope for the coroutines:
    private val uiScope = CoroutineScope(Dispatchers.Main +  viewModelJob)
    //TODO:Define a variable, tonight, to hold the current night,
    private var tonight = MutableLiveData<SleepNight?>()


    private val nights = database.getAllNights()
    //TODO: initializing tonight variable
    val nightsString = Transformations.map(nights) { nights ->
        formatNights(nights, application.resources)
    }
    init {
        initializeTonight()
    }
//TODO:  launch a coroutine in uiScope to get current Night Data from db.
    private fun initializeTonight() {
        uiScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }
    //TODO:Let the coroutine get tonight from the database. If the start and end times are the not the same, meaning, the night has already been completed, return null. Otherwise, return night:
    private suspend fun getTonightFromDatabase():  SleepNight? {
        return withContext(Dispatchers.IO) {
            var night = database.getTonight()

            if (night?.endTimeMilli != night?.startTimeMilli) {
                night = null
            }
            night
        }
    }
    //TODO:Implement onStartTracking(), the click handler for the Start button:
    fun onStartTracking() {
        //TODO:aunch a coroutine in uiScope
        uiScope.launch {
            val newNight = SleepNight()
            insert(newNight)
            tonight.value = getTonightFromDatabase()
        }
    }
    //TODO: For the body of insert(), launch a coroutine in the IO context and insert the night into the database:
    private suspend fun insert(night: SleepNight){
        withContext(Dispatchers.IO) {
            database.insert(night)
        }
    }
//TODO:Add onStopTracking() to the view model. Launch a coroutine in the uiScope.
    fun onStopTracking() {
        uiScope.launch {
            val oldNight = tonight.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)
            _navigateToSleepQuality.value = oldNight
        }
    }
    private suspend fun update(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.update(night)
        }
    }
    fun onClear() {
        uiScope.launch {
            clear()
            tonight.value = null
            _showSnackbarEvent.value = true
        }
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }
    //TODO Add a doneNavigating() function that resets the event.
    fun doneNavigating() {
        _navigateToSleepQuality.value = null
    }

    val startButtonVisible = Transformations.map(tonight) {
        null == it
    }
    val stopButtonVisible = Transformations.map(tonight) {
        null != it
    }
    val clearButtonVisible = Transformations.map(nights) {
        it?.isNotEmpty()
    }
    //TODO: create the encapsulated snackbar event:
    private var _showSnackbarEvent = MutableLiveData<Boolean>()

    val showSnackBarEvent: LiveData<Boolean>
        get() = _showSnackbarEvent

    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = false
    }



}