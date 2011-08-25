package ac.technion.iem.ontobuilder.core.utils.network;

/**
 * <p>Title: NetworkUtilitiesPropertiesEnum</p>
 * Available properties: <code>useProxy</code>, <code>proxyHost</code>, <code>proxyPort</code>, <code>proxySet</code> and <code>connectionTimeout</code>
 */
public enum NetworkUtilitiesPropertiesEnum
{
    USE_PROXY_PROPERTY("useProxy"),
    PROXY_HOST_PROPERTY("proxyHost"),
    PROXY_PORT_PROPERTY("proxyPort"),
    PROXY_SET_PROPERTY("proxySet"),
    CONNECTION_TIMEOUT_PROPERTY("connectionTimeout");
    
    private String _value;
    
    private NetworkUtilitiesPropertiesEnum(String value)
    {
        _value = value;
    }
    
    public String getValue()
    {
        return _value;
    }
}
