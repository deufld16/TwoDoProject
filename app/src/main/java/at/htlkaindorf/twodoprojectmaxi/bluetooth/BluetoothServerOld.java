package at.htlkaindorf.twodoprojectmaxi.bluetooth;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

/***
 * Class that handles all relevant steps for the Bluetooth connection as a server
 *
 * @author Maximilian Strohmaier
 */

public class BluetoothServerOld
{
    private BluetoothManager bm;
    private AcceptConnectionThread act;
    private final UUID THE_UUID;

    public BluetoothServerOld(BluetoothManager bm, UUID THE_UUID)
    {
        this.bm = bm;
        this.THE_UUID = THE_UUID;
        act = new AcceptConnectionThread();
        act.start();
    }

    /***
     * Method to terminate the Bluetooth-Server processes
     */
    public void cancelListening()
    {
        //act.cancel();
        if(act != null && act.isAlive()) {
            act.interrupt();
        }
    }

    /***
     * Class that accepts Bluetooth connection from a Server Socket and processes the information accordingly
     * @author Maximilian Strohmaier
     */
    private class AcceptConnectionThread extends Thread
    {
        private BluetoothServerSocket bss = null;
        private final String NAME = "TWO_DO";
        private BluetoothSocket socket;
        private BluetoothTransferOld bt;

        public AcceptConnectionThread()
        {
            try{
                bss = bm.getBluetoothAdapter().listenUsingRfcommWithServiceRecord(NAME, THE_UUID);
            } catch (IOException e) {
                bm.getSrcActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bm.getSrcActivity().informUser("Error while establishing connection");
                    }
                });
                bm.getSrcActivity().processFailed();
            }
        }

        @Override
        public void run()
        {
            socket = null;
            bm.getSrcActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bm.getSrcActivity().informUser("Device waits for connection attempt");
                }
            });
            while (!Thread.interrupted())
            {
                try {
                    socket = bss.accept();
                } catch (IOException e) {
                    break;
                }

                if (socket != null)
                {
                    bm.getSrcActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bm.getSrcActivity().informUser("Connected");
                        }
                    });
                    receiveData();
                    cancel();
                    break;
                }
            }
        }

        /***
         * Method to handle a successful connection and start wait for the data
         */
        private void receiveData()
        {
            bt = new BluetoothTransferOld(socket, bm.getSrcActivity(), true);
            bt.start();
        }

        /***
         * Method to close the server socket and stop listening
         */
        public void cancel()
        {
            try {
                bss.close();
            } catch (IOException e) {
                bm.getSrcActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bm.getSrcActivity().informUser("Error with Bluetooth connection");
                    }
                });
            }
        }
    }
}
