package com.jelly.baselibrary.moshi;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

/** Converts strings like #ff0000 to the corresponding color ints. */
public class ColorAdapter {
    @ToJson
    String toJson(@HexColor int rgb) {
        return String.format("#%06x", rgb);
    }

    @FromJson
    @HexColor int fromJson(String rgb) {
        return Integer.parseInt(rgb.substring(1), 16);
    }
}