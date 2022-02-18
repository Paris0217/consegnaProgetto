include "console.iol"
include "BankInterface.iol"

service BankService {
	execution: concurrent

	inputPort BankPort {
		Location: "socket://localhost:5001"
		Protocol: soap {
			wsdl = "./newBank.wsdl",
			wsdl.port = "BankPortServicePort"
		}
		Interfaces: BankInterface
	}

	outputPort ACMESkyPortServicePort {
		Location: "socket://localhost:8080/ws"
		Protocol: soap {
			.wsdl = "http://localhost:8080/ws/services.wsdl";
			.wsdl.port = "ACMESkyPortServicePort"
		}
		Interfaces: ACMESkyPort
	}

	cset {
		sid: PaymentResponse.sid
			CompensationMessage.sid
			ConcludePaymentMessage.sid
			paymentNotificationResponse.sid
	}

	init
	{
		println@Console( "Bank Service starting..." )(  )

		// Inizializza il "database" con alcuni valori di test
		global.bank.( "4000000000000000" ) = true
		global.bank.( "4000000000000000" ).secretNumber = 400
		global.bank.( "4000000000000000" ).credit = 10000.0

		global.bank.( "3000000000000000" ) = true
		global.bank.( "3000000000000000" ).secretNumber = 300
		global.bank.( "3000000000000000" ).credit = 5000.0

		global.bank.( "2000000000000000" ) = true
		global.bank.( "2000000000000000" ).secretNumber = 200
		global.bank.( "2000000000000000" ).credit = 400.0

		global.bank.( "1000000000000000" ) = true
		global.bank.( "1000000000000000" ).secretNumber = 100
		global.bank.( "1000000000000000" ).credit = 8000.0

		// Account di ACMESky
		global.bank.( "9999999999999999" ) = true
		global.bank.( "9999999999999999" ).secretNumber = 999
		global.bank.( "9999999999999999" ).credit = 10000.0

	}

	main
	{
		requestPayment( request )( response ) {
			install ( default =>
				// Attiva compensazione in caso di errore
				println@Console( "[PAGAMENTO] Errore rilevato! " + default.msg )( )
				comp(s1)
			);

			csets.sid = sid = response.sid = new

			scope ( s1 ) {

				install ( this =>
					println@Console( "[PAGAMENTO] Richiesta di pagamento annullata a causa di un errore!" )(  )
				);

				// Controlla che il numero della carta esista e che sia valido il numero segreto
				isValid = is_defined(global.bank.( request.card )) && global.bank.( request.card ).secretNumber == request.secretNumber
				if (isValid) {
					println@Console( "[PAGAMENTO] Richiesta di pagamento confermata." )(  )
					response.error = false


					install ( default =>
						// Attiva compensazione in caso di errore
						println@Console( "[PAGAMENTO] Errore rilevato! " + default.msg )( )
						comp(s2)
					);
					scope ( s2 ) {
						// Procede se non c'è nessun errore
						if (isValid) {
							// Viene congelato il credito
							println@Console( "[PAGAMENTO] Credito bloccato in preparazione del pagamento " + request.paymentID + " e sessione " + sid + "." )(  )
							global.bank.( request.card ).credit -= request.cost
							install ( this =>
								// Compensa in caso di errore
								println@Console( "[PAGAMENTO] Credito sbloccato per il pagamento " + request.paymentID + " e sessione " + sid + "!" )(  )
								global.bank.( ^request.card ).credit += ^request.cost
							);

							// invia notifica + invio errore acquisto
							scope ( s3 ) {
								install( default =>
									println@Console("Errore avvenuto durante la notifica del pagamento ad ACMESky.")()
									response.error = true
									comp (s2)
								);

								println@Console( "[NOTIFICA] Invio della notifica di pagamento ad ACMESky." )(  )
								notificationMessage.sid = sid
								notificationMessage.paymentID = request.paymentID
								notificationMessage.cost = request.cost
								paymentNotificationRequest@ACMESkyPortServicePort( notificationMessage )( notificationMessageResponse )

							/* Waiting for AcmeSky */
							if ( notificationMessageResponse.error ) {
								/* Trigger compensation */
								println@Console( "[COMPENSAZIONE] ACMESky ha risposto con errore alla notifica di pagamento " + request.paymentID + " con sessione " + sid )(  )
								response.error = true
								comp(s2)
							} else {
								[ compensation( message ) ] {
									/* Trigger compensation */
									println@Console( "[COMPENSAZIONE] ACMESky ha richiesto di annullare il pagamento " + request.paymentID + " con sessione " + sid )(  )
									response.error = true
									comp(s2)
								}

								[ concludePayment( message ) ] {
									install( this =>
										/* Removing credit compensation */
										empty = true
									);
									println@Console( "[CONCLUDI] ACMESky ha richiesto di concludere il pagamento " + request.paymentID + " con sessione " + sid )(  )
									global.bank.("9999999999999999").credit += request.cost
									println@Console( global.bank.("9999999999999999").credit )()
								}
							}
							}
						}
					}
				} else {
					println@Console( "[PAGAMENTO] Richiesta di pagamento rifiutata! Numero della carta o codice segreto non validi!" )(  )
					response.error = true
				}
			}
		}
	}
}
