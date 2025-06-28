## CLIENT WEB OVERVIEW
Il client web è un'applicazione sviluppata con Javascript e HTML/CSS, e consente di interagire con il server web per gestire generazione, modifica, consumo e visualizzazione dei buoni.

La struttura dell'applicazione è la seguente (in ogni cartella è presente un solo file, `index.html`, che rappresenta la pagina principale):

```
client-web/
├── dashboard/
│   ├── create-voucher/
│   ├── edit-voucher/
│   └── vouchers/
├── login/
├── signin/
├── logout/
└── admin/
```

### Pagine
| Pagina | Descrizione |
| - | - |
| `dashboard/` | Pagina principale, accessibile solo agli utenti autenticati. |
| `dashboard/create-voucher/` | Pagina per generare un nuovo buono. |
| `dashboard/edit-voucher/` | Pagina per modificare un buono esistente. |
| `dashboard/vouchers/` | Pagina per visualizzare i buoni generati, consumati e non consumati, in ordine cronologico. |
| `login/` | Pagina per effettuare il login con le credenziali di un utente registrato. |
| `signin/` | Pagina per registrare un nuovo utente. |
| `logout/` | Pagina per effettuare il logout dell'utente corrente. |
| `admin/` | Pagina per visualizzare le informazioni generali sul sistema. |


### Autenticazione
L'autenticazione avviene tramite il salvataggio dell'ID dell'utente nel `localStorage` del browser, e la verifica della presenza di tale ID in ogni richiesta al server.

Se l'ID è presente, viene verificato che appartenga effettivamente ad un utente registrato, e in caso contrario l'utente viene reindirizzato alla pagina di login.

In dettaglio, tutte le pagine appartenenti a `dashboard/` sono accessibili solo se l'utente è autenticato, mentre le pagine `login/`, `signin/`, `logout/` e `admin/` sono accessibili a tutti gli utenti.



