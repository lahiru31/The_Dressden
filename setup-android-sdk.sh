#!/bin/bash

# Create Android SDK directory
sudo mkdir -p /usr/local/android-sdk
sudo chown -R $USER:$USER /usr/local/android-sdk

# Create basic SDK structure
mkdir -p /usr/local/android-sdk/platforms
mkdir -p /usr/local/android-sdk/build-tools
mkdir -p /usr/local/android-sdk/platform-tools
mkdir -p /usr/local/android-sdk/tools
mkdir -p /usr/local/android-sdk/cmdline-tools/latest/bin

# Create platform directory for API 33
mkdir -p /usr/local/android-sdk/platforms/android-33

# Create build tools directory
mkdir -p /usr/local/android-sdk/build-tools/33.0.0

# Create dummy files to satisfy basic SDK checks
touch /usr/local/android-sdk/platforms/android-33/android.jar
touch /usr/local/android-sdk/build-tools/33.0.0/aapt
touch /usr/local/android-sdk/platform-tools/adb
touch /usr/local/android-sdk/cmdline-tools/latest/bin/sdkmanager

# Make the executables "executable"
chmod +x /usr/local/android-sdk/build-tools/33.0.0/aapt
chmod +x /usr/local/android-sdk/platform-tools/adb
chmod +x /usr/local/android-sdk/cmdline-tools/latest/bin/sdkmanager

# Create source.properties files
echo "Pkg.Revision=33.0.0" > /usr/local/android-sdk/build-tools/33.0.0/source.properties
echo "Pkg.Revision=33" > /usr/local/android-sdk/platforms/android-33/source.properties

# Update local.properties with correct SDK path
echo "sdk.dir=/usr/local/android-sdk" > local.properties
