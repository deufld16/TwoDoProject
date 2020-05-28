package at.htlkaindorf.twodoprojectmaxi.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

import androidx.annotation.Nullable;
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

    public final int BLUETOOTH_REQUEST_CODE = 1;

    public BluetoothManager(AppCompatActivity srcActivity) throws Exception {
        this.srcActivity = (TransferActivity) srcActivity;

        //check Bluetooth availability
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)
        {
            throw new Exception("Device does not support bluetooth");
        }

        //turn on Bluetooth
        if(!bluetoothAdapter.isEnabled())
        {
            Intent enaBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            srcActivity.startActivityForResult(enaBluetoothIntent, BLUETOOTH_REQUEST_CODE);
        }
        else
        {
            this.srcActivity.informUser("Bluetooth turned on");
        }
    }
}
