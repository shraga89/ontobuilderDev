package ac.technion.iem.ontobuilder.core.ontology.domain;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.*;
import java.text.*;

import ac.technion.iem.ontobuilder.core.utils.Email;
import ac.technion.iem.ontobuilder.core.utils.StringUtilities;
import ac.technion.iem.ontobuilder.core.utils.properties.PropertiesHandler;

/**
 * <p>Title: GuessedDomain</p>
 */
public class GuessedDomain
{
    public static final double DOMAIN_PENALTY = 0.00000001;

    protected String domainName;
    protected String domainType;
    protected Object object;

    final public static String months[] =
    {
        "january", "february", "march", "april", "may", "june", "july", "august", "september",
        "october", "november", "december"
    };

    /**
     * Constructs a GuessedDomain
     *
     * @param domainName the domain name
     * @param domainType the domain type
     * @param object the domain object
     */
    public GuessedDomain(String domainName, String domainType, Object object)
    {
        this.domainName = domainName;
        this.domainType = domainType;
        this.object = object;
    }

    /**
     * Set the domain name 
     *
     * @param domainName the domain name
     */
    public void setDomainName(String domainName)
    {
        this.domainName = domainName;
    }

    /**
     * Get the domain name 
     *
     * @return the domain name
     */
    public String getDomainName()
    {
        return domainName;
    }

    /**
     * Set the domain type 
     *
     * @param domainType the domain type
     */
    public void setDomainType(String domainType)
    {
        this.domainType = domainType;
    }

    /**
     * Get the domain type 
     *
     * @return the domain type
     */
    public String getDomainType()
    {
        return domainType;
    }

    /**
     * Set the object
     *
     * @param object the domain object
     */
    public void setObject(Object object)
    {
        this.object = object;
    }

    /**
     * Get the object
     *
     * @return the object
     */
    public Object getObject()
    {
        return object;
    }

    /**
     * Guess the domain according to an object
     *
     * @param object the object
     * @return a GuessedDomain
     */
    public static GuessedDomain guessDomain(Object object)
    {
        if (object == null)
            return null;
        String objectStr = StringUtilities.normalizeSpaces(object.toString().toLowerCase(),
            " \t\n\r\f");
        String s;

        // try to parse it as a integer
        s = objectStr;
        try
        {
            long l = Long.parseLong(s);
            return new GuessedDomain(
                PropertiesHandler.getResourceString("ontology.domain.integer"), "integer",
                new Long(l));
        }
        catch (NumberFormatException e)
        {
        }

        // try to parse it as a float
        s = objectStr;
        try
        {
            double d = Double.parseDouble(s);
            return new GuessedDomain(
                PropertiesHandler.getResourceString("ontology.domain.float"), "float",
                new Double(d));
        }
        catch (NumberFormatException e)
        {
        }

        // try to parse it as date
        s = objectStr;
        try
        {
            DateFormat f = DateFormat.getDateInstance(DateFormat.SHORT);
            Date date = f.parse(s);
            return new GuessedDomain(
                PropertiesHandler.getResourceString("ontology.domain.date"), "date", date);
        }
        catch (ParseException e)
        {
        }
        try
        {
            DateFormat f = DateFormat.getDateInstance(DateFormat.MEDIUM);
            Date date = f.parse(s);
            return new GuessedDomain(
                PropertiesHandler.getResourceString("ontology.domain.date"), "date", date);
        }
        catch (ParseException e)
        {
        }
        try
        {
            DateFormat f = DateFormat.getDateInstance(DateFormat.LONG);
            Date date = f.parse(s);
            return new GuessedDomain(
                PropertiesHandler.getResourceString("ontology.domain.date"), "date", date);
        }
        catch (ParseException e)
        {
        }
        try
        {
            DateFormat f = DateFormat.getDateInstance(DateFormat.FULL);
            Date date = f.parse(s);
            return new GuessedDomain(
                PropertiesHandler.getResourceString("ontology.domain.date"), "date", date);
        }
        catch (ParseException e)
        {
        }
        try
        {
            SimpleDateFormat f = new SimpleDateFormat("MMM/dd/yy");
            Date date = f.parse(s);
            return new GuessedDomain(
                PropertiesHandler.getResourceString("ontology.domain.date"), "date", date);
        }
        catch (ParseException e)
        {
        }
        try
        {
            SimpleDateFormat f = new SimpleDateFormat("MM/yyyy");
            Date date = f.parse(s);
            return new GuessedDomain(
                PropertiesHandler.getResourceString("ontology.domain.date"), "date", date);
        }
        catch (ParseException e)
        {
        }
        try
        {
            SimpleDateFormat f = new SimpleDateFormat("MMM-yyyy");
            Date date = f.parse(s);
            return new GuessedDomain(
                PropertiesHandler.getResourceString("ontology.domain.date"), "date", date);
        }
        catch (ParseException e)
        {
        }
        try
        {
            SimpleDateFormat f = new SimpleDateFormat("MMM yyyy");
            Date date = f.parse(s);
            return new GuessedDomain(
                PropertiesHandler.getResourceString("ontology.domain.date"), "date", date);
        }
        catch (ParseException e)
        {
        }
        try
        {
            SimpleDateFormat f = new SimpleDateFormat("MMM/yyyy");
            Date date = f.parse(s);
            return new GuessedDomain(
                PropertiesHandler.getResourceString("ontology.domain.date"), "date", date);
        }
        catch (ParseException e)
        {
        }
        /*
         * try { SimpleDateFormat f=new SimpleDateFormat("MMM"); Date date=f.parse(s); return new
         * GuessedDomain
         * (ApplicationUtilities.getResourceString("ontology.domain.date"),"date",date); }
         * catch(ParseException e) { }
         */

        // try to parse it as time
        s = objectStr;
        boolean couldBeTime = s.matches("[: \\d]*(am|pm|noon|midnight)?$");
        if (couldBeTime)
        {
            /*
             * if(s.equals("noon")) s="12:00 pm"; else if(s.equals("midnight")) s="12:00 am";
             */

            // Separate am/pm from rest
            int ampmIndex = s.indexOf("am");
            if (ampmIndex == -1)
                ampmIndex = s.indexOf("pm");
            if (ampmIndex > 0 && s.charAt(ampmIndex - 1) != ' ')
                s = s.substring(0, ampmIndex) + " " + s.substring(ampmIndex);

            // Fix to format 00:00:00
            StringBuffer sb = new StringBuffer();
            int prevColonIndex = 0;
            int colonIndex = s.indexOf(":");
            if (colonIndex > 0)
            {
                sb.append(s.substring(0, colonIndex));
                while (colonIndex != -1)
                {
                    if (colonIndex == 0 || !Character.isDigit(s.charAt(colonIndex - 1)) &&
                        prevColonIndex != colonIndex + 1)
                        sb.append("00");
                    sb.append(":");
                    if (colonIndex + 1 < s.length() && !Character.isDigit(s.charAt(colonIndex + 1)))
                        sb.append("00");
                    prevColonIndex = colonIndex;
                    colonIndex = s.indexOf(":", prevColonIndex + 1);
                    if (colonIndex != -1)
                        sb.append(s.substring(prevColonIndex + 1, colonIndex));
                }
                sb.append(s.substring(prevColonIndex + 1));
                s = sb.toString();
            }
        }
        try
        {
            DateFormat f = DateFormat.getTimeInstance(DateFormat.SHORT);
            Date date = f.parse(s);
            return new GuessedDomain(
                PropertiesHandler.getResourceString("ontology.domain.time"), "time", date);
        }
        catch (ParseException e)
        {
        }
        try
        {
            DateFormat f = DateFormat.getTimeInstance(DateFormat.MEDIUM);
            Date date = f.parse(s);
            return new GuessedDomain(
                PropertiesHandler.getResourceString("ontology.domain.time"), "time", date);
        }
        catch (ParseException e)
        {
        }
        try
        {
            DateFormat f = DateFormat.getTimeInstance(DateFormat.LONG);
            Date date = f.parse(s);
            return new GuessedDomain(
                PropertiesHandler.getResourceString("ontology.domain.time"), "time", date);
        }
        catch (ParseException e)
        {
        }
        try
        {
            DateFormat f = DateFormat.getTimeInstance(DateFormat.FULL);
            Date date = f.parse(s);
            return new GuessedDomain(
                PropertiesHandler.getResourceString("ontology.domain.time"), "time", date);
        }
        catch (ParseException e)
        {
        }
        try
        {
            if (s.matches("(0?\\d|1\\d|2[0123]):[012345]?\\d"))
            {
                SimpleDateFormat f = new SimpleDateFormat("HH:mm");
                Date date = f.parse(s);
                return new GuessedDomain(
                    PropertiesHandler.getResourceString("ontology.domain.time"), "time", date);
            }
        }
        catch (ParseException e)
        {
        }
        try
        {
            if (s.matches("(0?\\d|1\\d|2[0123])[012345]?\\d"))
            {
                SimpleDateFormat f = new SimpleDateFormat("HHmm");
                Date date = f.parse(s);
                return new GuessedDomain(
                    PropertiesHandler.getResourceString("ontology.domain.time"), "time", date);
            }
        }
        catch (ParseException e)
        {
        }
        try
        {
            if (s.matches("(0?\\d|1[12]) (am|pm)"))
            {
                SimpleDateFormat f = new SimpleDateFormat("hh a");
                Date date = f.parse(s);
                return new GuessedDomain(
                    PropertiesHandler.getResourceString("ontology.domain.time"), "time", date);
            }
        }
        catch (ParseException e)
        {
        }

        // try to parse it as boolean
        s = objectStr;
        if (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false"))
        {
            Boolean b = Boolean.valueOf(s);
            return new GuessedDomain(
                PropertiesHandler.getResourceString("ontology.domain.boolean"), "boolean", b);
        }

        // try to parse it as URL
        s = objectStr;
        try
        {
            URL url = new URL(s);
            return new GuessedDomain(PropertiesHandler.getResourceString("ontology.domain.url"),
                "url", url);
        }
        catch (MalformedURLException e)
        {
        }

        // try to parse it as email
        s = objectStr;
        Email email = Email.parse(s);
        if (email != null)
            return new GuessedDomain(
                PropertiesHandler.getResourceString("ontology.domain.email"), "email", email);

        // try to parse it as text
        s = objectStr;
        if (s.length() > 0)
            return new GuessedDomain(
                PropertiesHandler.getResourceString("ontology.domain.text"), "text", s);

        return null;
    }

    public static void main(String args[])
    {
        // Initialize the resource bundle
        try
        {
            PropertiesHandler.initializeResources("resources.resources", Locale.getDefault());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        GuessedDomain gd = GuessedDomain.guessDomain(args[0]);
        if (gd != null)
            System.out.println("Domain is : " + gd.getDomainName() + " (" + gd.getDomainType() +
                ") = " + gd.getObject());
        else
            System.out.println("Impossible to identify");
    }
}