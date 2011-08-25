package ac.technion.iem.ontobuilder.matching.utils;

import java.util.HashMap;

/**
 * <p>Title: PermanentCalculator</p>
 */
public class PermanentCalculator
{

    private int numElements;
    private double[][] matrix;
    private double sum;

    private class PermutationMap extends HashMap<Character, Integer>
    {
        private static final long serialVersionUID = -598979618862514559L;

        private char[] labels =
        {
            '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'W', 'U', 'V', 'X', 'Y',
            'Z'
        };

        private String seed;

        /**
         * Constructs a PermutationMap
         * 
         * @param size the size of map to create
         */
        public PermutationMap(int size)
        {
            if (size > 31 || size < 1)
                throw new IllegalArgumentException("illegal size");
            StringBuffer sb = new StringBuffer();
            for (int i = 1; i <= size; i++)
            {
                put(new Character(labels[i]), new Integer(i));
                sb.append(labels[i]);
            }
            seed = sb.toString();
        }

        /**
         * Get the label value (an integer)
         * 
         * @param label the label
         * @return the integer value
         */
        public int getLabelValue(char label)
        {
            return ((Integer) get(new Character(label))).intValue();
        }

        /**
         * Get the seed of the PermutationMap
         * 
         * @return the seed
         */
        public String getSeed()
        {
            return seed;
        }
    };

    private PermutationMap map;

    /**
     * Constructs a PermanentCalculator
     * 
     * @param numElements number of elements
     * @param matrix the matrix to make the calculation for
     */
    public PermanentCalculator(int numElements, double[][] matrix)
    {
        this.numElements = numElements;
        this.matrix = matrix;
        map = new PermutationMap(numElements);
    }

    public double getPermanentValue()
    {
        sum = 0;
        perm1(map.getSeed());
        return sum;
    }

    /**
     * Calculates the product of matrix value at place [i][value of prefix at place i]
     * 
     * @param prefix the prefix
     * @return the product
     */
    private double calcOneProd(String prefix)
    {
        double prod = 1;
        int index;
        for (int i = 0; i < numElements; i++)
        {
            index = map.getLabelValue(prefix.charAt(i));
            if (index == 0)
            {
                System.err.println("error:: charstr::" + prefix.charAt(i) + ", prefix::" + prefix +
                    ", i::" + i);
            }
            prod *= matrix[i][index - 1];
        }
        return prod;
    }

    /**
     * Print N! permutation of the characters of the string s (in order)
     * 
     * @param s the string
     */
    public void perm1(String s)
    {
        perm1("", s);
    }

    /**
     * Print N! permutation of the characters of the string s (in order)
     * 
     * @param s the string
     */
    private void perm1(String prefix, String s)
    {
        int N = s.length();
        if (N == 0)
            sum += calcOneProd(prefix);
        else
        {
            for (int i = 0; i < N; i++)
                perm1(prefix + s.charAt(i), s.substring(0, i) + s.substring(i + 1, N));
        }

    }

    public static void main(String[] args)
    {
        int N = 4;
        double[][] matrix =
        {
            {
                0, 1, 1, 0
            },
            {
                0, 0, 1, 1
            },
            {
                1, 1, 0, 1
            },
            {
                1, 0, 1, 0
            }
        };
        PermanentCalculator calc = new PermanentCalculator(N, matrix);
        System.out.println("Perm :: " + calc.getPermanentValue());
    }

}
