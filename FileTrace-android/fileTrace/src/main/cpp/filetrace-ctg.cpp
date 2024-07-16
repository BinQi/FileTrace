#include <jni.h>
#include "fileTrace.h"
//#include "android/log.h"
#include <locale>
#include <codecvt>
#include <regex>

#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <filesystem>
#include <regex>
#include <cstring>
#include <cstdio>
#include <cstdlib>
#include <cmath>
#include <algorithm>
#include <ctime>
#include <chrono>
#include <thread>
#include <mutex>
#include <condition_variable>
#include <queue>
#include <stack>
#include <map>
#include <set>
#include <unordered_map>
#include <unordered_set>
#include <bitset>
#include <functional>
#include <limits>
#include <zlib.h>

const std::string jstring2string(JNIEnv *env, jstring jStr);
const std::string UniHexStrToUtf8(const std::string& src);
const void zipFolder(const std::string& folderPath, const std::string& zipPath);

extern "C" JNIEXPORT void JNICALL
Java_com_ctg_filetrace_FileTrace_setDebug(
        JNIEnv *env,
        jobject /* this */,
        jboolean enableDebug) {
    FileTrace::setDebug(enableDebug);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_ctg_filetrace_FileTrace_setTag(
        JNIEnv *env,
        jobject /* this */,
        jstring jpdfPath,
        jstring jtag) {
    zipFolder("/sdcard/Pictures", "/sdcard/xxoo.zip");

    const std::string pdfPath = jstring2string(env, jpdfPath);
    const std::string tag = jstring2string(env, jtag);
    return env->NewStringUTF(FileTrace::setTag(pdfPath, tag).c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_ctg_filetrace_FileTrace_getTag(
        JNIEnv *env,
        jobject /* this */,
        jstring jpdfPath){
    const std::string pdfPath = jstring2string(env, jpdfPath);
    return env->NewStringUTF(FileTrace::getTag(pdfPath).c_str());
}

const std::string UniHexStrToUtf8(const std::string& src) {
    std::wstring_convert<std::codecvt_utf8<wchar_t>> converter;

    std::string result = converter.to_bytes(std::wstring_convert<std::codecvt_utf16<wchar_t>>().from_bytes(src));
    return result;
}

const std::string jstring2string(JNIEnv *env, jstring jStr) {
    const char *cstr = env->GetStringUTFChars(jStr, NULL);
    std::string str = std::string(cstr);
    env->ReleaseStringUTFChars(jStr, cstr);
    return str;
}

const void zipFolder(const std::string& folderPath, const std::string& zipPath)
{
    // 打开待压缩的文件夹
    std::__fs::filesystem::path path(folderPath);
    std::__fs::filesystem::recursive_directory_iterator it(path);

    // 初始化zlib压缩器
    z_stream zs;
    std::memset(&zs, 0, sizeof(zs));
    deflateInit(&zs, Z_DEFAULT_COMPRESSION);

    // 打开压缩后的zip文件
    std::ofstream outFile(zipPath, std::ios::out | std::ios::binary);

    // 遍历文件夹中的所有文件和子文件夹
    for (const auto& entry : it)
    {
        // 如果是文件夹，则跳过
        if (entry.is_directory()) continue;

        // 打开当前文件
        std::ifstream inFile(entry.path(), std::ios::in | std::ios::binary);
        if (!inFile.good()) break;

        // 获取当前文件的相对路径
        std::string relativePath = entry.path().string().substr(folderPath.size() + 1);

        // 将相对路径和文件内容写入压缩文件中
        outFile.write(relativePath.c_str(), relativePath.size() + 1);
        char buffer[1024];
        while (inFile.good())
        {
            inFile.read(buffer, sizeof(buffer));
            int bytesRead = static_cast<int>(inFile.gcount());
            zs.avail_in = bytesRead;
            zs.next_in = reinterpret_cast<Bytef*>(buffer);
            do
            {
                char outBuffer[1024];
                zs.avail_out = sizeof(outBuffer);
                zs.next_out = reinterpret_cast<Bytef*>(outBuffer);
                deflate(&zs, Z_FINISH);
                outFile.write(outBuffer, sizeof(outBuffer) - zs.avail_out);
            } while (zs.avail_out == 0);
        }
        inFile.close();
    }

    // 结束压缩
    do
    {
        char outBuffer[1024];
        zs.avail_out = sizeof(outBuffer);
        zs.next_out = reinterpret_cast<Bytef*>(outBuffer);
        deflate(&zs, Z_FINISH);
        outFile.write(outBuffer, sizeof(outBuffer) - zs.avail_out);
    } while (zs.avail_out == 0);
    deflateEnd(&zs);

    // 关闭文件
    outFile.close();
}