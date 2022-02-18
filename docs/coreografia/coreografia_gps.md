# Coreografia Proiettata: Calcolo delle Distanze
Progetto di Ingegneria del Software Orientata ai Servizi

## Coreografia

Prima di leggere questa proiezione, è consigliato leggere la pagina della [Coreografia](docs/coreografia.md) per conoscere il funzionamento generale del progetto, gli attori e alcune note sulle modifiche effettuate al linguaggio della coreografia.

# Proiezione

La coreografia consiste nell'esecuzione di `CHIEDI_DISTANZA`.

```
CALCOLO_DELLE_DISTANZE = (CHIEDI_DISTANZA)GPS
```

Il termine `CHIEDI_DISTANZA` contiene le azioni necessarie ad *ACMESky* per trovare la distanza tra due posizioni geografiche attraverso un servizio online di  *Calcolo delle Distanze*.

```
CHIEDI_DISTANZA ::=
(
	% Fase di calcolo della distanza tra due location,
	(
		% Richiesta di calcolo delle distanze
		ReqDistanza@ACMESky;

		% Timer
		+ELSE(1)

		% Risposta contenente la distanza o errore
		_ResDistanza_@ACMESky
	)>1
)
```

Progetto realizzato da: *Francesca Salvatore*, *Stefano Parisotto*.
