package at.wien.ma14.pvzd.verifysigapi;

/**
 * Datenstruktur mit dem hier relevanten Ergebnis der Siganturprüfung zur Übergabe an Python
 */
public class PvzdVerifySigResponse {
    /** pvzdCode is "OK" if no MOAException was catched and VerifyXMLSignatureResponse.getSignatureCheck().getCode()
        and VerifyXMLSignatureResponse.getSignatureCheck().getCode() are 0; otherwise "NOK" or MOAException.getMessageID()

        pvzdMessage explains pvzdCode

        signerCertificateEncoded contains the PEM encoded signer certificate
    */
    public String pvzdCode;
    public String pvzdMessage;
    public String signerCertificateEncoded;

    public PvzdVerifySigResponse(String c, String m, String cert) {
        pvzdCode = c;
        pvzdMessage = m;
        signerCertificateEncoded = cert;
    }
}
