package com.example.rmqtest;

import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import com.example.rmqtest.databinding.ActivityMainBinding;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private RealtimeClient realtimeClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    realtimeClient.publishLocation("demo-channel", 40.730610, -73.935242);
                    log("on Publish");
                } catch (MqttException e) {
                    throw new RuntimeException(e);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        try {
            realtimeClient = new RealtimeClient("%AUTH_TOKEN%");

            realtimeClient.connect();
            log("on connected");

            realtimeClient.subscribe("demo-channel", new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                   Logger.getGlobal().warning("on message received: " + message.toString());

                   try {
                       JSONObject json = new JSONObject(message.toString());

                       if (json.has("latitude") && json.has("longitude")) {
                           log("Received lat: " + json.getString("latitude") + " lng: " + json.getString("longitude"));
                       }
                   } catch (Exception e) {
                       log("Error parsing JSON" +  e.getMessage());
                   }
                }
            });
            log("on subscribed");
        } catch (Exception e) {
            log("on error" + e.getMessage());
            Snackbar.make(binding.getRoot(), e.getMessage(), Snackbar.LENGTH_LONG)
                    .setAnchorView(R.id.textview_first)
                    .setAction("Error", null).show();
        }
    }

    void log(String text) {
        binding.textviewFirst.setText(text + "\n" + binding.textviewFirst.getText().toString() );
    }
}
