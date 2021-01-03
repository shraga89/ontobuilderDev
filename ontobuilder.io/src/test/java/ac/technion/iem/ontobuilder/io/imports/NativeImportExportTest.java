package ac.technion.iem.ontobuilder.io.imports;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.utils.files.XmlFileHandler;
import ac.technion.iem.ontobuilder.io.exports.Exporter;
import ac.technion.iem.ontobuilder.io.exports.XmlOntologyExporter;
import junit.framework.TestCase;

import java.io.*;


public class NativeImportExportTest extends TestCase {

    XmlFileHandler xfh;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        xfh= new XmlFileHandler();
    }

    public void testImportExportNativeFile() throws IOException, ImportException {
        //Test Import
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

        // Test export
        File testExportFile = new File(strTmp,"exported.xml");
        if (testExportFile.exists())
            testExportFile.delete();
        o.save(testExportFile);
        assertTrue(testExportFile.exists());

    }

}