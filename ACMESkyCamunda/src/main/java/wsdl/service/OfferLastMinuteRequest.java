//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.7 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2022.01.21 alle 06:23:16 PM CET 
// In realtà questo file è stato costruito a mano copiandone uno già fatto
//


package wsdl.service;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per anonymous complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}:integer"/>
 *       &lt;element name="depaCountry" type="{http://www.w3.org/2001/XMLSchema}:string"/>
 *       &lt;element name="depaCity" type="{http://www.w3.org/2001/XMLSchema}:string"/>
 *       &lt;element name="destCountry" type="{http://www.w3.org/2001/XMLSchema}:string"/>
 *       &lt;element name="destCity" type="{http://www.w3.org/2001/XMLSchema}:string"/>
 *       &lt;element name="price" type="{http://www.w3.org/2001/XMLSchema}:double"/>
 *       &lt;element name="num_seat" type="{http://www.w3.org/2001/XMLSchema}:int"/>
 *       &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}:string"/>
 *       &lt;element name="company" type="{http://www.w3.org/2001/XMLSchema}:string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "id",
    "depaCountry",
    "depaCity",
    "destCountry",
    "destCity",
    "price",
    "num_seat",
    "timestamp",
    "company"
})
@XmlRootElement(name = "offerLastMinuteRequest")
public class OfferLastMinuteRequest {

    @XmlElement(required = true)
    protected BigInteger id;
    @XmlElement(required = true)
    protected String depaCountry;
    @XmlElement(required = true)
    protected String depaCity;
    @XmlElement(required = true)
    protected String destCountry;
    @XmlElement(required = true)
    protected String destCity;
    protected double price;
    @XmlElement(required = true)
    protected Integer num_seat;
    @XmlElement(required = true)
    protected String timestamp;
    @XmlElement(required = true)
    protected String company;

    /**
     * Recupera il valore della proprietà id.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getId() {
        return id;
    }

    /**
     * Imposta il valore della proprietà id.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setId(BigInteger value) {
        this.id = value;
    }

    /**
     * Recupera il valore della proprietà depaCountry.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepaCountry() {
        return depaCountry;
    }

    /**
     * Imposta il valore della proprietà depaCountry.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepaCountry(String value) {
        this.depaCountry = value;
    }

    /**
     * Recupera il valore della proprietà depaCity.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepaCity() {
        return depaCountry;
    }

    /**
     * Imposta il valore della proprietà depaCity.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepaCity(String value) {
        this.depaCity = value;
    }
    
    /**
     * Recupera il valore della proprietà destCountry.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestCountry() {
        return destCountry;
    }

    /**
     * Imposta il valore della proprietà destCountry.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestCountry(String value) {
        this.destCountry = value;
    }

    /**
     * Recupera il valore della proprietà destCity.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestCity() {
        return destCountry;
    }

    /**
     * Imposta il valore della proprietà destCity.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestCity(String value) {
        this.destCity = value;
    }

    /**
     * Recupera il valore della proprietà price.
     * 
     */
    public double getPrice() {
        return price;
    }

    /**
     * Imposta il valore della proprietà price.
     * 
     */
    public void setPrice(double value) {
        this.price = value;
    }

    /**
     * Recupera il valore della proprietà num_seat.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumSeat() {
        return num_seat;
    }

    /**
     * Imposta il valore della proprietà num_seat.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumSeat(Integer value) {
        this.num_seat = value;
    }
    
    /**
     * Recupera il valore della proprietà timestamp.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Imposta il valore della proprietà timestamp.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimestamp(String value) {
        this.timestamp = value;
    }

    /**
     * Recupera il valore della proprietà company.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompany() {
        return company;
    }

    /**
     * Imposta il valore della proprietà company.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompany(String value) {
        this.company = value;
    }

}
