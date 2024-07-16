#!/bin/sh
set -e
if test "$CONFIGURATION" = "Debug"; then :
  cd /Users/jerrywu/Dev/apple/FileTracer-ios/filetrace/filetrace-lib/build
  echo Build\ all\ projects
fi
if test "$CONFIGURATION" = "Release"; then :
  cd /Users/jerrywu/Dev/apple/FileTracer-ios/filetrace/filetrace-lib/build
  echo Build\ all\ projects
fi
if test "$CONFIGURATION" = "MinSizeRel"; then :
  cd /Users/jerrywu/Dev/apple/FileTracer-ios/filetrace/filetrace-lib/build
  echo Build\ all\ projects
fi
if test "$CONFIGURATION" = "RelWithDebInfo"; then :
  cd /Users/jerrywu/Dev/apple/FileTracer-ios/filetrace/filetrace-lib/build
  echo Build\ all\ projects
fi

