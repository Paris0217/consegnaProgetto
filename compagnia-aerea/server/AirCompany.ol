include "console.iol"
include "runtime.iol"
include "database.iol"
include "AirCompanyInterface.iol"
include "internal.iol"

service AirCompanyService {
	execution: concurrent

	inputPort AirCompanyPort {
		Location: "socket://localhost:5010"
		Protocol: soap {
			wsdl = "./aircompany.wsdl",
			wsdl.port = "AirCompanyPortServicePort"
		}
		Interfaces: AirCompanyInterface
	}



	inputPort InternalPort {
		Location: "socket://localhost:5011"
		Protocol: soap {
			wsdl = "./internal.wsdl",
			wsdl.port = "BankPortServicePort"
		}
		Interfaces: InternalInterface
	}

	outputPort ACMESkyLMOfferPort {
		Location: "socket://localhost:8080/ws"
		Protocol: soap {
			.wsdl = "http://localhost:8080/ws/services.wsdl";
			.wsdl.port = "ACMESkyPortServicePort"
		}
		Interfaces: ACMESkyInterface
	}


	cset {
		sid: PurchaseOfferResponse.sid
				ConfirmPurchaseMessage.sid
				CancelPurchaseMessage.sid
	}

	init
	{
		getenv@Runtime( "CA_ID" )( CA_ID )
		// global.env.CA_ID = CA_ID
		global.env.CA_ID = 0 						// DEBUG ONLY SET A DEFAULT ID
		global.env.CA_NAME = "Wingsitaly" // DEBUG ONLY SET A DEFAULT ID
		with (global.connection) {
			.username = "postgres"
			.password = "postgres"
			.host = "localhost:5433"
			.database = "aircompanies"
			.driver = "postgresql"
		}
		println@Console( "Air Company #" + global.env.CA_ID + " " + global.env.CA_NAME + " Service starting..." )()

		scope ( databaseSearch ) {
			install ( ConnectionError =>
				println@Console( "Errore nella connessione al database!" )(  )
			);

			connect@Database( global.connection )()

			close@Database(  )(  )
		}
	}

	define getSeats
	{
		query = "SELECT (f.seats - t.c) AS available, f.seats"
				+ " FROM (SELECT "+p.offerID+" as flight_id, COALESCE((SELECT COUNT(*)::int AS c FROM ids_ac_tickets WHERE flight_id = "+p.offerID+" AND active = TRUE GROUP BY flight_id), 0) as c) t"
				+ " INNER JOIN ids_ac_flights f"
				+ " ON t.flight_id = f.id"
				+ " LIMIT 1"

		undef(r)
		r.seats = 0
		r.availableSeats = 0

		scope ( databaseSearch ) {
			install ( default =>
				println@Console( "[POSTI] Errore nella conta dei posti disponibili del database!" )(  )
			);

			connect@Database( global.connection )()
			query@Database( query )( result )
			close@Database(  )(  )

			if (#result.row > 0) {
				r.seats = result.row[0].seats
				r.availableSeats = result.row[0].available
			}
		}

		undef(query)
		undef(result)
	}

	main
	{

		[ searchOffers( request )( response ) {
			println@Console( "[RICERCA] Ricevuta una richiesta di ricerca delle offerte." )()

			preferenceID = 0
			for (preference in request.preferences) {
				query = "SELECT * FROM ids_ac_flights" +
								" WHERE airport_id = "+ global.env.CA_ID +" AND \"timestamp\" >= TO_TIMESTAMP('" + preference.fromTimestamp + "', 'YYYY-MM-DD') AND departure_country = '" + preference.departureCountry + "'" +
								" AND arrival_country= '"+ preference.arrivalCountry +"' AND price <= " + preference.maxPrice
				if (is_defined(preference.toTimestamp)) query = query + " AND \"timestamp\" <= TO_TIMESTAMP('"+ preference.toTimestamp + "', 'YYYY-MM-DD')"
				if (is_defined(preference.departureCity)) query = query + " AND departure_city = '" + preference.departureCity + "'"
				if (is_defined(preference.arrivalCity)) query = query + " AND arrival_city = '" + preference.arrivalCity + "'"
				query += " ORDER BY \"timestamp\" ASC"

				// println@Console( query )(  )
				scope ( databaseSearch ) {
					install ( default =>
						println@Console( "[RICERCA] Errore nella ricerca del database!" )(  )
					);

					connect@Database( global.connection )()
					query@Database( query )( result )
					close@Database(  )(  )

					if (#result.row > 0) {
						for ( index=0, index < #result.row, index++ ) {
							offersLength = #response.offers
							response.offers[offersLength].preferenceID = preferenceID
							response.offers[offersLength].offerID = result.row[index].id
							response.offers[offersLength].airportID = result.row[index].airport_id
							response.offers[offersLength].timestamp = result.row[index].timestamp
							response.offers[offersLength].departureCountry = result.row[index].departure_country
							response.offers[offersLength].departureCity = result.row[index].departure_city
							response.offers[offersLength].arrivalCountry = result.row[index].arrival_country
							response.offers[offersLength].arrivalCity = result.row[index].arrival_city
							response.offers[offersLength].price = result.row[index].price
							response.offers[offersLength].seats = result.row[index].seats
						}
					}
				}

				undef(result)
				preferenceID++
			}
		} ]

		[ availableSeats( request )( response ) {
			p.offerID = request.offerID
			getSeats
			response << r
		} ]

		[ purchaseOffer( request )( response ) {
			install ( default =>
				println@Console( "[ACQUISTO] Errore nella prenotazione dei bigletti!" )(  )
				comp (databaseTicketInsert)
			);

			csets.sid = sid = new

			p.offerID = request.offerID
			getSeats
			available = r.availableSeats >= request.seats

			response.sid = sid
			if (available) {
					insertQuery = "INSERT INTO ids_ac_tickets (sid, flight_id) "
						+ "SELECT '"+sid+"' AS sid, t1.flight_id "
						+ "FROM (SELECT "+request.offerID+" as flight_id, COALESCE((SELECT COUNT(*)::int AS c FROM ids_ac_tickets WHERE flight_id = "+request.offerID+" AND sid != '"+sid+"' AND active = TRUE GROUP BY flight_id), 0) c) t1 "
						+ "INNER JOIN ids_ac_flights t2 ON t1.flight_id = t2.id AND (c + "+request.seats+") <= t2.seats"

				rollbackQuery = "UPDATE ids_ac_tickets SET active = FALSE WHERE flight_id = "+request.offerID+" AND sid = '"+sid+"' AND active = TRUE"

				ticketsQuery = "SELECT id FROM ids_ac_tickets WHERE flight_id = "+request.offerID+" AND sid = '"+sid+"' AND active = TRUE"

				scope (databaseTicketInsert) {
					connect@Database( global.connection )()

					for (i = 0, i < request.seats, i++) {
						insertRequest.statement[i] << insertQuery
					}

					executeTransaction@Database( insertRequest )( insertResponse )
					install ( this =>
						connect@Database( global.connection )()
						rollbackRequest.statement[0] << rollbackQuery
						executeTransaction@Database( rollbackRequest )( rollbackResponse )
						undef(response.ticket)
						close@Database(  )(  )
						println@Console( "[ACQUISTO] Acquisto " + sid + " compensato!" )(  )
					);

					query@Database( ticketsQuery )( ticketsResponse )
					for (ticket in ticketsResponse.row) {
						response.ticket[#response.ticket] << ticket
					}

					close@Database(  )(  )
				}
			}
		} ] {
			if (available) {
				[ confirmPurchase ( message ) ] {
					println@Console( "[ACQUISTO] Acquisto confermato!" )(  )
				}

				[ cancelPurchase ( message ) ] {
					println@Console( "[ACQUISTO] Acquisto annullato!" )(  )
					comp (databaseTicketInsert)
				}
			}
		}


		[ addLMOffer( request ) ] {
			install( default =>
				println@Console( "[LAST-MINUTE] Errore rilevato! " + default.msg )(  )
				comp ( lmofferScope )
			);

			println@Console( "[LAST-MINUTE] Ricevuta un messaggio di creazione di un'offerta last-minute." )()

			sid = new

			insertQuery =	"INSERT INTO ids_ac_flights( "
				+ "sid, airport_id, \"timestamp\", departure_country, departure_city, arrival_country, arrival_city, price, seats) "
				+ "VALUES ('"+sid+"', "+ global.env.CA_ID +", TO_TIMESTAMP('"+request.timestamp+"', 'HH24:MI YYYY-MM-DD'), '"+request.depaCountry+"', '"+request.depaCity+"', '"+request.destCountry+"', '"+request.destCity+"', '"+request.price+"', '"+request.num_seat+"')"
			println@Console( insertQuery )()

			rollback = "DELETE FROM ids_ac_flights WHERE airport_id = "+global.env.CA_ID+" AND sid = '"+sid+"'"
			println@Console( rollback )()

			findQuery = "SELECT * FROM ids_ac_flights WHERE airport_id = "+ global.env.CA_ID +" AND sid = '"+sid+"'"
			println@Console( findQuery )()

			scope ( lmofferScope ) {
				connect@Database( global.connection )(  )
				update@Database( insertQuery )(  )

				install (this =>
					println@Console( "[LAST-MINUTE] Compensazione in corso! " )(  )
					connect@Database( global.connection )(  )
					update@Database( rollback )(  )
					close@Database(  )(  )
				);

				query@Database( findQuery )( findResponse )
				close@Database(  )(  )
			}

			install (default =>
				println@Console( "[LAST-MINUTE] Errore rilevato! " + default.msg )(  )
				// Qui non viene eliminata dal DB perchè comunque sarà disponibile per le ricerche da ora in poi
			);

			if (#findResponse.row > 0) {
				response.id = findResponse.row[0].id
				response.depaCountry = findResponse.row[0].departure_country
				response.depaCity = findResponse.row[0].departure_city
				response.destCountry = findResponse.row[0].arrival_country
				response.destCity = findResponse.row[0].arrival_city
				response.price = findResponse.row[0].price
				response.num_seat = findResponse.row[0].seats
				response.timestamp = findResponse.row[0].timestamp
				response.company = global.env.CA_NAME

				offerLastMinuteRequest@ACMESkyLMOfferPort( response )
			} else {
				throw ( Error )
			}
		}

	}
}
