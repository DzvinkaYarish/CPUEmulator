package main;

import cpus.EU;
import instructimplement.InstructionMap;


/**
 * Created by dzvinka on 27.02.17.
 */
public class Main {
    static String[] instructNames = {"cprs", "lw", "lb", "sw", "sb", "seti",
           "jmp", "jmpi", "jmr", "jmri", "jme", "jmei", "jmer", "jmeri",
           "jle", "jlei", "jler", "jleri", "jne", "jnei", "jner", "jneri",
           "call", "calli", "callr", "callri", "syscall", "ret",
           "add", "sub", "mul", "div", "and", "xor", "nand", "or",
            "addi", "subi", "muli", "divi", "shl", "slr", "rol", "ror"};
    public static void main(String[] args) {
        EU eu1 = new EU(new int[32]); //temporarily
        InstructionMap im = new InstructionMap();
        byte opcode = 1;
        for (String methodName: instructNames) {
            try {
                im.addInstruction(opcode, eu1.getClass().getMethod(methodName));
            } catch (NoSuchMethodException|SecurityException ex) {
                continue;
            }
        }
        eu1.setInstTable(im);
        while(true) {
            eu1.execute((byte)5, 1,2); //just an example!!
        }

    }
}
