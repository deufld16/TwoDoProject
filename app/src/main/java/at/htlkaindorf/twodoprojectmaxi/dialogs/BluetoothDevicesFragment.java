package at.htlkaindorf.twodoprojectmaxi.dialogs;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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
import java.util.Set;

import at.htlkaindorf.twodoprojectmaxi.R;

public class BluetoothDevicesFragment extends DialogFragment
{
    private Set<BluetoothDevice> devices;
    private String actionStr;

    public BluetoothDevicesFragment(Set<BluetoothDevice> devices,
                                    String actionStr)
    {
        this.devices = devices;
        this.actionStr = actionStr;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dlgView = inflater.inflate(R.layout.dialog_bluetooth_devices, null);
        builder.setView(dlgView);

        ListView liDevices = dlgView.findViewById(R.id.liDeviceList);
        TextView tvTitle = dlgView.findViewById(R.id.tvBluetoothDeviceDialogTitle);
        TextView tvDescription = dlgView.findViewById(R.id.tvBluetoothDeviceDialogDesc);
        Button btAction = dlgView.findViewById(R.id.btBluetoothDeviceDialogButton);

        tvTitle.setText("Paired Devices");
        tvDescription.setText("Please choose a paired device");

        btAction.setText("Discover Further Devices");

        List<String> deviceNames = new ArrayList<>();
        for (BluetoothDevice device : devices) {
            deviceNames.add(device.getName());
        }
        ArrayAdapter deviceNamesAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.item_bluetooth_device, deviceNames);
        liDevices.setAdapter(deviceNamesAdapter);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}
