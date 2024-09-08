package comp412lab1;

public class IRRow {
    
    public int line;
    public int opcode;
    public Operant op1;
    public Operant op2;
    public Operant op3;
    public IRRow next;
    public IRRow prev;

    public class Operant {
        public int SR;
        public int VR;
        public int PR;
        public int NU;
    }
}