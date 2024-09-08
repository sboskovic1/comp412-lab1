package comp412lab1;

import java.io.*;
import java.util.*;

/** 
 * The scanner should be incremental. That is, the parser calls the scanner each time it needs the next word. The scanner, when called, looks at successive characters in the input stream, finds the next word, updates its state (see note 1) and returns the word.  
 * 
 * Read the input
 * Recognize all of the words
 * Producing tokens
 * Printing error messages for lexical errors and spelling mistakes
 */
public class Scanner {

    final int EOF = 0;
    final int EOL = 1;
    final int INTO = 2;
    final int LOAD = 3;
    final int LOADI = 4;
    final int STORE = 5;
    final int ADD = 6;
    final int SUB = 7;
    final int MULT = 8;
    final int LSHIFT = 9;
    final int RSHIFT = 10;
    final int OUTPUT = 11;
    final int NOP = 12;
    final int REGISTER = 13;
    final int COMMA = 14;
    final int SCAN_ERROR = 15;

    public Map<String, Integer> words;
    public BufferedReader input;
    private boolean newLine;
    private int lineLength;
    private int lineIndex;
    private char[] line;
    private int lastWord;

    public Scanner(String filepath) {
        words = new HashMap<String, Integer>();
        words.put("r", REGISTER);
        words.put("=>", INTO);
        words.put("load", LOAD);
        words.put("loadi", LOADI);
        words.put("store", STORE);
        words.put("add", ADD);
        words.put("sub", SUB);
        words.put("mult", MULT);
        words.put("lshift", LSHIFT);
        words.put("rshift", RSHIFT);
        words.put("output", OUTPUT);
        words.put("nop", NOP);
        words.put(",", COMMA);

        newLine = true;
        lineLength = 0;
        lineIndex = 0;
        lastWord = -1;

        try {
            input = new BufferedReader(new FileReader(filepath));
        } catch (IOException e) {
            System.out.println("File not found");
            throw new RuntimeException();
        }
    }

    public int nextWord() {
        switch (line[lineIndex]) {
            case 'l':
                if (lineIndex + 3 >= lineLength) {
                    return lineError();
                }
                lineIndex++;
                if (line[lineIndex] == 'o') {
                    lineIndex++;
                    if (line[lineIndex] == 'a') {
                        lineIndex++;
                        if (line[lineIndex] == 'd') {
                            lineIndex++;
                            if (lineIndex < lineLength && line[lineIndex] == 'I') {
                                lineIndex++;
                                return LOADI;
                            } else {
                            return LOAD;
                            }
                        }
                    }
                } else if (checkShift() == 1) {
                    return LSHIFT;
                }
                return SCAN_ERROR;
            case 's':
                lineIndex++;
                if (lineIndex + 2 >= lineLength) {
                    return lineError();
                }
                if (line[lineIndex] == 't' && lineIndex + 3 < lineLength) {
                    lineIndex++;
                    if (line[lineIndex] == 'o') {
                        lineIndex++;
                        if (line[lineIndex] == 'r') {
                            lineIndex++;
                            if (line[lineIndex] == 'e') {
                                lineIndex++;
                                return STORE;
                            }
                        }
                    }
                } else if (line[lineIndex] == 'u') {
                    lineIndex++;
                    if (line[lineIndex] == 'b') {
                        lineIndex++;
                        return SUB;
                    }
                }
                return SCAN_ERROR;
            case 'r':
                if (lineIndex + 1 >= lineLength) {
                    return lineError();
                }
                lineIndex++;
                if (Character.isDigit(line[lineIndex])) {
                    return REGISTER;
                }
                if (checkShift() == 1) {
                    return RSHIFT;
                }
                return SCAN_ERROR;
            case 'm':
                if (lineIndex + 3 >= lineLength) {
                    return lineError();
                }
                lineIndex++;
                if (line[lineIndex] == 'u') {
                    lineIndex++;
                    if (line[lineIndex] == 'l') {
                        lineIndex++;
                        if (line[lineIndex] == 't') {
                            lineIndex++;
                            return MULT;
                        }
                    }
                }
                return SCAN_ERROR;
            case 'a':
                if (lineIndex + 2 >= lineLength) {
                    return lineError();
                }
                lineIndex++;
                if (line[lineIndex] == 'd') {
                    lineIndex++;
                    if (line[lineIndex] == 'd') {
                        lineIndex++;
                        return ADD;
                    }
                }
                return SCAN_ERROR;
            case '=':
                if (lineIndex + 1 >= lineLength) {
                    return lineError();
                }
                lineIndex++;
                if (line[lineIndex] == '>') {
                    lineIndex++;
                    return INTO;
                }
                return SCAN_ERROR;
            case 'o':
                if (lineIndex + 4 >= lineLength) {
                    return lineError();
                }
                lineIndex++;
                if (line[lineIndex] == 'u') {
                    lineIndex++;
                    if (line[lineIndex] == 't') {
                        lineIndex++;
                        if (line[lineIndex] == 'p') {
                            lineIndex++;
                            if (line[lineIndex] == 'u') {
                                lineIndex++;
                                if (line[lineIndex] == 't') {
                                    lineIndex++;
                                    return OUTPUT;
                                }
                            }
                        }
                    }
                }
                return SCAN_ERROR;
            case 'n':
                if (lineIndex + 2 >= lineLength) {
                    return lineError();
                }
                lineIndex++;
                if (line[lineIndex] == 'o') {
                    lineIndex++;
                    if (line[lineIndex] == 'p') {
                        lineIndex++;
                        return NOP;
                    }
                }
                return SCAN_ERROR;
        }
        return SCAN_ERROR;
    }

    public int nextConstant() {
        int constant = 0;
        try {
            constant = line[lineIndex] - '0';
            lineIndex++;
            while (lineIndex + 1 < lineLength && line[lineIndex] != ' ') {
                if (!Character.isDigit(line[lineIndex]) && line[lineIndex] != ' ') {
                    System.out.println("ERROR: Expected a number, found " + line[lineIndex]);
                    return 15;
                }
                constant = constant * 10 + line[lineIndex] - '0';
                lineIndex++;
            }
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Expected a number, found " + line[lineIndex]);
            return -1;
        }
        return constant * -1;

    }

    public int nextSymbol() {
        if (newLine) {
            try {
                String lineStr = input.readLine();
                if (lineStr == null) {
                    return EOF;
                } else {
                    line = lineStr.toCharArray();
                }
                lineLength = line.length;
                lineIndex = 0;
                newLine = false;
            } catch (IOException e) {
                System.out.println("Error reading file");
                throw new RuntimeException();
            }
        }
        if (lineIndex >= lineLength) {
            newLine = true;
            lastWord = -1;
            return EOL;
        }
        int word = -1;
        if (lineIndex + 1 < lineLength && line[lineIndex] == '/' && line[lineIndex + 1] == '/') {
            newLine = true;
            lastWord = -1;
            return EOL;
        }
        if (lastWord == LOADI || lastWord == REGISTER) {
            word = nextConstant();
        } else {
            if (line[lineIndex] == ' ') {
                lineIndex++;
                while (lineIndex < lineLength && line[lineIndex] == ' ') {
                    lineIndex++;
                }
            } else if (lineIndex != 0){
                System.out.println("ERROR: Space Expected, found " + line[lineIndex]);
                while (lineIndex < lineLength && line[lineIndex] != ' ') {
                    lineIndex++;
                }
                return SCAN_ERROR;
            }
            word = nextWord();
        }
        lastWord = word;
        if (word == SCAN_ERROR) {
            while (lineIndex < lineLength && line[lineIndex] != ' ') {
                lineIndex++;
            }
        }
        return word;
    }

    private int lineError() {
        lineIndex = 0;
        newLine = true;
        return SCAN_ERROR;        
    }

    private int checkShift() {
        if (lineIndex + 4 >= lineLength) {
            return lineError();
        }
        if (line[lineIndex] == 's') {
            lineIndex++;
            if (line[lineIndex] == 'h') {
                lineIndex++;
                if (line[lineIndex] == 'i') {
                    lineIndex++;
                    if (line[lineIndex] == 'f') {
                        lineIndex++;
                        if (line[lineIndex] == 't') {
                            lineIndex++;
                            return 1;
                        }
                    }
                }
            }
        }
        return 2;
    }

}