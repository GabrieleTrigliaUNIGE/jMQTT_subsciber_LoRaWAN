# 📡 LoRaWAN-MQTT Accelerometer Data Pipeline

Questo progetto implementa un'infrastruttura IoT completa end-to-end per l'acquisizione, la trasmissione e la decodifica in tempo reale di dati accelerometrici (Assi X, Y, Z) sfruttando il protocollo LoRaWAN e l'integrazione MQTT.

Sviluppato come parte di un progetto di tesi, il sistema include una sezione di *reverse engineering* che analizza i colli di bottiglia hardware e i limiti di payload delle schede basate su modulo radio Murata.

## 🏗️ Architettura del Sistema

Il flusso dei dati è diviso in 3 stadi principali:

1. **End Node (Acquisizione e Trasmissione):** Un nodo acquisisce i dati di 3 assi (o li simula), li compatta in un payload binario di 6 Byte e li trasmette tramite LoRaWAN (OTAA).
2. **Gateway / Network Server:** Un gateway industriale riceve il pacchetto radio. Sfruttando il suo *Built-in Network Server*, decripta i dati, li codifica in Base64 all'interno di un payload JSON e li pubblica sul proprio Broker MQTT interno.
3. **Subscriber MQTT (Elaborazione):** Un'applicazione Java in ascolto sul topic MQTT intercetta il JSON, estrae la stringa Base64, la decodifica nei 6 byte originali e ricostruisce i valori interi degli assi X, Y e Z tramite operazioni bitwise.

## 🛠️ Hardware Utilizzato

* **End Node:** Arduino MKR WAN 1310 (compatibile con ESP32 + Modulo LoRa SX1276/RFM95)
* **Sensore:** Accelerometro a 3 assi (es. MPU6050) / Simulatore software
* **Gateway:** RAK Wireless WisGate Edge Pro (con WisGateOS / ChirpStack)
* **Elaborazione:** PC/Mac connesso alla rete locale del Gateway

## 💻 Software e Dipendenze

* **Nodo (C++ / Arduino IDE):**
  * Libreria `MKRWAN` (per MKR 1310) o `MCCI LoRaWAN LMIC` (per ESP32).
* **Subscriber (Java 11+ / VS Code):**
  * Gestore dipendenze: `Maven`
  * Libreria MQTT: `Eclipse Paho MQTT v3 Client` (v1.2.5)

## ⚙️ Configurazione e Avvio

### 1. Configurazione del Gateway (RAK WisGate)
1. Impostare la modalità del Gateway su **Built-in Network Server**.
2. Creare una nuova **Application** e annotare l'Application ID.
3. Aggiungere il dispositivo (End Device) in classe A e generare le chiavi **DevEUI**, **AppEUI** (Join EUI) e **AppKey** per l'attivazione **OTAA**.
4. Abilitare l'integrazione **MQTT Broker** nelle impostazioni globali. *Nessun Payload Codec richiesto (impostare su "None").*

### 2. Configurazione del Nodo (Arduino)
1. Inserire le chiavi `appEui` e `appKey` generate dal Gateway all'interno dello sketch.
2. Caricare il codice sulla scheda. Il nodo compatterà gli assi X,Y,Z in 6 byte esatti per minimizzare il *Time on Air* e rispettare le policy sul *Duty Cycle* (1% in EU868).

### 3. Avvio del Subscriber MQTT (Java)
1. Aprire il progetto Java.
2. Nel file `App.java`, modificare la costante `BROKER_URL` inserendo l'indirizzo IP locale del Gateway RAK (es. `tcp://192.168.1.xxx:1883`).
3. Compilare ed eseguire. L'applicazione si iscriverà al topic `application/+/device/+/rx` (o equivalenti in base alla versione di ChirpStack) stampando a schermo i valori decodificati ad ogni ricezione.

## 📦 Struttura del Payload

Per ottimizzare la trasmissione radio, i dati non vengono inviati in formato testo/JSON, ma come array di byte:

| Byte 0 | Byte 1 | Byte 2 | Byte 3 | Byte 4 | Byte 5 |
| :---: | :---: | :---: | :---: | :---: | :---: |
| X (High) | X (Low) | Y (High) | Y (Low) | Z (High) | Z (Low) |

---
  
**Licenza:** GPL V3
