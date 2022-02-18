//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.7 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2022.01.21 alle 06:23:16 PM CET 
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
 *         &lt;element name="sid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="paymentID" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="cost" type="{http://www.w3.org/2001/XMLSchema}double"/>
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
    "sid",
    "paymentID",
    "cost"
})
@XmlRootElement(name = "paymentNotificationRequest")
public class PaymentNotificationRequest {

    @XmlElement(required = true)
    protected String sid;
    @XmlElement(required = true)
    protected BigInteger paymentID;
    protected double cost;

    /**
     * Recupera il valore della proprietà sid.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSid() {
        return sid;
    }

    /**
     * Imposta il valore della proprietà sid.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSid(String value) {
        this.sid = value;
    }

    /**
     * Recupera il valore della proprietà paymentID.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPaymentID() {
        return paymentID;
    }

    /**
     * Imposta il valore della proprietà paymentID.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPaymentID(BigInteger value) {
        this.paymentID = value;
    }

    /**
     * Recupera il valore della proprietà cost.
     * 
     */
    public double getCost() {
        return cost;
    }

    /**
     * Imposta il valore della proprietà cost.
     * 
     */
    public void setCost(double value) {
        this.cost = value;
    }

}
