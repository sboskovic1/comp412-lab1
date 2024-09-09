package comp412lab1;

import java.util.Scanner;

public class IRRow {
    
    public int opcode;
    public Operant op1;
    public Operant op2;
    public Operant op3;
    public IRRow next;
    public IRRow prev;

    public IRRow(int opcode, int sr1, int sr2, int sr3) {
        this.opcode = opcode;
        op1 = new Operant(sr1);
        op2 = new Operant(sr2);
        op3 = new Operant(sr3);
        next = null;
        prev = null;
    }

    public IRRow() {
        opcode = -1;
        op1 = null;
        op2 = null;
        op3 = null;
        next = null;
        prev = null;
    }

    public IRRow(int opcode) {
        this.opcode = opcode;
    }

    public String toString() {
        String line = "{ ";
        if (op1 != null) {
            line += op1.toString();
        }
        if (op2 != null && (opcode >= 6 && opcode <= 10)) {
            line += op2.toString();
        }
        if (op3 != null) {
            line += op3.toString();
        }
        return line + "}";
    }

    public class Operant {
        public int SR;
        public int VR;
        public int PR;
        public int NU;

        public Operant(int sr) {
            SR = sr;
            VR = -1;
            PR = -1;
            NU = -1;
        }

        public String toString() {

            return "[SR: " + (SR == -1 ? "-" : SR)  + 
                    ", VR: " + (VR == -1 ? "-" : VR) + 
                    ", PR: " + (PR == -1 ? "-" : PR) + 
                    ", NU: " + (NU == -1 ? "-" : NU) + "] ";
        }
    }
}