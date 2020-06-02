package at.roteskreuz.stopcorona.model.receivers

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import at.roteskreuz.stopcorona.model.exceptions.SilentError
import at.roteskreuz.stopcorona.model.repositories.ExposureNotificationRepository
import at.roteskreuz.stopcorona.model.repositories.NotificationsRepository
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import timber.log.Timber

/**
 * Show notifications based on bluetooth state
 */
class BluetoothStateReceiver : BroadcastReceiver(), Registrable, KoinComponent {

    companion object {
        private const val ACTION = BluetoothAdapter.ACTION_STATE_CHANGED
    }

    private val notificationsRepository: NotificationsRepository by inject()
    private val exposureNotificationRepository: ExposureNotificationRepository by inject()

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == ACTION) {
            try {
                if (!exposureNotificationRepository.isAppRegisteredForExposureNotifications.not()){
                    //we are not registered, we don´t care
                    return
                }
                when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                    BluetoothAdapter.STATE_OFF -> {
                        notificationsRepository.displayPleaseActivateBluetoothNotification()
                    }
                }
            } catch (e:Exception){
                //if it goes wrong, we do not case as we can´t handle the error
                Timber.e(SilentError(e))
            }

        }
    }

    override fun register(context: Context) {
        context.registerReceiver(
            this,
            IntentFilter(ACTION)
        )
    }

    override fun unregister(context: Context) {
        context.unregisterReceiver(this)
    }
}