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

public class BluetoothServer
{
    private BluetoothManager bm;
    private AcceptConnectionThread act;
    private final UUID THE_UUID;

    public BluetoothServer(BluetoothManager bm, UUID THE_UUID)
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
        act.interrupt();
    }

    /***
     * Class that accepts Bluetooth connection from a Server Socket and processes the information accordingly
     * @author Maximilian Strohmaier
     */
    private class AcceptConnectionThread extends Thread
    {
        private BluetoothServerSocket bss = null;
        private final String NAME = "TWO_DO";

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
            BluetoothSocket socket = null;
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
                    //TODO: server-side process with opened connection
                    cancel();
                    break;
                }
            }
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
