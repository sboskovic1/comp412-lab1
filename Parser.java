/**
 * Identify correct and incorrect programs
 * print messages for syntax errors
 * build the IR?
 */
package comp412lab1;

import comp412lab1.Scanner;

public class Parser {

    public Scanner scanner;

    public Parser(String filepath) {
        this.scanner = new Scanner(filepath);
    }

    public void parse() {
        int token = scanner.nextSymbol();
        while (token != scanner.EOF && token != -1) {
            System.out.println(token);
            token = scanner.nextSymbol();
        }
        System.out.println(token);
        System.out.println("End of file");
    }

}