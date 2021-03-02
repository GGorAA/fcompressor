package me.ggoraa.fcompressor.ffmpeg

import me.ggoraa.fcompressor.tools.runFfmpegCommand
import java.io.File

fun runFfmpeg(input: String, output: String, codec: String, crf: String, workingDir: File? = null) {
    val process = ProcessBuilder("ffmpeg", "-i", input, "-vcodec", codec, "-crf", crf, "-y", output)
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start()
}