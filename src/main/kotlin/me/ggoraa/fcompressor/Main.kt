@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package me.ggoraa.fcompressor

import com.xenomachina.argparser.ArgParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.ggoraa.fcompressor.args.ProgramArgs
import me.ggoraa.fcompressor.tools.runCommand
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import java.io.File
import java.util.concurrent.TimeUnit

import net.bramp.ffmpeg.FFmpegUtils

import net.bramp.ffmpeg.progress.Progress

import net.bramp.ffmpeg.job.FFmpegJob
import net.bramp.ffmpeg.probe.FFmpegProbeResult
import net.bramp.ffmpeg.progress.ProgressListener
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

    val ffmpeg = FFmpeg("ffmpeg")
    val ffprobe = FFprobe("ffprobe")

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

    for (i in inputFilesFiltered.indices) {
        launch(Dispatchers.IO) {
            "ffmpeg -i $ffmpegInputDir/${inputFilesFiltered[i]} -vcodec $ffmpegCodec -crf $ffmpegCrf -y $ffmpegOutputDir/${inputFilesFiltered[i]}".runCommand()
        }
    }
    println("FCompressor is now compressing the videos, the program will automatically exit on finish")
}