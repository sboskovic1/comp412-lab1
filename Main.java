package comp412lab1;

import comp412lab1.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Parser");
        Parser parser = new Parser(args[0]);
        parser.parse();
    }
}