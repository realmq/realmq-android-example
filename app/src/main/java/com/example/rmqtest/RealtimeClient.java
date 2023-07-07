package com.example.rmqtest;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;


public class RealtimeClient {
    private MqttClient mqttClient;

    RealtimeClient(String authToken) throws MqttException {
        mqttClient = new MqttClient("ssl://rtm.realmq.com:8883", authToken, new MemoryPersistence());
    }

    void connect() throws MqttException {
        MqttConnectOptions connectionOptions = new MqttConnectOptions();
        connectionOptions.setAutomaticReconnect(true);
        connectionOptions.setCleanSession(true);

        mqttClient.connect(connectionOptions);
    }

    void subscribe(String channel, IMqttMessageListener listener) throws MqttException {
        mqttClient.subscribe(channel, listener);
    }

    void publishLocation(String channel, double latitude, double longitude) throws MqttException, JSONException {
        MqttMessage message = new MqttMessage();
        JSONObject payload = new JSONObject();
        payload.put("latitude", latitude);
        payload.put("longitude", longitude);

        message.setPayload(payload.toString().getBytes());
        mqttClient.publish(channel, message);
    }
}
