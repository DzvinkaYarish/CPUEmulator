package main;

import cpus.EU;
import helpers.Helper;
import instructimplement.InstructionMap;
import java.util.ArrayList;



/**
 * Created by dzvinka on 27.02.17.
 *
 */
public class Main {

    public static String[] instructNames = {"cprs", "lw", "lb", "sw", "sb", "seti",
           "jmp", "jmpi", "jmr",  "jmri", "jmp_t1", "jmp_t2", "jmp_t3", "jmp_t4",
           "call", "calli", "callr", "callri", "syscall", "ret",
           "alu","dummy",
            "shl", "shr", "ror", "rol", "cmp", };
    public static String[] ALUInstructs = {"add", "sub", "mul", "div", "and", "xor", "nand", "or"};

    public static String[] JumpSubInstructs = {"cjmp_t1", "cjmp_t2", "cjmp_t3", "cjmp_t4"};
    public static int[] opcodesforALU = {150, 182, 230, 246};
    public static String[] ALUInstrcutsImVal = {"addi", "subi", "divi", "muli"};


    public static void main(String[] args) {
        int RAMsize = 20;//temporarily , set a convenient size of RAM for yourself
        Helper h = new Helper();
        EU eu1 = new EU(new long[RAMsize]);
        InstructionMap im = new InstructionMap();
        InstructionMap ALUInst = new InstructionMap();
        int opcode = 1;
        for (String methodName: instructNames) {
            try {
                im.addInstruction(opcode, eu1.getClass().getDeclaredMethod(methodName, String.class));
                opcode++;
            } catch (NoSuchMethodException|SecurityException ex) {
                System.out.println(methodName);
                continue;

            }
        }

        opcode = 139;
        for (String methodName: JumpSubInstructs) {
            try {
                im.addInstruction(opcode, eu1.getClass().getDeclaredMethod(methodName, String.class));
                opcode++;
            } catch (NoSuchMethodException|SecurityException ex) {
                System.out.println(methodName + "blaa");

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
        for (int j = 0; j < 4; j++)
        {
            try {
                im.addInstruction(opcodesforALU[j], eu1.getClass().getDeclaredMethod(ALUInstrcutsImVal[j], String.class));
                opcode++;
            } catch (NoSuchMethodException|SecurityException ex) {
                System.out.println(ALUInstrcutsImVal[j]);
                continue;
            }
        }
        eu1.setInstTable(im);
        eu1.setALUInstructs(ALUInst);

        ArrayList<Long> ram = h.readFile("RAM.txt");
        if (ram.size() > RAMsize) {
            System.out.println("TOO MANY INSTRUCTIONS");
        } else {
            for (int i = 0; i < ram.size(); i++) {
                eu1.getMemory()[i] = ram.get(i);
            }
        }

        //eu1.getMemory()[0] = Integer.parseInt("00010101001100000100000000010001", 2);
        //eu1.getMemory()[1] = Integer.parseInt("00010101001100000100000000110001", 2);
        for (int i = 1; i < 33; i++) {
            eu1.getRegisters()[i - 1] = i;

        }


        int i = 0;


        eu1.getRegisters()[27] = 0;
        long prevReg[] = new long[32];
        long prevMem[] = new long[RAMsize];
        while(i < 3) { // main loop, adjust for you needs
            System.arraycopy( eu1.getRegisters(), 0, prevReg, 0, 32);
            System.arraycopy( eu1.getMemory(), 0, prevMem, 0, RAMsize);
            if (i > 511)
            {
                System.out.println("OUT OF RAM");
                break;
            }
            eu1.execute(h.intToBinString(eu1.getMemory()[(int)eu1.getRegisters()[27]]));
            //eu1.execute("00010101000100000100000000010001");
            eu1.execute(h.intToBinString((eu1.getMemory()[(int)eu1.getRegisters()[27] + 1])));
//        //eu1.execute("0010 0000 00010 00001 00000 000000001");
//            //eu1.execute("0010 1011 00000 00001 00000 000000001");
//            String inst = input.nextLine();
//            eu1.execute(inst);
            System.out.println("CHANGES AFTER ONE CYCLE");
            System.out.println(eu1.showRegisters(prevReg));

            System.out.println(eu1.showRAM(prevMem));
            eu1.getRegisters()[27] += 2;
            i++;
        }

    }
}
