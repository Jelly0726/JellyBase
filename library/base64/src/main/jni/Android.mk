LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := base64
LOCAL_SRC_FILES := Base64.cpp
LOCAL_LDLIBS    := -lm -llog
APP_CFLAGS += -Wno-error=format-security
include $(BUILD_SHARED_LIBRARY)