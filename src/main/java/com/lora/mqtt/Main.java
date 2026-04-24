package com.lora.mqtt;

import org.eclipse.paho.client.mqttv3.*;

public class Main {

    // SOSTITUISCI CON L'IP LOCALE DEL TUO GATEWAY RAK (es. 192.168.1.100)
    private static final String BROKER_URL = "tcp://192.168.230.1:1883"; 
    
    // Il Topic a cui abbonarsi. I "+" sono jolly.  
    // Questo è il formato standard per il Built-in Server del RAK/Chirpstack
    // APP NAME ProvaMQTT
    // DEVICE EUI a8610a3433407d04
    private static final String TOPIC = "application/ProvaMQTT/device/a8610a3433407d04/rx"; 
    
    
    private static final String CLIENT_ID = "MacBook-Subscriber-Tesi";

    public static void main(String[] args) {
        try {
            // 1. Creiamo il Client MQTT
            MqttClient client = new MqttClient(BROKER_URL, CLIENT_ID);

            // 2. Opzioni di connessione (per una sessione pulita ad ogni avvio)
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            
            // NOTA: Se hai impostato un utente/password nell'interfaccia del RAK per l'MQTT,
            // decommenta queste due righe e inseriscili:
            // options.setUserName("tuo_utente");
            // options.setPassword("tua_password".toCharArray());

            // 3. Impostiamo le Callback (Cosa fare quando succede qualcosa)
            client.setCallback(new MqttCallback() {
                
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("❌ Connessione persa con il Gateway!");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // QUESTO METODO SCATTA OGNI VOLTA CHE L'ESP32 MANDA UN PACCHETTO
                    String payloadJson = new String(message.getPayload());
                    
                    System.out.println("\n=== NUOVO PACCHETTO RICEVUTO ===");
                    System.out.println("📍 Topic: " + topic);
                    System.out.println("📦 Dati (JSON): " + payloadJson);
                    
                    // In futuro, qui potrai usare una libreria come Gson o org.json 
                    // per estrarre facilmente la stringa Base64 e riconvertirla nei tuoi 6 byte!
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Non ci serve, noi stiamo solo ricevendo (Subscriber), non inviando.
                }
            });

            // 4. Connessione effettiva
            System.out.println("Tentativo di connessione al Gateway: " + BROKER_URL);
            client.connect(options);
            System.out.println("✅ Connesso con successo al Broker MQTT!");

            // 5. Iscrizione al Topic
            client.subscribe(TOPIC);
            System.out.println("🎧 In ascolto dei dati dell'ESP32 sul topic: " + TOPIC);

        } catch (MqttException e) {
            System.out.println("Errore MQTT: " + e.getMessage());
            e.printStackTrace();
        }
        
    }
}