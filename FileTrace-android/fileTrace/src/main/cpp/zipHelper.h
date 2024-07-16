//
// Created by JerryWu on 2023/2/23.
//
#pragma once
#ifndef ZIPHELPER_H
#define ZIPHELPER_H
#include <string>
#include "zlib/unzip.h"
#include "zlib/zip.h"

class ZipHelper
{
public:
    // 压缩文件/文件夹
    bool ZipDir(const std::string& sourcePath, const std::string& zipPath);
    ZipHelper();
    ~ZipHelper();
private:
    bool AddFileToZip(zipFile zf, const std::string& relative, const std::string& sourcePath);
    bool AddDirToZip(zipFile zf, const std::string& relative);
    //
    bool InnerWriteFileToZip(zipFile zf, const std::string& path);
};
#endif // ZIPHELPER_H

