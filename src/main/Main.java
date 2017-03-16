package main;

import cpus.EU;
import instructimplement.InstructionMap;

import java.util.Scanner;


/**
 * Created by dzvinka on 27.02.17.
 *
 */
public class Main {

    public static String[] instructNames = {"cprs", "lw", "lb", "sw", "sb", "seti",
           "jmp", "jmpi", "jmr",  "jmri", "jme", "jmei", "jmer", "jmeri",
           "jle", "jlei",  "jler", "jleri", "jne", "jnei", "jner", "jneri",
           "call", "calli", "callr", "callri", "syscall", "ret",
           "add", "sub", "mul", "div", "and", "xor", "nand", "or",
            "addi", "subi", "muli", "divi", "shl", "shr", "ror", "rol"};



    public static void main(String[] args) {
        EU eu1 = new EU(new int[32]); //temporarily
        InstructionMap im = new InstructionMap();
        byte opcode = 1;
        for (String methodName: instructNames) {
            try {
                im.addInstruction(opcode, eu1.getClass().getDeclaredMethod(methodName, String.class));
                opcode++;
            } catch (NoSuchMethodException|SecurityException ex) {
                System.out.println(methodName);
                continue;

            }
        }
        eu1.setInstTable(im);
        eu1.getRegisters()[0] = 14;
        eu1.getRegisters()[1] = 15;
        eu1.getRegisters()[2] = 1 + 1057281;

        Scanner input = new Scanner(System.in);
        int i = 0;
        while(i < 100) {
        //eu1.execute("0010 0000 00010 00001 00000 000000001");
            //eu1.execute("0010 1011 00000 00001 00000 000000001");
            String inst = input.nextLine();
            eu1.execute(inst);
            System.out.println(eu1.showRegisters());
            System.out.println(eu1.showRAM());
            i++;



        }

    }
}
