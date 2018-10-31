#  Building MEGAchat #

The following steps will guide you to build MEGAchat library (including tests and Qtapp). In summary, you need to:  

 - Build WebRTC  
 - Clone MEGAchat  
 - Clone MEGA SDK in `<MEGAchat>/third-party/`  
 - Compile example QTapp  

## Prerequisites ##

You may need to install the following packages in your system

 - `autoconf`  
 - `automake`  
 - `libtool`  
 - `python`  
 - `g++`  
 - `qt`

## Chromium build system ##

You may need to get the Chromium build system to get Webrtc (in case `fetch` command fails, you will need to execute this step). Create a directory to get it (chomium_build_dir).

 - `cd chomium_build_dir`  
 - `git clone https://chromium.googlesource.com/chromium/tools/depot_tools.git`  
 - `export PATH="$PATH:/chomium_build_dir"`  

## Download WebRTC: ##

Create a directory to download Webrtc (webrtc_dir) and add it to $PATH

 - `cd webrtc_dir`  
 - `fetch --nohooks webrtc`  
 - `gclient sync`  
 - `cd ./src`  
 - `git checkout c1a58bae4196651d2f7af183be1878bb00d45a57`  
 - `gclient sync`  

## Get MEGAchat code ##

Checkout the MEGAchat repository:    

 - `git clone --recursive https://github.com/meganz/MEGAchat.git`    

The Megachat sources are at `MEGAchat_repository_dir/src`

*NOTE the `--recursive` switch - the repository contains git submodules that need to be checked out as well.

## Get SDK code ##

Change directory to the root of the MEGAchat checkout  

 - `cd ./third-party/`  
 - `git clone https://github.com/meganz/sdk.git`    
 - `mv sdk mega`    
 - `cd mega`    
 - `./autogen`    
 - `./configure`    


## QT version ##

 In some operating systems the `qmake` command could have different name (i.e Fedora). To be able to compile with `qmake` command we have to ensure that this command
 exists and that is included in $PATH

 To check where is `qmake` version, we have to execute `ls /usr/bin | grep -i qmake`

 The output for a Fedora system is.
 - `qmake-qt5`
 - `qmake-qt5.sh`

 Finally we have to create a soft link to `qmake`. 
 
 - `ln -s /usr/bin/qmake-qt5 /usr/bin/qmake`


## Automated build system ##

To build MEGAchat, you can do it with an automated script `build_with_webrtc.sh`.   
Change directory to the root of the MEGAchat checkout  

 - `cd ./third-party/mega/bindings/qt/` (This step is neccesary to set the right current path for the script)  

 - The sintax to invoke the script is: `build_with_webrtc.sh <all | clean> [withExamples]`

*NOTE: `[withExamples]` option will build the example QTapp to test MEGAchat

Now you're ready. Open `<MEGAchat>/contrib/qt/MEGAchat.pro` in QtCreator and hit `Build` button.

You may need to change the "Build directory" in the project setting to `<MEGAchat>/build` if building complains about files not found.

### List of dependencies
 - `cmake` and `ccmake` 
 * Windows
Under Windows, the cygwin version of cmake will not be sufficient, you need to install the native cmake for Windows from the official site, and instead of ccmake use cmake-gui. You will also need to add the path to Cmake to Cygwin.
 - `libevent2.1.x`  
Version 2.0.x will **not** work, you need at least 2.1.x. You may need to build it from source, as 2.1.x is currently considered beta (even though it has critical bugfixes) and system packages at the time of this writing use 2.0.x. For convenience, libevent is added as a git submodule in third-party/libevent, so its latest revision is automatically checked out from the official libevent repository.  
 - `openssl` - Needed by the MegaSDK, webrtc and MEGAchat itself.  
 - Native WebRTC stack from Chrome. See below for build instructions for it.  
 - The Mega SDK - Check out the repository, configure the SDK with --enable-chat, and in a minimalistic way - without image libs etc. Only the crypto and HTTP functionality is needed.  
 - `libcrypto++` - Needed by MegaSDK and MEGAchat.
 - `libsodium` - Needed by MegaSDK and MEGAchat.
 - `libcurl` - Needed by MegaSDK and MEGAchat.  
 - `Qt5` - QtCore and QtWidgets required only, needed only for the desktop example app.  
 - `Webrtc` - Needed by MegaSDK and MEGAchat.  


### Python version (deprecated) ###
Since the Chromium build system (at least the curent revision) relies heavily
on python, and it assumes the python version is 2.7, the `python` command must
map to python2 instead of python3. This may not be true on more recent systems.
To check, just type `python --version`. If it says version 2.x, then you are ok
and can skip the rest of this section.
To easily make python2 default in the current shell, you can create a symlink
named `python` that points to `/usr/bin/python2`. Put that symlink in a private directory that
doesn't contain other executables with names clashing with ones in the system path,
and include this directory to be first in the system PATH in the shell where you
build webrtc, before invoking `build-webrtc.sh`:  
`export PATH=/path/to/dir-with-python-symlink:$PATH`  

## Manual build the MEGAchat codebase, including a test app ##

In case you want to compile example QTapp manually, you can follow the steps below.

Change directory to the root of the MEGAchat checkout  

 - `mkdir ./build`    
 - `cd ./build`    
 - `qmake ./contrib/QtCreator/MEGAchat.pro -spec linux-g++ CONFIG+=qml_debug CONFIG+=force_debug_info CONFIG+=separate_debug_info && /usr/bin/make _all`    
 - `cd ./build/MEGAChatQt/`    
 - `make`    

Once the compilation has finished we can find the QTapp binary in

 - `./build/MEGAChatQt/megachat`

## Building the Doxygen documentation ##


 From within the build directory of the previous step, provided that you generated a make build, type  

 - `make doc`  

# Getting familiar with the codebase #

## MegaChatApi: the intermediate layer ##

To abstract the code complexity, MEGAchat provides an intermediate layer that enables to quickly create new applications.  

The documentation is available at `src/megachatapi.h`  


## Introduction to the threading model ##
The MEGAchat threading model is similar to the javascript threading model - everything runs in the main (GUI) thread, blocking is never allowed, and external events (network, timers etc, webrtc events etc) trigger callbacks on the main thread. For this to work, MEGAchat must be able to interact with the application's event loop - this is usually the event/message loop of the GUI framework in case of a GUI application, or a custom message loop in case of a console application. As this message loop is very platform-specific, it is the application developer's responsibility to implement the interface between it and MEGAchat. This may sound more complicated than it is in reality - the interface consists of two parts. One part is the implementation of megaPostMessageToGui(void*) function, which posts an opaque void* pointer to the application's message loop. This function is normally called by threads other than the main thread, but can also be called by the GUI thread itself. The other part is the code in the application's message loop that recognizes this type of messages and passes them back to MEGAchat, by calling megaProcessMessage(void*) with that same pointer - this time in the context of the main (GUI) thread. All this is implemented in /src/base/gcm.h and /src/base/gcm.hpp. These files contain detiled documentation. An example of implementing this on Windows is: megaPostMessageToGui(void*) would do a PostMessage() with a user message type, and the void* as the lParam or wParam of the message, and in the event processing `switch` statement, there will be an entry for that message type, getting the void* pointer by casting the lParam or wParam of the message, and passing it to megaProcessMessage(void*).     
MEGAchat relies on libevent, running in its own dedicated thread, to monitor multiple sockets for raw I/O events, and to implement timers. It also relies on the higher-level I/O functionality of libevent such as DNS resolution and SSL sockets. A thin layer on top of libevent, called 'services' (/src/base/?services\*.\*)is implemented on top of libevent and the GCM to have simple, javascript-like async C++11 APIs for timers (src/base/timer.h), dns resolution (/src/base/services-dns.hpp), http client (/src/base/services-http.hpp). This layer was originally designed to have a lower-level component with plain C interface (cservices*.cpp/h files), so that the services can be used by several DLLs built with different compilers, and a high-level header-only C++11 layer that is the frontend and contains the public API - these are the .hpp files.    
All network libraries in MEGAchat (libws, libcurl) use libevent for network operation and timers (C libraries use libevent directly, C++ code uses the C++11 layer, i.e. timers.hpp). It is strongly recommended that the SDK user also does the same, although it is possible for example to have a dedicated worker thread blocking on a socket, and posting events to the GUI thread via the GCM.
The usage pattern is as follows: a callback is registered for a certain event (socket I/O event, timer, etc), and that callback is called by *the libevent thread* when the event occurs. If the event may propagate outside the library whose callback is called, and especially to the GUI, then, at some point, event processing must be marshalled to the GUI thread, using the GCM mechanism. However, if the event is internal and never propagates outside the library then it can be handled directly in the context of the libevent thread (provided that it never blocks it). This saves the performance cost of marshalling it to the GUI thread, and is recommended if the event occurs at a high frequency, e.g. an incoming data chunk event that only needs the data appended to a buffer. When the transfer is complete, a completion event can be marshalled on the GUI thread once per transfer, combining the advantages of both approaches.

## Logger ##
MEGAchat has an advanced logging facility that supports file and console logging with color, log file rotation, multiple log channels, each with individual log level. Log levels are configured at runtime (at startup), and not at compile time (i.e. not by disabling log macros). This allows a release-built app to enable full debug logging for any channels. Log channels are defined and default-configured in src/base/loggerChannelConfig.h. The file contains detailed documentation. For convenience, dedicated logging macros for each channel are usually defined in the code that uses it - see the XXX_LOG_DEBUG/WARN/ERROR macros in karereCommon.h for examples. The SDK user is free to create additional log channels if needed. A GUI log channel is already  defined. Log channel configuration can be overriden at runtime by the KRLOG environment variable. Its format is as follows:  
    ```KRLOG=<chan name>=<log level>,<chan name2>=<log level2>...```  
    Log levels are 'off', 'error', 'warn', 'info', 'verbose', 'debug', 'debugv'.    
    There is one special channel name - 'all'. Setting the log level of this channel sets the log levels of all channels. This allows for example to easily silence all channels except one (or few), by:  
    ```KRLOG=all=warn,mychannel=debug,myotherchannel=info```   
    The same channel can be configured multiple times, and only the last setting will be effective, which makes the above trick possible.  
MEGAchat requires the function karere::getAppDir() to be defined by the application at compile time, in order to know where to create the log file and start logging as early as possible, before main() is entered. If MEGAchat is built as a static lib, this is not a problem. In case of dynamic lib, this function has to be a weak symbol, so that MEGAchat itself can compile without the function implementation, and the implementation to be linked when the MEGAchat shared lib is loaded at app startup. Weak symbols are not really portable across compilers, and this may be a problem. However they are supported by both gcc and clang. If no weak symbols are supported, karer ehas to be built as static lib.

## Files of interest ##

  * The Promise lib in base/promise.h and example usage for example in /src/test-promise.cpp
  * The setTimeout() and setInterval() timer functions in /src/base/timers.hpp  
  * The marshallCall() function marshalls lambda calls from a worker thread to the GUI thread. Examples of use can be seen for example in /src/webrtcAdapter.h and in many different places. This mechanism should not be directly needed in high-level code that runs in the GUI thread.
  * The overall client structure in /src/chatClient.h;.cpp
  * The test app main.cpp file in examples/qt - shows how to implement megaPostMessageToGui(), how to start the 'services', and how to instantiate the MEGAchat client. Also shows how to implement the getAppDir() method, which is a weak symbol needed by the MEGAchat library in order to create the log file and start logging as early as possible, before main() is entered. 
  * The video module public interface in src/rtcModile/IRtcModule.h and related headers  

## Video renderer widgets (deprecated) ##
MEGAchat provides platform-specific video renderer widgets for Qt and iOS (probably will work also for MacOS with no or minimal changes).
These widgets are implemented as subclasses of standard widgets. Their code is in src/videoRenderer_xxx.cpp;mm;h. They can be used directly
in the platform's GUI designer by placing the corresponding standard widget and then selecting the VideoRenderer_xxx class name from the menu
to tell the GUI designer to use that subclass. You must include these files in your project, including the headers, to make the subclass visible
to the GUI designer.

## Test Applications (deprecated) ##
MEGAchat provides several example apps, which, among other things, show how the GCM (Gui Call Marshaller) is implemented on the API user's side.
It also shows how to use the video renderer widgets and the IVideoRenderer interface they implement to do video playback.

The test apps are:
* examples/qt - a Qt application.
* examples/objc - an iOS app.

## For application implementors ##
  * The rtctestapp above is the reference app. Build it, study it, experiment with it.  
Note that there is one critical and platform-dependent function that each app that uses MEGAchat must provide, which will be referenced as `megaPostMessageToGui()`, but it can have any name, provided that the signature is `extern "C" void(void*)`. This function is the heart of the message passing mechanism (called the Gui Call Marshaller, or GCM) that MEGAchat relies on. You must pass a pointer to this function to `services_init()`.  
For more details, read the comments in base/gcm.h, and for reference implementation study rtctestapp/main.cpp
  * IRtcModule, IEventHandler in /src/IRtcModule.h. These are used to initiate rtc calls and receive events.
  * IVideoRenderer in /src/IVideoRenderer.h is used to implement video playback in arbitrary GUI environments.
    Example implementation for Qt is in src/videoRenderer_Qt.h;.cpp.
    The example usage can be seen from the rtctestapp application.

## If Mega API calls are required ##
  * To integrate with the environment, a simple bridge class called MyMegaApi is implemented in /src/sdkApi.h.
    Example usage of it is in /src/chatClient.cpp. 


## Toolchains (deprecated) ##

* Android  
Install the android NDK, and create a standalone CLang toolchain, using the `make-standalone-toolchain.sh` scrpt included in the NDK, using following example commandline:  
`./android-ndk-r11b/build/tools/make-standalone-toolchain.sh --toolchain=arm-linux-androideabi-clang
   --install-dir=/home/user/android-dev/toolchain --stl=libc++ --platform=android-21`  
Then, you need to prepare a cross-compile environment for android. For this purpose, you need to use the
`/platforms/android/env-android.sh` shell script. Please read the Readme.md in the same directory on how to use that script.

*Notes on building webrtc with Android*    
Building of webrtc is supported only on Linux. This is a limitation of Google's webrtc/chromium build system.  
Although webrtc comes with its own copy of the NDK, we are going to use the standard one for building the MEGAchat code, and let webrtc build with its own NDK. Both compilers are binary compatible. Forcing webrtc to build with an external NDK will not work. For some operations, like assembly code transformations, a host compiler is used, which is the clang version that comes with webrtc. To use an external NDK, we would need to specify explicitly specify the `--sysroot` path of the external NDK, which also gets passed to the clang host compiler, causing errors. 

* iOS  
Building of webrtc and MEGAchat is supported only on MacOS. XCode is required.
Then, you need to prepare a cross-compile environment for android. For this purpose, you need to use the `/platforms/ios/env-ios.sh` shell script. Please read the Readme.md in the same directory no how to use that script.

* Windows
Microsoft Visual Studio 2015 or later (only C++ compiler), and the Cygwin environment are required. You need to setup the start shortcut/batfile of the Cygwin shell to run the vcvars32.bat with the cmd.exe shell inside the cygwin shell, in order to add the compiler and system library paths to the Cygwin shell. Building of MEGAchat, webrtc and dependencies will be done under the Cygwin shell.



