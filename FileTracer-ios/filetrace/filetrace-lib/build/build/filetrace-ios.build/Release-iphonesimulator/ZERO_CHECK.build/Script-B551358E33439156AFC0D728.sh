#!/bin/sh
set -e
if test "$CONFIGURATION" = "Debug"; then :
  cd /Users/jerrywu/Dev/apple/FileTracer-ios/filetrace/filetrace-lib/build
  make -f /Users/jerrywu/Dev/apple/FileTracer-ios/filetrace/filetrace-lib/build/CMakeScripts/ReRunCMake.make
fi
if test "$CONFIGURATION" = "Release"; then :
  cd /Users/jerrywu/Dev/apple/FileTracer-ios/filetrace/filetrace-lib/build
  make -f /Users/jerrywu/Dev/apple/FileTracer-ios/filetrace/filetrace-lib/build/CMakeScripts/ReRunCMake.make
fi
if test "$CONFIGURATION" = "MinSizeRel"; then :
  cd /Users/jerrywu/Dev/apple/FileTracer-ios/filetrace/filetrace-lib/build
  make -f /Users/jerrywu/Dev/apple/FileTracer-ios/filetrace/filetrace-lib/build/CMakeScripts/ReRunCMake.make
fi
if test "$CONFIGURATION" = "RelWithDebInfo"; then :
  cd /Users/jerrywu/Dev/apple/FileTracer-ios/filetrace/filetrace-lib/build
  make -f /Users/jerrywu/Dev/apple/FileTracer-ios/filetrace/filetrace-lib/build/CMakeScripts/ReRunCMake.make
fi

