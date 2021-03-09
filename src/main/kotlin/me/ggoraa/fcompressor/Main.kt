package me.ggoraa.fcompressor

import com.jezhumble.javasysmon.JavaSysMon
import com.xenomachina.argparser.ArgParser
import kotlinx.coroutines.*
import me.ggoraa.fcompressor.args.ProgramArgs
import me.ggoraa.fcompressor.tools.clearScreen
import me.ggoraa.fcompressor.tools.getProcessedVideoLength
import me.tongfei.progressbar.ProgressBar
import org.apache.commons.io.IOUtils
import java.io.File
import kotlin.system.exitProcess

suspend fun main(args: Array<String>) = coroutineScope {
    // Splash
    clearScreen()
    println(
        "    ____________                                                    \n" +
                "   / ____/ ____/___  ____ ___  ____  ________  ______________  _____\n" +
                "  / /_  / /   / __ \\/ __ `__ \\/ __ \\/ ___/ _ \\/ ___/ ___/ __ \\/ ___/\n" +
                " / __/ / /___/ /_/ / / / / / / /_/ / /  /  __(__  |__  ) /_/ / /    \n" +
                "/_/    \\____/\\____/_/ /_/ /_/ .___/_/   \\___/____/____/\\____/_/     \n" +
                "                           /_/                                      \n"
    )

    println("A convenient tool for compressing a lot of videos at the same time with no effort\n\n")

    // All needed variables for ffmpeg
    val inputFilesFiltered: MutableList<String>
    val ffmpegCodec: String
    val ffmpegCrf: Int
    val ffmpegInputDir: String
    val ffmpegOutputDir: String
    val ffmpegForceOverride: Boolean

    // And some variables for progressbar calculation
    var videoLengthSum: Long = 0
    var videoLengthList = mutableListOf<Long>()
    var videoLengthProcessed: Long = 0

    ArgParser(args).parseInto(::ProgramArgs).run {
        println("Input: $inputDir")
        println("Output: $outputDir")
        println("Checking input and output directories...")

        // Check for if those directories actually exist
        if (!File(inputDir).isDirectory) {
            throw IllegalArgumentException("Input directory does not exist")
        }
        if (!File(outputDir).isDirectory) {
            throw IllegalArgumentException("Output directory does not exist")
        }

        println("Getting all video files...")

        val inputFilesRegex = """mp4$|avi$|mkv$""".toRegex() // Regex for filtering out videos

        inputFilesFiltered = mutableListOf<String>()
        val filesInInput = File(inputDir).list()

        for (i in filesInInput.indices) {
            if (inputFilesRegex.containsMatchIn(filesInInput[i])) {
                inputFilesFiltered.add(filesInInput[i])
            }
        }
        ffmpegCrf = crf
        ffmpegCodec = codec
        ffmpegInputDir = inputDir
        ffmpegOutputDir = outputDir
        ffmpegForceOverride = forceOverride
    }

    // Here we check if logs directory exists, if so, delete and create one, if not, just create one
    println("Checking for logs...")
    val logsDir = File("${System.getProperty("user.home")}/.fcompressor/logs")
    if (logsDir.exists()) {
        println("Logs exist, clearing...")
        logsDir.deleteRecursively()
        logsDir.mkdir()
    } else {
        println("Creating logs dir...")
        logsDir.mkdir()
    }

    // Here we find out the total length of all videos
    println("Getting complete video length...")
    for (i in inputFilesFiltered.indices) {
        val process = ProcessBuilder("ffprobe", "-show_entries", "format=duration", "-of", "default=noprint_wrappers=1:nokey=1", "$ffmpegInputDir/${inputFilesFiltered[i]}")
        val output = IOUtils.toString(process.start().inputStream, "UTF-8")

        val result = output.toString().toFloat().toLong()
        videoLengthSum += result
        videoLengthList.add(result)
        println(result)
        println("Done")
    }

    println("Starting the compression process...")
    val ffmpegProcessPids = mutableListOf<Long>()
    for (i in inputFilesFiltered.indices) {
        launch(Dispatchers.IO) {
            val logFile = File("${System.getProperty("user.home")}/.fcompressor/logs/latest$i.log")
            val process = ProcessBuilder(
                "ffmpeg",
                "-i",
                "$ffmpegInputDir/${inputFilesFiltered[i]}",
                "-vcodec",
                ffmpegCodec,
                "-crf",
                ffmpegCrf.toString(),
                if(ffmpegForceOverride) "-y" else "-n",
                "$ffmpegOutputDir/${inputFilesFiltered[i]}"
            )
                .redirectOutput(logFile)
                .redirectError(logFile)
                .start()
            ffmpegProcessPids.add(process.pid())
        }
    }

    val sysMon =
        JavaSysMon() // This thing is used for manipulating system processes, and is used to shut down every ffmpeg process when FCompressor shuts down.

    val closeChildThread: Thread = object : Thread() {
        override fun run() {
            println("FCompressor is shutting down...")
            for (i in ffmpegProcessPids) {
                sysMon.killProcess(i.toInt())
            }
        }
    }

    Runtime.getRuntime().addShutdownHook(closeChildThread)

    println("FCompressor is now compressing the videos, the program will automatically exit on finish")

    println("videoLengthSum: $videoLengthSum")
    println("videoLengthProcessed: $videoLengthProcessed")
    ProgressBar("Compressing...", videoLengthSum).use { progressbar ->  // name, initial max
        while (videoLengthProcessed != videoLengthSum) {
            progressbar.maxHint(videoLengthSum)
            videoLengthProcessed = getProcessedVideoLength(inputFilesFiltered.size - 1, videoLengthList)
            progressbar.stepTo(videoLengthProcessed)
        }
    }
}