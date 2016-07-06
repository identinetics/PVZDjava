package at.wien.ma14.pvzd.verifysigapi.cli;

import at.wien.ma14.pvzd.verifysigapi.PvzdVerifySig;
import at.wien.ma14.pvzd.verifysigapi.PvzdVerifySigResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLClassLoader;

import static org.junit.Assert.assertEquals;
import org.jhades.JHades;


public class VerifySigCLI {
    final File projdir = new File(System.getProperty("user.dir"));
    final File log4jprop = new File(projdir, "conf/log4j_info.properties");
    final File moaspprop = new File(projdir, "conf/moa-spss/MOASPSSConfiguration.xml");

    public void testVerifyGood(String xmldoc) throws Exception {
        File xmlFileOK = new File(xmldoc);
        assert xmlFileOK.exists() : "not found: " + xmlFileOK.getAbsolutePath();
        if (! log4jprop.exists()) {
            System.out.println(" + );
            throw new FileNotFoundException(" need to start from PROJ_HOME (not found: " + xmlFileOK.getAbsolutePath() + ")");
        }
        PvzdVerifySig verifier  = new PvzdVerifySig(moaspprop.getAbsolutePath(),
                log4jprop.getAbsolutePath(),
                xmlFileOK.getAbsolutePath());
        PvzdVerifySigResponse response  = verifier.verify();
        assertEquals("Expected OK for messageID", "OK", response.pvzdCode);
        System.out.println("Signer certificate:" + response.signerCertificateEncoded);
        System.out.println("Signed contents:");
        for (String s : response.referencedata) {
            System.out.println(s);
        }
    }

    public static void main(String[] argv) throws Exception {
        System.out.println("VerifySigCLI");
        VerifySigCLI verifySigCLI = new VerifySigCLI();
        if (argv.length == 1) {
            System.out.println("Verifying signature");
            verifySigCLI.testVerifyGood(argv[0]);
        } else {
            System.out.println("Usage: verifySigCLI <file-to-be-verified>");
            System.out.println("Classpath by courtesy of Jhades");
            new JHades().printClasspath();
            ClassLoader cl = ClassLoader.getSystemClassLoader();
            System.out.println("Files loaded by SystemClassLoader");
            URL[] urls = ((URLClassLoader)cl).getURLs();
            for(URL url: urls){
                System.out.println(url.getFile());
            }
        }
    }

}