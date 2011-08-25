package ac.technion.iem.ontobuilder.core.utils;

/**
 * <p>Title: Email</p>
 * <p>Description: Implements methods to keep an email address</p>
 */
public class Email
{
    String user;
    String domain;

    /**
     * Constructs an Email
     *
     * @param user the user name
     * @param domain the domain
     */
    public Email(String user, String domain)
    {
        this.user = user;
        this.domain = domain;
    }

    /**
     * Set the user 
     *
     * @param user the user
     */
    public void setUser(String user)
    {
        this.user = user;
    }

    /**
     * Get the user 
     *
     * @return the user
     */
    public String getUser()
    {
        return user;
    }

    /**
     * Set the domain 
     *
     * @param domain the domain
     */
    public void setDomain(String domain)
    {
        this.domain = domain;
    }

    /**
     * Get the domain 
     *
     * @return the domain
     */
    public String getDomain()
    {
        return domain;
    }

    public String toString()
    {
        return user + "@" + domain;
    }

    /**
     * Check if an email address is valid
     *
     * @param email the email string
     * @return true if it is a valid address
     */
    public static boolean isValidEmail(String email)
    {
        // check for only one @
        int index = email.indexOf('@');
        if (index < 1 || email.length() - 1 == index)
            return false;
        if (email.indexOf('@', index + 1) != -1)
            return false;

        // check for at least a .
        index = email.lastIndexOf(".");
        if (index < 1 || email.length() - 1 == index)
            return false;

        char characters[] = email.toCharArray();
        for (int i = 0; i < characters.length; i++)
        {
            char c = characters[i];
            if (!Character.isLetterOrDigit(c) && c != '-' && c != '_' && c != '@' && c != '.')
                return false;
        }
        return true;
    }

    /**
     * Parse an email address - keep user and domain
     *
     * @param email the email to parse
     * @return an email
     */
    public static Email parse(String email)
    {
        if (!isValidEmail(email))
            return null;
        int index = email.indexOf('@');
        return new Email(email.substring(0, index), email.substring(index + 1));
    }
}