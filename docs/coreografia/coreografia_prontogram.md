# Coreografia Proiettata: Prontogram
Progetto di Ingegneria del Software Orientata ai Servizi

## Coreografia

Prima di leggere questa proiezione, è consigliato leggere la pagina della [Coreografia](docs/coreografia.md) per conoscere il funzionamento generale del progetto, gli attori e alcune note sulle modifiche effettuate al linguaggio della coreografia.

# Proiezione

La coreografia consiste nell'esecuzione di una versione modificata di `MAIN`.

```
PRONTOGRAM ::= (MAIN)P

MAIN ::=
(
	RICERCA_OFFERTE;
	+
	INVIO_OFFERTA
);
```

Il termine `INVIO_OFFERTA` contiene le azioni necessarie ad *ACMESky* per inviare le offerte tramite *Prontogram*.

```
INVIO_OFFERTA ::=
(
  % Ricezione della richiesta di reference
  ReqReference@ACMESky;

  % Risposta contenente la reference o errore
  _ResReference_@ACMESky;
  
  % Ricezione della richiesta di invio di un messaggio
  ReqInvioMessaggio@ACMESky;

  % Risposta contenente la conferma dell'invio o errore
  _ResInvioMessaggio_@ACMESky
)
```

Il termine `RICERCA_OFFERTE` contiene le azioni necessarie ad ogni *Cliente* per controllare la lista dei messaggi contenenti offerte ricevuti tramite *Prontogram*.

```
RICERCA_OFFERTE ::=
(
	% Fase di controllo dei messaggi ricevuti
	% Ricezione della richiesta della offerta ricevuta
	ReqControlloOfferte@Client;

	% Risposta contenente il messaggio con l'offerta o errore
	_ResControlloOfferte_@Client
)
```

Progetto realizzato da: *Francesca Salvatore*, *Stefano Parisotto*.
