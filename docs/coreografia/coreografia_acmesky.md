# Coreografia Proiettata: ACMESky
Progetto di Ingegneria del Software Orientata ai Servizi

## Coreografia

Prima di leggere questa proiezione, è consigliato leggere la pagina della [Coreografia](docs/coreografia.md) per conoscere il funzionamento generale del progetto, gli attori e alcune note sulle modifiche effettuate al linguaggio della coreografia.

# Proiezione

La proiezione consiste nell'esecuzione di un `MAIN` modificato.

```
ACMESKY ::= (MAIN)ACMESky

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
	ReqCreazioneUtente@Client;

	% Risposta contenente la conferma o l'errore del tentativo di creazione
	_ResCreazioneUtente_@Client
)
```

Il termine `ACCESSO` contiene le due possibili modalità di accesso usate dal *Cliente* per accedere al proprio utente su *ACMESky*.

```
ACCESSO ::=
(
	% Invio credenziali di accesso (id + password)
	ReqAccesso@Client;

	% Risposta contenente la conferma dell'accesso o errore
	_ResAccesso_@Client
)
```

Il termine `SELEZIONEOP` permette la scelta delle operazioni che il *Cliente* può effettuare nella piattaforma *ACMESky*.

```
SELEZIONEOP ::=
(
  % Invio operazione da eseguire
  ReqOperazione@Client;

  % Risposta contenente i dati utili al fine di eseguire l'operazione
  _ResOperazione_@Client
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
  	ReqModificaInfo@Client;

  	% Risposta contenente la conferma della modifica o errore
  	_ResModificaInfo_@Client
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
  	ReqModificaInteressi@Client;

  	% Risposta contenente la conferma della modifica o errore
  	_ResModificaInteressi_@Client
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
  	ReqEliminaUtente@Client;

  	% Risposta contenente la conferma dell'eliminazione o errore
  	_ResEliminaUtente_@Client
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
		_ReqTabellaVoli_@CA;

		% Termina e ripeti dopo in caso di errore, o timeout
		+ELSE(1)

		% Risposta contenente la lista delle offerte trovate
		ResTabellaVoli@CA
	)*;

	% Invio delle offerte trovate che rientrano nei parametri degli interessi dell'utente
	(
		% Fase di invio dell'offerta
		(INVIO_OFFERTA)*
	)*
)
```

Il termine `GESTIONE_OFFERTELM` contiene le azioni necessarie a ogni *Compagnia Aerea* per inviare offerte last-minute ad *ACMESky*.

```
GESTIONE_OFFERTELM ::=
(
	% Invio dell'offerta last-minute
	SendInvioOffertaLM@CA;

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
    _ReqReference_@P;

    % Risposta contenente la reference o errore
    ResReference@P
  )>1;

  % Ripete finchè non ottiene una risposta corretta
  (
  	% Invio della richiesta di invio di un messaggio
  	_ReqInvioMessaggio_@P;

  	% Risposta contenente la conferma dell'invio o errore
  	ResInvioMessaggio@P
  )>1
)
```

Il termine `INFO_OFFERTA` contiene le azioni necessarie al *Cliente* per recuperare le informazioni di una particolare offerta sulla piattaforma *ACMESky*, se ancora valida.

```
INFO_OFFERTA ::=
(
	% Invio della richiesta di informazioni su una offerta
	ReqInformazioniOfferta@Client;

	% Fase di controllo dei posti rimasti
	1 + INFO_POSTI

	% Risposta contenente l'offerta o errore
	_ResInformazioniOfferta_@Client
)
```

Il termine `INFO_POSTI` contiene le azioni necessarie ad *ACMESky* per recuperare le informazioni sui posti rimasti per un volo di una *Compagnia Aerea*.

```
INFO_POSTI ::=
(
	% Invio della richiesta dei posti rimasti per un volo
	_ReqPostiRimasti_@CA;

	% Termina in caso di timeout
	+ELSE(1)

	% Risposta contenente i posti rimasti o errore
	ResPostiRimasti@CA
)
```

Il termine `ACQUISTO_OFFERTA` contiene le azioni necessarie al *Cliente* per acquistare tramite il *Sistema Bancario* una particolare offerta sulla piattaforma *ACMESky*, se ancora valida, che a sua volta procederà a recuperare i biglietti dalla *Compagnia Aerea* che gestisce quel volo.

```
ACQUISTO_OFFERTA ::=
(
	% Invia l'intenzione iniziale di acquisto di un'offerta
	ReqIntenzioneAcquisto@Client;

	% Risponde in caso di offerta non valida e poi termina
	+ELSE(_ResIntenzioneAcquisto_@Client)

	% Fase di controllo dei posti rimasti, se l'offerta è valida
	1 + INFO_POSTI;

	% Risponde e termina in caso di errore o posti terminati
	+ELSE(_ResIntenzioneAcquisto_@Client)

	% Risposta contenente la conferma dell'inizio della procedura
	_ResIntenzioneAcquisto@Client;

	% Termina in caso di timeout o errore
	+ELSE(1)

  % Manda la conferma contenente i dati dei passeggeri
  ConfermaIntezioneAcquisto@Client;

    (
      % Blocco dei soldi

      % Invio della conferma di pagamento in corso
      SendNotificaPagamento@Banca;

      % Invio della richiesta di acquisto dei biglietti, se disponibili
      _ReqAcquistoBiglietto_@CA;

      % Termina in caso di errore, o timeout,
      % sbloccando i soldi e avvisando il cliente
			+ELSE(_SendCompensaCredito_@Banca)


      % La compagnia aerea blocca i posti di volo e crea i biglietti

      % Rispondi con i biglietti o errore (e.g. biglietti terminati, ecc.)
      ResAcquistoBiglietto@CA;

      % Termina in caso in cui i biglietti siano finiti,
      % sbloccando i soldi e annullando i biglietti
      +ELSE(_SendCompensaCredito_@Banca)

      % Fase di gestione del noleggio, se il prezzo è superiore a 1000E
      1 + GESTIONE_NOLEGGIO;

      % Invio alla banca il segnale di concludere il pagamento prelevando i soldi
      _SendConcludiPagamento_@Banca;

      % Termina in caso di errore, o timeout
      % sbloccando i soldi, annullando i biglietti e annullando il noleggio (se avvenuta la prenotazione online)
      +ELSE(
				(
					_SendAnnullaBiglietti_@CA;
					1 + ANNULLA_NOLEGGIO
				)
				
			)

      % Invio alla compagnia aerea la chiusura della procedura di acquisto dei biglietti
      _SendConfermaBiglietti_@CA;

      % In caso di errore, o timeout in questa fase, ACMESky contatterà telefonicamente la compagnia aerea per confermare l'avvenuto acquisto
      % oppure invia il biglietto e l'eventuale prenotazione della navetta al cliente
			+ELSE(1)

			ReqBiglietti@Client
			_ResBiglietti_@Client
    )

	)*;

  % Termina in caso di timeout o fine
  +ELSE(1)

  % Termina in caso di annullamento dell'operazione, o fornitore dei servizi bancari non disponibile
  % avvisando ACMESky della decisione
  SendAnnullaAcquisto@Client
)	
```

Il termine `GESTIONE_NOLEGGIO` contiene le azioni necessarie ad *ACMESky* per prenotare una navetta tramite la *Compagnia di Noleggio* più vicina alla casa dell'acquirente, se questa si trova ad una distanza di massimo 30km dall'aeroporto, distanze trovate tramite un servizio di *Calcolo delle Distanze*.

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
	_ReqPrenotaVeicolo_@CDN;

	% Termina in caso di errore, o timeout,
	% completando la prenotazione da telefono
	+ELSE(1)

	% Risposta contenente la conferma della prenotazione o errore
	ResPrenotaVeicolo@CDN
)

	
```

```
ANNULLA_NOLEGGIO ::=
(
	% Richiesta di annullamento della prenotazione
	_ReqAnnullaPrenotazione_@CDN;

	% Termina in caso di errore, o timeout,
	% completando l'annullamento della prenotazione da telefono
	+ELSE(1)

	% Risposta contenente la conferma dell'annullamento o errore
	ResPrenotaVeicolo@CDN
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
		_ReqDistanza_@GPS;

		% Timer
		+ELSE(1)

		% Risposta contenente la distanza o errore
		ResDistanza@GPS
	)>1
)
```

Progetto realizzato da: *Francesca Salvatore*, *Stefano Parisotto*.
