package me.ggoraa.fcompressor.tools

import java.io.IOException

import java.io.File
import java.io.FileNotFoundException
import java.io.RandomAccessFile


fun fileTail(file: File?, lines: Int = 1): String? {
    var fileHandler: RandomAccessFile? = null
    return try {
        fileHandler = RandomAccessFile(file, "r")
        val fileLength = fileHandler.length() - 1
        val sb = StringBuilder()
        var line = 0
        for (filePointer in fileLength downTo -1 + 1) {
            fileHandler.seek(filePointer)
            val readByte = fileHandler.readByte().toInt()
            if (readByte == 0xA) {
                if (filePointer < fileLength) {
                    line += 1
                }
            } else if (readByte == 0xD) {
                if (filePointer < fileLength - 1) {
                    line += 1
                }
            }
            if (line >= lines) {
                break
            }
            sb.append(readByte.toChar())
        }
        sb.reverse().toString()
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        null
    } catch (e: IOException) {
        e.printStackTrace()
        null
    } finally {
        if (fileHandler != null) try {
            fileHandler.close()
        } catch (e: IOException) {
        }
    }
}