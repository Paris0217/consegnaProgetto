package unibo.progetto.acmesky;

public class OfferAdHoc {
	private long id;
	private long interestID;
	private long offerID;
	private String tokeURI;
	
	public OfferAdHoc(long id, long interestID, long offerID, String tokeURI) {
		this.id = id;
		this.interestID = interestID;
		this.offerID = offerID;
		this.tokeURI = tokeURI;
	}
	
	public OfferAdHoc(long interestID, long offerID, String tokeURI) {
		this.interestID = interestID;
		this.offerID = offerID;
		this.tokeURI = tokeURI;
	}

	public long getId() {
		return id;
	}

	public long getInterestID() {
		return interestID;
	}

	public long getOfferID() {
		return offerID;
	}

	public String getTokeURI() {
		return tokeURI;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setInterestID(long interestID) {
		this.interestID = interestID;
	}

	public void setOfferID(long offerID) {
		this.offerID = offerID;
	}

	public void setTokeURI(String tokeURI) {
		this.tokeURI = tokeURI;
	}

	@Override
	public String toString() {
		return "OfferAdHoc [id=" + id + ", interestID=" + interestID + ", offerID=" + offerID + ", tokeURI=" + tokeURI + "]";
	}
}