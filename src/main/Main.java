package main;

import cpus.EU;
import helpers.Helper;
import instructimplement.InstructionMap;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * Created by dzvinka on 27.02.17.
 *
 */
public class Main {

//    public static String[] instructNames = {"cprs", "lw", "lb", "sw", "sb", "seti",
//           "jmp", "jmpi", "jmr",  "jmri", "jme", "jmei", "jmer", "jmeri",
//           "jle", "jlei",  "jler", "jleri", "jne", "jnei", "jner", "jneri",
//           "call", "calli", "callr", "callri", "syscall", "ret",
//           "add", "sub", "mul", "div", "and", "xor", "nand", "or",
//            "addi", "subi", "muli", "divi", "shl", "shr", "ror", "rol", "cmp"};

    public static String[] instructNames = {"cprs", "lw", "lb", "sw", "sb", "seti",
           "jmp", "jmpi", "jmr",  "jmri", "jmp_t1", "jmp_t2", "jmp_t3", "jmp_t4",
           "call", "calli", "callr", "callri", "syscall", "ret",
           "alu",
            "shl", "shr", "ror", "rol", "cmp", "addi", "subi", "muli", "divi", };
    public static String[] ALUInstructs = {"add", "sub", "mul", "div", "and", "xor", "nand", "or"};






    public static void main(String[] args) {
        Helper h = new Helper();
        EU eu1 = new EU(new int[1024]); //temporarily
        InstructionMap im = new InstructionMap();
        InstructionMap ALUInst = new InstructionMap();
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

        opcode = 1;
        for (String methodName: ALUInstructs) {
            try {
                ALUInst.addInstruction(opcode, eu1.getClass().getDeclaredMethod(methodName, String.class));
                opcode++;
            } catch (NoSuchMethodException|SecurityException ex) {
                System.out.println(methodName);
                continue;
            }
        }
        eu1.setInstTable(im);
        eu1.setALUInstructs(ALUInst);

        ArrayList<Integer> ram = h.readFile("RAM.txt");

        for (int i = 0; i < ram.size(); i++) {
            eu1.getMemory()[i] = ram.get(i);
        }

        //eu1.getMemory()[0] = Integer.parseInt("00010101001100000100000000010001", 2);
        //eu1.getMemory()[1] = Integer.parseInt("00010101001100000100000000110001", 2);
        eu1.getRegisters()[0] = 14;
        eu1.getRegisters()[1] = 15;
        eu1.getRegisters()[2] = 1 + 1057281;

        Scanner input = new Scanner(System.in);
        int i = 0;


        eu1.getRegisters()[27] = 0;

        while(true) {
            if (i > 511)
            {
                System.out.println("OUT OF RAM");
                break;
            }
            eu1.execute(h.intToBinString(eu1.getMemory()[eu1.getRegisters()[27]]));
            //eu1.execute("00010101000100000100000000010001");
            eu1.execute(h.intToBinString((eu1.getMemory()[eu1.getRegisters()[27] + 1])));
//        //eu1.execute("0010 0000 00010 00001 00000 000000001");
//            //eu1.execute("0010 1011 00000 00001 00000 000000001");
//            String inst = input.nextLine();
//            eu1.execute(inst);
            System.out.println(eu1.showRegisters());

            System.out.println(eu1.showRAM());
            eu1.getRegisters()[27] += 2;
            i++;
        }

    }
}
