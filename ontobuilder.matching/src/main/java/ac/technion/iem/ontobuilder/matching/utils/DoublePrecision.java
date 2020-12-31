package ac.technion.iem.ontobuilder.matching.utils;


/**
 * <p>Title: DoublePrecision</p>
 * <p>Description: Handles double's precision formatting</p>
 */
public final class DoublePrecision
{

    /**
     * Trim a double according to a required precision
     * 
     * @param d the double
     * @param p the required precision
     * @return the formatted double
     */
    public static double getDoubleP(double d, int p)
    {
        String string = Double.toString(d);
        int indexOfDot = string.indexOf(".");
        String precision = string.substring(indexOfDot + 1,
            ((p + indexOfDot + 1 < string.length()) ? (indexOfDot + p + 1) : string.length()));
        return Double.parseDouble(string.substring(0, indexOfDot + 1) + precision);
    }

    public static void main(String args[])
    {
        System.out.println("2454949.122455646464 -> " +
            DoublePrecision.getDoubleP(2454949.122455646464, 7));
    }
}
