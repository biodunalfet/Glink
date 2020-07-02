#!/usr/bin/env kscript


import java.util.concurrent.TimeUnit
import java.io.IOException
import java.io.File
import java.util.regex.Pattern

println("----- Running Glink -----")
val unrefinedGitStatusOutput = "git status --porcelain".runCommand()

val refined = unrefinedGitStatusOutput!!
        .lines()
        .toMutableList()
        .map { it.trim() }
        .filter {
            it.length > 2 && it.split(Pattern.compile("\\s+")).first()
                    .contains(Pattern.compile("[AM]").toRegex()) }
        .map { it.substring(1).trim() }

var ktlintOutput = ""

for (r in refined) {
    if (r.takeLast(3).equals(".kt", true)) {
        print("ktlint $r".runCommand())
    }
}


fun String.runCommand(workingDir: File = File(".")): String? {
    try {
        val parts = this.split("\\s".toRegex())
        val proc = ProcessBuilder(*parts.toTypedArray())
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

        proc.waitFor(60, TimeUnit.SECONDS)
        return proc.inputStream.bufferedReader().readText()
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
}