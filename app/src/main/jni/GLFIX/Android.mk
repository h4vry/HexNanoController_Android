LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)  
LOCAL_MODULE    := glfix  
LOCAL_LDLIBS    := -lGLESv2
LOCAL_SRC_FILES := fix-GLES20.c

LOCAL_CFLAGS += -DANDROID_NDK
include $(BUILD_SHARED_LIBRARY)