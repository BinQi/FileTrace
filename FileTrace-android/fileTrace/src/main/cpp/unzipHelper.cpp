//
// Created by JerryWu on 2023/2/23.
//

#include "unzipHelper.h"
#include "unzipHelper.h"
#include "logs.h"
//#if __cplusplus >= 201703L
//#include <filesystem>
//#else
#include <boost/filesystem.hpp>
//#endif

#pragma comment(lib, "zlibwapi.lib")


#define BUF_MAX 65535
#define  MAX_PATH 260


UnZipHelper::UnZipHelper()
{

}

UnZipHelper::~UnZipHelper()
{

}
bool UnZipHelper::UnzipDir(const std::string& unpackPath, const std::string& zipFilePath)
{
//#if __cplusplus >= 201703L
//#else
//#endif
    const char* pwd{ NULL };
    unzFile uf = unzOpen(zipFilePath.c_str());
    unz_global_info64 gi;
    int err{ ZIP_ERRNO };
    if (uf == NULL)
    {
        LOG_ERROR("unzOpen %s error!", zipFilePath.c_str());
        return false;
    }
    if (!MakeDir(unpackPath))
    {
        return false;
    }
    boost::filesystem::path root{ unpackPath };
    int nRet = unzGoToFirstFile(uf);
    while (nRet == UNZ_OK)
    {
        char szRelative[MAX_PATH];
        unz_file_info unZipFileInfo;
        err = unzGetCurrentFileInfo(uf, &unZipFileInfo, szRelative, sizeof(szRelative), NULL, 0, NULL, 0);
        if (err != UNZ_OK)
        {
            LOG_ERROR("error %d with zipfile in unzGetCurrentFileInfo\n", err);
            break;
        }
        boost::filesystem::path relative(szRelative);
        int l = strlen(szRelative);
        if ('\\' == szRelative[l - 1] || '/' == szRelative[l - 1])
        {
            // 文件夹
            auto path = root / relative;
            if (!MakeDir(path.string()))
            {
                return false;
            }
            nRet = unzGoToNextFile(uf);
            continue;
        }
        // 文件
        auto path = root / relative;

        if (!MakeDir(path.parent_path().string()))
        {
            return false;
        }
        err = UnzipOneFile(uf, unZipFileInfo, path.string());
        if (UNZ_OK != err)
        {
            LOG_ERROR("UnzipOneFile %d", err);
        }
        nRet = unzGoToNextFile(uf);
    }
    bool res{ false };
    if (UNZ_OK == err && UNZ_END_OF_LIST_OF_FILE == nRet )
    {
        res = true;
    }
    err = unzClose(uf);
    if (UNZ_OK != err)
    {
        LOG_ERROR("unzClose %d", err);
    }
    return res;
}

int UnZipHelper::UnzipOneFile(unzFile uf, const unz_file_info& info, const std::string& path)
{
    int err = unzOpenCurrentFilePassword(uf, NULL);
    if (UNZ_OK != err)
    {
        return err;
    }
    FILE * pFile;
    fopen_s(&pFile, path.c_str(), "wb");
    if (NULL == pFile)
    {
        LOG_ERROR("fopen %s error!", path.c_str());
        return UNZ_ERRNO;
    }
    enum { MAX_BUFFER = 256 };
    char buffer[MAX_BUFFER];
    // 文件信息，后期加上
    size_t size = info.uncompressed_size;
    while (size > 0)
    {
        int nRead{ 0 };
        if (size > MAX_BUFFER)
        {
            nRead = MAX_BUFFER;
        }
        else
        {
            nRead = size;
        }
        int nReadBytes = unzReadCurrentFile(uf, buffer, nRead);
        int n = fwrite(buffer, nReadBytes, 1, pFile);
        if (size >= nReadBytes)
        {
            size -= nReadBytes;
        }
        else
        {
            LOG_ERROR("SIZE ERROR!");
            size = 0;
        }
    }
    fclose(pFile);
    err = unzCloseCurrentFile(uf);
    return err;
}

bool UnZipHelper::MakeDir(const std::string& path)
{
    if (boost::filesystem::exists(path))
    {
        if (!boost::filesystem::is_directory(path))
        {
            LOG_ERROR("%s is file", path.c_str());
            return false;
        }
        // LOG_INFO("%s dir is exists!", path.c_str());
        return true;
    }
    if (!boost::filesystem::create_directory(path))
    {
        LOG_ERROR("create dir %s error!", path.c_str());
        return false;
    }
    return true;
}