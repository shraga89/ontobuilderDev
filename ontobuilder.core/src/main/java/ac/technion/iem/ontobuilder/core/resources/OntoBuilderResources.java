package ac.technion.iem.ontobuilder.core.resources;

import java.io.File;

public class OntoBuilderResources
{
	
	public static class Config
	{
		
		private static final String CONFIG = "config";
		
		public static final String APPLICATION_PROPERTIES = "application.properties";
		public static final String CONFIGURATION_XML = 		"configuration.xml";
		public static final String RESOURCES_PROPERTIES = 	"resources.properties";
		public static final String THESAURUS_XML = 			"thesaurus.xml";
		public static final String LOG4J_PROPERTIES = "log4j.properties";
		
		public static class Matching
		{

			public static final String ALGORITHMS_XML = "algorithms.xml";
			
		}
		
		public static class IO
		{
			
			private static final String IO = CONFIG + File.separator + "io";

			public static final String EXPORTERS_XML = IO + File.separator + "exporters.xml";			
			public static final String IMPORTERS_XML = IO + File.separator + "importers.xml";
			
		}
		
		public static class GUI
		{
			
			private static final String GUI = CONFIG + File.separator + "gui";

			public static final String BROWSER_HISTORY = GUI + File.separator + "browser.history";
			
		}
		
	}
	
}
