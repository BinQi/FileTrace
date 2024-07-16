# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.14.1)
project (filetrace-ios)
enable_testing()

enable_language(CXX)
enable_language(OBJC)

MESSAGE( STATUS "CMAKE_CXX_FLAGS: " ${CMAKE_CXX_FLAGS} )
MESSAGE( STATUS "CMAKE_OBJC_FLAGS: " ${CMAKE_OBJC_FLAGS} )

# Add some sanitary checks that the toolchain is actually working!
include(CheckCXXSymbolExists)
check_cxx_symbol_exists(kqueue sys/event.h HAVE_KQUEUE)
if(NOT HAVE_KQUEUE)
  message(STATUS "kqueue NOT found!")
else()
  message(STATUS "kqueue found!")
endif()

find_library(APPKIT_LIBRARY AppKit)
if (NOT APPKIT_LIBRARY)
  message(STATUS "AppKit.framework NOT found!")
else()
  message(STATUS "AppKit.framework found! ${APPKIT_LIBRARY}")
endif()

find_library(FOUNDATION_LIBRARY Foundation)
if (NOT FOUNDATION_LIBRARY)
  message(STATUS "Foundation.framework NOT found!")
else()
  message(STATUS "Foundation.framework found! ${FOUNDATION_LIBRARY}")
endif()

find_library(UIKIT_LIBRARY UIKit)
if (NOT UIKIT_LIBRARY)
  message(STATUS "UIKit.framework NOT found!")
else()
  message(STATUS "UIKit.framework found! ${UIKIT_LIBRARY}")
endif()

# Hook up XCTest for the supported plaforms (all but WatchOS)
if(NOT PLATFORM MATCHES ".*WATCHOS.*")
  # Use the standard find_package, broken between 3.14.0 and 3.14.4 at least for XCtest...
  find_package(XCTest)
  # Fallback: Try to find XCtest as host package via toochain macro (should always work)
  find_host_package(XCTest REQUIRED)
endif()

# Includes
include_directories(${filetrace-ios_SOURCE_DIR})

# Make sure try_compile() works
include(CheckTypeSize)
check_type_size(time_t SIZEOF_TIME_T)

set(USE_BUNDLED FALSE)
set(PDFHUMMUS_NO_TIFF true)
set(PDFHUMMUS_NO_TIFF true)
set(PDFHUMMUS_NO_DCT true)
set(PDFHUMMUS_NO_PNG true)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        ctgfiletrace-lib

        # Sets the library as a shared library.
        STATIC

        # Provides a relative path to your source file(s).
        fileTrace.cpp
        fileTrace.h
        )

# 去除未使用函数与变量
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -ffunction-sections -fdata-sections")
set(CMAKE_CXX_FLAGS "${CMAKE_C_FLAGS}")
# 设置去除未使用代码的链接flag、 strip参数
SET_TARGET_PROPERTIES(ctgfiletrace-lib PROPERTIES LINK_FLAGS "-Wl,--gc-sections,-s")

# Debug symbols set in XCode project
set_xcode_property(ctgfiletrace-lib GCC_GENERATE_DEBUGGING_SYMBOLS YES "All")

add_subdirectory(${CMAKE_SOURCE_DIR}/FreeType)
add_subdirectory(${CMAKE_SOURCE_DIR}/LibAesgm)
add_subdirectory(${CMAKE_SOURCE_DIR}/LibJpeg)
add_subdirectory(${CMAKE_SOURCE_DIR}/LibPng)
add_subdirectory(${CMAKE_SOURCE_DIR}/LibTiff)
add_subdirectory(${CMAKE_SOURCE_DIR}/PDFWriter)
#add_subdirectory(${CMAKE_SOURCE_DIR}/PDFWriterTesting)
add_subdirectory(${CMAKE_SOURCE_DIR}/ZLib)
#add_subdirectory(${CMAKE_SOURCE_DIR}/Zip)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.



# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        ctgfiletrace-lib

        # Links the target library to the log library
        # included in the NDK.
        PDFWriter)

message(STATUS "FFFAA${PDFWriter}")