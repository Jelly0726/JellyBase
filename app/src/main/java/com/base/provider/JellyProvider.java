package com.base.provider;

import android.content.Context;
import android.support.v4.content.FileProvider;

public class JellyProvider extends FileProvider {
    /**
     * Get the provider of the external file path.
     *
     * @param context context.
     * @return provider.
     */
    public static String getProviderName(Context context) {
        return context.getPackageName() + ".jellybase.fileprovider";
    }
}
