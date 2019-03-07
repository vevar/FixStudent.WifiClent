package com.example.wificlent

import android.app.Activity
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.widget.Toast

class MainActivity : Activity() {

    private val intentFilter = IntentFilter()
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var manager: WifiP2pManager
    private lateinit var receiver : FixStudentWifiBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)

        manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(this, mainLooper, null)

    }

    override fun onResume() {
        super.onResume()
        receiver = FixStudentWifiBroadcastReceiver(manager, channel)
        registerReceiver(receiver, intentFilter)

        manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Toast.makeText(this@MainActivity, "Ready", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(reason: Int) {
                Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_LONG).show()
            }
        })
//        manager.requestPeers(channel) {
//            it.deviceList.forEach { device ->
//                Toast.makeText(this, device.deviceName, Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    override fun onPause() {
        super.onPause()

        unregisterReceiver(receiver)
    }
}
