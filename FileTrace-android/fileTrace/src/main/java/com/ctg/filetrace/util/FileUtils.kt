package com.ctg.filetrace.util

import android.content.Context
import android.os.Environment
import android.os.StatFs
import android.text.TextUtils
import android.util.Base64
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * @Description: 文件操作工具类
 */
object FileUtils {

    private const val TAG = "FileUtils"

    /**
     * 从asset目录拷贝文件到目标目录
     *
     * @param context
     * @param assetFile asset下文件名，相对asset的路径
     * @param destFile 目标文件名，绝对路径全称
     */
    fun copyAssetFile(context: Context, assetFile: String, destFile: String): Boolean {
        val assetManager = context.assets
        try {
            assetManager.open(assetFile).use { input ->
                val outFile = File(destFile)
                val parent = outFile.parentFile
                if (!parent.exists()) {
                    val isMKDirs = parent.mkdirs()
                    Logger.d(TAG, "[copyAssets] isMKDirs: $isMKDirs")
                }
                FileOutputStream(outFile).use { out ->
                    copyFile(input, out)
                    out.flush()
                }
                return true
            }
        } catch (e: IOException) {
            Logger.e(TAG, e)
            return false
        }
    }

    private fun copyFile(inputStream: InputStream?, os: OutputStream?): Boolean {
        if (inputStream == null || os == null) {
            return false
        }
        try {
            val bs = ByteArray(2 * 1024 * 1024)
            var len: Int
            while (inputStream.read(bs).also { len = it } > 0) {
                os.write(bs, 0, len)
            }
            os.flush()
        } catch (e: Exception) {
            Logger.e(TAG, e.message.toString())
        }
        return true
    }

    /**
     * 剪切文件
     * @param oldPath
     * @param newPath
     * @return
     */
    fun moveFile(oldPath: String?, newPath: String?): Boolean {
        if (TextUtils.isEmpty(oldPath)) {
            return false
        }
        if (TextUtils.isEmpty(newPath)) {
            return false
        }
        val file = File(oldPath)
        val newFile = File(newPath)
        newFile.parentFile.apply {
            if (!exists()) {
                mkdirs()
            }
        }
        return file.renameTo(newFile)
    }

    fun delete(path: String) {
        if (TextUtils.isEmpty(path)) {
            Logger.e(TAG, "file delete:$path")
            return
        }
        Logger.e(TAG, "file delete:$path")
        val f = File(path)
        delete(f)
    }

    /**
     * 递归删除文件及文件夹
     */
    fun delete(file: File?) {
        if (file == null) {
            Logger.e(TAG, "file delete file error, file null")
            return
        }
        if (file.exists().not()) {
            return
        }
        if (file.isFile) {
            val result = file.delete()
            Logger.e(TAG, "file delete file:" + file.absolutePath + ",result:" + result)
            return
        }
        if (file.isDirectory) {
            Logger.e(TAG, "file delete file is directory:" + file.absolutePath)
            val childFiles = file.listFiles()
            if (childFiles == null || childFiles.isEmpty()) {
                val result = file.delete()
                Logger.e(
                    TAG,
                    "file delete directory:" + file.absolutePath + ",result:" + result
                )
                return
            }
            for (i in childFiles.indices) {
                delete(childFiles[i])
            }
            val result = file.delete()
            Logger.e(
                TAG, "file finally delete directory:" + file.absolutePath + ",result:"
                        + result
            )
        }
    }

    /**
     * 从asset文件加载string
     */
    fun loadAssetsString(context: Context, path: String?): String {
        val buf = StringBuilder()
        path?.let {
            context.assets.open(path).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, "UTF-8")).use { bufferedReader ->
                    var line: String?
                    while (bufferedReader.readLine().also { line = it } != null) {
                        buf.append(line)
                        buf.append("\n")
                    }
                }
            }
        }
        return buf.toString()
    }

    fun readString(filePath: String): String? {
        if (!File(filePath).exists()) {
            return null
        }
        val buf = StringBuilder()
        var inputStream: InputStream? = null
        var bufferedReader: BufferedReader? = null
        try {
            inputStream = FileInputStream(filePath)
            bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                buf.append(line)
            }
            return buf.toString()
        } catch (e: IOException) {
            Logger.e(TAG, e.message.toString())
        } finally {
            inputStream?.close()
            bufferedReader?.close()
        }
        return null
    }

    fun writeString(file: File, str: String, append: Boolean): Boolean {
        val targetFile = createNewFile(file.absolutePath, append) ?: return false
        FileWriter(targetFile, append).use {
            it.write(str)
            it.flush()
        }
        return true
    }

    fun deleteFile(file: File) {
        if (file != null && file.exists()) { // 文件是否存在
            if (file.isFile) { // 如果是文件
                file.delete()
            } else if (file.isDirectory) { // 如果是目录
                val subFiles = file.listFiles()
                val length = subFiles?.size ?: 0
                for (i in 0 until length) {
                    val subFile = subFiles!![i]
                    if (subFile.isDirectory) {
                        deleteFile(subFile) // 递归调用del方法删除子目录和子文件
                    }
                    subFile.delete()
                }
                file.delete()
            }
        }
    }

    /**
     * 解压[filePath]文件到[location]
     * @param filePath
     * @param location
     * @return 是否解压成功
     */
    fun unzip(filePath: String, location: String): Boolean {
        var result = false
        try {
            result = File(filePath).run {
                if (exists()) {
                    unzip(File(location))
                    true
                } else {
                    false
                }
            }
        } catch (e: IOException) {
            Logger.e(TAG, e)
        }
        return result
    }

    /**
     * 压缩文件数组，输出到指定文件
     *
     * @param srcFiles 源文件数组
     * @param destFile 输出文件
     * @return 压缩是否成功
     */
    fun zip(srcFiles: Array<File>, destFile: File): Boolean {
        if (srcFiles.isEmpty()) {
            return false
        }
        var result = false
        val buffer = ByteArray(4096)
        ZipOutputStream(BufferedOutputStream(FileOutputStream(destFile, false))).use {
            result = try {
                for (file in srcFiles) {
                    doZip(it, file, null, buffer)
                }
                it.flush()
                it.closeEntry()
                true
            } catch (e: IOException) {
                Logger.e(TAG, e)
                false
            }
        }
        return result
    }

    private fun doZip(zos: ZipOutputStream, file: File, parentDirName: String?, buffer: ByteArray) {
        if (!file.exists()) {
            throw FileNotFoundException("Target File is missing!")
        }
        val name =
                if (parentDirName.isNullOrEmpty()) file.name else parentDirName + File.separator + file.name
        if (file.isFile) {
            BufferedInputStream(FileInputStream(file)).use { inputStream ->
                try {
                    zos.putNextEntry(ZipEntry(name))
                    var len: Int
                    while (-1 != inputStream.read(buffer, 0, buffer.size).also { len = it }) {
                        zos.write(buffer, 0, len)
                    }
                } catch (e: IOException) {
                    Logger.e(TAG, e)
                }
            }
            return
        }
        if (file.isDirectory) {
            val subFiles = file.listFiles() ?: return
            for (element in subFiles) {
                doZip(zos, element, name, buffer)
            }
        }
    }

    fun exist(path: String?): Boolean {
        return if (path.isNullOrEmpty()) {
            false
        } else {
            File(path).exists()
        }
    }

    /**
     * The getExternalFilesDir storage status is OK.
     * 判断外部存储路径是否可用
     */
    fun isExternalStorageAvailable(): Boolean {
        var available = false
        try {
            available =
                    Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()
        } catch (e: Exception) {
            // do nothing
        }
        return available
    }

    /**
     * 判断文件是否可写入
     */
    fun isDirectoryWritable(directory: String?): Boolean {
        val file = File(directory)
        if (file.exists() && !file.isDirectory) {
            return false
        }
        if (!file.exists()) {
            val isMKDirs = file.mkdirs()
            Logger.d(
                    TAG,
                    "[isDirectoryWritable] isMKDirs: $isMKDirs"
            )
        }
        if (file.isDirectory) {
            try {
                return file.canWrite()
            } catch (e: Exception) {
                Logger.e(TAG, "check file can write error: ", e)
            }
        }
        return false
    }

    /**
     * The external storage empty size is enough
     * 判断外部存储大小是否足够
     */
    fun isExternalStorageSpaceEnough(fileSize: Long): Boolean {
        return try {
            val sdcard = Environment.getExternalStorageDirectory()
            val statFs = StatFs(sdcard.absolutePath)
            statFs.availableBytes > fileSize
        } catch (e: Exception) {
            // do nothing
            false
        }
    }

    /**
     * 判断是否文件夹
     * @param directory 文件夹路径
     */
    fun isDirectory(directory: String?): Boolean {
        if (directory.isNullOrEmpty()) {
            return false
        }
        val file = File(directory)
        if (!file.exists() || !file.isDirectory) {
            return false
        }
        return true
    }

    /**
     * 递归查找目标文件
     * @param dir 文件父路径
     * @param fileName 目标文件名
     */
    fun find(dir: String, fileName: String): List<String> {
        val subDirs = listOf(dir)
            .flatMap { File(it).listFiles().filter { it.isDirectory } }
            .toTypedArray()

        return listOf(File(dir), *subDirs)
            .flatMap { it.listFiles { _, name -> name == fileName }.toList() }
            .map { it.path }
    }


    /**
     * 查找文件路径下以suffix后缀结尾的第一个目标文件
     * @param dir 文件父路径
     * @param suffix 目标文件名
     */
    fun findFirstFileBySuffix(dir: String, suffix: String): String? {
        val dirFile = File(dir)
        if (dirFile.isDirectory.not() || suffix.isEmpty()) {
            return null
        }

        var dstPath = dirFile.listFiles().filter { it.isFile }.firstOrNull {
            val fileName = it.name
            val index = fileName.indexOf(".")
            if (index > -1) {
                val ext = fileName.substring(index + 1, fileName.length)
                ext == suffix
            } else {
                false
            }
        }?.path

        if (dstPath == null) {
            val subDirs = listOf(dir)
                .flatMap { File(it).listFiles().filter { it.isDirectory } }.map { it.path }
                .toTypedArray()
            subDirs.forEach { subDir ->
                dstPath = findFirstFileBySuffix(subDir, suffix)
                if (dstPath != null) {
                    return dstPath
                }
            }
        } else {
            return dstPath
        }

        return null
    }

    /**
     * 获取应用专属缓存目录
     * android 4.4及以上系统不需要申请SD卡读写权限
     * 因此也不用考虑6.0系统动态申请SD卡读写权限问题，切随应用被卸载后自动清空 不会污染用户存储空间
     *
     * @param context 上下文
     * @param type    文件夹类型 可以为空，为空则返回API得到的一级目录
     * @return 缓存文件夹 如果没有SD卡或SD卡有问题则返回内存缓存目录，否则优先返回SD卡缓存目录
     */
    fun getCacheDirectory(context: Context, type: String): String {
        var appCacheDir = getExternalCacheDirectory(context, type)
        if (appCacheDir == null) {
            appCacheDir = getInternalCacheDirectory(context, type)
        }
        if (appCacheDir == null) {
            Logger.e(TAG, "getCacheDirectory fail ,the reason is mobile phone unknown exception !")
            return ""
        }
        if (!appCacheDir.exists() && !appCacheDir.mkdirs()) {
            Logger.e(TAG, "getCacheDirectory fail ,the reason is make directory fail !")
            return ""
        }
        return appCacheDir.path
    }

    /**
     * 获取SD卡缓存目录
     *
     * @param context 上下文
     * @param type    文件夹类型 如果为空则返回 /storage/emulated/0/Android/data/app_package_name/cache
     * 否则返回对应类型的文件夹如Environment.DIRECTORY_PICTURES 对应的文件夹为 .../data/app_package_name/files/Pictures
     * [android.os.Environment.DIRECTORY_MUSIC],
     * [android.os.Environment.DIRECTORY_PODCASTS],
     * [android.os.Environment.DIRECTORY_RINGTONES],
     * [android.os.Environment.DIRECTORY_ALARMS],
     * [android.os.Environment.DIRECTORY_NOTIFICATIONS],
     * [android.os.Environment.DIRECTORY_PICTURES], or
     * [android.os.Environment.DIRECTORY_MOVIES].or 自定义文件夹名称
     * @return 缓存目录文件夹 或 null（无SD卡或SD卡挂载失败）
     */
    private fun getExternalCacheDirectory(context: Context, type: String): File? {
        var appCacheDir: File? = null
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            appCacheDir = if (TextUtils.isEmpty(type)) {
                context.externalCacheDir
            } else {
                context.getExternalFilesDir(type)
            }
            if (appCacheDir == null) { // 有些手机需要通过自定义目录
                appCacheDir = File(
                    Environment.getExternalStorageDirectory(),
                    "Android/data/" + context.packageName + "/cache/" + type)
            }
        }
        return appCacheDir
    }

    /**
     * 获取内存缓存目录
     *
     * @param type 子目录，可以为空，为空直接返回一级目录
     * @return 缓存目录文件夹 或 null（创建目录文件失败）
     * 注：该方法获取的目录是能供当前应用自己使用，外部应用没有读写权限，如 系统相机应用
     */
    private fun getInternalCacheDirectory(context: Context, type: String?): File? {
        return if (TextUtils.isEmpty(type)) {
            context.cacheDir // /data/data/app_package_name/cache
        } else {
            File(context.filesDir, type) // /data/data/app_package_name/files/type
        }
    }

    /**
     * 将文件转换为base64
     * @param filePath 文件路径
     */
    fun fileToBase64(filePath: String): String? {
        if (filePath.isEmpty() || !exist(filePath)) {
            return ""
        }
        val rawData: ByteArray = convertFileToByteArray(filePath) ?: return ""
        return Base64.encodeToString(rawData, Base64.DEFAULT)
    }

    /**
     * 将文件转为ByteArray
     * @param filePath 文件路径
     */
    fun convertFileToByteArray(filePath: String): ByteArray? {
        var rawData: ByteArray? = null
        if (filePath.isEmpty() || !exist(filePath)) {
            return rawData
        }
        FileInputStream(filePath).use {
            rawData = ByteArray(it.available())
            it.read(rawData)
        }
        return rawData
    }

    /**
     * 分割文件
     * @param filePath 文件路径
     * @param partLength 每一分段的长度，单位字节
     * @param partFileDir 每一分段存储的文件夹
     * @param fileSuffix 文件后缀
     */
    fun splitFile(filePath: String, partLength: Int, partFileDir: String, fileSuffix: String): List<FileInfo> {
        if (filePath.isEmpty() || !exist(filePath) || partFileDir.isEmpty()) {
            return listOf()
        }
        if (File(filePath).length() < partLength) {
            return listOf(FileInfo(filePath, 0))
        }
        var length: Int
        val data = ByteArray(partLength)
        val fileList = mutableListOf<FileInfo>()
        var totalLength = 0L
        try {
            FileInputStream(filePath).use { fis ->
                length = fis.read(data)
                while (length != -1) {
                    val filePartFilePath = partFileDir + File.separator + System.currentTimeMillis() + fileSuffix
                    val raf = RandomAccessFile(filePartFilePath, "rw")
                    raf.use {
                        it.write(data, 0, length)
                        totalLength += length
                        fileList.add(FileInfo(filePartFilePath, totalLength - length))
                    }
                    length = fis.read(data)
                }
            }
        } catch (ex: IOException) {
            Logger.e(TAG, "Spilt file failed", ex)
        }

        return fileList
    }

    /**
     * 获取文件大小
     * @return 文件长度，单位字节
     */
    fun getFileSize(filePath: String): Long {
        if (filePath.isEmpty() || !exist(filePath)) {
            return 0
        }
        return File(filePath).length()
    }

    data class FileInfo(
            /**
             * 文件路径
             */
            val filePath: String,
            /**
             * 文件offset，针对分段文件场景
             */
            val offset: Long)

    /**
     * 创建一个文件
     * @param filePath 文件路径
     * @return 创建好的文件
     */
    fun createNewFile(filePath: String, append: Boolean = true): File? {
        var tmpFile = File(filePath)
        if (append.not() && tmpFile.exists()) {
            tmpFile.delete()
        }
        val file = createParentDir(filePath)
        if (file.exists()) {
            return file
        }
        try {
            file.createNewFile()
        } catch (e: IOException) {
            Logger.e(TAG, "Create file failed", e)
            return null
        }
        return file
    }

    /**
     * 创建父目录文件夹
     *
     * @param filePath 文件路径
     * @return 返回[filePath]对应的文件
     */
    fun createParentDir(filePath: String): File {
        val file = File(filePath)
        file.parentFile?.let {
            if (it.exists().not()) {
                it.mkdirs()
            }
        }
        return file
    }

    /**
     * 递归删除空目录
     */
    fun deleteEmptyDirectory(dirFile: File) {
        if (dirFile.exists().not() or dirFile.isDirectory.not()) {
            return
        }
        // 先删除子目录
        dirFile.listFiles().forEach {
            deleteEmptyDirectory(it)
        }
        // 最后删除自己
        val list = dirFile.list()
        if (list.isEmpty()) {
            dirFile.delete()
        }
    }

    /**
     * 删除文件夹中所有[filter]返回`true`的文件
     *
     * @param file 目标文件夹
     * @param filter 过滤条件,满足filter的会被删除
     */
    fun deleteDirectoryFileWithFilter(file: File, filter: (File) -> Boolean) {
        val dirList = mutableListOf<File>()
        getDirectoryPathsWithFilter(file, filter).forEach {
            if (it.isFile) {
                delete(it)
            } else if (it.isDirectory) {
                dirList.add(it)
            }
        }
        // 删除目录
        dirList.forEach {
            deleteEmptyDirectory(it)
        }
    }

    /**
     * 获取文件夹中所有满足条件的文件
     * @param file 文件夹
     * @param filter 过滤条件,满足filter的会添加
     * @return 返回该文件夹中[filter]返回`true`文件的列表
     */
    fun getDirectoryFilesWithFilter(file: File, filter: (File) -> Boolean): List<File> {
        return file.walk().filter {
            it.isFile
        }.filter {
            filter(it)
        }.toList()
    }

    /**
     * 获取文件夹中所有满足条件的路径(包含文件和文件夹)
     * @see getDirectoryFilesWithFilter
     */
    fun getDirectoryPathsWithFilter(file: File, filter: (File) -> Boolean): List<File> {
        return file.walk().filter {
            filter(it)
        }.toList()
    }

    /**
     * @return The size, in bytes, of internal storage and external storage.
     */
    fun getPhoneStorageSize(): Long {
        val internalStatFs = StatFs(Environment.getRootDirectory().absolutePath)
        val externalStatFs = StatFs(Environment.getExternalStorageDirectory().absolutePath)
        val internalTotal =
            internalStatFs.blockCountLong * internalStatFs.blockSizeLong
        val externalTotal =
            externalStatFs.blockCountLong * externalStatFs.blockSizeLong
        return internalTotal + externalTotal
    }

    /**
     * 保存数据到文件
     *
     * @param filePath 文件路径
     * @param data 文件数据
     */
    fun saveFile(filePath: String, data: ByteArray) {
        val file = createParentDir(filePath)
        val fos = FileOutputStream(file)
        fos.use {
            it.write(data)
        }
    }

    /**
     * copy file from source to target
     * @param fromPath source file path
     * @param toPath target file path
     * @return 'true' if success else false
     */
    fun copyFile(fromPath: String, toPath: String): Boolean? {
        var result = false
        val targetFile = createNewFile(toPath) ?: return result
        val originFile = File(fromPath)
        if (!originFile.exists() || targetFile.isDirectory || originFile.isDirectory) {
            Logger.e(TAG, "copy failed, from $fromPath, to $toPath")
            return result
        }
        try {
            FileOutputStream(targetFile).use { out ->
                FileInputStream(originFile).use { input ->
                    result = copyFile(input, out)
                }
            }
        } catch (e: FileNotFoundException) {
            Logger.e(TAG, e)
        }
        return result
    }
}