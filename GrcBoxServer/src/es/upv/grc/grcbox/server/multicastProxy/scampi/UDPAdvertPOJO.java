package es.upv.grc.grcbox.server.multicastProxy.scampi;


/**
 * Advertisement to use with UDPDiscovery. Gets turned into JSON.
 *
 * @author teemuk at http://comnet.aalto.fi
 * simplified by sertinell
 */
public class UDPAdvertPOJO {
    // Need to carry the destination address because POSIX multicast APIs are
    // still broken and provide no means to discover which multicast address
    // the packet was sent to or received from.
    /** The destination_ip. */
    public String		destination_ip = null;


    /** The scampi_id. */
    public String		scampi_id = null;
    
    /** The ip. */
    public String		ip = null;
    
    /** The cl_types. */
    public String[]		cl_types = null;
    
    /** The cl_ports. */
    public int[]		cl_ports = null;

    // If probe is set to true, the sender expects replies from other nodes.
    /** The probe. */
    public Boolean      probe = false;

    // Random ID, changed every time the discoverer is instantiated.
    // Allows peers to discover rebooting of nodes.
    /** The disc_id. */
    public long			disc_id = 0;

    // Known nodes. For probes, the sender should record the known scampi_ids
    // so that other nodes do not need to reply if they've already been
    // discovered.
    // This is a 128 byte bloom filter with 7 hashes encoded as Base64.
    /** The known. */
    public String     known = "";
}
