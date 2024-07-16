# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

#set(USING_UNBUNDLED_ZLIB true)
#set(USING_UNBUNDLED_Freetype true)
#set(USING_UNBUNDLED_aesgm true)
#set(USING_UNBUNDLED_JPEG true)
#set(USING_UNBUNDLED_TIFF true)
#set(USING_UNBUNDLED_PNG true)
set(USE_BUNDLED FALSE)
set(PDFHUMMUS_NO_TIFF true)
set(PDFHUMMUS_NO_TIFF true)
set(PDFHUMMUS_NO_DCT true)
set(PDFHUMMUS_NO_PNG true)

add_library( # Sets the name of the library.
        ctgfiletrace-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        filetrace-ctg.cpp
        fileTrace.cpp
#        zipHelper.cpp
#        unzipHelper.cpp

        fileTrace.h
#        zipHelper.h
#        unzipHelper.h
#        logs.h
        )

# 去除未使用函数与变量
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -ffunction-sections -fdata-sections")
set(CMAKE_CXX_FLAGS "${CMAKE_C_FLAGS}")
# 设置去除未使用代码的链接flag、 strip参数
SET_TARGET_PROPERTIES(ctgfiletrace-lib PROPERTIES LINK_FLAGS "-Wl,--gc-sections,-s")

add_subdirectory(${CMAKE_SOURCE_DIR}/FreeType)
add_subdirectory(${CMAKE_SOURCE_DIR}/LibAesgm)
add_subdirectory(${CMAKE_SOURCE_DIR}/LibJpeg)
add_subdirectory(${CMAKE_SOURCE_DIR}/LibPng)
add_subdirectory(${CMAKE_SOURCE_DIR}/LibTiff)
add_subdirectory(${CMAKE_SOURCE_DIR}/PDFWriter)
#add_subdirectory(${CMAKE_SOURCE_DIR}/PDFWriterTesting)
add_subdirectory(${CMAKE_SOURCE_DIR}/ZLib)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

#find_library( # Sets the name of the path variable.
#        log-lib
#
#        # Specifies the name of the NDK library that
#        # you want CMake to locate.
#        log)

#SET (ENV{BOOST_ROOT} "/usr/local/Cellar/boost/1.81.0_1")
#SET (ENV{BOOST_INCLUDEDIR} "$ENV{BOOST_ROOT}/include")
#SET (ENV{BOOST_LIBRARYDIR} "$ENV{BOOST_ROOT}/lib")
#
#find_package(Boost REQUIRED COMPONENTS filesystem)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        ctgfiletrace-lib

        # Links the target library to the log library
        # included in the NDK.
#        ${log-lib}
#        Boost::filesystem
        PDFWriter)

message(STATUS "FFFAAA$ENV{BOOST_INCLUDEDIR}")