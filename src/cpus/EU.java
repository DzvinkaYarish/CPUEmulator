package cpus;


import instructimplement.InstructionMap;
import instructimplement.InstructionParser;

import java.lang.reflect.InvocationTargetException;
import java.util.Stack;

//TODO: ask about separate operations for ALU, commands of type c...(can be 3 commands for jump, change and add+ im value)
//TODO: make zero all flags after execution of each instrcution or what?
//TODO: ask about exception
//TODO: same opcodes for rr, different for ri
// can't have 4 diff 1 byte values in 1 register
// we don't have parallel fetching
// add + 8 to get next command with starting address in PC()

/**
 * Created by dzvinka on 26.02.17.
 * $(raddr)  - value stored at this address
 * Predefined registers
 * r26 -
 * r27 – Exception Program Counter
 * r28 – Memory Base Address
 * r29 – Instruction Pointer
 * r30 – used as a status register for ALU operations and other events (e.g. interrupts).
 – [31:27] Exception code.
 – [26] Overflow flag
 – [25] Z(ero) flag
 – [24] N(egative) flag
 – [23:0] reserved
 * r31 – used as a stack pointer
 */
public class EU {
    protected int[] registers = new int[32];
    protected int[] RAM;
    protected Stack<Integer> stk = new Stack<>();

    private static final int RAM_OFFSET = 1057281;
    protected InstructionMap instTable = new InstructionMap();



    protected InstructionMap ALUInstructs = new InstructionMap();
    protected InstructionParser instParser = new InstructionParser();

    public EU(int[] RAM) {
        this.RAM = RAM;
    }
    public int[] getMemory() {
        return RAM;
    }
    public int[] getRegisters() {
        return registers;
    }
    public void setRegisters(int[] registers) {
        this.registers = registers;
    }

    public InstructionMap getInstTable() {
        return instTable;
    }

    public void setInstTable(InstructionMap instTable) {
        this.instTable = instTable;
    }
    public void setALUInstructs(InstructionMap ALUInstructs) { this.ALUInstructs = ALUInstructs;}


    /**
     * execute an instruction, based on its opcode
     * @param instruction 32 binary string
     */
    public void execute(String instruction) {
        instruction = instruction.replaceAll("\\s","");
        if (instruction.length() != 32) {
            System.out.println("TOO SHORT/ LONG INSTRUCTION");
        }
        byte opcode = Byte.parseByte(instruction.substring(0, 8), 2);
        try {
            instTable.getInstruction(opcode).invoke(this, instruction.substring(8));
        } catch (IllegalAccessException|InvocationTargetException ex) {

            if (ex.getCause().getClass().equals(IllegalArgumentException.class)) {
                System.out.println("Illegal arguments were passed to" + instTable.getInstruction(opcode).getName());}else if (ex.getCause().getClass().equals(IndexOutOfBoundsException.class)) {
                System.out.println("Invalid main RAM/register address in " + instTable.getInstruction(opcode).getName());
            }
        }
    }

    public void executeALU(String instruction, byte ALUopcode) {
        try {
        ALUInstructs.getInstruction(ALUopcode).invoke(this, instruction);
        } catch (IllegalAccessException|InvocationTargetException ex) {
            System.out.println("NOT VALID OPCODE");
        }
    }


    //================================================Load/Store========================================================

    protected void cprs(String params){}

    /**
     * Loads one word (basically  32-bit) from RAM at the address maddr to the register raddr.
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void lw(String params) {
        byte [] parsedParams = instParser.parseLoadStoretype(params);
        registers[parsedParams[0]] = RAM[addrToArrIndex(registers[parsedParams[1]])];
        System.out.println("lw");
    }

    /**
     * Loads one byte from RAM at the ad-
     * dress maddr to the 8 least significant bits of register raddr.
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void lb(String params) { //TODO: think about it

    }

    /**
     * Stores a word from a register raddr2 to RAM at the address stored in register raddr1
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void sw(String params) {
        byte [] parsedParams = instParser.parseLoadStoretype(params);
        RAM[addrToArrIndex(registers[parsedParams[1]])] = registers[parsedParams[0]];
        System.out.println("sw");
    }

    /**
     * Stores a byte from 8 most signicant bits
     * of a register raddr1 to RAM at the address stored in register raddr2
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void sb(String params) {

    }
    /**
     * Stores sign extended value of value to the
     * register raddr
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void seti(String params) { //Stores sign extended value of val to the $t register
        short[] parsedParams = instParser.parseLoadStoretypeImValue(params);
        registers[parsedParams[0]] = parsedParams[1];
        System.out.println("seti");
    }
    //==============================================Branching===========================================================

    /**
     * Changes PC to the $raddr.
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void jmp(String params) {
        byte[] parsedParams = instParser.parseBtypeOnlyJump(params);
        registers[29] = registers[parsedParams[1]];
        System.out.println("jmp");
    }

    /**
     * Changes PC to the value.
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void jmpi(String params) {
        short[] parsedParams = instParser.parseBtypeOnlyJumpImValue(params);
        registers[29] = parsedParams[1];
        System.out.println("JMPI");
    }

    /**
     * Adds the $raddr to the value of current PC to get next one.
     * (HOW? WHAT HAPPENS TO THE VALUE, WHICH WAS THERE FIRST?)
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void jmr(String params) {
        byte[] parsedParams = instParser.parseBtypeOnlyJump(params);
        registers[29] += registers[parsedParams[1]];
        System.out.println("jmr");

    }

    /**
     * Adds the value to the value of current PC to get next one.
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void jmri(String params) {
        short[] parsedParams = instParser.parseBtypeOnlyJumpImValue(params);
        registers[29] += parsedParams[1];
        System.out.println("jmri");
    }

    /**
     * Jumps to $raddr1
     * if $raddr2 == $raddr3
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void jme(String params) {
        byte[] parsedParams = instParser.parseBtype(params);
        if (registers[parsedParams[1]] == registers[parsedParams[2]]) {
            registers[29] = registers[28] + registers[parsedParams[3]];
        }
        System.out.println("jme");
    }

    /**
     * Jumps to the value
     * if $raddr2 and $raddr3 are equal.
     * are equal.
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void jmei(String params) {
        short[] parsedParams = instParser.parseBtypeImValue(params);
        if (registers[parsedParams[1]] == registers[parsedParams[2]]) {
            registers[29] = registers[28] + parsedParams[3];
        }
        System.out.println("jmei");
    }

    /**
     * Adds $raddr1 to the PC if
     * if $raddr2 and $raddr3 are equal.
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void jmer(String params) {
        byte[] parsedParams = instParser.parseBtype(params);
        if (registers[parsedParams[1]] == registers[parsedParams[2]]) {
            registers[29] += registers[parsedParams[3]];
        }
        System.out.println("jmer");
    }

    /**
     * Adds value to the PC if
     * if $raddr2 and $raddr3 are equal.
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void jmeri(String params) {
        short[] parsedParams = instParser.parseBtypeImValue(params);
        if (registers[parsedParams[1]] == registers[parsedParams[2]]) {
            registers[29] += parsedParams[3];
        }
        System.out.println("jmeri");
    }

    /**
     * Jumps to the $raddr1
     * if $raddr2 != raddr3
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void jne(String params) {
        byte[] parsedParams = instParser.parseBtype(params);
        if (registers[parsedParams[1]] != registers[parsedParams[2]]) {
            registers[29] = registers[28] + registers[parsedParams[3]];
        }
        System.out.println("jne");
    }

    /**
     * Jumps to the value
     * if $raddr2 != raddr3
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void jnei(String params) {
        short[] parsedParams = instParser.parseBtypeImValue(params);
        if (registers[parsedParams[1]] != registers[parsedParams[2]]) {
            registers[29] = registers[28] + parsedParams[3];
        }
        System.out.println("jnei");
    }

    /**
     * Adds $raadr1 to the PC if
     * $raddr2 != $raddr3
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void jner(String params) {
        byte[] parsedParams = instParser.parseBtype(params);
        if (registers[parsedParams[1]] != registers[parsedParams[2]]) {
            registers[29] += registers[parsedParams[3]];
        }
        System.out.println("jner");
    }

    /**
     * Adds value to the PC if
     * $raddr2 != $raddr3
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void jneri(String params) {
        short[] parsedParams = instParser.parseBtypeImValue(params);
        if (registers[parsedParams[1]] != registers[parsedParams[2]]) {
            registers[29] += parsedParams[3];
        }
        System.out.println("jneri");
    }

    /**
     * Changes next value of PC to the $raddr1   //TODO: ask about change next value
     * if $raddr2 is less or equal to $raddr3
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void jle(String params) {
        byte[] parsedParams = instParser.parseBtype(params);
        if (registers[parsedParams[1]] <= registers[parsedParams[2]]) {
            registers[29] = registers[parsedParams[3]];
        }
        System.out.println("jle");
    }

    /**
     * Changes next value of PC to the value
     * if $raddr2 is less or equal to $raddr3
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void jlei(String params) {
        short[] parsedParams = instParser.parseBtypeImValue(params);
        if (registers[parsedParams[1]] <= registers[parsedParams[2]]) {
            registers[29] = parsedParams[3];
        }
        System.out.println("jlei");
    }

    /**
     * Jumps to the address stored in r1 if value
     * of  r2 ≤ r3
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void jler(String params) {
        byte[] parsedParams = instParser.parseBtype(params);
        if (registers[parsedParams[1]] <= registers[parsedParams[2]]) {
            registers[29] = registers[28] + registers[parsedParams[3]];
        }
        System.out.println("jler");

    }

    /**
     * Adds value of {val} to the PC if value of
     * ✩ s1 ≤ ✩ s2
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void jleri(String params) {
        short[] parsedParams = instParser.parseBtypeImValue(params);
        if (registers[parsedParams[1]] <= registers[parsedParams[2]]) {
            registers[29] = registers[28] + parsedParams[3];
        }
        System.out.println("jleri");
    }

    /**
     * Puts the current next value of the PC to
     the top of stack and jumps to the value of
     ✩ t.
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void call(String params) {
        byte[] parsedParams = instParser.parseBtypeOnlyJump(params);
        stk.push(registers[29]);
        registers[31] += 1;
        registers[29] = registers[28] + registers[parsedParams[1]];
        System.out.println("call");
    }

    /**
     * Puts the current next value of the PC to
     * the top of stack and jums assigns PC to
     * the {val}.
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void calli(String params) {
        short[] parsedParams = instParser.parseBtypeOnlyJumpImValue(params);
        stk.push(registers[29]);
        registers[31] += 1;
        registers[29] = registers[28] + parsedParams[1];
        System.out.println("calli");
    }

    /**
     * Puts the current next value of the PC to
     * the top of stack and adds value of ✩ t register to the PC.
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void callr(String params) {
        byte[] parsedParams = instParser.parseBtypeOnlyJump(params);
        stk.push(registers[29]);
        registers[31] += 1;
        registers[29] += registers[parsedParams[1]];
        System.out.println("callr");
    }

    /**
     *   Puts the current next value of the PC to
     * the top of stack and adds value of {val} to the PC.
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void callri(String params) {
        short[] parsedParams = instParser.parseBtypeOnlyJumpImValue(params);
        stk.push(registers[29]);
        registers[31] += 1;
        registers[29] += parsedParams[1];
        System.out.println("callri");
    }

    /**
     * Stores current PC to the EPC register and
     * jumps to address 0x00001201 in RAM
     * @param params not used
     */
    protected void syscall(String params) {
        registers[27] = registers[29];
        registers[29] = 4609; //start address of interuption handler for syscalls
        System.out.println("syscall");
    }

    /**
     * Pops a value from the stack and jumps to
     that value
     * @param params not used
     */
    protected void ret(String params) {
        registers[29] = registers[28] + stk.pop();
        registers[31] -= 1;
        System.out.println("ret");
    }

   //=============================================BranchingBuffers======================================================
    protected void jmp_t1(String binaryString) {
        if (binaryString.substring(17, 20).equals("001")) {
            jme(binaryString.substring(8));
        } else if (binaryString.substring(17, 20).equals("010")) {
            jle(binaryString.substring(8));
        } else {
            jne(binaryString.substring(8));
        }
    }

    protected void jmp_t2(String binaryString) {
        if (binaryString.substring(17, 20).equals("001")) {
            jmei(binaryString.substring(8));
        } else if (binaryString.substring(17, 20).equals("010")) {
            jlei(binaryString.substring(8));
        } else {
            jnei(binaryString.substring(8));
        }
    }

    protected void jmp_t3(String binaryString) {
        if (binaryString.substring(17, 20).equals("001")) {
            jmer(binaryString.substring(8));
        } else if (binaryString.substring(17, 20).equals("010")) {
            jler(binaryString.substring(8));
        } else {
            jner(binaryString.substring(8));
        }
    }

    protected void jmp_t4(String binaryString) {
        if (binaryString.substring(17, 20).equals("001")) {
            jmeri(binaryString.substring(8));
        } else if (binaryString.substring(17, 20).equals("010")) {
            jleri(binaryString.substring(8));
        } else {
            jneri(binaryString.substring(8));
        }
    }



    //============================================Integer arithmetic====================================================

    protected void alu(String params) {
        byte[] parsedParams = instParser.parseInstructForALU(params);
        executeALU(params, parsedParams[0]);
        System.out.println("ALU");  
    }


    protected void add(String params) {
        byte[] parsedParams = instParser.parseRRtype(params);
        long res = (long)registers[parsedParams[1]] + (long)registers[parsedParams[2]];
        if (res > Integer.MAX_VALUE || res < Integer.MIN_VALUE) {
            registers[parsedParams[0]] = Integer.parseInt(Long.toBinaryString(res).substring(1), 2);
            registers[30] = registers[30] | (1 << 26);

        } else {
            registers[parsedParams[0]] = registers[parsedParams[1]] + registers[parsedParams[2]];
        }
        System.out.println("add");
    }
    protected void addi(String params) {

        short[] parsedParams  = instParser.parseRItype(params);
        long res = (long)registers[parsedParams[1]] + (long)parsedParams[2];
        if (res > Integer.MAX_VALUE || res < Integer.MIN_VALUE) {
            registers[parsedParams[0]] = Integer.parseInt(Long.toBinaryString(res).substring(1), 2);
            registers[30] = registers[30] | (1 << 26);
        } else {
            registers[parsedParams[0]] = registers[parsedParams[1]] + parsedParams[2];
        }
        System.out.println("addi");
    }

    protected void sub(String params) {
        byte[] parsedParams = instParser.parseRRtype(params);
        long res = (long)registers[parsedParams[1]] - (long)registers[parsedParams[2]];
        if (res > Integer.MAX_VALUE || res < Integer.MIN_VALUE) {
            registers[parsedParams[0]] = Integer.parseInt(Long.toBinaryString(res).substring(1), 2);
            registers[30] = registers[30] | (1 << 26);
        } else {
            registers[parsedParams[0]] = registers[parsedParams[1]] - registers[parsedParams[2]];
        }
        System.out.println("sub");
    }
    protected void subi(String params) {
        short[] parsedParams  = instParser.parseRItype(params);
        long res = (long)registers[parsedParams[1]] - (long)parsedParams[2];
        if (res > Integer.MAX_VALUE || res < Integer.MIN_VALUE) {
            registers[parsedParams[0]] = Integer.parseInt(Long.toBinaryString(res).substring(1), 2);
            registers[30] = registers[30] | (1 << 26);
        } else {
            registers[parsedParams[0]] = registers[parsedParams[1]] - parsedParams[2];
        }
        System.out.println("subi");

    }
    protected void mul(String params) {
        byte[] parsedParams = instParser.parseRRtype(params);
        long res = registers[parsedParams[1]] * registers[parsedParams[2]];
        registers[parsedParams[0] + 1] = (int)(res & 0x00000000FFFFFFFFL);
        registers[parsedParams[0]] = (int)(res & 0xFFFFFFFF00000000L);
        System.out.println("mul");
    }
    protected void muli(String params) {
        short[] parsedParams  = instParser.parseRItype(params);
        long res = registers[parsedParams[1]] * parsedParams[2];
        registers[parsedParams[0] + 1] = (int)(res & 0x00000000FFFFFFFFL);
        registers[parsedParams[0]] = (int)(res & 0xFFFFFFFF00000000L);
        System.out.println("muli");
    }
    protected void div(String params) {
        byte[] parsedParams = instParser.parseRRtype(params);
        registers[parsedParams[0]] = registers[parsedParams[1]] / registers[parsedParams[2]];
        System.out.println("div");
    }
    protected void divi(String params) {
        short[] parsedParams  = instParser.parseRItype(params);
        registers[parsedParams[0]] = registers[parsedParams[1]] / parsedParams[2];
        System.out.println("divi");
    }
    //============================================Logical operations====================================================

    protected void and(String params) {
        byte[] parsedParams = instParser.parseRRtype(params);
        registers[parsedParams[0]] = registers[parsedParams[1]] & registers[parsedParams[2]];
        System.out.println("and");
    }
    protected void or(String params) {
        byte[] parsedParams = instParser.parseRRtype(params);
        registers[parsedParams[0]] = registers[parsedParams[1]] | registers[parsedParams[2]];
        System.out.println("or");
    }
    protected void xor(String params) {
        byte[] parsedParams = instParser.parseRRtype(params);
        registers[parsedParams[0]] = registers[parsedParams[1]] ^ registers[parsedParams[2]];
        System.out.println("xor");
    }
    protected void nand(String params) {
        byte[] parsedParams = instParser.parseRRtype(params);
        registers[parsedParams[0]] = ~(registers[parsedParams[1]] & registers[parsedParams[2]]);
        System.out.println("nand");
    }

    //===========================================Shift operations=======================================================
    protected void shl(String params) {  // Think about carry reg
        byte[] parsedParams = instParser.parseInRegisterOperation(params);
        registers[parsedParams[0]] = registers[parsedParams[0]] >>> 1;
        System.out.println("shl");
    }
    protected void shr(String params) {
        byte[] parsedParams = instParser.parseInRegisterOperation(params);

        registers[parsedParams[0]] = registers[parsedParams[0]] << 1;
        System.out.println("shr");
    }
    protected void ror(String params) {
        byte[] parsedParams = instParser.parseInRegisterOperation(params);
        int lsb = registers[parsedParams[0]] % 2;
        int res = registers[parsedParams[0]];
        int len = Integer.toBinaryString(res).length();
        registers[parsedParams[0]] = Integer.parseInt(
                Integer.toBinaryString(res).charAt(lsb) + Integer.toBinaryString(res).substring(0, len - 2), 2);
        System.out.println("ror");
    }
    protected void rol(String params) {
        byte[] parsedParams = instParser.parseInRegisterOperation(params);
        int res = registers[parsedParams[0]];
        registers[parsedParams[0]] = Integer.parseInt(Integer.toBinaryString(res).substring(1) +
                Integer.toBinaryString(res).charAt(0), 2);
        System.out.println("rol");
    }

    /**
     * compare values of two registers by subtracting, store result in the third register
     * and set corresponding flags: Zero, Negative or Overflow
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void cmp(String params) { //TODO: add updates for all flags
        byte[] parsedParams = instParser.parseRRtype(params);
        long res = (long)registers[parsedParams[1]] - (long)registers[parsedParams[2]];
        if (res > Integer.MAX_VALUE || res < Integer.MIN_VALUE) {
            registers[parsedParams[0]] = Integer.parseInt(Long.toBinaryString(res).substring(1), 2);
            registers[30] = registers[30] | (1 << 26);

        } else {
            registers[parsedParams[0]] = registers[parsedParams[1]] - registers[parsedParams[2]];
            if (registers[parsedParams[1]] == registers[parsedParams[2]]) {
                registers[30] = registers[30] | (1 << 25);
            } else if (registers[parsedParams[1]] <= registers[parsedParams[2]]) {
                registers[30] = registers[30] | (1 << 24);
            }
        }
        System.out.println("cmp");
    }






//===================================================Auxiliary methods==================================================
    protected int addrToArrIndex(int addr) { //TODO:
        return addr - RAM_OFFSET;
    }
    public String showRegisters() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            String temp = "$r" + i + ": " + registers[i] + "\n";
            sb.append(temp);
        }
        return sb.toString();
    }

    public String showRAM() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < RAM.length; i++) {
            String temp = "memory addr 0x" + (Integer.toHexString(i + RAM_OFFSET)) + ": " + RAM[i] + "\n";
            sb.append(temp);
        }
        return sb.toString();
    }


}
