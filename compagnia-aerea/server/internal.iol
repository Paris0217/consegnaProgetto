type OfferLM: void {
	.depaCountry: string
	.depaCity: string
	.destCountry: string
	.destCity: string
	.price: double
	.num_seat: int
  .timestamp: string
}

type offerLastMinuteRequest: void {
	.id: int
	.depaCountry: string
	.depaCity: string
	.destCountry: string
	.destCity: string
	.price: double
	.num_seat: int
  .timestamp: string
  .company: string
}


interface InternalInterface {
  OneWay:
    addLMOffer( OfferLM )
}

interface ACMESkyInterface {
	OneWay:
		offerLastMinuteRequest( offerLastMinuteRequest )
}
