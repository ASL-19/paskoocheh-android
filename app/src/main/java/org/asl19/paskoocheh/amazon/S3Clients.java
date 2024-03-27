package org.asl19.paskoocheh.amazon;

import android.content.Context;
import java.lang.Thread;
import java.lang.Runnable;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import org.asl19.paskoocheh.PaskoochehApplication;
import org.asl19.paskoocheh.utils.ProxyUrlHttpClient;

import static org.asl19.paskoocheh.OuinetService.PROXY_HOST;
import static org.asl19.paskoocheh.OuinetService.PROXY_PORT;
import static org.asl19.paskoocheh.Constants.COGNITO_POOL_ID;

public final class S3Clients {
    // In censorship conditions, the AWS server may not be available. This would
    // cause the Cognito AWS credentials provider to block indefinitely. This class
    // serves to avoid such indefinite blocking.
    //
    // Why is this done this way? When the AmazonS3Client uses the
    // AWSCredentialsProvider internally, deep inside it creates a another
    // UrlHttpClient to request those credentials from the amazon server. On
    // construction, this client receives the ClientConfiguration that we
    // create in this file. Unfortunately, even if we setConnectionTimeout to
    // something small, requesting content through this client when connection
    // can't be established still takes a long time (between 2 and 7 minutes!).
    //
    // I think what is going on is that even though the connection itself may take
    // little, there is still DNS resolution going. It seems Amazon's servers
    // resolve to hundreds of IP addresses and perhaps going through all of them
    // and timing out is what takes so long. This I don't know for sure though.
    //
    // I was thinking about subclassing this AmazonS3Client again (i.e. as we
    // do here for normal http requests), unfortunately simply modifying the
    // ProxyUrlHttpClient wouldn't help because that one is also using
    // java.net.HttpUrlConnection which is the object causing the blockage.
    private class ThreadedCredentialsProvider  {
        private class GetCredentialsJob implements Runnable {
            private AWSCredentialsProvider origProvider;
            public AWSCredentials result;
            public Exception exception;

            GetCredentialsJob(AWSCredentialsProvider origProvider) {
                this.origProvider = origProvider;
            }

            @Override
            public void run() {
                try {
                    result = origProvider.getCredentials();
                } catch(Exception e) {
                    exception = e;
                }
            }
        }

        private AWSCredentialsProvider origProvider;
        private AWSCredentials credentials;
        private Exception exception;
        private Thread thread;

        private GetCredentialsJob getCredentialsJob;
        private long getCredentialsJobStart = 0;

        private Runnable refreshJob;
        private long refreshJobStart = 0;

        public ThreadedCredentialsProvider(AWSCredentialsProvider origProvider) {
            this.origProvider = origProvider;
        }

        public AWSCredentials getCredentials(int timeout_millis) {
            long start = 0;

            AWSCredentials cred;
            synchronized (this) {
                if (credentials != null) {
                    cred = credentials;
                    credentials = null;
                    return cred;
                }
                if (getCredentialsJob == null) {
                    getCredentialsJobStart = System.currentTimeMillis();
                    getCredentialsJob = new GetCredentialsJob(origProvider);
                }
                start = getCredentialsJobStart;
            }
            long now = System.currentTimeMillis();

            Thread thread = startThread();

            if (start + timeout_millis > now) {
                try { thread.join(start + timeout_millis - now /* ms */); } catch (Exception e) {}
            }

            synchronized (this) {
                //if (exception != null) {
                //    Exception e = exception;
                //    exception = null;
                //    Utils.throwException(e);
                //}

                if (credentials != null) {
                    cred = credentials;
                    credentials = null;
                    return cred;
                }
            }

            return null;

            //return new AWSCredentials() {
            //    @Override
            //    public String getAWSAccessKeyId() { return "BogusAccessKeyId"; }
            //    @Override
            //    public String getAWSSecretKey()   { return "BogusSecretKey"; }
            //};
        }

        public void refresh(int timeout_millis) {
            long start = 0;

            synchronized (this) {
                if (refreshJob == null) {
                    refreshJobStart = System.currentTimeMillis();
                    refreshJob = new Runnable() {
                        public void run() {
                            origProvider.refresh();
                        }
                    };
                }
                start = refreshJobStart;
            }

            Thread thread = startThread();

            long now = System.currentTimeMillis();

            if (start + timeout_millis > now) {
                try { thread.join(start + timeout_millis - now /* ms */); } catch (Exception e) {}
            }
        }

        private Thread startThread() {
            synchronized (this) {
                if (thread != null) {
                    return thread;
                }

                final ThreadedCredentialsProvider self = this;

                thread = new Thread(new java.lang.Runnable() {
                    public void run() {
                        while (true) {
                            GetCredentialsJob getCredentials = null;
                            Runnable refresh = null;

                            synchronized (self) {
                                if (self.getCredentialsJob != null) {
                                    getCredentials = self.getCredentialsJob;
                                } else if (self.refreshJob != null) {
                                    refresh = self.refreshJob;
                                } else {
                                    break;
                                }
                            }

                            if (getCredentials != null) {
                                getCredentials.run();
                                synchronized (self) {
                                    self.credentials = getCredentials.result;
                                    self.exception = getCredentials.exception;
                                    self.getCredentialsJobStart = 0;
                                    self.getCredentialsJob = null;
                                }
                            } else {
                                refresh.run();
                                synchronized (self) {
                                    self.refreshJobStart = 0;
                                    self.refreshJob = null;
                                }
                            }
                        }

                        synchronized (self) {
                            self.thread = null;
                        }
                    }
                });

                thread.start();
                return thread;
            }
        }
    }

    private class TimedCredentialsProvider implements AWSCredentialsProvider {
        int timeout;
        ThreadedCredentialsProvider provider;

        TimedCredentialsProvider(int timeout, ThreadedCredentialsProvider provider) {
            this.timeout = timeout;
            this.provider = provider;
        }

        @Override
        public AWSCredentials getCredentials() {
            return provider.getCredentials(timeout);
        }

        @Override
        public void refresh() {
            provider.refresh(timeout);
        }
    }

    private final AmazonS3Client directClient;
    private final AmazonS3Client ouinetClient;

    private final TransferUtility directTransferUtility;
    private final TransferUtility ouinetTransferUtility;
    
    public S3Clients(Context context) {
        ThreadedCredentialsProvider credProvider = new ThreadedCredentialsProvider(getCredProvider(context));

        ClientConfiguration direct_config = clientConfig();
        ClientConfiguration ouinet_config = clientProxyConfig();

        directClient = new AmazonS3Client(new TimedCredentialsProvider(60*1000, credProvider), direct_config);
        directClient.setRegion(Region.getRegion(Regions.US_EAST_1));
        
        ouinetClient = new AmazonS3Client(new TimedCredentialsProvider(10*1000, credProvider), ouinet_config, new ProxyUrlHttpClient(ouinet_config));
        ouinetClient.setRegion(Region.getRegion(Regions.US_EAST_1));

        directTransferUtility = new TransferUtility(directClient, context);
        ouinetTransferUtility = new TransferUtility(ouinetClient, context);
    }

    public AmazonS3Client chooseClient() {
        if (isOuinetStarted()) return ouinetClient;
        return directClient;
    }

    public TransferUtility chooseTransferUtility() {
        if (isOuinetStarted()) return ouinetTransferUtility;
        return directTransferUtility;
    }

    // AmazonS3Client uses these values to call setConnectTimeout and
    // setReadTimeout on java.net.URLConnection.
    //
    // https://docs.oracle.com/javase/8/docs/api/java/net/URLConnection.html#setConnectTimeout-int-
    // https://docs.oracle.com/javase/8/docs/api/java/net/URLConnection.html#setReadTimeout-int-
    //
    // XXX: These timeouts were increased from 150000 (2.5 min) to 300000 (5
    // min) in commit dd5afd5a. This value seems excessive for the connect so
    // decreasing to 15s.
    //
    // Note that (on Android at least), the connect timeout is not strictly
    // followed. Even the documentation says
    //
    // > Some non-standard implementation of this method may ignore the specified timeout.
    //
    // On Android, when DEFAULT_CONNECT_TIMEOUT was 5 minutes and if the client couldn't
    // connect to any of the (hundreds of) IP addresses as resolved by the DNS, the actual
    // timeout was somewhere around 7 minutes. In case this constant was set to 15 seconds,
    // under the same conditions the timeout seemed to be around 2 minutes.
    static final int DEFAULT_CONNECT_TIMEOUT = 15*1000 /* ms */;
    static final int DEFAULT_READ_TIMEOUT    = 300*1000 /* ms */;

    private static ClientConfiguration clientConfig() {
        ClientConfiguration config = new ClientConfiguration();
        config.setConnectionTimeout(DEFAULT_CONNECT_TIMEOUT);
        config.setSocketTimeout(DEFAULT_READ_TIMEOUT);
        return config;
    }

    private static ClientConfiguration clientProxyConfig() {
        ClientConfiguration config = new ClientConfiguration();
        config.setConnectionTimeout(DEFAULT_CONNECT_TIMEOUT);
        config.setSocketTimeout(DEFAULT_READ_TIMEOUT);
        // Note that these options are ignored inside the AmazonS3Client unless we use
        // our custop ProxyUrlHttpClient.
        config.setProxyHost(PROXY_HOST);
        config.setProxyPort(PROXY_PORT);
        return config;
    }

    public static CognitoCachingCredentialsProvider getCredProvider(Context context) {
        return new CognitoCachingCredentialsProvider(
                context,
                COGNITO_POOL_ID,
                Regions.US_EAST_1,
                clientConfig());
    }

    private static boolean isOuinetStarted() {
        return PaskoochehApplication.isOuinetStarted();
    }
}

