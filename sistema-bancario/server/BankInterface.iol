type PaymentRequest: void {
	.paymentID: int
	.cost: double
	.card: string( length( [16, 16] ) )
	.secretNumber: int( ranges( [100, 999] ) )
}

type PaymentResponse: void {
	.error: bool
	.sid: string
}

type CompensationMessage: void {
	.sid: string
}

type ConcludePaymentMessage: void {
	.sid: string
}

type PaymentErrorMessage: void {
	.message: string
}

type paymentNotificationRequest:void {
	.cost:double
	.paymentID:int
	.sid:string
}

type paymentNotificationResponse:void {
	.error:bool
	.sid:string
}

interface BankInterface {
	RequestResponse:
		requestPayment( PaymentRequest )( PaymentResponse )
	OneWay:
		compensation( CompensationMessage ),
		concludePayment( ConcludePaymentMessage )
}

interface ACMESkyPort {
	RequestResponse:
		paymentNotificationRequest( paymentNotificationRequest )( paymentNotificationResponse )
}
