package org.asl19.paskoocheh.utils;


import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.openpgp.PGPCompressedData;
import org.spongycastle.openpgp.PGPException;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPPublicKeyRingCollection;
import org.spongycastle.openpgp.PGPSignature;
import org.spongycastle.openpgp.PGPSignatureList;
import org.spongycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.spongycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.spongycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class PGPUtil {

    public static boolean verifySignature(InputStream inputFile, InputStream signatureFile, InputStream publicKeyFile) throws GeneralSecurityException, IOException, PGPException {
        signatureFile = org.spongycastle.openpgp.PGPUtil.getDecoderStream(signatureFile);

        JcaPGPObjectFactory pgpObjectFactory = new JcaPGPObjectFactory(signatureFile);
        PGPSignatureList pgpSignatureList;

        Object object = pgpObjectFactory.nextObject();
        if (object instanceof PGPCompressedData) {
            PGPCompressedData compressedData = (PGPCompressedData) object;
            pgpObjectFactory = new JcaPGPObjectFactory(compressedData.getDataStream());
            pgpSignatureList = (PGPSignatureList) pgpObjectFactory.nextObject();
        } else {
            pgpSignatureList = (PGPSignatureList) object;
        }

        PGPPublicKeyRingCollection pgpPubRingCollection = new PGPPublicKeyRingCollection(org.spongycastle.openpgp.PGPUtil.getDecoderStream(publicKeyFile), new JcaKeyFingerprintCalculator());

        if(pgpSignatureList.size() <= 0) {
            signatureFile.close();
            publicKeyFile.close();
            inputFile.close();
            return false;
        }

        PGPSignature pgpSignature = pgpSignatureList.get(0);
        PGPPublicKey publicKey = pgpPubRingCollection.getPublicKey(pgpSignature.getKeyID());

        pgpSignature.init(new JcaPGPContentVerifierBuilderProvider().setProvider(new BouncyCastleProvider()), publicKey);

        signatureFile.close();
        publicKeyFile.close();

        final byte[] b = new byte[4096];
        int n;
        while ((n = inputFile.read(b)) != -1) {
            pgpSignature.update(Arrays.copyOfRange(b, 0, n));
        }

        inputFile.close();

        return pgpSignature.verify();
    }
}
