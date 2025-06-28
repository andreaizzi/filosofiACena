## DATABASE SERVER OVERVIEW

### Project Structure
La classe `Main` si occupa solo di creare un `Database`, un `DatabaseServer` e di metterlo in ascolto sulla porta 3030.

Il `DatabaseServer` resta in ascolto di connessioni, appena ne riceve una crea un oggetto di tipo `RequestHandler` a cui affida la gestione della connessione.

`RequestHandler` estende la classe `Thread`, e si occupa di ricevere il contenuto della richiesta e di chiamare il metodo handle di `ProtocolHandler`.

`ProtocolHandler` è sostanzialmente il cuore dell'applicazione, si occupa di
- capire che richiesta viene ricevuta
- separare i dati ricevuti assieme alla richiesta
- chiamare il metodo corretto in base alla richiesta ricevuta

---

### Database
Il database è un database documentale: consente di creare una serie di collezioni, ognuna delle quali ha associato una mappa di documenti.
Le tre classi che gestiscono il database sono `Database`, `Collection` e `Document`.

---

### Protocol Info
Il protocollo implementato è di tipo testuale. I comandi accettati sono

`GET collectionName`
- Intera collezione
- ERR 404 Collection not found

`GET collectionName documentId`
- Contenuto del documento
- ERR 404 Collection not found
- ERR 404 Document not found

`PUT collectionName documentId documentData`
- Document updated
- ERR 404 Collection not found
- ERR 404 Document not found

`POST collectionName documentId documentData`
- Document created
- ERR 400 Document ID and data must be provided for POST
- ERR 409 Document already exists

`DELETE collectionName documentId`
- Document deleted
- ERR 404 Document/Collection not found

`CREATE collectionName`
- Collection created
- ERR 409 Collection already exists

`SAVE`
- Status saved on filename
- ERR 500 Status not saved 

In tutti gli altri casi, viene ritornato come messaggio di errore
- ERR 400 Invalid request format

