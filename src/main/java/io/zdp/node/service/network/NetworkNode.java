package io.zdp.node.service.network;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zdp.crypto.Base58;
import io.zdp.crypto.Curves;
import io.zdp.crypto.Keys;
import io.zdp.crypto.key.ZDPKeyPair;

/**
 * 	Validation nodes
 *
 */
@SuppressWarnings ( "serial" )
public class NetworkNode implements Serializable {

	private final Logger log = LoggerFactory.getLogger( NetworkNode.class );

	private String hostname;

	private int port;

	private String uuid;

	private String publicKey;

	private String privateKey;

	private PublicKey pub;

	private PrivateKey priv;

	private NetworkNodeType nodeType;

	public String getHttpEndpointUrl ( ) {
		return null;
	}

	public PrivateKey getECPrivateKey ( ) {
		return priv;
	}

	public String getPrivateKey ( ) {
		return privateKey;
	}

	public void setPrivateKey ( String privateKey ) {

		this.privateKey = privateKey;

		ZDPKeyPair kp = ZDPKeyPair.createFromPrivateKeyBase58( privateKey, Curves.VALIDATION_NODE_CURVE );

		this.priv = kp.getPrivateKeyAsPrivateKey();
		this.publicKey = kp.getPublicKeyAsBase58();
		this.pub = kp.getPublicKeyAsPublicKey();
	}

	public NetworkNodeType getNodeType ( ) {
		return nodeType;
	}

	public void setNodeType ( NetworkNodeType nodeType ) {
		this.nodeType = nodeType;
	}

	public PublicKey getECPublicKey ( ) {
		return pub;
	}

	public String getPublicKey ( ) {
		return publicKey;
	}

	public void setPublicKey ( String publicKey ) {

		this.publicKey = publicKey;

		try {
			this.pub = Keys.toPublicKey( Base58.decode( publicKey ), Curves.VALIDATION_NODE_CURVE );
		} catch ( Exception e ) {
			log.error( "Error: ", e );
		}
	}

	public String getHostname ( ) {
		return hostname;
	}

	public void setHostname ( String hostname ) {
		this.hostname = hostname;
	}

	public int getPort ( ) {
		return port;
	}

	public void setPort ( int port ) {
		this.port = port;
	}

	public String getUuid ( ) {
		return uuid;
	}

	public void setUuid ( String uuid ) {
		this.uuid = uuid;
	}

	@Override
	public String toString ( ) {
		return "ValidationNode [hostname=" + hostname + ", port=" + port + ", uuid=" + uuid + ", publicKey=" + publicKey + "]";
	}

	@Override
	public int hashCode ( ) {
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( uuid == null ) ? 0 : uuid.hashCode() );
		return result;
	}

	@Override
	public boolean equals ( Object obj ) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		NetworkNode other = (NetworkNode) obj;
		if ( uuid == null ) {
			if ( other.uuid != null )
				return false;
		} else if ( !uuid.equals( other.uuid ) )
			return false;
		return true;
	}

	public String getBaseUrl ( ) {
		return "https://" + hostname + ":" + port;
	}

	public boolean isValidating ( ) {
		return NetworkNodeType.VALIDATING.equals( nodeType );
	}

}
