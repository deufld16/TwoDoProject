package at.htlkaindorf.twodoprojectmaxi.dialogs;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.bluetooth.BluetoothManager;

/***
 * PopUp that informs user about available Bluetooth devices
 *
 * @author Maximilian Strohmaier
 */

public class BluetoothDevicesFragment extends DialogFragment
{
    private List<BluetoothDevice> devices = new ArrayList<>();
    private List<String> deviceNames = new ArrayList<>();
    private BluetoothManager bm;
    private String title;
    private String description;
    private String buttonText;
    private ArrayAdapter deviceNamesAdapter;

    private ListView liDevices;

    public BluetoothDevicesFragment(BluetoothManager bm, List<BluetoothDevice> devices, String title, String description,
                                    String buttonText)
    {
        this.bm = bm;
        this.devices = devices;
        deviceNames = this.devices.stream().map(BluetoothDevice::getName).collect(Collectors.toList());
        this.title = title;
        this.description = description;
        this.buttonText = buttonText;
    }

    public void setDevices(List<BluetoothDevice> devices) {
        this.devices = devices;
        deviceNames = devices.stream().map(BluetoothDevice::getName).collect(Collectors.toList());
        refreshDeviceNamesAdapter();
    }

    /***
     * Main method to inflate the GUI and initialize and configure vital variables
     *
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dlgView = inflater.inflate(R.layout.dialog_bluetooth_devices, null);
        builder.setView(dlgView);

        liDevices = dlgView.findViewById(R.id.liDeviceList);
        TextView tvTitle = dlgView.findViewById(R.id.tvBluetoothDeviceDialogTitle);
        TextView tvDescription = dlgView.findViewById(R.id.tvBluetoothDeviceDialogDesc);
        Button btAction = dlgView.findViewById(R.id.btBluetoothDeviceDialogButton);

        tvTitle.setText(title);
        tvDescription.setText(description);

        btAction.setText(buttonText);
        btAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (buttonText.toLowerCase())
                {
                    case "discover further devices":
                        discoverFurther();
                        break;
                    case "cancel":
                        selectionDone(null);
                        break;
                }
            }
        });

        refreshDeviceNamesAdapter();
        liDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectionDone(devices.get(i));
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    /***
     * reset the adapter for the devices list
     */
    private void refreshDeviceNamesAdapter()
    {
        deviceNamesAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.item_bluetooth_device, deviceNames);
        liDevices.setAdapter(deviceNamesAdapter);
    }

    /***
     * Handler for Discover Further Devices Button
     */
    private void discoverFurther()
    {
        dismiss();
        bm.discoverDevices();
    }

    /***
     * Handler for Discovery Cancel Button resp. an item selection
     */
    private void selectionDone(BluetoothDevice device)
    {
        bm.definePartnerDevice(device);
        dismiss();
    }

    /***
     * Method that handles the cancelation of the dialog (i.e. when pressing "back")
     *
     * @param dialog
     */
    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        selectionDone(null);
        super.onCancel(dialog);
    }
}
