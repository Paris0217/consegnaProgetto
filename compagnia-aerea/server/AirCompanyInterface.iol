type Preference: void {
	.preferenceID: int
	.fromTimestamp: string
	.toTimestamp?: string
	.departureCountry: string
	.departureCity?: string
	.arrivalCountry: string
	.arrivalCity?: string
	.maxPrice: double
}

type Offer: void {
	.preferenceID?: int
	.airportID: int
	.offerID?: int
	.timestamp: string
	.departureCountry: string
	.departureCity: string
	.arrivalCountry: string
	.arrivalCity: string
	.price: double
	.seats: int
}

type Ticket: void {
	.id: int
}

type SearchOffersRequest: void {
	preferences*: Preference
}

type SearchOffersResponse: void {
	offers*: Offer
}

type AvailableSeatsRequest: void {
	.offerID: int
}

type AvailableSeatsResponse: void {
	.availableSeats: int
	.seats: int
}

type PurchaseOfferRequest: void {
	.offerID: int
	.seats: int
}

type PurchaseOfferResponse: void {
	.sid: string
	.ticket[0,*]: Ticket
}

type ConfirmPurchaseMessage: void {
	.sid: string
}

type CancelPurchaseMessage: void {
	.sid: string
}

interface AirCompanyInterface {
	RequestResponse:
		searchOffers( SearchOffersRequest )( SearchOffersResponse ),
		availableSeats( AvailableSeatsRequest )( AvailableSeatsResponse ),
		purchaseOffer( PurchaseOfferRequest )( PurchaseOfferResponse )
	OneWay:
		confirmPurchase( ConfirmPurchaseMessage ),
		cancelPurchase( CancelPurchaseMessage )
}
