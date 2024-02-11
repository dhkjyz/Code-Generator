package com.mango.cli.example;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.awt.*;
import java.awt.image.BufferedImage;
// some exports omitted for the sake of brevity

// @Command annotation tells picocli to process this class as a command line application
//mixinStandardHelpOptions = true tells picocli to generate --help and --version options
@Command(name = "ASCIIArt", version = "ASCIIArt 1.0", mixinStandardHelpOptions = true)
public class ASCIIArt implements Runnable {
    // @Option annotation tells picocli to process this field as a command line option
    @Option(names = { "-s", "--font-size" }, description = "Font size")
    int fontSize = 19;

    @Parameters(paramLabel = "<word>", defaultValue = "Hello, picocli",
            description = "Words to be translated into ASCII art.")
    private String[] words = { "Hello,", "picocli" };

    @Override
    public void run() {
        System.out.println("fontSize: " + fontSize);
        System.out.println(
                "words: " + String.join(", ", words)
        );
    }

    public static void main(String[] args) {
        //在main方法中，通过Commandline对象的execute方法，执行命令行程序，剩下的工作由picocli完成
        int exitCode = new CommandLine(new ASCIIArt()).execute(args);
        System.exit(exitCode);
    }
}