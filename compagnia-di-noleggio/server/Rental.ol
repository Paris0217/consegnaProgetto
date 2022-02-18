include "console.iol"
include "time.iol"
include "RentalInterface.iol"

service RentalService {
	execution: concurrent

	inputPort RentalPort {
		location: "socket://localhost:5004"
		protocol: soap {
			wsdl = "rental.wsdl",
			wsdl.port = "RentalPortServicePort"
		}
		interfaces: RentalInterface
	}

	cset {
		sid: ID.sid
	}

	define printAllReservations
	{
		println@Console( "------------ Prenotazioni ------------" )(  )
		foreach (sid : global.reservations) {
			println@Console( sid + " | " + global.reservations.( sid ).name + " | " + global.reservations.( sid ).address + " | " + global.reservations.( sid ).timestamp )(  )
		}
		println@Console( "--------------------------------------" )(  )

	}

	main
	{
		[ reserve( request )( response ) {
			install ( default =>
				println@Console( "[NOLEGGIO] Errore rilevato! " + default.msg )(  )
				comp(s)
			);

			scope ( s ) {
				csets.sid = sid = new

				install ( this =>
					println@Console( "[NOLEGGIO] Noleggio annullato a causa di un errore!" )(  )
				);

				global.reservations.( sid ) = true
				global.reservations.( sid ).address = request.address
				global.reservations.( sid ).name = request.name
				global.reservations.( sid ).timestamp = request.timestamp

				println@Console( "[NOLEGGIO] Noleggio prenotato a nome " + request.name + " per la data " + request.timestamp + " all'indirizzo " + request.address + ". ID associato: " + sid )(  )

				install ( this =>
					undef(global.reservations.( sid ))
					println@Console( "[NOLEGGIO] Noleggio in corso con ID " + sid + " annullato e compensato!" )(  )
				);

				// printAllReservations /* DEBUG ONLY */

				response.reserved = true
				response.id.sid = sid
			}
		} ]

		[ cancelReservation( request )( response ) {
			sid = request.id.sid

			if (is_defined(global.reservations.( sid ))) {
				undef(global.reservations.( sid ))

				println@Console( "[CANCELLAZIONE] Noleggio con ID " + sid + " cancellato!" )(  )

				response.canceled = true
			} else {
				println@Console( "[CANCELLAZIONE] Noleggio con ID " + sid + " non esiste!" )(  )

				response.canceled = false
			}

			// printAllReservations /* DEBUG ONLY */
		} ]
	}
}
