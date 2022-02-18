# Coreografia Proiettata: Compagnia Aerea
Progetto di Ingegneria del Software Orientata ai Servizi

## Coreografia

Prima di leggere questa proiezione, è consigliato leggere la pagina della [Coreografia](docs/coreografia.md) per conoscere il funzionamento generale del progetto, gli attori e alcune note sulle modifiche effettuate al linguaggio della coreografia.

## Convenzioni

Consideriamo `_o_@a` come output. 

# Proiezione

La coreografia consiste nell'esecuzione di `MAIN`.

```
COMPAGNIA_AEREA ::= (MAIN)CA

MAIN ::=
(
	GESTIONE_OFFERTE
	+
	GESTIONE_OFFERTELM
	+
  	INFO_POSTI
  	+
	ACQUISTO_OFFERTA
)
```

Il termine `GESTIONE_OFFERTE` contiene le azioni necessarie ad *ACMESky* per richiedere periodicamente ad ogni *Compagnia Aerea* la lista dei voli che rientrano tra gli interessi degli utenti e successivo invio delle offerte trovate agli utenti interessati tramite *Prontogram*.

```
GESTIONE_OFFERTE ::=
(
	% Ricezione richiesta della lista contenente i voli di interesse
	ReqTabellaVoli@ACMESky;

  	% Termina in caso di Errore
	+ELSE(1)

	% Risposta contenente la lista delle offerte trovate
 	 _ResTabellaVoli_@ACMESky
)
```

Il termine `GESTIONE_OFFERTELM` contiene le azioni necessarie a ogni *Compagnia Aerea* per inviare offerte last-minute ad *ACMESky* ed a quest'ultima di notificarle ai possibili interessati.

```
GESTIONE_OFFERTELM ::=
(
	% Invio dell'offerta last-minute
	_SendInvioOffertaLM_@ACMESky
)
```

Il termine `INFO_POSTI` contiene le azioni necessarie ad *ACMESky* per recuperare le informazioni sui posti rimasti per un volo di una *Compagnia Aerea*.

```
INFO_POSTI ::=
(
	% Invio della richiesta dei posti rimasti per un volo
	ReqPostiRimasti@ACMESky;

	% Termina in caso di Errore
	+ELSE(1)

	% Risposta contenente i posti rimasti o errore
	_ResPostiRimasti_@ACMESky
)
```

Il termine `ACQUISTO_OFFERTA` contiene le azioni necessarie al *Cliente* per acquistare tramite il *Sistema Bancario* una particolare offerta sulla piattaforma *ACMESky*, se ancora valida, che a sua volta procederà a recuperare i biglietti dalla *Compagnia Aerea* che gestisce quel volo.

```
ACQUISTO_OFFERTA ::=
(
 	% Invio della richiesta di acquisto dei biglietti, se disponibili
  	ReqAcquistoBiglietto@ACMESky;

  	% Blocca i posti di volo e crea i biglietti

  	% Rispondi con i biglietti o errore (e.g. biglietti terminati, ecc.)
  	_ResAcquistoBiglietto_@ACMESky;


  	% Riceve di annullare i biglietti
  	SendAnnullaBiglietti@ACMESky
  	+
  	% Riceve la conferma di acquisto dei biglietti
  	SendConfermaBiglietti@ACMESky
  	+
  	% Timer
  	1
)
```

Progetto realizzato da: *Francesca Salvatore*, *Stefano Parisotto*.
