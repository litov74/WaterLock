package ru.litov74dev.waterlock.services

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import ru.litov74dev.waterlock.MainActivity

class AppTileService : TileService() {

    companion object {

        const val SERVICE_STATUS_FLAG = "SERVICE_STATUS_FLAG"
        const val PREFERENCES_KEY = "ru.litov74dev.waterlock"

    }

    override fun onClick() {
        val isActive = getTileStatus()
        qsTile.state = if (isActive) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        if (isActive) {
            launchProtection()
        } else {
            stopProtection()
        }
        qsTile.updateTile()
    }

    private fun getTileStatus(): Boolean {

        val prefs = applicationContext.getSharedPreferences(
            PREFERENCES_KEY,
            MODE_PRIVATE
        )

        val isActive = !prefs.getBoolean(SERVICE_STATUS_FLAG, false)

        prefs.edit().putBoolean(SERVICE_STATUS_FLAG, isActive).apply()

        return isActive

    }

    private fun launchProtection() {
        MainActivity.launchActivity(applicationContext)
    }

    private fun stopProtection() {
        val intent = Intent("end-event")
        intent.putExtra(MainActivity.REQUEST_ACTIVITY_END, 1)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }


}