# Coreografia Proiettata: Fornitore di Servizi Bancari
Progetto di Ingegneria del Software Orientata ai Servizi

## Coreografia

Prima di leggere questa proiezione, è consigliato leggere la pagina della [Coreografia](docs/coreografia.md) per conoscere il funzionamento generale del progetto, gli attori e alcune note sulle modifiche effettuate al linguaggio della coreografia.

## Convenzioni

Consideriamo `_o_@a` come output. 

# Proiezione

La proiezione consiste nell'esecuzione di `ACQUISTO_OFFERTA`.

```
SISTEMA_BANCARIO ::= (ACQUISTO_OFFERTA)Banca
```

Il termine `ACQUISTO_OFFERTA` contiene le azioni necessarie al *Cliente* per acquistare tramite il *Sistema Bancario* una particolare offerta sulla piattaforma *ACMESky*, se ancora valida, che a sua volta procederà a recuperare i biglietti dalla *Compagnia Aerea* che gestisce quel volo.

```
ACQUISTO_OFFERTA ::=
(
	% Ricezione dati carta
	ReqInvioPagamento@Client;

	% Termina in caso di errore o timeout
	+ELSE(1)

	% Risposta contenente l' errore nei dati di pagamento (e.g. credito insufficiente, metodo non valido, ecc.),
	+ELSE(_ResInvioPagamento_@Client)

  	% Blocco dei soldi

  	% Invio della conferma di pagamento in corso
  	_SendNotificaPagamento_@ACMESky;

  	% Termina sbloccando i soldi
	SendCompensaCredito@ACMESky
  	+
  	SendConcludiPagamento@ACMESky
)
```


Progetto realizzato da: *Francesca Salvatore*, *Stefano Parisotto*.
