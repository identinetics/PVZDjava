package at.wien.ma14.pvzd.validatexsd.cli;


import at.wien.ma14.pvzd.validatexsd.XSDValidator;
import java.net.URL;
import java.net.URLClassLoader;

public class XSDValidatorCLI {

    public void testVerifyGood(String xmldoc, String samlxsdDir) throws Exception {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        URL[] urls = ((URLClassLoader)cl).getURLs();
        for(URL url: urls){
            System.out.println(url.getFile());
        }

        //String cwd_parent = (new File(new File(".").getAbsolutePath())).getPare0nt();
        XSDValidator validator = new XSDValidator(samlxsdDir, true);
        validator.validateSchema(xmldoc);
    }

    public static void main(String[] argv) throws Exception {
        System.out.println("XSDValidatorCLI (" + argv.length + ")");
        XSDValidatorCLI r = new XSDValidatorCLI();
        String default_xsd_dir = "/Users/admin/devl/java/rhoerbe/PVZDjava/ValidateXSD/SAML_MD_Schema";
        if (argv.length == 1) {
            System.out.println("Validating " + argv[0] + "against xsd in " + default_xsd_dir);
            r.testVerifyGood(argv[0], default_xsd_dir);
        } else if (argv.length == 2) {
            System.out.println("Validating " + argv[0] + argv[1]);
            r.testVerifyGood(argv[0], argv[1]);
        } else {
            System.out.println("wrong number of arguments.\n Usage: XSDValidatorCLI <file-to-be-validated> [schema-dir]");
        }
    }

}