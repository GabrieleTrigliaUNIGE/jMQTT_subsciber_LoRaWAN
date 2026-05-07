package com.lora.mqtt;

import java.util.Base64;

public class PayloadParser {

    public static AccelerometerData parseLoRaPayload(String payloadJson) throws Exception {
        // 1. Estrazione del Base64 dal JSON
        String ricerca = "\"data\":\"";
        int inizio = payloadJson.indexOf(ricerca);
        
        if (inizio == -1) {
            throw new Exception("Nessun campo 'data' trovato nel JSON.");
        }
        
        inizio += ricerca.length();
        int fine = payloadJson.indexOf("\"", inizio);
        String base64Data = payloadJson.substring(inizio, fine);
        
        // 2. Decodifica Base64
        byte[] bytes = Base64.getDecoder().decode(base64Data);
        
        // 3. Ricostruzione Assi
        if (bytes.length == 6) {
            int asseX = (bytes[0] << 8) | (bytes[1] & 0xFF);
            int asseY = (bytes[2] << 8) | (bytes[3] & 0xFF);
            int asseZ = (bytes[4] << 8) | (bytes[5] & 0xFF);
            
            return new AccelerometerData(asseX, asseY, asseZ);
        } else {
            throw new Exception("Ricevuti " + bytes.length + " byte invece di 6.");
        }
    }
}