package ac.technion.iem.ontobuilder.core.resources;

import java.io.File;

public class OntoBuilderResources
{
	
	public static class Config
	{
		
		private static final String CONFIG = "config";
		
		public static final String APPLICATION_PROPERTIES = CONFIG + File.separator + "application.properties";
		public static final String CONFIGURATION_XML = 		CONFIG + File.separator + "configuration.xml";
		public static final String RESOURCES_PROPERTIES = 	CONFIG + File.separator + "resources.properties";
		public static final String THESAURUS_XML = 			CONFIG + File.separator + "thesaurus.xml";
		
		public static class Matching
		{
			
			private static final String MATCHING = CONFIG + File.separator + "matching";
			
			public static final String ALGORITHMS_XML = MATCHING + File.separator + "algorithms.xml";
			
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
