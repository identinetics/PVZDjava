package at.wien.ma14.pvzd.verifysigapi;

import at.gv.egovernment.moa.spss.MOAException;
import at.gv.egovernment.moa.spss.api.SPSSFactory;
import at.gv.egovernment.moa.spss.api.SignatureVerificationService;
import at.gv.egovernment.moa.spss.api.common.*;
import at.gv.egovernment.moa.spss.api.impl.ContentBinaryImpl;
import at.gv.egovernment.moa.spss.api.impl.InputDataBinaryImpl;
import at.gv.egovernment.moa.spss.api.xmlverify.VerifySignatureInfo;
import at.gv.egovernment.moa.spss.api.xmlverify.VerifySignatureLocation;
import at.gv.egovernment.moa.spss.api.xmlverify.VerifyXMLSignatureRequest;
import at.gv.egovernment.moa.spss.api.xmlverify.VerifyXMLSignatureResponse;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.NodeList;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

public class PvzdVerifySig {
    /** Prüfung einer XML-Signatur (XAdES)
        - verwendet die MOA-SP Bibliothek
        - Trustprofil: Bürgerkarte
        - erlaubt nur 1 dsig:Object mit binary/base64 content
        - Retourniert das validierte Signaturobjekt (See what is signed: https://www.w3.org/TR/xmldsig-core/#sec-Security
        - Retourniert das Siganturzertifikat (nur ein Signator ist erlaubt)
     
      Basiert auf Dokumentation und Beispielprogramm von https://joinup.ec.europa.eu/site/moa-idspss
     **/

    private String sigDocFileName;

    public PvzdVerifySig(String moaspss_conf, String log4j_conf, String sig_doc) {
        /**
         * moaspss_conf: Pfad der Konfigurations-Datei von MOA SP/SS. Pfad ist absolut oder relativ
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
                null,    // The date for which the verification is to be performed, null = current time
                sigInfo, // Information about the signature environment and the location of the signature
                null,    // Supplemental information for the signature environment
                null,    // Additional information for checking the signature manifest
                true,    // If true, hash input data will be returned in the response, otherwise not
                "MOAIDBuergerkarteAuthentisierungsDaten"); // The ID of the trust profile containing the trusted root certificates

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

        // call signature validation
        VerifyXMLSignatureResponse verifyResponse = null;
        try {
            verifyResponse = sigVerifyService.verifyXMLSignature(verifyRequest);
        } catch (MOAException e) {
            return new PvzdVerifySigResponse(e.getMessageId(), e.getMessage(), null, null);
        }

        // get signing certificate
        SignerInfo signerInfo = verifyResponse.getSignerInfo();
        String signerCertificateEncoded = null;
        try {
            signerCertificateEncoded = Base64.getEncoder().encodeToString(signerInfo.getSignerCertificate().getEncoded());
        } catch (java.security.cert.CertificateEncodingException e) {
            return new PvzdVerifySigResponse("NOK", "Certificate encoding error: " + e, null, null);
        }

        // return response
        /* List signedData = verifyResponse.getHashInputDatas();
        if (verifyResponse.getSignatureCheck().getCode() == 0 &&
                verifyResponse.getCertificateCheck().getCode() == 0) {
            String bytesAsString = new String(((ByteArrayInputStream)((ContentBinaryImpl)((InputDataBinaryImpl)signedData.get(0))).binaryContent).buf);
            return new PvzdVerifySigResponse("OK", "OK", signerCertificateEncoded, signedData);
        } else {
            if (verifyResponse.getSignatureCheck().getCode() != 0) {
                return new PvzdVerifySigResponse("NOK", "NOK: Signature Check returned code=" +
                                                 verifyResponse.getSignatureCheck().getCode(), null, null);
            } else {
                return new PvzdVerifySigResponse("NOK", "NOK: Certificate Check returned code=" +
                                                 verifyResponse.getCertificateCheck().getCode(), null, null);
            }
        }   */
        List signedData = verifyResponse.getHashInputDatas();
        List<String> signedData_str = new ArrayList<String>();
        if (signedData != null && !signedData.isEmpty()) {
            for (Object el : signedData) {
                InputData inputData = (InputData) el;
                switch (inputData.getContentType()) {
                    case Content.XML_CONTENT :
                        //ContentXML contentXml = (ContentXML) inputData;
                        //NodeList input_XML = contentXml.getXMLContent();
                        return new PvzdVerifySigResponse("NOK", "Error: XML content not allowed here", null, null);
                    case Content.BINARY_CONTENT :
                        ContentBinary contentBinary = (ContentBinary) inputData;
                        try {
                            String signed_elem_str = IOUtils.toString(contentBinary.getBinaryContent());
                            signedData_str.add(signed_elem_str);
                        } catch (java.io.IOException e) {
                            return new PvzdVerifySigResponse("NOK", "Error when decoding binary content: " + e, null, null);
                        } catch (Exception e) {
                            return new PvzdVerifySigResponse("NOK", "Error when decoding binary content: " + e, null, null);
                        }
                }
            }
        } else {
            return new PvzdVerifySigResponse("NOK", "NOK: Signature value is empty", null, null);
        }
        return new PvzdVerifySigResponse("OK", "OK", signerCertificateEncoded, signedData_str);
    }
}
       // TODO: ((ByteArrayInputStream)((ContentBinaryImpl)((InputDataBinaryImpl)signedData.get(0)).wrapped_).binaryContent).buf