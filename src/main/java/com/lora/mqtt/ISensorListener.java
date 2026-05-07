package com.lora.mqtt;

public interface ISensorListener {
    // Metodo che verrà chiamato ogni volta che arriva un nuovo dato valido
    void onNewDataReceived(AccelerometerData data);
    
    // Metodo opzionale per gestire eventuali errori di connessione o decodifica
    void onError(String errorMessage);
}
