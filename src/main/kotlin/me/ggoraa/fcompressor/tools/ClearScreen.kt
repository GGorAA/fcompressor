package me.ggoraa.fcompressor.tools

fun clearScreen() {
    print("\u001b[H\u001b[2J")
    System.out.flush()
}