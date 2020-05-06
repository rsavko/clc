package com.codeminders.clc;

import java.io.*;

public class CodeLinesCounter {
    private static final String LINE_COMMENT = "//";
    private static final String MULTILINE_START_COMMENT = "/*";
    private static final String MULTILINE_END_COMMENT = "*/";

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: java CodeLinesCounter file/folder");
            System.exit(1);
        }
        for (String filePath : args) {
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("File not exists: " + filePath);
            } else {
                prettyPrint(file, 0);
            }
        }
    }

    private static void prettyPrint(File file, int nestedLevel) throws Exception {
        if (file.isDirectory()) {

            File[] files = file.listFiles();
            if (files == null) {
                System.err.println("Failed to list files within folder " + file.getAbsolutePath());
            } else {
                System.out.println(String.format("%s:", file.getName()));
                for (File f : files) {
                    prettyPrint(f, nestedLevel + 1);
                }
            }
        } else {
            for (int i = 0; i < nestedLevel; i++) {
                System.out.print('\t');
            }
            System.out.println(String.format("%s: %d", file.getName(), countLines(new FileReader(file))));
        }
    }

    public static int countLines(Reader source) throws IOException {
        BufferedReader reader = new BufferedReader(source);
        int codeLinesCount = 0;
        boolean multilineCommentStarted = false;
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (isSkipLine(line) && !multilineCommentStarted) {
                continue; // skip empty/comment line
            }
            if (multilineCommentStarted) {
                if (isMultilineCommentEnded(line)) {
                    line = line.substring(line.indexOf(MULTILINE_END_COMMENT) + 2).trim();
                    multilineCommentStarted = false;
                    if (isSkipLine(line)) {
                        continue;
                    }
                } else {
                    continue;
                }
            }
            if (isMultilineCommentStarted(line)) {
                multilineCommentStarted = true;
            }
            if (isSourceCode(line)) {
                codeLinesCount++;
            }
        }
        return codeLinesCount;
    }

    private static boolean isSkipLine(String line) {
        return line.isBlank() || line.trim().startsWith(LINE_COMMENT);
    }

    private static boolean isMultilineCommentStarted(String line) {
        int index = line.indexOf(MULTILINE_START_COMMENT);
        if (index < 0) {
            return false;
        }
        int quoteStartIndex = line.indexOf("\"");
        if (quoteStartIndex != -1 && quoteStartIndex < index) {
            while (quoteStartIndex > -1) {
                line = line.substring(quoteStartIndex + 1);
                int quoteEndIndex = line.indexOf("\"");
                line = line.substring(quoteEndIndex + 1);
                quoteStartIndex = line.indexOf("\"");
            }
            return isMultilineCommentStarted(line);
        }
        return !isMultilineCommentEnded(line.substring(index + 2));
    }

    private static boolean isMultilineCommentEnded(String line) {
        int index = line.indexOf(MULTILINE_END_COMMENT);
        if (index < 0) {
            return false;
        } else {
            String subString = line.substring(index + 2).trim();
            if (isSkipLine(subString)) {
                return true;
            }
            return !isMultilineCommentStarted(subString);
        }
    }

    private static boolean isSourceCode(String line) {
        if (isSkipLine(line)) {
            return false;
        }
        line = line.trim();
        if (line.length() == 1) {
            return true;
        }
        int index = line.indexOf(MULTILINE_START_COMMENT);
        if (index != 0) {
            return true;
        } else {
            while (line.length() > 0) {
                line = line.substring(index + 2);
                int endCommentPosition = line.indexOf(MULTILINE_END_COMMENT);
                if (endCommentPosition < 0) {
                    return false;
                }
                if (endCommentPosition == line.length() - 2) {
                    return false;
                } else {
                    String subString = line.substring(endCommentPosition + 2).trim();
                    if (subString.isBlank() || subString.indexOf(LINE_COMMENT) == 0) {
                        return false;
                    } else {
                        if (subString.startsWith(MULTILINE_START_COMMENT)) {
                            line = subString;
                            continue;
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
