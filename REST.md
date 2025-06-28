## WEB SERVER OVERVIEW

Il server web offre un serivizio di tipo REST sulla porta 8080.
L'API esposta dal server contiene i seguenti endpoints:

| Endpoint | Metodo | Effetto |
| - | - | - |
| `/users` | GET | Fornisce una serie di utenti |
| `/users/{userId}` | GET | Fornisce le informazioni per un singolo utente |
| `/users/{userId}` | PUT | Aggiorna un utente |
| `/users` | POST | Crea un nuovo utente |
| `/users/{userId}` | DELETE | Elimina un utente |
| `/vouchers` | GET | Fornisce una serie di buoni |
| `/vouchers/{voucherId}` | GET | Fornisce le informazioni per un singolo buono |
| `/vouchers/{voucherId}` | PUT | Aggiorna un buono |
| `/vouchers` | POST | Crea un nuovo buono |
| `/vouchers/{voucherId}` | DELETE | Elimina un buono |
| `/save-database` | POST | Effettua il dump del database documentale |

---

### Spiegazione dettagliata degli endpoints

- **GET `/users/{userId}`**  
    Restituisce la lista di tutti gli utenti.  

    È possibile filtrare gli utenti per nome, cognome o codice fiscale, specificando i parametri `name`, `surname`, `email` o `cf` nella query string. Se non viene specificato alcun filtro, vengono restituiti tutti gli utenti.

    Codici di stato:
    - `200`: ok
    - `404`: la collezione non è presente nel database

    GET `/users?name=Andrea`

    Risposta:
    ```json
        [
            {
                "cf": "DFLNDR",
                "email": "defilippiandrea@mail.com",
                "id": "8a785460-3531-41fb-8bef-7179be20afd4",
                "moneyLeft": 10,
                "name": "Andrea",
                "surname": "De Filippi"
            }
        ]
    ```

- **GET `/users/{userId}`**  
    Restituisce nel dettaglio tutte le informazioni riguardo ad un utente. 

    Codici di stato:
    - `200`: ok
    - `404`: l'utente non è presente nel database

    GET `/users/8a785460-3531-41fb-8bef-7179be20afd4`

    Risposta:
    ```json
        {
            "cf": "DFLNDR",
            "email": "defilippiandrea@mail.com",
            "id": "8a785460-3531-41fb-8bef-7179be20afd4",
            "moneyLeft": 10,
            "name": "Andrea",
            "surname": "De Filippi"
        }
    ```

- **PUT `/users/{userId}`**
    Aggiorna le informazioni di un utente. I campi che non vengono specificati nella richiesta non vengono modificati. Viene ritornato l'utente aggiornato. I campi che possono essere aggiornati sono:
    - `cf`
    - `email`
    - `name`
    - `surname`
    
    Codici di stato:
    - `200`: ok
    - `404`: l'utente non è presente nel database

    PUT `/users/8a785460-3531-41fb-8bef-7179be20afd4`

    Body:
    ```json
        {
            "name": "Alessio",
        }
    ```
    Risposta:
    ```json
        {
            "cf": "DFLNDR",
            "email": "defilippiandrea@mail.com",
            "id": "8a785460-3531-41fb-8bef-7179be20afd4",
            "moneyLeft": 10,
            "name": "Alessio",
            "surname": "De Filippi"
        }
    ```

- **POST `/users`**
    Crea un nuovo utente. I campi che devono essere specificati sono:
    - `cf`
    - `email`
    - `name`
    - `surname`

    Il campo `id` viene generato automaticamente dal server. Il campo `moneyLeft` viene inizializzato a 500.

    Codici di stato:
    - `201`: utente creato
    - `400`: i campi `cf`, `email`, `name` e `surname` devono essere specificati
    - `409`: l'utente con l'`id` generato esiste già

    POST `/users`

    Body:
    ```json
        {
            "name": "Niall",
            "surname": "Horan",
            "email": "horanniall@mail.com",
            "cf": "HRNNLL"
        }
    ``` 
    Risposta:
    ```json
        {
            "cf": "HRNNLL",
            "email": "horanniall@mail.com",
            "id": "4b5e6f69-b13e-4026-88d1-a0c98d291e33",
            "moneyLeft": 500,
            "name": "Niall",
            "surname": "Horan"
        }
    ```

- **DELETE `/users/{userId}`**
    Elimina un utente.

    Codici di stato:
    - `204`: utente eliminato
    - `404`: l'utente non è presente nel database

    DELETE `/users/8a785460-3531-41fb-8bef-7179be20afd4`

    Risposta: `204 No Content`

- **GET `/vouchers`**
    Restituisce la lista di tutti i buoni.

    Codici di stato:
    - `200`: ok
    - `404`: la collezione non è presente nel database

    GET `/vouchers?type=MUSICAL_INSTRUMENT`

    Risposta:
    ```json
        [
            {
                "creationDate": "2025-06-08T13:07:28.429Z[UTC]",
                "id": "feb9b847-bfb1-4585-a480-7655f362b2ac",
                "type": "MUSICAL_INSTRUMENT",
                "used": false,
                "userId": "8a785460-3531-41fb-8bef-7179be20afd4",
                "value": 320
            }
        ]
    ```

- **GET `/vouchers/{voucherId}`**
    Restituisce nel dettaglio tutte le informazioni riguardo ad un buono.

    Codici di stato:
    - `200`: ok
    - `404`: il buono non è presente nel database

    GET `/vouchers/feb9b847-bfb1-4585-a480-7655f362b2ac`

    Risposta:
    ```json
        {
            "creationDate": "2025-06-08T13:07:28.429Z[UTC]",
            "id": "feb9b847-bfb1-4585-a480-7655f362b2ac",
            "type": "MUSICAL_INSTRUMENT",
            "used": false,
            "userId": "8a785460-3531-41fb-8bef-7179be20afd4",
            "value": 320
        }
    ```

- **PUT `/vouchers/{voucherId}`**
    Aggiorna le informazioni di un buono. I campi che non vengono specificati nella richiesta non vengono modificati. Viene ritornato il buono aggiornato. I campi che possono essere aggiornati sono:
    - `type`
    - `used`

    Nel caso in cui il buono venga utilizzato, il campo `used` deve essere impostato a `true`. Quando un buono viene utilizzato, viene impostato il campo `consumeDate` alla data corrente.

    Codici di stato:
    - `200`: ok
    - `404`: il buono non è presente nel database

    PUT `/vouchers/feb9b847-bfb1-4585-a480-7655f362b2ac`

    Body:
    ```json
        {
            "used": true
        }
    ```
    Risposta:
    ```json
        {
            "consumeDate": "2025-06-28T11:56:57.153Z[UTC]",
            "creationDate": "2025-06-08T13:07:28.429Z[UTC]",
            "id": "feb9b847-bfb1-4585-a480-7655f362b2ac",
            "type": "MUSICAL_INSTRUMENT",
            "used": true,
            "userId": "8a785460-3531-41fb-8bef-7179be20afd4",
            "value": 320
        }
    ```


- **POST `/vouchers`**
    Crea un nuovo buono. I campi che devono essere specificati sono:
    - `type`
    - `value`
    - `userId`

    Il campo `id` viene generato automaticamente dal server. Il campo `creationDate` viene inizializzato alla data corrente. 
    Viene controllato che l'utente con `userId` esista nel database, e che l'utente abbia abbastanza soldi per poter creare il buono. Il valore del buono non può essere negativo. Se l'utente non ha abbastanza soldi, il buono non viene creato e viene restituito un errore. 
    Il campo `used` viene inizializzato a `false`.

    Codici di stato:
    - `201`: buono creato
    - `400`: i campi `type`, `value` e `userId` devono essere specificati
    - `409`: il buono con l'`id` generato esiste già

    POST `/vouchers`

    Body:
    ```json
        {
            "type": "MUSICAL_INSTRUMENT",
            "value": 10,
            "userId": "8a785460-3531-41fb-8bef-7179be20afd4"
        }
    ```
    Risposta:
    ```json
        {
            "creationDate": "2025-06-28T11:59:20.198Z[UTC]",
            "id": "cd7bfe34-b0b8-4c81-bf24-758fe9334f5b",
            "type": "MUSICAL_INSTRUMENT",
            "used": false,
            "userId": "8a785460-3531-41fb-8bef-7179be20afd4",
            "value": 10
        }
    ```

- **DELETE `/vouchers/{voucherId}`**
    Elimina un buono. Possono essere eliminati solo i buoni che non sono stati ancora utilizzati (il campo `used` deve essere `false`).
    L'ammontare del buono viene restituito all'utente associato al buono, incrementando il campo `moneyLeft` dell'utente.

    Codici di stato:
    - `204`: buono eliminato
    - `404`: il buono non è presente nel database

    DELETE `/vouchers/feb9b847-bfb1-4585-a480-7655f362b2ac`

    Risposta: `204 No Content`

- **POST `/save-database`**
    Effettua il dump del database documentale, salvando i dati in un file JSON.

    Codici di stato:
    - `200`: dump effettuato con successo
    - `500`: errore durante il dump

    POST `/save-database`
    
    Risposta: `200 OK`