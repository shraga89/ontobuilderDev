package ac.technion.iem.ontobuilder.io.matchimport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * Importer for proprietary match format used by CRF. 
 * 
 * @author Tomer Sagi
 * @author Matthias Weidlich

 */
public class CRFMatchImporter implements MatchImporter {

	public class CRFMatchImporterHandler extends DefaultHandler {
		
		private List<ProvenancePair> pairs = new ArrayList<ProvenancePair>();
		
		private boolean matchOpen = false;
		private boolean sourceElementOpen = false;
		private boolean targetElementOpen = false;
		private boolean similarityOpen = false;
		private boolean sourceNameOpen = false;
		private boolean targetNameOpen = false;

		private ProvenancePair currentPair = null;
		
		@Override
		public void startElement(String uri, String localName,String qName, 
                Attributes attributes) throws SAXException {
 
			super.startElement(uri, localName, qName, attributes);

			if (sourceElementOpen) {
				if (qName.equalsIgnoreCase("NAME")) 
					sourceNameOpen = true;
			}
			else if (targetElementOpen) {
				if (qName.equalsIgnoreCase("NAME")) 
					targetNameOpen = true;
			}
			else if (matchOpen) {
				if (qName.equalsIgnoreCase("SOURCE_ELEMENT")) 
					sourceElementOpen = true;
				else if (qName.equalsIgnoreCase("TARGET_ELEMENT")) 
					targetElementOpen = true;
				else if (qName.equalsIgnoreCase("SIMILARITY")) 
					similarityOpen = true;
			}
			else if	(qName.equalsIgnoreCase("MATCH")) {
				this.currentPair = new ProvenancePair("", "", 0);
				matchOpen = true;
			}
		}	

		@Override
		public void endElement(String uri, String localName,
				String qName) throws SAXException {
			super.endElement(uri, localName, qName);

			if	(qName.equalsIgnoreCase("MATCH")) {
				this.pairs.add(this.currentPair);
				matchOpen = false;
			}
		}
		
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			super.characters(ch, start, length);

			if (sourceNameOpen) {
				char[] text = new char[length];
				System.arraycopy(ch, start, text, 0, length);
				this.currentPair.setLeftP(new String(text));
				sourceNameOpen = false;
				sourceElementOpen = false;
			}				
			else if (targetNameOpen) {
				char[] text = new char[length];
				System.arraycopy(ch, start, text, 0, length);
				this.currentPair.setRightP(new String(text));
				targetNameOpen = false;
				targetElementOpen = false;
			}		
			else if (similarityOpen) {
				char[] text = new char[length];
				System.arraycopy(ch, start, text, 0, length);
				this.currentPair.setConf(Double.valueOf(new String(text)));
				similarityOpen = false;
			}
		}
		
		public List<ProvenancePair> getPairs() {
			return this.pairs;
		}
	}
	
	/* (non-Javadoc)
	 * @see ac.technion.iem.ontobuilder.io.matchimport.MatchImporter#importMatch(java.io.File)
	 */
	@Override
	public MatchInformation importMatch(MatchInformation mi, File file) {
		try {
			MatchInformation res = new MatchInformation(mi.getCandidateOntology(),mi.getTargetOntology());
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			CRFMatchImporterHandler handler = new CRFMatchImporterHandler();
			saxParser.parse(file, handler);
			
			List<ProvenancePair> list = handler.getPairs();
			for (ProvenancePair p : list)
			{
				Term c = mi.getCandidateOntology().getTermByProvenance(p.getLeftP(),'/');
				if (c==null)
					{System.err.println("Attribute with provenenance:" + p.getLeftP() + "not found in " + mi.getCandidateOntology().getName());
					continue;}
				Term t = mi.getTargetOntology().getTermByProvenance(p.getRightP(),'/');
				if (t==null)
				{
					System.err.println("Attribute with provenenance:" + p.getRightP() + "not found in " + mi.getTargetOntology().getName());
					continue;
				}
				res.updateMatch(t,c,p.getConf());
			}

			return res;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


}
