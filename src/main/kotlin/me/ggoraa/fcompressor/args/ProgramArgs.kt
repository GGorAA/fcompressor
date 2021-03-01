package me.ggoraa.fcompressor.args

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

class ProgramArgs(parser: ArgParser) {
    val inputDir by parser.positional("INPUT",
        help = "Input folder, where all videos FCompressor needs to compress are stored"
    )

    val outputDir by parser.positional(
        "OUTPUT",
        help = "Output folder, where compressed videos will be stored"
    )

    val crf by parser.storing("--crf", help = "CRF value for ffmpeg. By default is 28. Long story short, it is how intense the compressing will be") {
        toInt()
    }.default(28)

    val codec by parser.storing(
        "--codec", help = "The output codec. By default is libx265. I recommend to not touch this setting if you don't know what you're doing. "
    ) {
        toString()
    }.default("libx265")
}