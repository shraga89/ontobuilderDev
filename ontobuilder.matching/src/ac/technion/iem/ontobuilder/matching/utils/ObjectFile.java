package ac.technion.iem.ontobuilder.matching.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;

/**
 * <p>Title: ObjectFile</p>
 * <p>Description: Read/Write Objects from/to Files</p>
 */
public class ObjectFile
{
    private String fileName;
    private RandomAccessFile random;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    /**
     * Constructs an ObjectFile
     * 
     * @param file file name
     * @param append if append == false, create a new file.
     */
    public ObjectFile(String file, boolean append)
    {
        fileName = file;
        try
        {
            if (!append)
                trunc();
            random = new RandomAccessFile(file, "rw");
            out = new ObjectOutputStream(new FileOutputStream(random.getFD()));
            random.seek(0);
            in = new ObjectInputStream(new FileInputStream(random.getFD()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Append Object at the end
     * 
     * @return object-offset on file
     */
    public synchronized long write(Object o)
    {
        long offset = 0;
        try
        {
            offset = random.length();
            random.seek(offset); // Any write action is append to End-Of-File
            out.writeObject(o);
            // System.out.println("origin offset = " + offset + ", after write object = " +
            // random.length());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return offset;
    }

    /**
     * Get the current offset of the file
     * 
     * @return the offset
     */
    public long getNextOffset()
    {
        long offset = 0;
        try
        {
            offset = random.length();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return offset;
    }

    /**
     * Write Object to specific offset. For package member only.
     * 
     * @return object-offset on file
     */
    synchronized long write(Object o, long offset)
    {
        try
        {
            random.seek(offset);
            out.writeObject(o);
            // System.out.println("origin offset = " + offset + ", after write object = " +
            // random.length());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return offset;
    }

    /**
     * Read object from offset address
     * 
     * @return the object
     */
    public Object read(long offset)
    {
        Object o = new Object();
        try
        {
            random.seek(offset);
            System.out.println("fp:" + random.getFilePointer());
            o = in.readObject();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return o;
    }

    /**
     * Read the next object from the current offset
     * 
     * @return the object
     */
    public Object readNextObject()
    {
        Object o = new Object();
        try
        {
            o = in.readObject();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return o;
    }

    /**
     * Get the file name
     * 
     * @return the file name
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * Check ObjectFile, if it can be READ and WRITE
     * 
     * @param file the file name
     * @return true if it can be READ & WRITE, else false
     */
    public static boolean checkObjectFile(String file)
    {
        SecurityManager s = System.getSecurityManager();

        try
        {
            s.checkRead(file);
            s.checkWrite(file);
        }
        catch (SecurityException e)
        {
            // e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Close the file
     * 
     * @return true on success
     * @throws IOException
     */
    private boolean trunc() throws IOException
    {
        // if (checkObjectFile(fileName)) {
        try
        {
            FileOutputStream f = new FileOutputStream(fileName, false);
            f.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return true;
        // }
        // throw new IOException("File \"" + fileName +
        // "\" cannot been read or write, check it out first");
    }

    /**
     * Close all Output/Input streams and the file
     */
    public void close()
    {
        try
        {
            in.close();
            out.close();
            random.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String args[])
    {
        long offset = 0;
        try
        {
            Testf1 test = new Testf1(1, 2, (float) 3.0, 4.0, "Test");
            ObjectFile f = new ObjectFile("t.tmp", false);
            // long offset = f.write(test);
            offset = f.write(test);
            ((Testf1) f.read(offset)).print();
            f.close();
            test.set(1234567, 987654321, (float) 2222223.0, 666666666664.3333333333330, "Test 2");
            f = new ObjectFile("t.tmp2", false);
            offset = f.write(test);
            ((Testf1) f.read(offset)).print();
            f.close();
            // System.out.println(f.read(offset));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

class Testf1 implements java.io.Serializable
{
    private static final long serialVersionUID = 6411133838391636078L;
    String S = new String();
    Integer A;
    Long B;
    Float C;
    Double D;

    Testf1(int A, long B, float C, double D, String s)
    {
        S = s;
        this.A = new Integer(A);
        this.B = new Long(B);
        this.C = new Float(C);
        this.D = new Double(D);
    }

    public void set(int A, long B, float C, double D, String s)
    {
        S = s;
        this.A = new Integer(A);
        this.B = new Long(B);
        this.C = new Float(C);
        this.D = new Double(D);
    }

    public void print()
    {
        System.out.println("A = " + A + ", B = " + B + ", C = " + C + ", D = " + D);
        System.out.println("S = " + S);
    }
}