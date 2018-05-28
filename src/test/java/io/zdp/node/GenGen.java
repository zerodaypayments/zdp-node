package io.zdp.node;

import io.zdp.crypto.Curves;
import io.zdp.crypto.key.ZDPKeyPair;

public class GenGen {

	public static void main ( String [ ] args ) {
		
		// ZDPKeyPair kp = ZDPKeyPair.createFromPrivateKeyBase58( "7ryHDXrpChJzVLTMMcMLH4Q9w7dMYz7dEbBenDDFUvoV", Curves.DEFAULT_CURVE );
		ZDPKeyPair kp = ZDPKeyPair.createRandom( Curves.DEFAULT_CURVE );
		System.out.println( kp.getPrivateKeyAsBase58() );
		System.out.println( kp.getPublicKeyAsBase58() );
		System.out.println( kp.getZDPAccount().getPublicKeyHashAsBase58() );
		System.out.println( kp.getZDPAccount().getUuid() );

	}

}
