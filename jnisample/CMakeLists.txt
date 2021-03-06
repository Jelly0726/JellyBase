# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)
#C++编译
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

set(openssl_dir ${CMAKE_SOURCE_DIR})
#配置加载头文件
include_directories(${openssl_dir}/alley/include)
# 设置so文件输出到jniLibs目录中
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI})
# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        cipher

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ../jnisample/src/main/jni/cipher.cpp ../jnisample/src/main/jni/endorse.cpp)

#动态方式加载
add_library(openssl SHARED IMPORTED)
add_library(ssl SHARED IMPORTED)
#引入第三方.so库
set_target_properties(openssl PROPERTIES IMPORTED_LOCATION ${openssl_dir}/alley/lib/${ANDROID_ABI}/libcrypto.so)
set_target_properties(ssl PROPERTIES IMPORTED_LOCATION ${openssl_dir}/alley/lib/${ANDROID_ABI}/libssl.so)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        cipher
        openssl
        ssl
        android

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})