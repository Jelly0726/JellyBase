LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

APP_ABI := armeabi-v7a x86 arm64-v8a x86_64

APP_PLATFORM := android-21

include $(BUILD_SHARED_LIBRARY)
