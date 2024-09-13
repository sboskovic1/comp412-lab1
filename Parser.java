/**
 * Identify correct and incorrect programs
 * print messages for syntax errors
 * build the IR?
 */
package comp412lab1;

import comp412lab1.Scanner;
import comp412lab1.IRRow;
import java.util.*;

public class Parser {

    public Scanner scanner;
    public int flag;
    public boolean error = false;
    public int lineNumber;
    public int blankLines;
    public IRRow head;
    static final Map<Integer, String> tokenMap = new HashMap<Integer, String>() {{
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
        put(16, "PARSE_ERROR");
        put(17, "EOF");
    }};

    public Parser(String filepath, int flag) {
        try {
            this.scanner = new Scanner(filepath);
        } catch (RuntimeException e) {
            throw new RuntimeException("ERROR: Could not create scanner");
        }
        this.flag = flag;
        this.lineNumber = 1;
        this.blankLines = 1;
    }

    public void parse() {

        int token = scanner.nextSymbol(lineNumber);

        head = parseLine(flag, token);
        if (head.opcode == 16) {
            parseError();
        }
        IRRow prev = head;
        token = scanner.nextSymbol(lineNumber);
        IRRow row;

        while (token != scanner.EOF) {
            // Will this access take a long time? (scanner.eof, row.opcode)
            row = parseLine(flag, token);
            if (!error && row.opcode != 1) {
                prev.next = row;
                row.prev = prev;
                prev = row;
            } else if (row.opcode == 16) {
                parseError();
            }
            token = scanner.nextSymbol(lineNumber);
        }
        if (head.opcode == scanner.EOL && head.next != null) {
            head = head.next;
            head.prev = null;
        }
        if (flag == 2) {
            printIR();
        } else if (flag == 0) {
            System.out.println("LINE " + lineNumber + ": EOF");
        }

        if (flag == 1) {
            if (error) {
                System.out.println("Parse found errors.");
            } else {
                System.out.println("Parse Succeeded. Processed " + (lineNumber - blankLines) + " operations.");
            }
        }
    }

    public void parseError() {
        int symbol;
        if (scanner.endOfLine()) {
            lineNumber++;
            return;
        }
        while ((symbol = scanner.nextSymbol(lineNumber)) != scanner.EOL) {
            if (flag == 0) {
                if (symbol == scanner.REGISTER) {
                    symbol = scanner.nextSymbol(lineNumber);
                    if (symbol <= 0) {
                        System.out.println("LINE " + lineNumber + ": r" + (symbol * -1));
                    } else {
                        System.err.println("ERROR " + lineNumber + ": Expected constant after register, found" + tokenMap.get(symbol));
                    }
                } else {
                    System.out.println("LINE " + lineNumber + ": " + tokenMap.get(symbol));
                }
            }
        }
        if (flag == 0) {
            System.out.println("LINE " + lineNumber + ": EOL");
        }
        lineNumber++;
    }

    public IRRow parseLine(int flag, int token) {
        // Will this access take a long time?
        if (token == scanner.EOL) {
            if (flag == 0) {
                System.out.println("LINE " + lineNumber + ": EOL");
            }
            lineNumber++;
            blankLines++;
            return new IRRow(token);
        } else if (token == scanner.STORE || token == scanner.LOAD) { // MEMOP
            return parseMemop(flag, token);
        } else if (token == scanner.ADD || token == scanner.SUB || token == scanner.MULT || token == scanner.LSHIFT || token == scanner.RSHIFT) {
            return parseArithop(flag, token); // ARITHOP
        } else if (token == scanner.LOADI) { // LOADI
            return parseLoadI(flag, token);
        } else if (token == scanner.OUTPUT) { // OUTPUT
            return parseOutput(flag, token);
        } else if (token == scanner.NOP) { // NOP
            return parseNop(flag, token);
        } else if (token == scanner.SCAN_ERROR) { // ERROR
            System.err.println("ERROR " + lineNumber + ": Invalid token at the start of line");
            error = true;
            parseError();
            return new IRRow();
        }
        System.err.println("ERROR " + lineNumber + ": Line can not start with " + (tokenMap.containsKey(token) ? tokenMap.get(token) : "CONSTANT: " + token * -1));
        error = true;
        parseError();
        return new IRRow(16);
    }

    public IRRow parseMemop(int flag, int token) {
        int r1;
        int r2;
        int currSymbol = token;
        if (flag == 0) {
            System.out.println("LINE " + lineNumber + ": " + tokenMap.get(token));
        }
        if ((currSymbol = scanner.nextSymbol(lineNumber)) == scanner.REGISTER) {
            if ((currSymbol = scanner.nextSymbol(lineNumber)) <= 0) {
                if (flag == 0) {
                    System.out.println("LINE " + lineNumber + ": r" + (currSymbol * -1));
                }
                r1 = currSymbol * -1;
                if ((currSymbol = scanner.nextSymbol(lineNumber)) == scanner.INTO) {
                    if (flag == 0) {
                        System.out.println("LINE " + lineNumber + ": INTO");
                    }
                    if ((currSymbol = scanner.nextSymbol(lineNumber)) == scanner.REGISTER) {
                        if ((currSymbol = scanner.nextSymbol(lineNumber)) <= 0) {
                            if (flag == 0) {
                                System.out.println("LINE " + lineNumber + ": r" + (currSymbol * -1));
                            }
                            r2 = currSymbol * -1;
                            if ((currSymbol = scanner.nextSymbol(lineNumber)) == scanner.EOL) {
                                if (flag == 0) {
                                    System.out.println("LINE " + lineNumber + ": EOL");
                                }
                                lineNumber++;
                                return new IRRow(token, r1, 0, r2);
                            } else {
                                System.err.println("ERROR " + lineNumber + ": Expected EOL after register, found " + tokenMap.get(currSymbol));
                                error = true;
                            }
                        } else {
                            System.err.println("ERROR " + lineNumber + ": Expected register after INTO, found " + tokenMap.get(currSymbol));
                            error = true;
                        }
                    } else {
                        System.err.println("ERROR " + lineNumber + ": Expected register after INTO, found " + tokenMap.get(currSymbol));
                        error = true;
                    }
                } else {
                    System.err.println("ERROR " + lineNumber + ": Expected INTO after register, found " + tokenMap.get(currSymbol));
                    error = true;
                }
            } else {
                System.err.println("ERROR " + lineNumber + ": Expected constant after register, found " + tokenMap.get(currSymbol));
                error = true;
            }
        } else {
            System.err.println("ERROR " + lineNumber + ": Expected register after MEMOP, found " + tokenMap.get(currSymbol));
            error = true;
        }
        return new IRRow(16);
    }

    public IRRow parseArithop(int flag, int token) {
        int r1;
        int r2;
        int r3;
        int currSymbol = token;
        if (flag == 0) {
            System.out.println("LINE " + lineNumber + ": " + tokenMap.get(token));
        }
        if ((currSymbol = scanner.nextSymbol(lineNumber)) == scanner.REGISTER) {
            if ((currSymbol = scanner.nextSymbol(lineNumber)) <= 0) {
                if (flag == 0) {
                    System.out.println("LINE " + lineNumber + ": r" + (currSymbol * -1));
                }
                r1 = currSymbol * -1;
                if ((currSymbol = scanner.nextSymbol(lineNumber)) == scanner.COMMA) {
                    if (flag == 0) {
                        System.out.println("LINE " + lineNumber + ": COMMA");
                    }
                    if ((currSymbol = scanner.nextSymbol(lineNumber)) == scanner.REGISTER) {
                        if ((currSymbol = scanner.nextSymbol(lineNumber)) <= 0) {
                            r2 = currSymbol * -1;
                            if (flag == 0) {
                                System.out.println("LINE " + lineNumber + ": r" + (currSymbol * -1));
                            }
                            if ((currSymbol = scanner.nextSymbol(lineNumber)) == scanner.INTO) {
                                if (flag == 0) {
                                    System.out.println("LINE " + lineNumber + ": INTO");
                                }
                                if ((currSymbol = scanner.nextSymbol(lineNumber)) == scanner.REGISTER) {
                                    if ((currSymbol = scanner.nextSymbol(lineNumber)) <= 0) {
                                        r3 = currSymbol * -1;
                                        if (flag == 0) {
                                            System.out.println("LINE " + lineNumber + ": r" + (currSymbol * -1));
                                        }
                                        if ((currSymbol = scanner.nextSymbol(lineNumber)) == scanner.EOL) {
                                            if (flag == 0) {
                                                System.out.println("LINE " + lineNumber + ": EOL");
                                            }
                                            lineNumber++;
                                            return new IRRow(token, r1, r2, r3);
                                        } else {
                                            System.err.println("ERROR " + lineNumber + ": Expected EOL after register, found " + tokenMap.get(currSymbol));
                                            error = true;
                                        }
                                    } else {
                                        System.err.println("ERROR " + lineNumber + ": Expected constant after register, found " + tokenMap.get(currSymbol));
                                        error = true;
                                    }
                                } else {
                                    System.err.println("ERROR " + lineNumber + ": Expected register after INTO, found " + tokenMap.get(currSymbol));
                                    error = true;
                                }
                            } else {
                                System.err.println("ERROR " + lineNumber + ": Expected INTO after register, found " + tokenMap.get(currSymbol));
                                error = true;
                            }
                        } else {
                            System.err.println("ERROR " + lineNumber + ": Expected constant after register, found " + tokenMap.get(currSymbol));
                            error = true;
                        }
                    } else {
                        System.err.println("ERROR " + lineNumber + ": Expected register after COMMA, found " + tokenMap.get(currSymbol));
                        error = true;
                    }
                } else {
                    System.err.println("ERROR " + lineNumber + ": Expected COMMA after register, found " + tokenMap.get(currSymbol));
                    error = true;
                }
            } else {
                System.err.println("ERROR " + lineNumber + ": Expected constant after REGISTER, found " + tokenMap.get(currSymbol));
                error = true;
            }
        } else {
            System.err.println("ERROR " + lineNumber + ": Expected register after ARITHOP, found " + tokenMap.get(currSymbol));
            error = true;
        }
        return new IRRow(16);
    }

    public IRRow parseLoadI(int flag, int token) {
        int r1;
        int constant;
        int currSymbol = token;
        if (flag == 0) {
            System.out.println("LINE " + lineNumber + ": " + tokenMap.get(token));
        }
        if ((currSymbol = scanner.nextSymbol(lineNumber)) <= 0) {
            constant = currSymbol * -1;
            if (flag == 0) {
                System.out.println("LINE " + lineNumber + ": " + "CONSTANT " + constant);
            }
            if ((currSymbol = scanner.nextSymbol(lineNumber)) == scanner.INTO) {
                if (flag == 0) {
                    System.out.println("LINE " + lineNumber + ": INTO");
                }
                if ((currSymbol = scanner.nextSymbol(lineNumber)) == scanner.REGISTER) {
                    if ((currSymbol = scanner.nextSymbol(lineNumber)) <= 0) {
                        if (flag == 0) {
                            System.out.println("LINE " + lineNumber + ": r" + (currSymbol * -1));
                        }
                        r1 = currSymbol * -1;
                        if ((currSymbol = scanner.nextSymbol(lineNumber)) == scanner.EOL) {
                            if (flag == 0) {
                                System.out.println("LINE " + lineNumber + ": EOL");
                            }
                            lineNumber++;
                            return new IRRow(token, constant, 0, r1);
                        } else {
                            System.err.println("ERROR " + lineNumber + ": Expected EOL after register, found " + tokenMap.get(currSymbol));
                            error = true;
                        }
                    } else {
                        System.err.println("ERROR " + lineNumber + ": Expected register after INTO, found " + tokenMap.get(currSymbol));
                        error = true;
                    }
                } else {
                    System.err.println("ERROR " + lineNumber + ": Expected register after INTO, found " + tokenMap.get(currSymbol));
                    error = true;
                }
            } else {
                System.err.println("ERROR " + lineNumber + ": Expected INTO after constant, found " + tokenMap.get(currSymbol));
                error = true;
            }
        } else {
            System.err.println("ERROR " + lineNumber + ": Expected constant after LOADI, found " + tokenMap.get(currSymbol));
            error = true;
        }
        return new IRRow(16);
    }

    public IRRow parseOutput(int flag, int token) {
        int constant;
        int currSymbol = token;
        if (flag == 0) {
            System.out.println("LINE " + lineNumber + ": " + tokenMap.get(token));
        }
        if ((currSymbol = scanner.nextSymbol(lineNumber)) <= 0) {
            constant = currSymbol * -1;
            if (flag == 0) {
                System.out.println("LINE " + lineNumber + ": " + "CONSTANT " + constant);
            }
            if ((currSymbol = scanner.nextSymbol(lineNumber)) == scanner.EOL) {
                if (flag == 0) {
                    System.out.println("LINE " + lineNumber + ": EOL");
                }
                lineNumber++;
                return new IRRow(token, constant, 0, 0);
            } else {
                System.err.println("ERROR " + lineNumber + ": Expected EOL after constant, found " + tokenMap.get(currSymbol));
                error = true;
            }
        } else {
            System.err.println("ERROR " + lineNumber + ": Expected constant after OUTPUT, found " + tokenMap.get(currSymbol));
            error = true;
        }
        return new IRRow(16);
    }

    public IRRow parseNop(int flag, int token) {
        if (flag == 0) {
            System.out.println("LINE " + lineNumber + ": " + tokenMap.get(token));
        }
        if ((scanner.nextSymbol(lineNumber)) == scanner.EOL) {
            if (flag == 0) {
                System.out.println("LINE " + lineNumber + ": EOL");
            }
            lineNumber++;
            return new IRRow(token);
        } else {
            System.err.println("ERROR " + lineNumber + ": Expected EOL after NOP, found " + tokenMap.get(scanner.nextSymbol(lineNumber)));
            error = true;
        }
        return new IRRow(16);
    }

    public void printIR() {
        if (error) {
            System.out.println("Could not construct IR due to syntax errors");
            return;
        }
        IRRow curr = head;
        int line = 1;
        while (curr != null) {
            System.out.println("LINE " + line + ": " + tokenMap.get(curr.opcode) + " " + curr.toString());
            curr = curr.next;
            line++;
        }
    }

}