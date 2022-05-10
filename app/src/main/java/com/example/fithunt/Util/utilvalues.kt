package com.example.fithunt.Util

import android.graphics.Color
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import java.util.regex.Pattern.compile

class utilvalues {
    companion object{
        const val databasename="FitHuntDatabase"

        const val REQUEST_CODE_LOCATION_PERMISSION=0

        const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"

        const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"

        const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"

        const val CHANNEL_NAME="CHANNELNAME"

        const val  CHANNEL_ID="CHANNELID"

        const val NOTIFICATION_ID =100

        const val ACTION_SHOW_TRACKING_FRAGMENT="ACTION_SHOW_TRACKING_FRAGMENT"

        const val POLYLINE_COLOR = Color.RED

        const val POLYLINE_WIDTH = 8f

        const val MAP_ZOOM = 15f

        const val MetWalk = 3

        const val MetRun = 9.8

        const val  MetCycling= 8

        const val MetTreadmill = 9.0

        const val MetHiking = 6.5

        const val MetSwimming = 5.8

        const val MetYoga = 3

        const val Metcalisthenics = 3.5

        const val MetPilates = 3.8

        var thisexercisemet=0f

        var listforadapter=MutableLiveData<Int>()

        var typeofexercise="Running"

        var steps=MutableLiveData<Int>(0)

        var moving=false
        var totalsteps=0f
        var previoustotalsteps=MutableLiveData<Float>(0f)
        var issamedate=true
        var maxsteps=6000
        var todaysglassofwater=MutableLiveData<Int>(0)
        var todayminutes=MutableLiveData<Long>(0L)

        val emailRegex = compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )

        var imageprofiledpuri=MutableLiveData<Uri>(null)

        var nameofuser=MutableLiveData<String>("--")
        var ageofuser=MutableLiveData<String>("--")
        var weightofuser=MutableLiveData("--")
        var heightofuser=MutableLiveData("--")
        var bloodgrpofuser=MutableLiveData("--")
        var genderofuser=MutableLiveData("--")
        var bmiofuser="--"

        val quotes = arrayOf("¨Look in the mirror. That’s your competition.¨ – John Assaraf",
            "¨Tough times don’t last. Tough people do.¨ – Robert H. Schuller",
            "¨A feeble body weakens the mind.¨ – Jean-Jacques Rousseau",
            "¨The groundwork for all happiness is good health.¨ – Leigh Hunt",
            "¨Reading is to the mind what exercise is to the body.¨- Joseph Addison",
            "¨Success is what comes after your stop making excuses.¨ – Luis Galarza",
            "“The successful warrior is the average man, with laser-like focus.”- bruce Lee",
            "¨Discipline is the bridge between goals and accomplishment.¨ – Jim Rohn")


        var shouldloadbio=false
    }
}