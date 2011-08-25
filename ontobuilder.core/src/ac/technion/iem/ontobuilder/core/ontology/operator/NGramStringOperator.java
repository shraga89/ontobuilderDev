package ac.technion.iem.ontobuilder.core.ontology.operator;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * <p>Title: NGramStringOperator</p>
 * Implements {@link StringOperator}
 */
public final class NGramStringOperator
{

    private class Gram
    {

        String gram;

        int occurences;

        String str;

        public Gram(String inGram, String inString)
        {
            gram = inGram;
            str = inString;
            occurences = 0;
            int ng = gram.length();
            int len = str.length();
            for (int i = 0; i <= (len - ng); i++)
            {
                String gr = str.substring(i, i + ng);
                if (gram.equalsIgnoreCase(gr))
                {
                    occurences++;
                }
            }
        }

    }

    public double compare(String str1, String str2, int ng)
    {

        String str = " _";
        char blank = str.charAt(0); // replacing spaces with underlines
        char underline = str.charAt(1);
        str1 = str1.replace(blank, underline);
        str2 = str2.replace(blank, underline);

        Hashtable<String, Gram> gramList1 = new Hashtable<String, Gram>();
        int len1 = str1.length();
        for (int i = 0; i <= (len1 - ng); i++)
        { // builing the substrings for
          // str1
            String gr = str1.substring(i, i + ng);
            Gram gram = this.new Gram(gr, str1);
            if (gram.occurences > 0)
            {
                gramList1.put(gram.gram, gram); // Hashtable with the substrings
                                                // and number of occurences for
                                                // each one
            }

        }

        int repeat1 = 0;
        int cnt1 = 0;
        Enumeration<String> keys1 = gramList1.keys();
        while (keys1.hasMoreElements())
        {
            String key = (String) keys1.nextElement();
            Gram gr1 = (Gram) gramList1.get(key);
            repeat1 += gr1.occurences;
            cnt1++;
        }
        repeat1 -= cnt1;

        Hashtable<String, Gram> gramList2 = new Hashtable<String, Gram>();
        int len2 = str2.length();
        for (int i = 0; i <= (len2 - ng); i++)
        { // builing the substrings for
          // str2
            String gr = str2.substring(i, i + ng);
            Gram gram = new Gram(gr, str2);
            if (gram.occurences > 0)
            {
                gramList2.put(gram.gram, gram); // Hashtable with the substrings
                                                // and number of occurences for
                                                // each one
            }
        }

        int repeat2 = 0;
        int cnt2 = 0;
        Enumeration<String> keys2 = gramList2.keys();
        while (keys2.hasMoreElements())
        {
            String key = (String) keys2.nextElement();
            Gram gr2 = (Gram) gramList2.get(key);
            repeat2 += gr2.occurences;
            cnt2++;
        }
        repeat2 -= cnt2;

        int count = 0;

        keys2 = gramList2.keys();
        while (keys2.hasMoreElements())
        {
            String key = (String) keys2.nextElement();
            if (gramList1.containsKey(key))
            {
                count++;
            }
        }

        int denominator = Math.max(len1, len2);
        if (denominator == len1)
        {
            denominator = (denominator - ng - repeat1 + 1);
        }
        else
        {
            denominator = (denominator - ng - repeat2 + 1);
        }
        if (denominator == 0)
        {
            return 0;
        }
        else
        {
            return (double) count / (double) denominator;
        }

    }

    // this method is only to test your implementation:
    public static void main(String[] args)
    {
        // String str1 = args[0];
        // String str2 = args[1];
        // String n = Integer.parseInt(args[2]);
        String str1 = "Test";
        String str2 = "Teest";
        int n = 2;
        double result = new NGramStringOperator().compare(str1, str2, n);
        System.out.println("The outcome is:" + result);
    }

}
