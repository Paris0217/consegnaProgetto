# Coreografia Proiettata: Cliente
Progetto di Ingegneria del Software Orientata ai Servizi

## Coreografia

Prima di leggere questa proiezione, è consigliato leggere la pagina della [Coreografia](docs/coreografia.md) per conoscere il funzionamento generale del progetto, gli attori e alcune note sulle modifiche effettuate al linguaggio della coreografia.

## Convenzioni

Consideriamo `_o_@a` come output. 

# Proiezione

La proiezione consiste nell'esecuzione di `MAIN`.

```
CLIENTE ::= (MAIN)Client

MAIN ::=
(
	GESTIONE_UTENTE
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
	_ReqCreazioneUtente_@ACMESky;

	% Risposta contenente la conferma o l'errore del tentativo di creazione
	ResCreazioneUtente@ACMESky
)
```

Il termine `ACCESSO` contiene le azioni necessarie al *Cliente* per eseguire l'accesso su *ACMESky*.

```
ACCESSO ::=
(
	% Invio credenziali di accesso (id + password)
	_ReqAccesso_@ACMESky;

	% Risposta contenente la conferma dell'accesso o errore
	ResAccesso@ACMESky
)
```

Il termine `SELEZIONEOP` contiene le azioni necessarie al *Cliente* per indicare ad *ACMESky* il tipo di operazione che si desidera fare sul proprio profilo.

```
SELEZIONEOP ::=
(
  	% Invio operazione da eseguire
  	_ReqOperazione_@ACMESky;

	% Timer
	+ELSE(1)

  	% Risposta contenente i dati utili al fine di eseguire l'operazione
  	ResOperazione@ACMESky
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
  		_ReqModificaInfo_@ACMESky;

  		% Risposta contenente la conferma della modifica o errore
  		ResModificaInfo@ACMESky
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
  		_ReqModificaInteressi_@ACMESky;

  		% Risposta contenente la conferma della modifica o errore
  		ResModificaInteressi@ACMESky
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
  		_ReqEliminaUtente_@ACMESky;

  		% Risposta contenente la conferma dell'eliminazione o errore
  		ResEliminaUtente@ACMESky
  	)
)
```

Il termine `RICERCA_OFFERTE` contiene le azioni necessarie ad ogni *Cliente* per controllare la lista dei messaggi contenenti offerte ricevuti tramite *Prontogram*.

```
RICERCA_OFFERTE ::=
(
	% Fase di controllo dei messaggi ricevuti
	(
		% Invio della richiesta della offerta ricevuta
		_ReqControlloOfferte_@P;

		% Risposta contenente il messaggio con l'offerta o errore
		ResControlloOfferte@P
	)>1
)
```

Il termine `INFO_OFFERTA` contiene le azioni necessarie al *Cliente* per recuperare le informazioni di una particolare offerta sulla piattaforma *ACMESky*, se ancora valida.

```
INFO_OFFERTA ::=
(
	% Invio della richiesta di informazioni su una offerta
	_ReqInformazioniOfferta_@ACMESky;

	% Risposta contenente l'offerta o errore
	ResInformazioniOfferta@ACMESky
)
```

Il termine `ACQUISTO_OFFERTA` contiene le azioni necessarie al *Cliente* per acquistare tramite il *Sistema Bancario* una particolare offerta sulla piattaforma *ACMESky*, se ancora valida, che a sua volta procederà a recuperare i biglietti dalla *Compagnia Aerea* che gestisce quel volo.

```
ACQUISTO_OFFERTA ::=
(
	% Invia l'intenzione iniziale di acquisto di un'offerta
	_ReqIntenzioneAcquisto_@ACMESky;

	% Risposta contenente la conferma dell'inizio della procedura o errore
	ResIntenzioneAcquisto@ACMESky;

	% Termina in caso di timeout o errore
	+ELSE(1)

  	% Manda la conferma contenente i dati dei passeggeri
  	_ConfermaIntezioneAcquisto_@ACMESky;

	% Redirect con il sistema bancario

	% Scelta del metodo di pagamento o annullamento operazione
	(
		% Invio del pagamento con scelta del metodo di pagamento
		_ReqInvioPagamento_@Banca;

		% Termina in caso di timeout
		+ELSE(1)

	);

  	% Termina in caso di timeout o fine
  	+ELSE(1)

  	% Termina in caso di annullamento dell'operazione, o fornitore dei servizi bancari non disponibile
 	% avvisando ACMESky della decisione
  	_SendAnnullaAcquisto_@ACMESky
)
```


Progetto realizzato da: *Francesca Salvatore*, *Stefano Parisotto*.
