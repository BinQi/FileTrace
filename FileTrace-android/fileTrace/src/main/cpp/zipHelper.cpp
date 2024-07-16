//
// Created by JerryWu on 2023/2/23.
//

#define _CRT_SECURE_NO_WARNINGS
#include "zipHelper.h"
#include "unzipHelper.h"
#include <boost/filesystem.hpp>
#include "logs.h"

ZipHelper::ZipHelper()
{

}

ZipHelper::~ZipHelper()
{

}

bool ZipHelper::AddFileToZip(zipFile zf, const std::string& relativeInZip, const std::string& sourcePath)
{
    FILE* fps{ NULL };
    int err{ ZIP_ERRNO };
    bool ret{ false };
    zip_fileinfo zi { 0 };
    memset(&zi, 0, sizeof(zip_fileinfo));
    std::string newFileName{ relativeInZip };
    err = zipOpenNewFileInZip(zf, newFileName.c_str(), &zi, NULL, 0, NULL, 0, NULL, Z_DEFLATED, Z_DEFAULT_COMPRESSION);
    if (ZIP_OK != err)
    {
        LOG_ERROR("add error!");
        return false;
    }
    ret = InnerWriteFileToZip(zf, sourcePath);
    err = zipCloseFileInZip(zf);
    return ret && (ZIP_OK == err);
}


bool ZipHelper::AddDirToZip(zipFile zf, const std::string& relative)
{
    zip_fileinfo zi{ 0 };
    memset(&zi, 0, sizeof(zip_fileinfo));
    int ret{ ZIP_ERRNO };
    std::string newRelative { relative + "/" };
    ret = zipOpenNewFileInZip(zf, newRelative.c_str(), &zi, NULL, 0, NULL, 0, NULL, Z_DEFLATED, Z_DEFAULT_COMPRESSION);
    if (ZIP_OK != ret)
    {
        return false;
    }
    ret = zipCloseFileInZip(zf);
    return ret == ZIP_OK;
}

bool ZipHelper::InnerWriteFileToZip(zipFile zf, const std::string& path)
{
    FILE* fps = fopen(path.c_str(), "rb");
    if (NULL == fps)
    {
        return false;
    }
    int err{ ZIP_ERRNO };
    do
    {
        enum {MAX_BUFFER = 40960 };
        char buf[MAX_BUFFER];
        size_t nRead{ 0 };
        while (!feof(fps))
        {
            nRead = fread(buf, 1, sizeof(buf), fps);
            err = zipWriteInFileInZip(zf, buf, nRead);
            if (ZIP_OK != err)
            {
                break;
            }
            if (ferror(fps))
            {
                err = ZIP_ERRNO;
                break;
            }
        }
    } while (0);
    fclose(fps);
    return ZIP_OK == err;
}

//目录或者文件压缩为zip文件
bool ZipHelper::ZipDir(const std::string& sourcePath, const std::string& zipPath)
{
    if (!boost::filesystem::exists(sourcePath))
    {
        return false;
    }
    int ret { ZIP_ERRNO };
    boost::filesystem::path home{sourcePath};
    zipFile zf = zipOpen(zipPath.c_str(), APPEND_STATUS_CREATE);
    if (boost::filesystem::is_directory(sourcePath))
    {
        // AddFileToZip(newZipFile, home.filename().string(), "");
        boost::filesystem::recursive_directory_iterator it;
        for (auto& it : boost::filesystem::recursive_directory_iterator(home))
        {
            boost::filesystem::path relative = boost::filesystem::relative(it.path(), home);
            if (boost::filesystem::is_directory(it.path()))
            {
                AddDirToZip(zf, relative.string());
            }
            else
            {
                AddFileToZip(zf, relative.string(), it.path().string());
            }
        }
    }
    else if (boost::filesystem::is_regular_file(sourcePath))
    {
        AddFileToZip(zf, home.filename().string(), home.string());
    }
    else
    {
        return false;
    }
    ret = zipClose(zf, NULL); //关闭zip文件
    return ZIP_OK == ret;
}

