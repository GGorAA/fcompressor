package me.ggoraa.fcompressor.tools

import java.io.File

fun String.runFfmpegCommand(workingDir: File? = null) {
    ProcessBuilder(this.split(" "))
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start()
}