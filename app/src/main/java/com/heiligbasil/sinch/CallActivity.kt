package com.heiligbasil.sinch

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sinch.android.rtc.PushPair
import com.sinch.android.rtc.Sinch
import com.sinch.android.rtc.calling.Call
import com.sinch.android.rtc.calling.CallListener

class CallActivity : AppCompatActivity() {
    var call: Call? = null
    var callButton: Button? = null
    var callState: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        if (ContextCompat.checkSelfPermission(this@CallActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this@CallActivity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@CallActivity, arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE),
                    1)
        }

        val sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(USER_ID)
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT)
                .build()
        sinchClient.setSupportCalling(true)
        sinchClient.start()

        callState = findViewById<View>(R.id.callState) as TextView
        callButton = findViewById<View>(R.id.callButton) as Button

        callButton?.setOnClickListener {
            if (call == null) {
                call = sinchClient.callClient.callPhoneNumber(PHONE_NO)
                call?.addCallListener(SinchCallListener())
                callButton?.text = "Hang Up"
            } else {
                call?.hangup()
            }
        }
    }

    private inner class SinchCallListener : CallListener {
        override fun onCallEnded(endedCall: Call) {
            call = null
            callButton?.text = "Call"
            callState?.text = ""
            volumeControlStream = AudioManager.USE_DEFAULT_STREAM_TYPE
        }

        override fun onCallEstablished(establishedCall: Call) {
            callState?.text = "connected"
            volumeControlStream = AudioManager.STREAM_VOICE_CALL
        }

        override fun onCallProgressing(progressingCall: Call) {
            callState?.text = "ringing"
        }

        override fun onShouldSendPushNotification(call: Call, pushPairs: List<PushPair>) {}
    }

    companion object {
        private const val USER_ID = "PRIVATE"
        private const val APP_KEY = "PRIVATE"
        private const val APP_SECRET = "PRIVATE"
        private const val ENVIRONMENT = "PRIVATE"
        private const val PHONE_NO = "PRIVATE"
    }
}