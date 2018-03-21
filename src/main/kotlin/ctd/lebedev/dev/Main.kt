package ctd.lebedev.dev

import java.lang.System.exit

fun showUsage() {
    println("usage: <hw index> <input file> <ouput file>")
    exit(0)
}

fun main(args: Array<String>) {
    try {
        if (args.size < 3) showUsage()
        val hwIndex = args[0].toInt()
        val inputFilePath = args[1]
        val outputFilePath = args[2]

        val supported = listOf(1, 2, 3, 5)
        if (hwIndex !in supported) showUsage()

        when (hwIndex) {
            1 -> hw1(inputFilePath, outputFilePath)
            2 -> hw2(inputFilePath, outputFilePath)
            3 -> hw3(inputFilePath, outputFilePath)
            5 -> hw5(inputFilePath, outputFilePath)
            else -> println("hw $hwIndex is not implemented")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        showUsage()
    }
}

