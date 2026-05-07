package com.lora.mqtt;

public class Main implements ISensorListener {

    private static final String BROKER_URL = "tcp://192.168.230.1:1883"; 
    private static final String TOPIC = "application/ProvaMQTT/device/a8610a3433407d04/rx"; 
    private static final String CLIENT_ID = "MacBook-Subscriber-Tesi";

    public static void main(String[] args) {
        Main app = new Main();
        app.avviaSistema();
    }

    public void avviaSistema() {
        // Creiamo il client di rete
        MqttLoRaClient mqttClient = new MqttLoRaClient(BROKER_URL, CLIENT_ID, TOPIC);
        
        // Colleghiamo questa classe (che fa da finta UI) al client MQTT
        mqttClient.setListener(this);
        
        // Avviamo la connessione
        mqttClient.connectAndSubscribe();
        
        System.out.println("Sistema avviato. In attesa di dati...");
    }

    // --- METODI DELL'INTERFACCIA (LA NOSTRA "FINTA UI" PER ORA) ---

    @Override
    public void onNewDataReceived(AccelerometerData data) {
        // DOMANI QUI AGGIORNERAI I GRAFICI.
        // Oggi stampiamo semplicemente in console.
        System.out.println("\nRicevuti: " + data.toString());
    }

    @Override
    public void onError(String errorMessage) {
        // DOMANI QUI MOSTRERAI UN POPUP DI ERRORE ALL'UTENTE
        System.err.println("ERRORE: " + errorMessage);
    }
}