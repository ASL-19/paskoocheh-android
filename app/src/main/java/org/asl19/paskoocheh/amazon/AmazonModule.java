package org.asl19.paskoocheh.amazon;

import android.content.Context;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.asl19.paskoocheh.Constants.COGNITO_POOL_ID;

/**
 * This class only contains static methods.
 * Provide Amazon Service Clients.
 */
@Module
public final class AmazonModule {

    private AmazonModule() {
    }

    @Provides
    @Singleton
    public static ClientConfiguration clientConfig() {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setConnectionTimeout(300000);
        clientConfiguration.setSocketTimeout(300000);
        return clientConfiguration;
    }

    /**
     * Gets an instance of a Cognito Credentials provider using the given
     * Context.
     *
     * @param context An Context instance.
     * @return A Cognito Caching Credentials provider.
     */
    @Provides
    @Singleton
    public static CognitoCachingCredentialsProvider getCredProvider(Context context) {
        return new CognitoCachingCredentialsProvider(
                context,
                COGNITO_POOL_ID,
                Regions.US_EAST_1,
                clientConfig());
    }

    /**
     * Gets an instance of a S3 client which is constructed using the given
     * credentials.
     *
     * @param cognitoCachingCredentialsProvider Credentials provider.
     * @return A default S3 client.
     */
    @Provides
    @Singleton
    public static AmazonS3Client getS3Client(CognitoCachingCredentialsProvider cognitoCachingCredentialsProvider) {
        AmazonS3Client sS3Client = new AmazonS3Client(cognitoCachingCredentialsProvider, clientConfig());
        sS3Client.setRegion(Region.getRegion(Regions.US_EAST_1));
        return sS3Client;
    }

    /**
     * Gets an instance of the TransferUtility which is constructed using the
     * given Context
     *
     * @param context An Context instance.
     * @return A TransferUtility instance.
     */
    @Provides
    @Singleton
    public static TransferUtility getTransferUtility(AmazonS3Client amazonS3Client, Context context) {
        return new TransferUtility(amazonS3Client, context);
    }

    /**
     * Gets the Cognito Id which the user is registered under
     *
     * @param cognitoCachingCredentialsProvider A CredencialsProvider instance.
     * @return A Cognito Id.
     */
    @Provides
    @Singleton
    public static String getCognitoId(CognitoCachingCredentialsProvider cognitoCachingCredentialsProvider) {
        return cognitoCachingCredentialsProvider.getIdentityId();
    }
}
