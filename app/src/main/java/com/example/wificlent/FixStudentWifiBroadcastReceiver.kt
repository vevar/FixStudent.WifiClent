package com.example.wificlent

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pManager
import android.nfc.Tag
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.ObjectOutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

class FixStudentWifiBroadcastReceiver(
    private val manager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel
) : BroadcastReceiver() {

//    4a:2c:a0:f9:1e:38

    companion object {
        const val TAG = "FixClient"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action: String = intent.action ?: ""
        when (action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)

                val text = "Hotspot is " +
                        if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) "on" else "off"
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {

                manager.requestPeers(channel) {
                    it.deviceList.forEach { device ->
                        Log.d(TAG, "Found ${device.deviceName} ${device.deviceAddress}")
                        if (device.deviceAddress == "4a:2c:a0:f9:1e:38") {
                            val wifiP2pConfig = WifiP2pConfig()
                            wifiP2pConfig.deviceAddress = device.deviceAddress
                            manager.connect(channel, wifiP2pConfig, object : WifiP2pManager.ActionListener {
                                override fun onSuccess() {

                                }

                                override fun onFailure(reason: Int) {
                                    Log.d(TAG, "Connection ERROR")
                                }

                            })
                        }
                    }
                }
                // Call WifiP2pManager.requestPeers() to get a list of current peers
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                val networkInfo = intent
                    .getParcelableExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
                if (networkInfo.isConnected) {
                    manager.requestConnectionInfo(channel) {
                        Log.d(TAG, it.groupOwnerAddress.toString())
                        val runnable = Runnable {
                            val port: Int = 9980

                            val socket = Socket()
                            socket.bind(null)

                            socket.connect(InetSocketAddress(it.groupOwnerAddress, port))
                            val gson = Gson()
                            val student = Student(1, "firstName1", "surName1", "middleName1")
                            val group = Group(1, "AVT-515")
                            student.group = group

                            val jsonStudent = gson.toJson(student)
                            val dataOutputStream = DataOutputStream(socket.getOutputStream())
                            dataOutputStream.writeUTF(jsonStudent)
                            dataOutputStream.flush()

                            val dataInputStream = DataInputStream(socket.getInputStream())
                            val responseString = dataInputStream.readUTF()
                            Log.d(TAG, "Response: $responseString")


                            Log.d(TAG, "Connection OK")
                        }
                        Thread(runnable).start()
                    }
                } else {

                }
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                // Respond to this device's wifi state changing
            }
        }
    }


}