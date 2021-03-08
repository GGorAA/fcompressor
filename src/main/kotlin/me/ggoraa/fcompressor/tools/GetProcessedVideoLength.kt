package me.ggoraa.fcompressor.tools

import java.io.File

fun getProcessedVideoLength(videoCount: Int, processedList: List<Long>): Long {
    var endValue = 0L
    for (i in 0..videoCount) {
        val fileTail = fileTail(File("${System.getProperty("user.home")}/.fcompressor/logs/latest$i.log"))

        // This is the string we will parse:
        // frame=  128 fps=6.9 q=36.0 size=       0kB time=00:00:04.38 bitrate=   0.1kbits/s speed=0.238x
        try {
            // We will check first if ffmpeg has finished
            if ("^encoded".toRegex().matches(fileTail!!)) {
                endValue = processedList[i]
            } else { // if not, do usual stuff
                val fileTailFiltered =
                    fileTail.split("=", ":") // Here we split the string above into different pieces
                val parsedSeconds =
                    fileTailFiltered[7].split(".") // Parse seconds and milliseconds from the string above
                val time =
                    (fileTailFiltered[5].toLong() * 3600) + (fileTailFiltered[6].toLong() * 60) + parsedSeconds[0].toLong()
                endValue += time
            }
        } catch (e: IndexOutOfBoundsException) {
            println("Failed to get processed video length")
        }
    }
    return endValue
}