# Coreografia
Progetto di Ingegneria del Software Orientata ai Servizi

## Convenzioni

Aggiungiamo alcune pseudo-funzionalità al linguaggio utilizzato per specificare le coreografie:
- consideriamo `(A)>1;` equivalente a `(A); (A)*;`;
	- esempio: `(messaggio: A -> B)>1;` diventa `messaggio: A -> B; (messaggio: A -> B)*;`;
- consideriamo `+ELSE(A'; ...; Z';); A; ...; Z;` equivalente a `(A'; ...; Z';) + (A; ...; Z;);`;
	- esempio: `invia: A -> B; +ELSE(1); ricevi: B -> A;` diventa `invia: A -> B; (1) + (ricevi: B -> A;)`;
- consideriamo `% [a-z;A-Z]+` equivalente a ` `, ovvero un commento.

## Note

Immaginiamo per comodità che Prontogram gestisca in maniera indipendente da questo progetto le fasi di registrazione ed accesso di un cliente a questa api.

Ipotizziamo inoltre che ACMESky possa acquistare i biglietti senza pagare immediatamente la Compagnia Aerea, ma pagando mensilmente il totale delle spese del mese.

Supponiamo inoltre che le compagnie di noleggio, quando chiamate in gioco nella coreografia, non abbiano mai problemi di disponibilità per il luogo e la data prevista.

Inoltre possiamo ipotizzare che i servizi esterni di Prontogram e Calcolo delle Distanze siano sempre attive e in breve tempo rispondando risolvendo in tempi brevi eventuali errori di connessione e traffico.

## Attori
-   ACMESky, l'azienda;
-   Client, il cliente;
-   CA, la compagnia aerea;
-   CDN, la compagnia di noleggio;
-   GPS, l'api per il calcolo delle distanze;
-   P, il software di messaggistica Prontogram;
-   Banca, il fornitore di servizi bancari.
    

## Coreografia

La coreografia consiste nella ripetizione, anche parallela, del termine `MAIN`.

```
MAIN ::=
(
	GESTIONE_UTENTE
	+
	GESTIONE_OFFERTE
	+
	GESTIONE_OFFERTELM
	+
	RICERCA_OFFERTE
	+
	INFO_OFFERTA
	+
	ACQUISTO_OFFERTA
)
```

Il termine `GESTIONE_UTENTE` contiene tutte le operazioni che il singolo *Cliente* può eseguire per gestire il proprio utente sulla piattaforma di *ACMESky*.

```
GESTIONE_UTENTE ::=
(
	REGISTRAZIONE
	+
	(
		ACCESSO;
    		(
			SELEZIONEOP;
  			(
  				MODIFICA_INFO
  				+
  				MODIFICA_INTERESSI
  				+
  				ELIMINAZIONE_UTENTE
  				+
  				1
  			)
    		)
    		+
    		1
	)
)
```

Il termine `REGISTRAZIONE` contiene le azioni necessarie al *Cliente* per eseguire la registrazione di un nuovo utente su *ACMESky*.

```
REGISTRAZIONE ::=
(
	% Richiesta di creazione utente contenente i dati necessari
	ReqCreazioneUtente: Client -> ACMESky;

	% Risposta contenente la conferma o l'errore del tentativo di creazione
	ResCreazioneUtente: ACMESky -> Client
)
```

Il termine `ACCESSO` contiene le azioni necessarie al *Cliente* per eseguire l'accesso su *ACMESky*.

```
ACCESSO ::=
(
	% Invio credenziali di accesso (id + password)
	ReqAccesso: Client -> ACMESky;

	% Risposta contenente la conferma dell'accesso o errore
	ResAccesso: ACMESky -> Client
)
```

Il termine `SELEZIONEOP` contiene le azioni necessarie al *Cliente* per indicare ad *ACMESky* il tipo di operazione che si desidera fare sul proprio profilo.

```
SELEZIONEOP ::=
(
	% Invio operazione da eseguire
	ReqOperazione: Client -> ACMESky;

	% Timer
	+ELSE(1)

	% Risposta contenente i dati utili al fine di eseguire l'operazione
	ResOperazione: ACMESky -> Client
)
```

Il termine `MODIFICA_INFO` contiene le azioni necessarie al *Cliente* per modificare le informazioni di un utente sulla piattaforma *ACMESky*.

```
MODIFICA_INFO ::=
(
	% Termina in caso di timeout
	1
	+
	(
  		% Invio delle modifiche delle informazioni (nome, cognome. ecc) da apportare
  		ReqModificaInfo: Client -> ACMESky;

  		% Risposta contenente la conferma della modifica o errore
  		ResModificaInfo: ACMESky -> Client
	)
)
```

Il termine `MODIFICA_INTERESSI` contiene le azioni necessarie al *Cliente* per modificare la lista degli interessi di un utente sulla piattaforme *ACMESky*.

```
MODIFICA_INTERESSI ::=
(
	% Termina in caso di timeout
	1
	+
	(
		% Invio della lista modificata di interessi
		ReqModificaInteressi: Client -> ACMESky;

  		% Risposta contenente la conferma della modifica o errore
  		ResModificaInteressi: ACMESky -> Client
  	)
)
```

Il termine `ELIMINAZIONE_UTENTE` contiene le azioni necessarie al *Cliente* per eliminare un utente dalla piattaforma *ACMESky*.

```
ELIMINAZIONE_UTENTE ::=
(
	% Termina in caso di errore, o timeout
	1
	+
 	(
   		% Invio della richiesta di eliminazione
  		ReqEliminaUtente: Client -> ACMESky;

  		% Risposta contenente la conferma dell'eliminazione o errore
  		ResEliminaUtente: ACMESky -> Client
  	)
)
```

Il termine `GESTIONE_OFFERTE` contiene le azioni necessarie ad *ACMESky* per richiedere periodicamente ad ogni *Compagnia Aerea* la lista dei voli che rientrano tra gli interessi degli utenti e successivo invio delle offerte trovate agli utenti interessati tramite *Prontogram*.

```
GESTIONE_OFFERTE ::=
(
	% Recupero degli interessi
	
	(
		% Invio della richiesta della lista dei voli che rientrano negli interessi
		ReqTabellaVoli: ACMESky -> CA;

		% Termina e ripeti dopo in caso di errore, o timeout
		+ELSE(1)

		% Risposta contenente la lista delle offerte trovate
		ResTabellaVoli: CA -> ACMESky
	)*;

	% Invio delle offerte trovate che rientrano nei parametri degli interessi dell'utente
	(
		% Fase di invio dell'offerta
		(INVIO_OFFERTA)*
	)*
)
```

Il termine `GESTIONE_OFFERTELM` contiene le azioni necessarie a ogni *Compagnia Aerea* per inviare offerte last-minute ad *ACMESky* ed a quest'ultima di notificarle ai possibili interessati.

```
GESTIONE_OFFERTELM ::=
(
	% Invio dell'offerta last-minute
	SendInvioOffertaLM: CA -> ACMESky;

	% Fase di invio dell'offerta last-minute ai clienti interessati (se esistono)
	(INVIO_OFFERTA)*
)
```

Il termine `INVIO_OFFERTA` contiene le azioni necessarie ad *ACMESky* per inviare le offerte tramite *Prontogram*.

```
INVIO_OFFERTA ::=
(
	% Ripete finchè non ottiene una risposta corretta
 	(
		% Invio della richiesta di reference
    		ReqReference: ACMESky -> P;

    		% Risposta contenente la reference o errore
    		ResReference: P -> ACMESky
  	)>1;

  	% Ripete finchè non ottiene una risposta corretta
  	(
  		% Invio della richiesta di invio di un messaggio
  		ReqInvioMessaggio: ACMESky -> P;

  		% Risposta contenente la conferma dell'invio o errore
  		ResInvioMessaggio: P -> ACMESky
  	)>1
)
```

Il termine `RICERCA_OFFERTE` contiene le azioni necessarie ad ogni *Cliente* per controllare la lista dei messaggi contenenti offerte ricevuti tramite *Prontogram*.

```
RICERCA_OFFERTE ::=
(
	% Fase di controllo dei messaggi ricevuti
	(
		% Invio della richiesta della offerta ricevuta
		ReqControlloOfferte: Client -> P;

		% Risposta contenente il messaggio con l'offerta o errore
		ResControlloOfferte: P -> Client
	)>1
)
```

Il termine `INFO_OFFERTA` contiene le azioni necessarie al *Cliente* per recuperare le informazioni di una particolare offerta sulla piattaforma *ACMESky*, se ancora valida.

```
INFO_OFFERTA ::=
(
	% Invio della richiesta di informazioni su una offerta
	ReqInformazioniOfferta: Client -> ACMESky;

	% Fase di controllo dei posti rimasti
	1 + INFO_POSTI;

	% Risposta contenente l'offerta o errore
	ResInformazioniOfferta: ACMESky -> Client
)
```

Il termine `INFO_POSTI` contiene le azioni necessarie ad *ACMESky* per recuperare le informazioni sui posti rimasti per un volo di una *Compagnia Aerea*.

```
INFO_POSTI ::=
(
	% Invio della richiesta dei posti rimasti per un volo
	ReqPostiRimasti: ACMESky -> CA;

	% Termina in caso di timeout
	+ELSE(1)

	% Risposta contenente i posti rimasti o errore
	ResPostiRimasti: CA -> ACMESky
)
```

Il termine `ACQUISTO_OFFERTA` contiene le azioni necessarie al *Cliente* per acquistare tramite il *Sistema Bancario* una particolare offerta sulla piattaforma *ACMESky*, se ancora valida, che a sua volta procederà a recuperare i biglietti dalla *Compagnia Aerea* che gestisce quel volo.

```
ACQUISTO_OFFERTA ::=
(
	% Invia l'intenzione iniziale di acquisto di un'offerta
	ReqIntenzioneAcquisto: Client -> ACMESky;

	% Risponde in caso di offerta non valida e poi termina
	+ELSE(ResIntenzioneAcquisto: ACMESky -> Client)

	% Fase di controllo dei posti rimasti, se l'offerta è valida
	1 + INFO_POSTI;

	% Risponde e termina in caso di errore o posti terminati
	+ELSE(ResIntenzioneAcquisto: ACMESky -> Client)

	% Risposta contenente la conferma dell'inizio della procedura
	ResIntenzioneAcquisto: ACMESky -> Client;

	% Termina in caso di timeout o errore
	+ELSE(1)

  	% Manda la conferma contenente i dati dei passeggeri
  	ConfermaIntezioneAcquisto: Client -> ACMESky;

	% Redirect con il sistema bancario

	% Scelta del metodo di pagamento
	(
		% Invio del pagamento con scelta del metodo di pagamento
		ReqInvioPagamento: Client -> Banca;

		% Termina in caso di timeout
		+ELSE(1)

    	+
    	(
      		% Blocco dei soldi

     		% Invio della conferma di pagamento in corso
      		SendNotificaPagamento: Banca -> ACMESky;

      		% Invio della richiesta di acquisto dei biglietti, se disponibili
      		ReqAcquistoBiglietto: ACMESky -> CA;

      		% Termina in caso di errore, o timeout,
      		% sbloccando i soldi
		+ELSE(SendCompensaCredito: ACMESky -> Banca)

      		% La compagnia aerea blocca i posti di volo e crea i biglietti

     		% Rispondi con i biglietti o errore (e.g. biglietti terminati, ecc.)
      		ResAcquistoBiglietto: CA -> ACMESky;

      		% Termina in caso in cui i biglietti siano finiti,
      		% sbloccando i soldi e annullando i biglietti
      		+ELSE(SendCompensaCredito: ACMESky -> Banca)

      		% Fase di gestione del noleggio, se il prezzo è superiore a 1000E
      		1 + GESTIONE_NOLEGGIO;

      		% Invio alla banca il segnale di concludere il pagamento prelevando i soldi
      		SendConcludiPagamento: ACMESky -> Banca;

      		% Termina in caso di errore, o timeout
      		% sbloccando i soldi, annullando i biglietti e annullando il noleggio (se avvenuta la prenotazione online)
      		+ELSE(
			(
				SendAnnullaBiglietti: ACMESky -> CA;
				1 + ANNULLA_NOLEGGIO
			)
			|
			(
				RICHIESTA_CHIARIMENTO
			)
		)

      		% Invio alla compagnia aerea la chiusura della procedura di acquisto dei biglietti
      		SendConfermaBiglietti: ACMESky->CA;

      		% In caso di errore, o timeout in questa fase, ACMESky contatterà telefonicamente la compagnia aerea per confermare l'avvenuto acquisto
      		% oppure invia il biglietto e l'eventuale prenotazione della navetta al cliente
      		+ELSE(1)
			ReqBiglietti: Client -> ACMESky
			ResBiglietti: ACMESky -> Client
    		)
	)*;

  	% Termina in caso di timeout o fine
  	+ELSE(1)

  	% Termina in caso di annullamento dell'operazione, o fornitore dei servizi bancari non disponibile
  	% avvisando ACMESky della decisione
  	SendAnnullaAcquisto: Client -> ACMESky
)
```

Il termine `GESTIONE_NOLEGGIO` e `ANNULLA_NOLEGGIO` contengono le azioni necessarie ad *ACMESky* per prenotare una navetta e annullare una prenotazione tramite la *Compagnia di Noleggio* più vicina alla casa dell'acquirente, se questa si trova ad una distanza di massimo 30km dall'aeroporto, distanze trovate tramite un servizio di *Calcolo delle Distanze*.

```
GESTIONE_NOLEGGIO ::=
(
	% Fase di controllo della distanza tra la casa del cliente e l'aeroporto
	CHIEDI_DISTANZA;

	% Termina in caso di distanza superiore a 30km
	+ELSE(1)

	% Ricerca del noleggio più vicino alla casa del cliente
	(
		% Fase di calcolo della distanza tra il noleggio e la casa del cliente
		CHIEDI_DISTANZA
	)*;

	% Richiesta di prenotazione del veicolo
	ReqPrenotaVeicolo: ACMESky -> CDN;

	% Termina in caso di errore, o timeout,
	% completando la prenotazione da telefono
	+ELSE(1)

	% Risposta contenente la conferma della prenotazione o errore
	ResPrenotaVeicolo: CDN -> ACMESky
)
```

```
ANNULLA_NOLEGGIO ::=
(
	% Richiesta di annullamento della prenotazione
	ReqAnnullaPrenotazione: ACMESky -> CDN;

	% Termina in caso di errore, o timeout,
	% completando l'annullamento della prenotazione da telefono
	+ELSE(1)

	% Risposta contenente la conferma dell'annullamento o errore
	ResPrenotaVeicolo: CDN -> ACMESky
)
```

Il termine `CHIEDI_DISTANZA` contiene le azioni necessarie ad *ACMESky* per trovare la distanza tra due posizioni geografiche attraverso un servizio online di  *Calcolo delle Distanze*.

```
CHIEDI_DISTANZA ::=
(
	% Fase di calcolo della distanza tra due location,
	% ripetuta in caso di errore
	(
		% Richiesta di calcolo delle distanze
		ReqDistanza: ACMESky -> GPS;

		% Timer
		+ELSE(1)

		% Risposta contenente la distanza o errore
		ResDistanza: GPS -> ACMESky
	)>1
)
```

Progetto realizzato da: *Francesca Salvatore*, *Stefano Parisotto*.
