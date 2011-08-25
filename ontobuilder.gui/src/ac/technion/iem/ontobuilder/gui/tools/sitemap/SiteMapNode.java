package ac.technion.iem.ontobuilder.gui.tools.sitemap;

import java.net.URL;
import java.util.Vector;

import javax.swing.JTable;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.ObjectWithProperties;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>Title: SiteMapNode</p>
 * Implements {@link ObjectWithProperties} 
 */
public class SiteMapNode implements ObjectWithProperties
{
	private URL url;
	private String description;
	private String title;
	private Vector<SiteMapNode> childs;
	private SiteMapNode parent;

	/**
	 * Constructs a SiteMapNode
	 *
	 * @param url the site's URL
	 * @param description the description of the node
	 */
	public SiteMapNode(URL url, String description)
	{
		this.title=url.toExternalForm();
		this.url=url;
		this.description=description;
		this.parent=null;
		childs=new Vector<SiteMapNode>();
	}

    /**
     * Constructs a SiteMapNode
     *
     * @param url the site's URL
     * @param description the description of the node
     * @param parent the parent node of this node
     */
	public SiteMapNode(URL url, String description, SiteMapNode parent)
	{
		this.title=url.toExternalForm();
		this.url=url;
		this.description=description;
		this.parent=parent;
		childs=new Vector<SiteMapNode>();
	}

	/**
	 * Set the title of the node 
	 *
	 * @param title the title to set
	 */
	public void setTitle(String title)
	{
		this.title=title;
	}

	/**
	 * Get the title of the node
	 *
	 * @return the title
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * Set the URL of the site
	 *
	 * @param url the URL
	 */
	public void setURL(URL url)
	{
		this.url=url;
	}

	 /**
     * Get the URL of the site
     *
     * @return the URL
     */
	public URL getURL()
	{
		return url;
	}

	/**
	 * Set the description 
	 *
	 * @param description the description to set
	 */
	public void setDescription(String description)
	{
		this.description=description;
	}

	/**
	 * Get the description
	 *
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Get the number of children of this node
	 *
	 * @return the number of children
	 */
	public int getChildCount()
	{
		return childs.size();
	}

	/**
	 * Get a child with a certain index
	 *
	 * @param i the index of the child to get
	 * @return the requested child
	 */
	public SiteMapNode getChild(int i)
	{
		return (SiteMapNode)childs.get(i);
	}

	/**
	 * Add a child to the node
	 *
	 * @param child the node child to add
	 */
	public void addChild(SiteMapNode child)
	{
		child.setParent(this);
		childs.add(child);
	}

	/**
	 * Get the parent node of this node
	 *
	 * @return the parent node
	 */
	public SiteMapNode getParent()
	{
		return parent;
	}

	/**
	 * Set the parent node of this node
	 *
	 * @param parent the parent node
	 */
	public void setParent(SiteMapNode parent)
	{
		this.parent=parent;
	}

	public JTable getProperties()
	{
		String columnNames[]={ApplicationUtilities.getResourceString("properties.attribute"),ApplicationUtilities.getResourceString("properties.value")};
		Object data[][]={{ApplicationUtilities.getResourceString("sitemap.url"),url.toExternalForm()},
						 {ApplicationUtilities.getResourceString("sitemap.description"),description}};
		JTable propertiesTable=new JTable(new PropertiesTableModel(columnNames,2,data));
		return propertiesTable;
	}

	public String toString()
	{
		return title;
	}

	/**
	 * Check if this node is the root
	 *
	 * @return true if this is the root
	 */
	public boolean isRoot()
	{
		return parent==null;
	}
}