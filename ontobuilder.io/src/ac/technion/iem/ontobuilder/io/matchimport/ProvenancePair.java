package ac.technion.iem.ontobuilder.io.matchimport;

/**
 * Helper class to capture a provenance relation, used
 * in several importers.
 * 
 * @author Tomer Sagi
 * @author Matthias Weidlich
 *
 */
public class ProvenancePair {

		private String leftP;
		private String rightP;
		private double conf;

		/**
		 * @param leftP
		 * @param rightP
		 * @param conf
		 */
		public ProvenancePair(String leftP, String rightP, double conf) {
			this.setLeftP(leftP);
			this.setRightP(rightP);
			this.setConf(conf);
		}
		/**
		 * @return the leftP
		 */
		public String getLeftP() {
			return leftP;
		}
		/**
		 * @param leftP the leftP to set
		 */
		public void setLeftP(String leftP) {
			this.leftP = leftP;
		}
		/**
		 * @return the rightP
		 */
		public String getRightP() {
			return rightP;
		}
		/**
		 * @param rightP the rightP to set
		 */
		public void setRightP(String rightP) {
			this.rightP = rightP;
		}
		/**
		 * @return the conf
		 */
		public double getConf() {
			return conf;
		}
		/**
		 * @param conf the conf to set
		 */
		public void setConf(double conf) {
			this.conf = conf;
		}


}
