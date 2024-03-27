package org.asl19.paskoocheh.amazon;

import android.content.Context;

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
    public static S3Clients getS3Clients(Context context) {
        return new S3Clients(context);
    }
}
