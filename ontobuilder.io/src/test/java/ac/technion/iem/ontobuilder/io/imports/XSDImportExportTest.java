package ac.technion.iem.ontobuilder.io.imports;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.utils.files.XmlFileHandler;
import junit.framework.TestCase;

import java.io.*;


public class XSDImportExportTest extends TestCase {

    XmlFileHandler xfh;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        xfh= new XmlFileHandler(); // Initializes the singleton pattern members of OntoBuilder and this class
    }

    public void testImportExportXSDFile() throws IOException, ImportException {
        //Test Import
        String dirname = "Apertum";
        String fname = "Apertum.xsd";
        InputStream toLoad = getClass().getClassLoader().getResourceAsStream(fname);
        assertNotNull(toLoad);
        byte[] buffer = new byte[toLoad.available()];
        toLoad.read(buffer);
        String strTmp = System.getProperty("java.io.tmpdir");
        File testFile = new File(strTmp,fname);
        OutputStream outStream = new FileOutputStream(testFile);
        outStream.write(buffer);
        XSDImporterUsingXSOM importer = new XSDImporterUsingXSOM();
        Ontology o = importer.importFile(testFile);
        assertEquals(144,o.getAllTermsCount());

        // Test export
        File testExportFile = new File(strTmp,"exported.xsd");
        if (testExportFile.exists())
            testExportFile.delete();
        o.save(testExportFile);
        assertTrue(testExportFile.exists());

    }

}