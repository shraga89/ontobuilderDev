package ac.technion.iem.ontobuilder.io.imports;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.utils.files.XmlFileHandler;
import junit.framework.TestCase;

import java.io.*;


public class NativeImporterTest extends TestCase {

    XmlFileHandler xfh;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        xfh= new XmlFileHandler();
    }

    public void testImportFile() throws IOException, ImportException {
        String dirname = "linesmaker.com.xml_www.youbet.com.xml_EXACT/";
        String fname = "linesmaker.com.xml";
        InputStream toLoad = getClass().getClassLoader().getResourceAsStream(dirname + fname);
        assertNotNull(toLoad);
        byte[] buffer = new byte[toLoad.available()];
        toLoad.read(buffer);
        String strTmp = System.getProperty("java.io.tmpdir");
        File testFile = new File(strTmp,fname);
        OutputStream outStream = new FileOutputStream(testFile);
        outStream.write(buffer);
        NativeImporter ni = new NativeImporter();
        Ontology o = ni.importFile(testFile);
        assertEquals(28,o.getAllTermsCount());
    }
}