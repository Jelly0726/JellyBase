/*
 * Copyright (C) 2014 nohana, Inc.
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this html_edit_file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rex.editor.common;


import androidx.collection.ArraySet;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * MIME Type enumeration to restrict selectable media on the selection activity. Matisse only supports images and
 * videos.
 * <p>
 * Good example of mime types Android supports:
 * https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/media/java/android/media/MediaFile.java
 */
@SuppressWarnings("unused")
public enum MimeType {

    // ============== images ==============
    JPEG("image/jpeg", arraySetOf(
            "jpg",
            "jpeg"
    )),
    PNG("image/png", arraySetOf(
            "png"
    )),
    GIF("image/gif", arraySetOf(
            "gif"
    )),
    BMP("image/x-ms-bmp", arraySetOf(
            "bmp"
    )),
    WEBP("image/webp", arraySetOf(
            "webp"
    )),

    // ============== videos ==============
    MPEG("html_edit_video/mpeg", arraySetOf(
            "mpeg",
            "mpg"
    )),

    MP3("html_edit_audio/mp3", arraySetOf(
            "mpeg",
            "mpg"
    )),

    WAV("html_edit_audio/wav", arraySetOf(
            "wav",
            "x-wav"
    )),
    X_WAV("html_edit_audio/x-wav", arraySetOf(
            "wav","x-wav"
    )),
    MP4("html_edit_video/mp4", arraySetOf(
            "mp4",
            "m4v"
    )),
    QUICKTIME("html_edit_video/quicktime", arraySetOf(
            "mov"
    )),
    THREEGPP("html_edit_video/3gpp", arraySetOf(
            "3gp",
            "3gpp"
    )),
    THREEGPP2("html_edit_video/3gpp2", arraySetOf(
            "3g2",
            "3gpp2"
    )),
    MKV("html_edit_video/x-matroska", arraySetOf(
            "mkv"
    )),
    WEBM("html_edit_video/webm", arraySetOf(
            "webm"
    )),
    TS("html_edit_video/mp2ts", arraySetOf(
            "ts"
    )),
    AVI("html_edit_video/avi", arraySetOf(
            "avi"
    ));

    private final String mMimeTypeName;
    private final Set<String> mExtensions;

    MimeType(String mimeTypeName, Set<String> extensions) {
        mMimeTypeName = mimeTypeName;
        mExtensions = extensions;
    }

    public static Set<MimeType> ofAll() {
        return EnumSet.allOf(MimeType.class);
    }

    public static Set<MimeType> of(MimeType type, MimeType... rest) {
        return EnumSet.of(type, rest);
    }

    public static Set<MimeType> ofImage() {
        return EnumSet.of(JPEG, PNG, GIF, BMP, WEBP);
    }

    public static Set<MimeType> ofVideo() {
        return EnumSet.of(MPEG, MP4, QUICKTIME, THREEGPP, THREEGPP2, MKV, WEBM, TS, AVI);
    }

    private static Set<String> arraySetOf(String... suffixes) {
        return new ArraySet<>(Arrays.asList(suffixes));
    }

    @Override
    public String toString() {
        return mMimeTypeName;
    }

}
