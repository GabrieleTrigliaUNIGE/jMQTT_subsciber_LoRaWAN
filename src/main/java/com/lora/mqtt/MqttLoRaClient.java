package com.lora.mqtt;

import org.eclipse.paho.client.mqttv3.*;

public class MqttLoRaClient {

    private final String brokerUrl;
    private final String clientId;
    private final String topic;
    private ISensorListener listener; // Chi sta ascoltando i dati?

    public MqttLoRaClient(String brokerUrl, String clientId, String topic) {
        this.brokerUrl = brokerUrl;
        this.clientId = clientId;
        this.topic = topic;
    }

    // Permette di collegare l'interfaccia grafica al client MQTT
    public void setListener(ISensorListener listener) {
        this.listener = listener;
    }

    public void connectAndSubscribe() {
        try {
            MqttClient client = new MqttClient(brokerUrl, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    if (listener != null) listener.onError("Connessione persa con il Gateway!");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    try {
                        String payloadJson = new String(message.getPayload());
                        
                        // Deleghiamo la decodifica al Parser
                        AccelerometerData data = PayloadParser.parseLoRaPayload(payloadJson);
                        
                        // Se c'è qualcuno in ascolto (es. la UI), gli passiamo il dato pulito!
                        if (listener != null) {
                            listener.onNewDataReceived(data);
                        }
                        
                    } catch (Exception e) {
                        if (listener != null) listener.onError("Errore decodifica: " + e.getMessage());
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) { }
            });

            client.connect(options);
            client.subscribe(topic);
            System.out.println("✅ Connesso al Broker e in ascolto sul topic: " + topic);

        } catch (MqttException e) {
            System.err.println("❌ Errore MQTT critico: " + e.getMessage());
        }
    }
}