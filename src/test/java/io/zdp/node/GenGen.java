package io.zdp.node;

import io.zdp.crypto.Curves;
import io.zdp.crypto.key.ZDPKeyPair;

public class GenGen {

	public static void main ( String [ ] args ) {

		for ( int i = 0; i < 1; i++ ) {
//			 ZDPKeyPair kp = ZDPKeyPair.createFromPrivateKeyBase58( "19tifCwtQVC7qKtHjAgevcHRJ99DgjAWWCVyjdDfcYMt4", Curves.VALIDATION_NODE_CURVE);
			ZDPKeyPair kp = ZDPKeyPair.createRandom( Curves.DEFAULT_CURVE);
			System.out.println("Priv: "+ kp.getPrivateKeyAsBase58() );
			System.out.println("Pub: "+ kp.getPublicKeyAsBase58() );
			System.out.println(kp.getZDPAccount().getPublicKeyHashAsBase58()  );
			System.out.println(kp.getZDPAccount().getUuid() );
		}
	}

}
