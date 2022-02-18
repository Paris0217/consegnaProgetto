type ID: void {
	sid: string
}

type Reservation: void {
	.sid: string
	.address: string
	.name: string
	.timestamp: long
}

type ReservationRequest: void {
	.address: string
	.name: string
	.timestamp: long
}

type ReservationResponse: void {
	.reserved: bool
	.id: ID
}

type CancelReservationRequest: void {
	.id: ID
}

type CancelReservationResponse: void {
	.canceled: bool
}

interface RentalInterface {
	RequestResponse:
		reserve( ReservationRequest )( ReservationResponse ),
		cancelReservation( CancelReservationRequest )( CancelReservationResponse )
}

