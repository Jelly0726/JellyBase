package com.jelly.baselibrary.provider;

import android.content.Context;
import androidx.core.content.FileProvider;

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
