# Coreografia Proiettata: Compagnia di Noleggio
Progetto di Ingegneria del Software Orientata ai Servizi

## Coreografia

Prima di leggere questa proiezione, è consigliato leggere la pagina della [Coreografia](docs/coreografia.md) per conoscere il funzionamento generale del progetto, gli attori e alcune note sulle modifiche effettuate al linguaggio della coreografia.

# Proiezione

La coreografia consiste nell'esecuzione di una versione modificata di `MAIN`.

```
COMPAGNIA_DI_NOLEGGIO ::= (MAIN)CDN

MAIN ::=
(
	GESTIONE_NOLEGGIO
  +
  ANNULLA_NOLEGGIO
)
```

Il termine `GESTIONE_NOLEGGIO` contiene le azioni necessarie ad *ACMESky* per prenotare una navetta tramite la *Compagnia di Noleggio* più vicina alla casa dell'acquirente, se questa si trova ad una distanza di massimo 30km dall'aeroporto, distanze trovate tramite un servizio di *Calcolo delle Distanze*.

```
GESTIONE_NOLEGGIO ::=
(
    % Richiesta di prenotazione del veicolo
    ReqPrenotaVeicolo@ACMESky;

    % Termina in caso di errore, o timeout
    +ELSE(1)

    % Risposta contenente la conferma della prenotazione o errore
    _ResPrenotaVeicolo_@ACMESky
)
```

```
ANNULLA_NOLEGGIO ::=
(
	% Richiesta di annullamento della prenotazione
	ReqAnnullaPrenotazione@ACMESky;

	% Termina in caso di errore, o timeout,
	+ELSE(1)

	% Risposta contenente la conferma dell'annullamento o errore
	_ResPrenotaVeicolo_@ACMESky
)
```

Progetto realizzato da: *Francesca Salvatore*, *Stefano Parisotto*.
