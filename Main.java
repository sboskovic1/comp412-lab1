package comp412lab1;

import comp412lab1.*;

public class Main {

    static final String helpMessage = "COMP 412, Fall 2024, Front End by Stefan Boskovic (sb121)\r\n" + //
                "Command Syntax:\r\n" + //
                "        ./lab1_ref [flags] filename\r\n" + //
                "\r\n" + //
                "Required arguments:\r\n" + //
                "        filename  is the pathname (absolute or relative) to the input file\r\n" + //
                "\r\n" + //
                "At most one of the following three flags:\r\n" + //
                "        -h       prints the help message, ignoring any file passed as 'filename'\r\n" + //
                "        -s       prints tokens in token stream\r\n" + //
                "        -p       invokes parser and reports on success or failure\r\n" + //
                "        -r       prints human readable version of parser's IR\r\n" + //
                "If none is specified, the default action is '-p'.";
    public static void main(String[] args) {

        if (args[0].equals("-h")) {
            System.out.println(helpMessage);
            return;
        } 

        Parser parser = null;
        System.out.println("Starting Parser");
        try {
            parser = new Parser(args[0], 0);

        } catch (RuntimeException e) {
            return;
        }
        parser.parse();
        return;
    }
}