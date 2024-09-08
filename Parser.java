/**
 * Identify correct and incorrect programs
 * print messages for syntax errors
 * build the IR?
 */
package comp412lab1;

import comp412lab1.Scanner;
import java.util.*;

public class Parser {

    public Scanner scanner;
    public int flag;
    static final Map<Integer, String> tokenMap = new HashMap<Integer, String>() {{
        put(0, "EOF");
        put(1, "EOL");
        put(2, "INTO");
        put(3, "LOAD");
        put(4, "LOADI");
        put(5, "STORE");
        put(6, "ADD");
        put(7, "SUB");
        put(8, "MULT");
        put(9, "LSHIFT");
        put(10, "RSHIFT");
        put(11, "OUTPUT");
        put(12, "NOP");
        put(13, "REGISTER");
        put(14, "COMMA");  
        put(15, "SCAN_ERROR");
    }};

    public Parser(String filepath, int flag) {
        try {
            this.scanner = new Scanner(filepath);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error creating scanner");
        }
        this.flag = flag;
    }

    public void parse() {

        int token = scanner.nextSymbol();
        int lineNumber = 1;


        while (token != scanner.EOF) {
            System.out.println("LINE " + lineNumber + ": " + (tokenMap.containsKey(token) ? tokenMap.get(token) : "CONSTANT: " + token * -1));
            if (token == scanner.EOL) {
                lineNumber++;
            }
            token = scanner.nextSymbol();
        }

        System.out.println("LINE " + lineNumber + ": " + tokenMap.get(token));
        System.out.println("End of file");
    }

}