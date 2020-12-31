package ac.technion.iem.ontobuilder.core.utils;

/**
 * <p>Title: Soundex<p>
 * <p>Description: Encodes words using the soundex phonetic algorithm. The primary method to call is
 * Soundex.encode(String).
 * </p>
 * The main method encodes arguments to System.out.
 * 
 * @author Aaron Hansen
 */
public class Soundex
{

    // Public Fields
    // -------------

    /**
     * Handy for the Length property.
     */
    public static final transient int NO_MAX = -1;

    // Protected Fields
    // ----------------

    /**
     * If true, the final 's' of the word being encoded is dropped.
     */
    protected boolean DropFinalSBoolean = false;

    /**
     * Length of code to build.
     */
    protected int LengthInt = 4;

    /**
     * If true, codes are padded to the LengthInt with zeros.
     */
    protected boolean PadBoolean = true;

    /**
     * Soundex code table.
     */
    protected int[] SoundexInts = createArray();

    // Private Fields
    // --------------

    private static final String LowerS = "s";

    private static final String UpperS = "S";

    // Public Methods
    // --------------

    /**
     * Returns the soundex code for the specified word.
     * 
     * @param string The word to encode.
     */
    public String encode(String word)
    {
        word = word.trim();
        if (DropFinalSBoolean)
        {
            // we're not dropping double s as in guess
            if ((word.length() > 1) && (word.endsWith(UpperS) || word.endsWith(LowerS)))
                word = word.substring(0, (word.length() - 1));
        }
        word = reduce(word);
        int wordLength = word.length(); // original word size
        int sofar = 0; // how many codes have been created
        int max = LengthInt - 1; // max codes to create (less the first char)
        if (LengthInt < 0) // if NO_MAX
            max = wordLength; // wordLength was the max possible size.
        int code = 0;
        StringBuffer buf = new StringBuffer(max);
        buf.append(Character.toLowerCase(word.charAt(0)));
        for (int i = 1; (i < wordLength) && (sofar < max); i++)
        {
            code = getCode(word.charAt(i));
            if (code > 0)
            {
                buf.append(code);
                sofar++;
            }
        }
        if (PadBoolean && (LengthInt > 0))
        {
            for (; sofar < max; sofar++)
                buf.append('0');
        }
        return buf.toString();
    }

    /**
     * Returns the Soundex code for the specified character.
     * 
     * @param ch Should be between A-Z or a-z
     * @return -1 if the character has no phonetic code.
     */
    public int getCode(char ch)
    {
        int arrayidx = -1;
        if (('a' <= ch) && (ch <= 'z'))
            arrayidx = ch - 'a';
        else if (('A' <= ch) && (ch <= 'Z'))
            arrayidx = ch - 'A';
        if ((arrayidx >= 0) && (arrayidx < SoundexInts.length))
            return SoundexInts[arrayidx];
        else
            return -1;
    }

    /**
     * If true, a final char of 's' or 'S' of the word being encoded will be dropped. By dropping
     * the last s, lady and ladies for example, will encode the same. False by default.
     */
    public boolean getDropFinalS()
    {
        return DropFinalSBoolean;
    }

    /**
     * The length of code strings to build, 4 by default. If negative, length is unlimited.
     * 
     * @see #NO_MAX
     */
    public int getLength()
    {
        return LengthInt;
    }

    /**
     * If true, appends zeros to a soundex code if the code is less than Soundex.getLength(). True
     * by default.
     */
    public boolean getPad()
    {
        return PadBoolean;
    }

    /**
     * Displays the codes for the parameters.
     */
    public static void main(String[] strings)
    {
        if ((strings == null) || (strings.length == 0))
        {
            System.out.println("Displays a soundex code for each argument.");
            System.exit(0);
        }
        Soundex sndx = new Soundex();
        for (int i = 0; i < strings.length; i++)
            System.out.println(sndx.encode(strings[i]));
    }

    /**
     * Allows you to modify the default code table
     * 
     * @param ch The character to specify the code for.
     * @param code The code to represent ch with, must be -1, or 1 thru 9
     */
    public void setCode(char ch, int code)
    {
        int arrayidx = -1;
        if (('a' <= ch) || (ch <= 'z'))
            arrayidx = ch - 'a';
        else if (('A' <= ch) || (ch <= 'Z'))
            arrayidx = ch - 'A';
        if ((0 <= arrayidx) && (arrayidx < SoundexInts.length))
            SoundexInts[arrayidx] = code;
    }

    /**
     * If true, a final char of 's' or 'S' of the word being encoded will be dropped.
     */
    public void setDropFinalS(boolean bool)
    {
        DropFinalSBoolean = bool;
    }

    /**
     * Sets the length of code strings to build. 4 by default.
     * 
     * @param Length of code to produce, must be &gt;= 1
     */
    public void setLength(int length)
    {
        LengthInt = length;
    }

    /**
     * If true, appends zeros to a soundex code if the code is less than Soundex.getLength(). True
     * by default.
     */
    public void setPad(boolean bool)
    {
        PadBoolean = bool;
    }

    // Protected Methods
    // -----------------

    /**
     * Creates the Soundex code table.
     */
    protected static int[] createArray()
    {
        return new int[]
        {
            -1, // a
            1, // b
            2, // c
            3, // d
            -1, // e
            1, // f
            2, // g
            -1, // h
            -1, // i
            2, // j
            2, // k
            4, // l
            5, // m
            5, // n
            -1, // o
            1, // p
            2, // q
            6, // r
            2, // s
            3, // t
            -1, // u
            1, // v
            -1, // w
            2, // x
            -1, // y
            2
        // z
        };
    }

    /**
     * Removes adjacent sounds.
     */
    protected String reduce(String word)
    {
        int len = word.length();
        StringBuffer buf = new StringBuffer(len);
        char ch = word.charAt(0);
        int currentCode = getCode(ch);
        buf.append(ch);
        int lastCode = currentCode;
        for (int i = 1; i < len; i++)
        {
            ch = word.charAt(i);
            currentCode = getCode(ch);
            if ((currentCode != lastCode) && (currentCode >= 0))
            {
                buf.append(ch);
            }
            lastCode = currentCode;
        }
        return buf.toString();
    }

}// Soundex
