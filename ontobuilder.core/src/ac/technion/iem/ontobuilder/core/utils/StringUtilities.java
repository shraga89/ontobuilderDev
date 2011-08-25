package ac.technion.iem.ontobuilder.core.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import ac.technion.iem.ontobuilder.core.ontology.operator.NGramStringOperator;
import ac.technion.iem.ontobuilder.core.thesaurus.Thesaurus;

/**
 * <p>Title: StringUtilities</p>
 * <p>Description: Internal string utilities</p>
 */
public class StringUtilities
{
    public final static double THESAURUS_PENALTY = 0.00001;
    public final static double SOUNDEX_PENALTY = 0.00002;

    private static boolean thesaurusUsed = false;
    private static boolean soundexUsed = false;

    private static NGramStringOperator nGramOpr = new NGramStringOperator();

    public static boolean hasLetterOrDigit(String text)
    {
        if (text == null)
            return false;
        char characters[] = text.toCharArray();
        for (int i = 0; i < characters.length; i++)
        {
            if (Character.isLetterOrDigit(characters[i]))
                return true;
        }
        return false;
    }

    final static String REPLACEMENT_STRING = "%s";

    public static String getReplacedString(String text, String[] replacements)
    {
        StringBuffer result = new StringBuffer("");
        int replacement = 0;
        int prevIndex = 0;
        int index = text.indexOf(REPLACEMENT_STRING);
        while (index != -1)
        {
            result.append(text.substring(prevIndex, index));
            result.append(replacements[replacement++]);
            prevIndex = index + REPLACEMENT_STRING.length();
            index = text.indexOf(REPLACEMENT_STRING, prevIndex);
        }
        result.append(text.substring(prevIndex));
        return result.toString();
    }

    /**
     * Replaces all occurrences of s1 in s for s2
     */
    public static String replace(String s, String s1, String s2)
    {
        if (s == null)
            return null;
        int previndex = 0;
        int index = s.indexOf(s1);
        while (index != -1)
        {
            String prev = s.substring(previndex, index);
            String next = s.substring(index + s1.length(), s.length());
            s = prev + s2 + next;
            previndex = index;
            index = s.indexOf(s1, previndex);
        }
        return s;
    }

    public static String removeIgnorableCharacters(String text)
    {
        if (text == null)
            return null;
        ArrayList<String> words = breakTextIntoWords(text);
        ArrayList<String> result = new ArrayList<String>();
        for (Iterator<String> i = words.iterator(); i.hasNext();)
        {
            String word = i.next();
            StringBuffer resultWord = new StringBuffer("");
            int length = word.length();
            for (int j = 0; j < length; j++)
            {
                char c = word.charAt(j);
                if (Character.isJavaIdentifierPart(c) || c == '-')
                    resultWord.append(c);
            }
            result.add(resultWord.toString());
        }
        return buildTextFromWords(result);
    }

    public static String removeHyphen(String text, boolean merge)
    {
        if (merge)
        {
            StringBuffer result = new StringBuffer("");
            StringTokenizer st = new StringTokenizer(text, "-");
            while (st.hasMoreTokens())
                result.append(st.nextToken());
            return result.toString();
        }
        else
            return text.replace('-', ' ');
    }

    public static String removeWords(String text, ArrayList<String> words)
    {
        ArrayList<String> textWords = breakTextIntoWords(text);
        ArrayList<String> result = new ArrayList<String>();
        for (Iterator<String> i = textWords.iterator(); i.hasNext();)
        {
            String word = (String) i.next();
            if (!words.contains(word))
                result.add(word);
        }
        return buildTextFromWords(result);
    }

    public static ArrayList<String> breakTextIntoWords(String text)
    {
        return breakTextIntoWords(text, " \t\n\r\f\\/");
    }

    public static ArrayList<String> breakTextIntoWords(String text, String separators)
    {
        ArrayList<String> words = new ArrayList<String>();
        if (text == null || text.length() == 0)
            return words;
        StringTokenizer st = new StringTokenizer(text, separators);
        while (st.hasMoreTokens())
            words.add(st.nextToken());
        return words;
    }

    public static String buildTextFromWords(ArrayList<String> words)
    {
        StringBuffer textBuffer = new StringBuffer("");
        for (Iterator<String> i = words.iterator(); i.hasNext();)
        {
            String word = (String) i.next();
            if (!word.equals(""))
                textBuffer.append(word).append(" ");
        }
        return textBuffer.toString().trim();
    }

    public static String normalizeSpaces(String text)
    {
        return buildTextFromWords(breakTextIntoWords(text));
    }

    public static String normalizeSpaces(String text, String separator)
    {
        return buildTextFromWords(breakTextIntoWords(text, separator));
    }

    public static double getSubstringEffectivity(String s1, String s2)
    {
        return getSubstringEffectivity(s1, s2, null);
    }

    public static double getNGramEffectivity(String s1, String s2, int ng)
    {
        return nGramOpr.compare(s1, s2, ng);
    }

    public static double getSubstringEffectivity(String s1, String s2, Thesaurus thesaurus)
    {
        return getSubstringEffectivity(s1, s2, thesaurus, false);
    }

    public static double getSubstringEffectivity(String s1, String s2, boolean useSoundex)
    {
        return getSubstringEffectivity(s1, s2, null, useSoundex);
    }

    public static double getSubstringEffectivity(String s1, String s2, Thesaurus thesaurus,
        boolean useSoundex)
    {
        if (s1.equals(s2))
            return 1;
        ArrayList<String> words1 = breakTextIntoWords(s1);
        ArrayList<String> words2 = breakTextIntoWords(s2);

        thesaurusUsed = false;
        soundexUsed = false;

        if (words2.size() == 0)
            return 0;
        int totalSubstrings = 0;
        for (Iterator<String> i = words2.iterator(); i.hasNext();)
        {
            String word = (String) i.next();
            if (s1.indexOf(word) != -1)
                totalSubstrings++;
            else if (thesaurus != null || useSoundex)
            {
                for (Iterator<String> j = words1.iterator(); j.hasNext();)
                {
                    String word1 = (String) j.next();
                    if ((thesaurus != null && thesaurus.isSynonym(word, word1)))
                    {
                        thesaurusUsed = true;
                        totalSubstrings++;
                        break;
                    }
                    else if (useSoundex && getSoundexEffectivity(word, word1) >= 0.75)
                    {
                        if (thesaurus != null && thesaurus.isHomonym(word, word1))
                            continue;
                        soundexUsed = true;
                        totalSubstrings++;
                        break;
                    }
                }
            }
        }
        double effectiveness = totalSubstrings / (double) words2.size(); // *ratio;
        if (thesaurusUsed)
            effectiveness -= THESAURUS_PENALTY;
        if (soundexUsed)
            effectiveness -= SOUNDEX_PENALTY;
        return effectiveness;
    }

    public static void addWordToSet(List<String> set, Thesaurus thesaurus, boolean useSoundex,
        String word)
    {
        // check that is not contained already
        if (set.contains(word))
            return;
        if (thesaurus != null || useSoundex) // check if a synonym is already in the set or there is
                                             // a word that sounds the same, in which case the word
                                             // is not added
        {
            boolean add = true;
            for (Iterator<String> i = set.iterator(); i.hasNext();)
            {
                String setWord = (String) i.next();
                if ((thesaurus != null && thesaurus.isSynonym(word, setWord)) ||
                    (useSoundex && getSoundexEffectivity(word, setWord) >= .75))
                {
                    add = false;
                    break;
                }
            }
            if (add)
                set.add(word);
        }
        else
            set.add(word);
    }

    public static boolean isWordInSet(List<String> set, Thesaurus thesaurus, boolean useSoundex,
        String word)
    {
        // check that is not contained already
        if (set.contains(word))
            return true;
        if (thesaurus != null || useSoundex) // check if a synonym is already in the set or there is
                                             // a word that sounds the same, in which case the word
                                             // is not added
        {
            for (Iterator<String> i = set.iterator(); i.hasNext();)
            {
                String setWord = (String) i.next();
                if ((thesaurus != null && thesaurus.isSynonym(word, setWord)))
                {
                    thesaurusUsed = true;
                    return true;
                }
                else if ((useSoundex && getSoundexEffectivity(word, setWord) >= .75))
                {
                    if (thesaurus != null && thesaurus.isHomonym(word, setWord))
                        continue;
                    soundexUsed = true;
                    return true;
                }
            }
        }
        return false;
    }

    public static double getSymmetricSubstringEffectivity(String s1, String s2)
    {
        return getSymmetricSubstringEffectivity(s1, s2, null, false);
    }

    public static double getSymmetricSubstringEffectivity(String s1, String s2, Thesaurus thesaurus)
    {
        return getSymmetricSubstringEffectivity(s1, s2, thesaurus, false);
    }

    public static double getSymmetricSubstringEffectivity(String s1, String s2, boolean useSoundex)
    {
        return getSymmetricSubstringEffectivity(s1, s2, null, true);
    }

    public static double getSymmetricSubstringEffectivity(String s1, String s2,
        Thesaurus thesaurus, boolean useSoundex)
    {
        if (s1.equals(s2))
            return 1;
        ArrayList<String> words1 = breakTextIntoWords(s1);
        ArrayList<String> words2 = breakTextIntoWords(s2);

        thesaurusUsed = false;
        soundexUsed = false;

        ArrayList<String> intersection = new ArrayList<String>();
        ArrayList<String> union = new ArrayList<String>();

        for (Iterator<String> i = words1.iterator(); i.hasNext();)
        {
            String word1 = (String) i.next();
            addWordToSet(union, thesaurus, useSoundex, word1);
        }

        int unionSize = union.size();
        for (Iterator<String> i = words2.iterator(); i.hasNext();)
        {
            String word2 = (String) i.next();
            if (isWordInSet(union.subList(0, unionSize), thesaurus, useSoundex, word2))
                addWordToSet(intersection, thesaurus, useSoundex, word2);
            else
                addWordToSet(union, thesaurus, useSoundex, word2);
        }

        double effectiveness = (double) intersection.size() / (double) union.size();
        if (thesaurusUsed)
            effectiveness -= THESAURUS_PENALTY;
        if (soundexUsed)
            effectiveness -= SOUNDEX_PENALTY;
        return effectiveness;
    }

    public static double getPartSubstringEffectivity(String s1, String s2, int characters)
    {
        if (s1.equals(s2))
            return 1;
        if (s2.length() < characters)
            return 0;
        int possibleSubstrings = s2.length() - characters + 1;
        int match = 0;
        for (int i = 0; i < possibleSubstrings; i++)
        {
            String partialString = s2.substring(i, i + characters);
            if (s1.indexOf(partialString) != -1)
                match++;
        }
        return match / (double) possibleSubstrings;
    }

    public static double getSymmetricPartSubstringEffectivity(String s1, String s2, int characters)
    {
        if (s1.equals(s2))
            return 1;
        if (s1.length() < characters || s2.length() < characters)
            return 0;

        ArrayList<String> intersection = new ArrayList<String>();
        ArrayList<String> union = new ArrayList<String>();

        int possibleSubstrings = s1.length() - characters + 1;
        for (int i = 0; i < possibleSubstrings; i++)
        {
            String part = s1.substring(i, i + characters);
            if (!union.contains(part))
                union.add(part);
        }

        possibleSubstrings = s2.length() - characters + 1;
        for (int i = 0; i < possibleSubstrings; i++)
        {
            String part = s2.substring(i, i + characters);
            if (union.contains(part))
            {
                if (!intersection.contains(part))
                    intersection.add(part);
            }
            else
                union.add(part);
        }

        return (double) intersection.size() / (double) union.size();
    }

    public static double getSoundexEffectivity(String s1, String s2)
    {
        if (s1 == null || s2 == null || s1.length() == 0 || s2.length() == 0)
            return 0;
        Soundex sdx = new Soundex();
        String s1sdx = sdx.encode(s1);
        String s2sdx = sdx.encode(s2);
        if (s1sdx.charAt(0) != s2sdx.charAt(0))
            return 0;
        int chars = s2sdx.length();
        int diff = 0;
        for (int i = 0; i < chars; i++)
            if (s1sdx.charAt(i) != s2sdx.charAt(i))
                diff++;
        return (chars - diff) / (double) chars;
    }

    public static String buildString(char c, int length)
    {
        StringBuffer bf = new StringBuffer("");
        for (int i = 0; i < length; i++)
            bf.append(c);
        return bf.toString();
    }

//    public static String getJTableStringRepresentation(JTable table)
//    {
//        StringBuffer bf = new StringBuffer("");
//
//        TableModel model = table.getModel();
//        ArrayList<Integer> columnsWidths = new ArrayList<Integer>();
//        for (int i = 0; i < model.getColumnCount(); i++)
//        {
//            int width = model.getColumnName(i).length();
//            for (int j = 0; j < model.getRowCount(); j++)
//            {
//                Object o = model.getValueAt(j, i);
//                if (o == null)
//                    continue;
//                width = Math.max(width, o.toString().length());
//            }
//            columnsWidths.add(new Integer(width));
//        }
//        for (int i = 0; i < model.getColumnCount(); i++)
//        {
//            String columnName = model.getColumnName(i);
//            int width = ((Integer) columnsWidths.get(i)).intValue();
//            bf.append(columnName).append(buildString(' ', width - columnName.length() + 1));
//        }
//        bf.append("\n");
//        for (int i = 0; i < columnsWidths.size(); i++)
//        {
//            int width = ((Integer) columnsWidths.get(i)).intValue();
//            bf.append(buildString('-', width)).append(" ");
//        }
//        bf.append("\n");
//
//        for (int i = 0; i < model.getRowCount(); i++)
//        {
//            for (int j = 0; j < model.getColumnCount(); j++)
//            {
//                int width = ((Integer) columnsWidths.get(j)).intValue();
//                Object o = model.getValueAt(i, j);
//                if (o == null)
//                    continue;
//                String cell = o.toString();
//                bf.append(cell).append(buildString(' ', width - cell.length() + 1));
//            }
//            bf.append("\n");
//        }
//
//        return bf.toString();
//    }
    
    public static String getTableStringRepresentation(String[] columnNames, int propsCnt, Object data[][])
    {
        StringBuffer bf = new StringBuffer("");

        ArrayList<Integer> columnsWidths = new ArrayList<Integer>();
        for (int i = 0; i < columnNames.length; i++)
        {
            int width = columnNames[i].length();
            for (int j = 0; j < data.length; j++)
            {
                Object o = data[j][i];
                if (o == null)
                    continue;
                width = Math.max(width, o.toString().length());
            }
            columnsWidths.add(new Integer(width));
        }
        for (int i = 0; i < columnNames.length; i++)
        {
            String columnName = columnNames[i];
            int width = ((Integer) columnsWidths.get(i)).intValue();
            bf.append(columnName).append(buildString(' ', width - columnName.length() + 1));
        }
        bf.append("\n");
        for (int i = 0; i < columnsWidths.size(); i++)
        {
            int width = ((Integer) columnsWidths.get(i)).intValue();
            bf.append(buildString('-', width)).append(" ");
        }
        bf.append("\n");

        for (int i = 0; i < data.length; i++)
        {
            for (int j = 0; j < columnNames.length; j++)
            {
                int width = ((Integer) columnsWidths.get(j)).intValue();
                Object o = data[i][j];
                if (o == null)
                    continue;
                String cell = o.toString();
                bf.append(cell).append(buildString(' ', width - cell.length() + 1));
            }
            bf.append("\n");
        }

        return bf.toString();
    }

    static public boolean isUpperCase(String text)
    {
        for (int i = 0; i < text.length(); i++)
            if (!Character.isUpperCase(text.charAt(i)))
                return false;
        return true;
    }

    static public String trimWhitespace(String text)
    {
        int firstIndex = 0;
        int lastIndex = text.length() - 1;
        for (; firstIndex < text.length(); firstIndex++)
            if (!Character.isWhitespace(text.charAt(firstIndex)) &&
                Character.getType(text.charAt(firstIndex)) != Character.SPACE_SEPARATOR)
                break;
        for (; lastIndex > firstIndex; lastIndex--)
            if (!Character.isWhitespace(text.charAt(lastIndex)) &&
                Character.getType(text.charAt(lastIndex)) != Character.SPACE_SEPARATOR)
                break;

        return text.substring(firstIndex, lastIndex + 1);
    }

    static public String cleanText(String text)
    {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);
            if (Character.getType(c) == Character.SPACE_SEPARATOR)
                buffer.append(" ");
            else
                buffer.append(c);
        }
        return normalizeSpaces(buffer.toString());
    }

    static public boolean isWhitespace(String text)
    {
        for (int i = 0; i < text.length(); i++)
            if (!Character.isWhitespace(text.charAt(i)))
                return false;
        return true;
    }

    static public String makeIdentifierString(String text)
    {
        return makeIdentifierString(text, true);
    }

    static public String makeIdentifierString(String text, boolean checkFirst)
    {
        if (text == null)
            return null;
        if (text.trim().length() == 0)
            return "";

        StringBuffer result = new StringBuffer();

        text = normalizeSpaces(text);
        if (checkFirst &&
            (!Character.isJavaIdentifierStart(text.charAt(0)) || text.charAt(0) == '$'))
            result.append('_');

        char last = '_';
        for (int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);
            if (Character.isWhitespace(last))
                c = Character.toUpperCase(c);
            if (Character.isJavaIdentifierPart(c) && c != '$')
                result.append(c);
            last = c;
        }
        return result.toString();
    }

    static public String findMaxCommonSubstring(String s1, String s2)
    {
        if (s1 == null || s1.length() == 0 || s2 == null || s2.length() == 0)
            return "";

        s1 = makeIdentifierString(s1, false).toLowerCase();
        s2 = makeIdentifierString(s2, false).toLowerCase();

        for (int i = s1.length(); i > 0; i--)
        {
            int possibleSubstrings = s1.length() - i + 1;
            for (int j = 0; j < possibleSubstrings; j++)
            {
                String partialString = s1.substring(j, j + i);
                if (s2.indexOf(partialString) != -1)
                {
                    return partialString;
                }
            }
        }
        return "";
    }

    static public double getMaxCommonSubstringEffectivity(String s1, String s2)
    {
        if (s1 == null || s1.length() == 0 || s2 == null || s2.length() == 0)
            return 0;
        String si1 = makeIdentifierString(s1, false);
        String si2 = makeIdentifierString(s2, false);
        String common = findMaxCommonSubstring(s1, s2);
        return (double) common.length() / (double) Math.max(si1.length(), si2.length());
    }

    static public String separateCapitalizedWords(String text)
    {
        if (text == null || text.length() == 0)
        {
            return "";
        }
        text = text.replace('_', ' ');
        ArrayList<String> words = new ArrayList<String>();

        int index = 0;
        for (int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);
            if ((Character.isUpperCase(c) || Character.isWhitespace(c)) && i > index)
            {
                words.add(text.substring(index, i));
                index = i;
            }
        }

        if (index < text.length())
            words.add(text.substring(index));

        for (int i = 0; i < words.size();)
        {
            String word = (String) words.get(i);
            String next = i + 1 >= words.size() ? null : (String) words.get(i + 1);
            if (next != null && isUpperCase(word) && next.trim().length() == 1 &&
                Character.isUpperCase(next.charAt(0)))
            {
                String composed = word + next;
                words.remove(i);
                words.set(i, composed);
            }
            else
                i++;
        }

        return replace(normalizeSpaces(StringUtilities.buildTextFromWords(words)), "- ", "-");
    }

    public static void main(String args[])
    {
        // Max common substring
        // System.out.println("Max common substring between '" + args[0] + "' and '" + args[1] + "'"
        // + findMaxCommonSubstring(args[0],args[1]));
        // System.out.println("\tEffectivity: " +
        // getMaxCommonSubstringEffectivity(args[0],args[1]));

        // Word separation
        // System.out.println("Word separation for '" + args[0] + "': " +
        // separateCapitalizedWords(args[0]));

        // Soundex
        // Soundex sdx=new Soundex();
        // System.out.println("Soundex for " + args[0] + " is: " + sdx.encode(args[0]));

        char c = args[0].charAt(0);
        System.out.println("Char '" + c + "' is " + (int) c);
    }
}