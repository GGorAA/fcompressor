import com.xenomachina.argparser.ArgParser
import me.ggoraa.fcompressor.args.ProgramArgs
import java.io.File

fun main(args: Array<String>) {
    // Splash
    println("    ____________                                                    \n" +
            "   / ____/ ____/___  ____ ___  ____  ________  ______________  _____\n" +
            "  / /_  / /   / __ \\/ __ `__ \\/ __ \\/ ___/ _ \\/ ___/ ___/ __ \\/ ___/\n" +
            " / __/ / /___/ /_/ / / / / / / /_/ / /  /  __(__  |__  ) /_/ / /    \n" +
            "/_/    \\____/\\____/_/ /_/ /_/ .___/_/   \\___/____/____/\\____/_/     \n" +
            "                           /_/                                      \n")

    println("A convenient tool for compressing a lot of videos at the same time with no effort\n\n")
    ArgParser(args).parseInto(::ProgramArgs).run {
        println("Input: $inputDir")
        println("Output: $outputDir")
        println("Getting all video files...")

        val inputFilesRegex = """mp4$|avi$|mkv$""".toRegex() // Regex for filtering out videos

        val inputFilesFiltered = mutableListOf<String>()
        val filesInInput = File(inputDir).list()

        for (i in filesInInput.indices){
            if(inputFilesRegex.containsMatchIn(filesInInput[i])) {
                inputFilesFiltered.add(filesInInput[i])
            }
        }
        println(inputFilesFiltered)

    }
}