package io.zdp.node;

import io.zdp.crypto.Curves;
import io.zdp.crypto.key.ZDPKeyPair;

public class GenGen {

	public static void main ( String [ ] args ) {

		for ( int i = 0; i < 3; i++ ) {
			// ZDPKeyPair kp = ZDPKeyPair.createFromPrivateKeyBase58( "7ryHDXrpChJzVLTMMcMLH4Q9w7dMYz7dEbBenDDFUvoV", Curves.DEFAULT_CURVE );
			ZDPKeyPair kp = ZDPKeyPair.createRandom( Curves.VALIDATION_NODE_CURVE );
			System.out.println("Priv: "+ kp.getPrivateKeyAsBase58() );
			System.out.println("Pub: "+ kp.getPublicKeyAsBase58() );
			System.out.println(  );
		}
	}

}
