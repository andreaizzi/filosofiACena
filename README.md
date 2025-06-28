# PROGETTO SD

## Descrizione

Questo progetto implementa un sistema client-server per la gestione di voucher culturali, utilizzando una comunicazione RESTful per il frontend e un database custom TCP per la persistenza dei dati.

## Struttura del progetto

- `client-web/`: Frontend web (HTML/CSS + JS) per interagire con il server.
- `server-web/`: Server REST sviluppato in Java con Jersey, che gestisce le richieste degli utenti e comunica con il database tramite socket TCP.
- `database/`: Database custom scritto in Java, che gestisce la persistenza dei dati tramite file JSON e offre un protocollo TCP semplice.

## Come eseguire

### 1. Avviare il database
```sh
cd database
mvn compile exec:java
```
Il database resterà in ascolto sulla porta 3030, sarà possibile comunicare mediante un protocollo (più dettagli nel file TCP.md).

### 2. Avviare il server web
```sh
cd server-web
mvn jetty:run
```
Il server web espone una API REST sulla porta 8080 (maggiori dettagli sono presenti nel file REST.md).

### 3. Avviare il client web
Aprire il file `client-web/index.html` in un browser web. Il client interagirirà con il server web per gestire i voucher. Si consiglia di usare l'estensione di Visual Studio Code "Live Server".