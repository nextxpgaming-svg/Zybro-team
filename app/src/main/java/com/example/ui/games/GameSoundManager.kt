package com.example.ui.games

import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.os.Vibrator
import android.os.VibrationEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object GameSoundManager {

    private var isSoundEnabled = true
    private var isVibrationEnabled = true

    fun setSoundEnabled(enabled: Boolean) {
        isSoundEnabled = enabled
    }

    fun setVibrationEnabled(enabled: Boolean) {
        isVibrationEnabled = enabled
    }

    // Synthesize simple 8-bit retro arcade sounds
    fun playBeep(frequency: Float, durationMs: Int) {
        if (!isSoundEnabled) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sampleRate = 8000
                val numSamples = (durationMs * sampleRate / 1000)
                val sample = ByteArray(numSamples)

                for (i in 0 until numSamples) {
                    val angle = 2.0 * Math.PI * i / (sampleRate / frequency)
                    sample[i] = (Math.sin(angle) * 127.0).toInt().toByte()
                }

                val audioTrack = AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_8BIT,
                    numSamples,
                    AudioTrack.MODE_STATIC
                )
                audioTrack.write(sample, 0, numSamples)
                audioTrack.play()
                // Wait briefly then release
                Thread.sleep(durationMs.toLong() + 50)
                audioTrack.release()
            } catch (e: Exception) {
                // Squelch
            }
        }
    }

    fun playClick() {
        playBeep(600f, 60)
    }

    fun playPop() {
        playBeep(440f, 100)
    }

    fun playScoreUp() {
        playBeep(880f, 120)
    }

    fun playSuccess() {
        playBeep(523.25f, 100) // C5
        Thread.sleep(120)
        playBeep(659.25f, 100) // E5
        Thread.sleep(120)
        playBeep(783.99f, 250) // G5
    }

    fun playGameOver() {
        playBeep(392f, 150) // G4
        Thread.sleep(180)
        playBeep(349.23f, 150) // F4
        Thread.sleep(180)
        playBeep(261.63f, 400) // C4
    }

    fun triggerHaptic(context: Context) {
        if (!isVibrationEnabled) return
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(30)
        }
    }

    fun triggerStrongHaptic(context: Context) {
        if (!isVibrationEnabled) return
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(100)
        }
    }
}
