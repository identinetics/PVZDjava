package at.wien.ma14.pvzd.verifysigapitest;

import at.wien.ma14.pvzd.verifysigapi.PvzdVerifySig;
import at.wien.ma14.pvzd.verifysigapi.PvzdVerifySigResponse;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;



/**
 * Created by r2h2 on 10.08.15.
 */
public class PvzdVerifySigTest {
    final File projdir = new File(System.getProperty("user.dir"));
    final File moddir = new File(projdir, "VerifySigAPI");
    //final File parentdir = new File(projdir.getParent());
    final File log4jprop = new File(projdir, "conf/log4j_info.properties");
    final File moaspprop = new File(projdir, "conf/moa-spss/MOASPSSConfiguration.xml");

    @Test
    public void testVerifyGood() throws Exception {
        final File xmlFileOK = new File(moddir, "tests/testdata/idp5_valid.xml_sig.xml");
        assert xmlFileOK.exists() : "not found: " + xmlFileOK.getAbsolutePath();
        final File signerCert = new File(moddir, "tests/testdata/r2h2_ecard_qcert.b64");
        System.out.println("Projdir: " + projdir);
        System.out.println("Moddir: " + moddir);

        System.out.println("Checking if signature is OK and compare extracted signer certificate with reference copy");
        PvzdVerifySig verifier  = new PvzdVerifySig(moaspprop.getAbsolutePath(),
                                                    log4jprop.getAbsolutePath(),
                                                    xmlFileOK.getAbsolutePath());
        PvzdVerifySigResponse response  = verifier.verify();
        assertEquals("Expected OK for messageID", "OK", response.pvzdCode);
        String cert_b64 = new String(Files.readAllBytes(Paths.get(signerCert.getAbsolutePath())));
        assertEquals("Certificate mismatch", cert_b64, response.signerCertificateEncoded);
    }

    /*@Test
    public void testVerifySignatureValueBroken() throws Exception {
        final File sigXmlFileNOK = new File("testdata/idp5_invalid.xml_sig.xml");
        assert sigXmlFileNOK.exists() : "not found: " + sigXmlFileNOK.getAbsolutePath();

        System.out.println("Checking if broken signature is detected");
        PvzdVerifySig verifier  = new PvzdVerifySig(moaspprop.getAbsolutePath(),
                                                    log4jprop.getAbsolutePath(),
                                                    sigXmlFileNOK.getAbsolutePath());
        PvzdVerifySigResponse response  = verifier.verify();
        assertEquals("Expected 2203 (NOK )for messageID", "2203", response.pvzdCode);   *//* message codes undocumented *//*
    }*/
}