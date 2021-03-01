@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package me.ggoraa.fcompressor

import com.xenomachina.argparser.ArgParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.ggoraa.fcompressor.args.ProgramArgs
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import java.io.File

suspend fun main(args: Array<String>) = coroutineScope {
        // Splash
        println("    ____________                                                    \n" +
                "   / ____/ ____/___  ____ ___  ____  ________  ______________  _____\n" +
                "  / /_  / /   / __ \\/ __ `__ \\/ __ \\/ ___/ _ \\/ ___/ ___/ __ \\/ ___/\n" +
                " / __/ / /___/ /_/ / / / / / / /_/ / /  /  __(__  |__  ) /_/ / /    \n" +
                "/_/    \\____/\\____/_/ /_/ /_/ .___/_/   \\___/____/____/\\____/_/     \n" +
                "                           /_/                                      \n")

        println("A convenient tool for compressing a lot of videos at the same time with no effort\n\n")

        val ffmpeg = FFmpeg("ffmpeg")
        val ffprobe = FFprobe("ffprobe")

        val inputFilesFiltered: MutableList<String>
        val ffmpegCodec: String
        val ffmpegCrf: Int
        val ffmpegInputDir: String
        val ffmpegOutputDir: String

        ArgParser(args).parseInto(::ProgramArgs).run {
            println("Input: $inputDir")
            println("Output: $outputDir")
            println("Checking input and output directories...")

            // Check for if those directories actually exist
            if(!File(inputDir).isDirectory) {
                throw IllegalArgumentException("Input directory does not exist")
            }
            if(!File(outputDir).isDirectory) {
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
            val builder = FFmpegBuilder()
                .setInput("$ffmpegInputDir/${inputFilesFiltered[i]}")
                .overrideOutputFiles(false)
                .addOutput("$ffmpegOutputDir/${inputFilesFiltered[i]}")
                .setVideoCodec(ffmpegCodec)
                .setConstantRateFactor(ffmpegCrf.toDouble())
                .done()

            val executor = FFmpegExecutor(ffmpeg, ffprobe)
            launch(Dispatchers.IO) {
                executor.createJob(builder).run()
            }
        }
        println("FCompressor is now compressing the videos, the program will automatically exit on finish")
    }