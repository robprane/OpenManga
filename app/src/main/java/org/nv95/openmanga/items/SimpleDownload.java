package org.nv95.openmanga.items;

import org.nv95.openmanga.providers.staff.MangaProviderManager;
import org.nv95.openmanga.utils.NoSSLv3SocketFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import javax.net.ssl.HttpsURLConnection;

import info.guardianproject.netcipher.NetCipher;

/**
 * Created by nv95 on 12.02.16.
 */
public class SimpleDownload implements Runnable {

    private final String mSourceUrl;
    private final File mDestination;

    public SimpleDownload(String sourceUrl, File destination) {
        this.mSourceUrl = sourceUrl;
        this.mDestination = destination;
    }

    @Override
    public void run() {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            connection = NetCipher.getHttpURLConnection(mSourceUrl);
            if (connection instanceof HttpsURLConnection) {
                ((HttpsURLConnection) connection).setSSLSocketFactory(NoSSLv3SocketFactory.getInstance());
            }
            MangaProviderManager.prepareConnection(connection);
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return;
            }
            input = connection.getInputStream();
            output = new FileOutputStream(mDestination);
            byte data[] = new byte[4096];
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
            }
        } catch (Exception e) {
            if (mDestination.exists()) {
                mDestination.delete();
            }
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }
            if (connection != null)
                connection.disconnect();
        }
    }

    public boolean isSuccess() {
        return mDestination.exists();
    }

    public File getDestination() {
        return mDestination;
    }

    public String getSourceUrl() {
        return mSourceUrl;
    }
}
