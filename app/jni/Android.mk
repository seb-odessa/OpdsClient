LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := opds_jni
LOCAL_SRC_FILES := libs/$(TARGET_ARCH_ABI)/libopds_jni.so
include $(PREBUILT_SHARED_LIBRARY)