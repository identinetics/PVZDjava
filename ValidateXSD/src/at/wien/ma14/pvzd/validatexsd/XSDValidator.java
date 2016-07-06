package at.wien.ma14.pvzd.validatexsd;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import static java.lang.System.out;
import static org.junit.Assert.assertTrue;

public class XSDValidator {
    private String [] xsdFiles = null;
    private final boolean _verbose;

    public XSDValidator(String schemaDir, boolean verbose) {
        final File samlMdSchemaDir = new File(schemaDir);
        _verbose = verbose;
        assertTrue("directory with SAML MD schema files not found: " + samlMdSchemaDir.getAbsolutePath(), samlMdSchemaDir.exists());
        FilenameFilter xsdFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.toLowerCase().endsWith(".xsd"));
            }
        };
        final String[] schemasFiles = samlMdSchemaDir.list(xsdFilter);
        xsdFiles = new String[schemasFiles.length];
        for (int i = 0; i < schemasFiles.length; i++) {
            xsdFiles[i] = samlMdSchemaDir + File.separator + schemasFiles[i];
        }
    }

    /**
     * Validate provided XML against the XSD schema files in a directory provided with the constructor.
     *
     * @param xmlFilePath    Path/name of XML file to be validated;
     * @return error message. If null then file is valid
     */
    public String validateSchema(final String xmlFilePath) {
        final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        final StreamSource[] xsdSources = generateStreamSourcesFromXsdPaths(xsdFiles);
        String retmsg = null;
        if (! new File(xmlFilePath).exists()) {
            retmsg = "File to be validated not found: " + xmlFilePath;
        } else {
            try {
                final Schema schema = schemaFactory.newSchema(xsdSources);
                final Validator validator = schema.newValidator();
                if (_verbose) out.println("Validating " + xmlFilePath + " against XSDs " + Arrays.toString(xsdFiles) + "...");
                validator.validate(new StreamSource(new File(xmlFilePath)));
                retmsg = "done.";
            } catch (IOException e) {
                if (_verbose) out.println("ERROR: Unable to validate " + xmlFilePath + "\n" + e);
                retmsg = "File to be validated not be opened: " + xmlFilePath;
            } catch (SAXException e) {
                retmsg = "ERROR: Validation of " + xmlFilePath + " failed\n" + e;
                if (_verbose && retmsg != null) out.println(retmsg);
            }
        }
        return retmsg;
    }

    /**
     * @param xsdFilesPaths String representations of paths/names XSD files.
     * @return array of StreamSource instances representing XSDs.
     */
    private StreamSource[] generateStreamSourcesFromXsdPaths(
            final String[] xsdFilesPaths) {
        return Arrays.stream(xsdFilesPaths)
                .map(StreamSource::new)
                .collect(Collectors.toList())
                .toArray(new StreamSource[xsdFilesPaths.length]);
    }
}

