package at.wien.ma14.pvzd.verifysigapi;

import at.gv.egovernment.moa.spss.MOAException;
import at.gv.egovernment.moa.spss.api.SPSSFactory;
import at.gv.egovernment.moa.spss.api.SignatureVerificationService;
import at.gv.egovernment.moa.spss.api.common.Content;
import at.gv.egovernment.moa.spss.api.common.SignerInfo;
import at.gv.egovernment.moa.spss.api.xmlverify.VerifySignatureInfo;
import at.gv.egovernment.moa.spss.api.xmlverify.VerifySignatureLocation;
import at.gv.egovernment.moa.spss.api.xmlverify.VerifyXMLSignatureRequest;
import at.gv.egovernment.moa.spss.api.xmlverify.VerifyXMLSignatureResponse;

import java.io.*;
import java.util.Base64;
import java.util.HashMap;

public class PvzdVerifySig {
    /** Prüfung einer XML-Signatur (XAdES) mittels MOA-SP
     * Weitere Dokumentation im MOA-SS/SP Handbuch und dem einthaltenen Beispielprogramm
     * https://joinup.ec.europa.eu/site/moa-idspss/moa-spss-2.0.0/doc/api-doc/index.html
     * **/

    private String sigDocFileName;

    public PvzdVerifySig(String moaspss_conf, String log4j_conf, String sig_doc) {
        /**
         * moaspss_conf: Pfad der Konfigurations-Datei von MOA SP/SS. Pfa ist absolut oder relativ
         * zum Arbeitsverzeichnis der Java VM.
         *
         * log4j_conf: Pfad der Konfigurations-Datei von Log4J
         *
         * sig_doc: Dateiname des zu prüfenden XML-Dokuments.
         */
        System.setProperty("moa.spss.server.configuration", moaspss_conf);
        System.setProperty("log4j.configuration", "file:" + log4j_conf);
        sigDocFileName = sig_doc;
    }

    public PvzdVerifySigResponse verify() {
        FileInputStream sigDocFIS = null;
        try {
            sigDocFIS = new FileInputStream(sigDocFileName);
        } catch (FileNotFoundException e1) {
            System.err.println("XML-Dokument mit zu pruefender Signatur nicht gefunden: " + sigDocFileName);
            System.exit(-1);
        }

        SPSSFactory spssFac = SPSSFactory.getInstance();
        SignatureVerificationService sigVerifyService = SignatureVerificationService.getInstance();
        Content sigDocContent = spssFac.createContent(sigDocFIS, null);

        // Position der zu pruefenden Signatur
        HashMap nSMap = new HashMap();
        nSMap.put("dsig", "http://www.w3.org/2000/09/xmldsig#");
        VerifySignatureLocation sigLocation = spssFac.createVerifySignatureLocation("//dsig:Signature", nSMap);

        // Pruefrequest zusammenstellen
        VerifySignatureInfo sigInfo = spssFac.createVerifySignatureInfo(sigDocContent, sigLocation);
        VerifyXMLSignatureRequest verifyRequest = spssFac.createVerifyXMLSignatureRequest(
                null,    // Verwende aktuelle Zeit als Pruefzeit
                sigInfo,
                null,    // Keine Ergaenzungsobjekte
                null,    // Signaturmanifest-Pruefung soll nicht durchgefuehrt werden
                true,   // Hash-Inputdaten, d.h. tatsaechlich signierte Daten werden nicht zurueckgeliefert
                "MOAIDBuergerkarteAuthentisierungsDaten");

        // suppress stdout to get rid of distracting IAIK lib startup licence messages
        File file = new File("/dev/null");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        PrintStream ps = new PrintStream(fos);
        System.setOut(ps);
        System.setErr(ps);

        // pruefen
        VerifyXMLSignatureResponse verifyResponse = null;

        try {
            verifyResponse = sigVerifyService.verifyXMLSignature(verifyRequest);
        } catch (MOAException e) {
            return new PvzdVerifySigResponse(e.getMessageId(), e.getMessage(), null, null);
        }
        SignerInfo signerInfo = verifyResponse.getSignerInfo();
        String signerCertificateEncoded = null;
        try {
            signerCertificateEncoded = Base64.getEncoder().encodeToString(signerInfo.getSignerCertificate().getEncoded());
        } catch (java.security.cert.CertificateEncodingException e) {
            return new PvzdVerifySigResponse("NOK", "NOK", "Certificate encoding error", null);
        }
        if (verifyResponse.getSignatureCheck().getCode() == 0 &&
                verifyResponse.getCertificateCheck().getCode() == 0) {
            return new PvzdVerifySigResponse("OK", "OK", signerCertificateEncoded,
                                             verifyResponse.getReferenceInputDatas());
        } else {
            if (verifyResponse.getSignatureCheck().getCode() != 0) {
                return new PvzdVerifySigResponse("NOK", "NOK: Signature Check returned code=" +
                                                 verifyResponse.getSignatureCheck().getCode(), null, null);
            } else {
                return new PvzdVerifySigResponse("NOK", "NOK: Certificate Check returned code=" +
                                                 verifyResponse.getCertificateCheck().getCode(), null, null);
            }
        }
    }
}
