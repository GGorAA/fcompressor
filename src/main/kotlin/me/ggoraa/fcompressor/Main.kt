@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package me.ggoraa.fcompressor

import com.jezhumble.javasysmon.JavaSysMon
import com.xenomachina.argparser.ArgParser
import kotlinx.coroutines.*
import me.ggoraa.fcompressor.args.ProgramArgs
import me.ggoraa.fcompressor.tools.fileTail
import java.io.File
import kotlin.system.exitProcess


suspend fun main(args: Array<String>) = coroutineScope {
    // Splash
    println(
            "    ____________                                                    \n" +
                "   / ____/ ____/___  ____ ___  ____  ________  ______________  _____\n" +
                "  / /_  / /   / __ \\/ __ `__ \\/ __ \\/ ___/ _ \\/ ___/ ___/ __ \\/ ___/\n" +
                " / __/ / /___/ /_/ / / / / / / /_/ / /  /  __(__  |__  ) /_/ / /    \n" +
                "/_/    \\____/\\____/_/ /_/ /_/ .___/_/   \\___/____/____/\\____/_/     \n" +
                "                           /_/                                      \n"
    )

    println("A convenient tool for compressing a lot of videos at the same time with no effort\n\n")

    val inputFilesFiltered: MutableList<String>
    val ffmpegCodec: String
    val ffmpegCrf: Int
    val ffmpegInputDir: String
    val ffmpegOutputDir: String

    ArgParser(args).parseInto(::ProgramArgs).run {
        if (acceptWarnings) {
            println("You accepted all warnings using a flag. I am NOT responsible if something goes wrong.")
        } else {
            println("All files in the output directory with the same name as in input wil be OVERRIDDEN. Are you sure to continue? (yes|no)")
            val consent = readLine()!!
            if (consent == "yes") {
                println("Starting...")
            } else if (consent == "no") {
                println("Okay, exiting...")
                exitProcess(0)
            } else {
                println("Exiting...")
                exitProcess(0)
            }
        }
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
    }
    println("Starting the compression process...")
    val ffmpegProcessPids = mutableListOf<Long>()
    for (i in inputFilesFiltered.indices) {
        launch(Dispatchers.IO) {
            println("Creating ffmpeg process...")
            println("Process count: $i")
            val logFile = File("${System.getProperty("user.home")}/.fcompressor/logs/latest$i.log")
            val process = ProcessBuilder(
                "ffmpeg",
                "-i",
                "$ffmpegInputDir/${inputFilesFiltered[i]}",
                "-vcodec",
                ffmpegCodec,
                "-crf",
                ffmpegCrf.toString(),
                "-y",
                "$ffmpegOutputDir/${inputFilesFiltered[i]}"
            )
                .redirectOutput(logFile)
                .redirectError(logFile)
                .start()
            println("Saving ffmpeg process PID...")
            ffmpegProcessPids.add(process.pid())
        }
    }
    val job = launch {
        val totalProcessedLength = 0
        Thread.sleep(3000)
        for (i in inputFilesFiltered.indices) {
            val lastLine = fileTail(File("${System.getProperty("user.home")}/.fcompressor/logs/latest$i.log"))
            val lastLineArray = lastLine?.split("=")?.toTypedArray()
            println(lastLineArray?.get(0))
        }
    }
    job.join()

    val sysMon = JavaSysMon() // This thing is used for manipulating system processes, and is used to shut down every ffmpeg process when FCompressor shuts down.

    val closeChildThread: Thread = object : Thread() {
        override fun run() {
            for (i in ffmpegProcessPids) {
                sysMon.killProcess(i.toInt())
            }
        }
    }

    Runtime.getRuntime().addShutdownHook(closeChildThread)

    println("FCompressor is now compressing the videos, the program will automatically exit on finish")
}