package at.htlkaindorf.twodoprojectmaxi.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import at.htlkaindorf.twodoprojectmaxi.activities.TransferActivity;

/***
 * Class that handles all relevant steps for the bluetooth communication
 *
 * @author Maximilian Strohmaier
 */

public class BluetoothManager
{
    private BluetoothAdapter bluetoothAdapter;
    private TransferActivity srcActivity;
    private boolean processDone = true;

    public final int BLUETOOTH_ENABLE_REQUEST_CODE = 1;

    public final BroadcastReceiver bluetoothChangeReceiver = new BroadcastReceiver() {
        /***
         * Listener for changes concerning the Bluetooth state
         *
         * @param context
         * @param intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);

                if(state == BluetoothAdapter.STATE_OFF) {
                    srcActivity.informUser("Bluetooth turned off");
                    if(!processDone) {
                        srcActivity.informUserProcessFailed();
                    }
                }
            }
        }
    };

    public BluetoothManager(AppCompatActivity srcActivity) throws Exception {
        processDone = false;
        this.srcActivity = (TransferActivity) srcActivity;

        //check Bluetooth availability
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)
        {
            throw new Exception("Device does not support bluetooth");
        }

        enableBluetooth();
    }

    /***
     * Method to enable Bluetooth on device
     */
    private void enableBluetooth()
    {
        if(!bluetoothAdapter.isEnabled())
        {
            Intent enaBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            srcActivity.startActivityForResult(enaBluetoothIntent, BLUETOOTH_ENABLE_REQUEST_CODE);
        }
        else
        {
            srcActivity.informUser("Bluetooth turned on");
        }
    }
}
