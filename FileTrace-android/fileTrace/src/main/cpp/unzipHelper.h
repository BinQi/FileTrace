//
// Created by JerryWu on 2023/2/23.
//

#pragma once
#ifndef UNZIPHELPER_H
#define UNZIPHELPER_H
#include <string>
#include "zlib/unzip.h"
#include "zlib/zip.h"
class UnZipHelper
{
public:
    UnZipHelper();
    ~UnZipHelper();
    // 解压到文件夹
    bool UnzipDir(const std::string& unpackPath, const std::string& zipFilePath);
private:
    bool MakeDir(const std::string& path);
    int UnzipOneFile(unzFile uf, const unz_file_info& info, const std::string& path);
};

#endif // UNZIPHELPER_H

