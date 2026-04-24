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
                    System.out.println(" Connessione persa con il Gateway!");
                }

               @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String payloadJson = new String(message.getPayload());
                    
                    System.out.println("\n=== NUOVO PACCHETTO RICEVUTO ===");
                    System.out.println(" Topic: " + topic);
                    
                    // 1. Troviamo il campo "data":" nel JSON
                    String ricerca = "\"data\":\"";
                    int inizio = payloadJson.indexOf(ricerca);
                    
                    if (inizio != -1) {
                        inizio += ricerca.length();
                        int fine = payloadJson.indexOf("\"", inizio);
                        String base64Data = payloadJson.substring(inizio, fine);
                        
                        System.out.println(" Payload Base64 grezzo: " + base64Data);
                        
                        // 2. Decodifichiamo il Base64 per riavere i nostri 6 Byte
                        byte[] bytes = java.util.Base64.getDecoder().decode(base64Data);
                        
                        // 3. Se abbiamo esattamente 6 byte, ricostruiamo X, Y e Z
                        if(bytes.length == 6) {
                            // Ricostruiamo i numeri unendo i due byte (High e Low)
                            // La formula usa gli operatori bitwise (Shift e OR)
                            int asseX = (bytes[0] << 8) | (bytes[1] & 0xFF);
                            int asseY = (bytes[2] << 8) | (bytes[3] & 0xFF);
                            int asseZ = (bytes[4] << 8) | (bytes[5] & 0xFF);
                            
                            System.out.println(" DATI ACCELEROMETRO DECODIFICATI:");
                            System.out.println("   Asse X: " + asseX);
                            System.out.println("   Asse Y: " + asseY);
                            System.out.println("   Asse Z: " + asseZ);
                        } else {
                            System.out.println("Attenzione: ricevuti " + bytes.length + " byte invece di 6.");
                        }
                        
                    } else {
                        System.out.println("Nessun campo 'data' trovato in questo JSON.");
                    }
                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Non ci serve, noi stiamo solo ricevendo (Subscriber), non inviando.
                }
            });

            // 4. Connessione effettiva
            System.out.println("Tentativo di connessione al Gateway: " + BROKER_URL);
            client.connect(options);
            System.out.println(" Connesso con successo al Broker MQTT!");

            // 5. Iscrizione al Topic
            client.subscribe(TOPIC);
            System.out.println(" In ascolto dei dati dell'ESP32 sul topic: " + TOPIC);

        } catch (MqttException e) {
            System.out.println("Errore MQTT: " + e.getMessage());
            e.printStackTrace();
        }
        
    }
}