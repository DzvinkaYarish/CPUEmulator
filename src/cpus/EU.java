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
    protected long[] registers = new long[32];
    protected long[] RAM;
    protected Stack<Long> stk = new Stack<>();

    private static final int RAM_OFFSET = 1053185;
    protected InstructionMap instTable = new InstructionMap();



    protected InstructionMap ALUInstructs = new InstructionMap();
    protected InstructionParser instParser = new InstructionParser();

    public EU(long[] RAM) {
        this.RAM = RAM;
    }
    public long[] getMemory() {
        return RAM;
    }
    public long[] getRegisters() {
        return registers;
    }
    public void setRegisters(long[] registers) {
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
    public int execute(String instruction) {
        instruction = instruction.replaceAll("\\s","");
        if (instruction.length() != 32) {
            System.out.println("TOO SHORT/ LONG INSTRUCTION");
            return -1;
        } else if (Long.parseLong(instruction, 2) == 0) {
            return -1;
        }
        short opcode = Short.parseShort(instruction.substring(0, 8), 2);
        try {
            instTable.getInstruction(opcode).invoke(this, instruction.substring(8));
        } catch (IllegalAccessException|InvocationTargetException ex) {

            if (ex.getCause().getClass().equals(IllegalArgumentException.class)) {
                System.out.println("Illegal arguments were passed to" + instTable.getInstruction(opcode).getName());}else if (ex.getCause().getClass().equals(IndexOutOfBoundsException.class)) {
                System.out.println("Invalid main RAM/register address in " + instTable.getInstruction(opcode).getName());
            }
        }
        return 0;
    }

    public void executeALU(String instruction, short ALUopcode) {
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
        short [] parsedParams = instParser.parseLoadStoretype(params);
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
        short [] parsedParams = instParser.parseLoadStoretype(params);
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
        int[] parsedParams = instParser.parseLoadStoretypeImValue(params);
        registers[parsedParams[0]] = parsedParams[1];
        System.out.println("seti");
    }
    //==============================================Branching===========================================================

    /**
     * Changes PC to the $raddr.
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void jmp(String params) {
        short[] parsedParams = instParser.parseBtypeOnlyJump(params);
        registers[29] = registers[parsedParams[1]];
        System.out.println("jmp");
    }

    /**
     * Changes PC to the value.
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void jmpi(String params) {
        int[] parsedParams = instParser.parseBtypeOnlyJumpImValue(params);
        registers[29] = parsedParams[1];
        System.out.println("JMPI");
    }

    /**
     * Adds the $raddr to the value of current PC to get next one.
     * (HOW? WHAT HAPPENS TO THE VALUE, WHICH WAS THERE FIRST?)
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void jmr(String params) {
        short[] parsedParams = instParser.parseBtypeOnlyJump(params);
        registers[29] += registers[parsedParams[1]];
        System.out.println("jmr");

    }

    /**
     * Adds the value to the value of current PC to get next one.
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void jmri(String params) {
        int[] parsedParams = instParser.parseBtypeOnlyJumpImValue(params);
        registers[29] += parsedParams[1];
        System.out.println("jmri");
    }

    /**
     * Jumps to $raddr1
     * if $raddr2 == $raddr3
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void jme(String params) {
        short[] parsedParams = instParser.parseBtype(params);
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
        int[] parsedParams = instParser.parseBtypeImValue(params);
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
        short[] parsedParams = instParser.parseBtype(params);
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
        int[] parsedParams = instParser.parseBtypeImValue(params);
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
        short[] parsedParams = instParser.parseBtype(params);
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
        int[] parsedParams = instParser.parseBtypeImValue(params);
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
        short[] parsedParams = instParser.parseBtype(params);
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
        int[] parsedParams = instParser.parseBtypeImValue(params);
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
        short[] parsedParams = instParser.parseBtype(params);
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
        int[] parsedParams = instParser.parseBtypeImValue(params);
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
        short[] parsedParams = instParser.parseBtype(params);
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
        int[] parsedParams = instParser.parseBtypeImValue(params);
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
        short[] parsedParams = instParser.parseBtypeOnlyJump(params);
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
        int[] parsedParams = instParser.parseBtypeOnlyJumpImValue(params);
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
        short[] parsedParams = instParser.parseBtypeOnlyJump(params);
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
        int[] parsedParams = instParser.parseBtypeOnlyJumpImValue(params);
        stk.push(registers[29]);
        registers[31] += 1;
        registers[29] += parsedParams[1];
        System.out.println("callri");
    }

    /**
     * Stores current PC to the EPC register and
     * jumps to address 0x00101201 in RAM
     * @param params not used
     */
    protected void syscall(String params) {
        registers[27] = registers[29];
        registers[29] = registers[28]; //start address of interuption handler for syscalls TODO: BASE  ADDRESSS??
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
    //============================================Branching with comparisons=======================================
    protected void cjme(String binaryString)
    {
        if ((registers[30] & (1 << 25)) != 0) {
            short[] parsedParams = instParser.parseBtypeOnlyJump(binaryString);
            registers[29] = registers[parsedParams[1]] + registers[28]; //add base address + offset
        }
        System.out.println("cjme");

    }

    protected void cjmei(String binaryString)
    {
        if ((registers[30] & (1 << 25)) != 0) {
            int[] parsedParams = instParser.parseBtypeOnlyJumpImValue(binaryString);
            registers[29] = parsedParams[1] + registers[28];
        }
        System.out.println("cjmei");

    }

    protected void cjmer(String binaryString)
    {
        if ((registers[30] & (1 << 25)) != 0) {
            short[] parsedParams = instParser.parseBtypeOnlyJump(binaryString);
            registers[29] += registers[parsedParams[1]];
        }
        System.out.println("cjmer");

    }

    protected void cjmeri(String binaryString)
    {
        if ((registers[30] & (1 << 25)) != 0) {
            int[] parsedParams = instParser.parseBtypeOnlyJumpImValue(binaryString);
            registers[29] += parsedParams[1];
        }
        System.out.println("cjmeri");

    }

    protected void cjne(String binaryString)
    {
        if ((registers[30] & (1 << 25)) == 0) {
            short[] parsedParams = instParser.parseBtypeOnlyJump(binaryString);
            registers[29] = registers[parsedParams[1]] + registers[28]; //add base address + offset
        }
        System.out.println("cjne");

    }

    protected void cjnei(String binaryString)
    {
        if ((registers[30] & (1 << 25)) == 0) {
            int[] parsedParams = instParser.parseBtypeOnlyJumpImValue(binaryString);
            registers[29] = parsedParams[1] + registers[28]; //add base address + offset
        }
        System.out.println("cjnei");

    }

    protected void cjner(String binaryString)
    {
        if ((registers[30] & (1 << 25)) == 0) {
            short[] parsedParams = instParser.parseBtypeOnlyJump(binaryString);
            registers[29] += registers[parsedParams[1]];
        }
        System.out.println("cjner");

    }

    protected void cjneri(String binaryString)
    {
        if ((registers[30] & (1 << 25)) == 0) {
            int[] parsedParams = instParser.parseBtypeOnlyJumpImValue(binaryString);
            registers[29] += parsedParams[1];
        }
        System.out.println("cjneri");

    }

    protected void cjle(String binaryString)
    {
        if ((registers[30] & (1 << 24)) != 0) {
            short[] parsedParams = instParser.parseBtypeOnlyJump(binaryString);
            registers[29] = registers[parsedParams[1]] + registers[28]; //add base address + offset
        }
        System.out.println("cjle");

    }

    protected void cjlei(String binaryString)
    {
        if ((registers[30] & (1 << 24)) != 0) {
            int[] parsedParams = instParser.parseBtypeOnlyJumpImValue(binaryString);
            registers[29] = parsedParams[1] + registers[28]; //add base address + offset
        }
        System.out.println("cjlei");

    }

    protected void cjler(String binaryString)
    {
        if ((registers[30] & (1 << 24)) != 0) {
            short[] parsedParams = instParser.parseBtypeOnlyJump(binaryString);
            registers[29] += registers[parsedParams[1]];
        }
        System.out.println("cjler");

    }

    protected void cjleri(String binaryString)
    {
        if ((registers[30] & (1 << 24)) != 0) {
            int[] parsedParams = instParser.parseBtypeOnlyJumpImValue(binaryString);
            registers[29] += parsedParams[1];
        }
        System.out.println("cjleri");

    }//=============================================BranchingBuffers======================================================
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

    protected void cjmp_t1(String binaryString) {
        if (binaryString.substring(17, 20).equals("001")) {
            cjme(binaryString.substring(8));
        } else if (binaryString.substring(17, 20).equals("010")) {
            cjle(binaryString.substring(8));
        } else {
            cjne(binaryString.substring(8));
        }
    }

    protected void cjmp_t2(String binaryString) {
        if (binaryString.substring(17, 20).equals("001")) {
            cjmei(binaryString.substring(8));
        } else if (binaryString.substring(17, 20).equals("010")) {
            cjlei(binaryString.substring(8));
        } else {
            cjnei(binaryString.substring(8));
        }
    }

    protected void cjmp_t3(String binaryString) {
        if (binaryString.substring(17, 20).equals("001")) {
            cjmer(binaryString.substring(8));
        } else if (binaryString.substring(17, 20).equals("010")) {
            cjler(binaryString.substring(8));
        } else {
            cjner(binaryString.substring(8));
        }
    }

    protected void cjmp_t4(String binaryString) {
        if (binaryString.substring(17, 20).equals("001")) {
            cjmeri(binaryString.substring(8));
        } else if (binaryString.substring(17, 20).equals("010")) {
            cjleri(binaryString.substring(8));
        } else {
            cjneri(binaryString.substring(8));
        }
    }





    //============================================Integer arithmetic====================================================

    protected void alu(String params) {
        short[] parsedParams = instParser.parseInstructForALU(params);
        executeALU(params, parsedParams[0]);
        System.out.println("ALU");  
    }


    protected void add(String params) {
        short[] parsedParams = instParser.parseRRtype(params);
        long res = registers[parsedParams[1]] + registers[parsedParams[2]];
        if (res > Integer.MAX_VALUE || res < Integer.MIN_VALUE) {
            registers[parsedParams[0]] = Integer.parseInt(Long.toBinaryString(res).substring(1), 2);
            registers[30] = registers[30] | (1 << 26);

        } else {
            registers[parsedParams[0]] = registers[parsedParams[1]] + registers[parsedParams[2]];
            registers[30] = registers[30] & (~ (1 << 26));
        }
        setFlags(res);
        System.out.println("add");
    }
    protected void addi(String params) {

        int[] parsedParams  = instParser.parseRItype(params);
        long res = registers[parsedParams[1]] + (long)parsedParams[2];
        if (res > Integer.MAX_VALUE || res < Integer.MIN_VALUE) {
            registers[parsedParams[0]] = Integer.parseInt(Long.toBinaryString(res).substring(1), 2);
            registers[30] = registers[30] | (1 << 26);
        } else {
            registers[parsedParams[0]] = registers[parsedParams[1]] + parsedParams[2];
            registers[30] = registers[30] & (~ (1 << 26));
        }
        setFlags(res);
        System.out.println("addi");
    }

    protected void sub(String params) {
        short[] parsedParams = instParser.parseRRtype(params);
        long res = registers[parsedParams[1]] - registers[parsedParams[2]];
        if (res > Integer.MAX_VALUE || res < Integer.MIN_VALUE) {
            registers[parsedParams[0]] = Integer.parseInt(Long.toBinaryString(res).substring(1), 2);
            registers[30] = registers[30] | (1 << 26);
        } else {
            registers[parsedParams[0]] = registers[parsedParams[1]] - registers[parsedParams[2]];
        }
        setFlags(res);
        System.out.println("sub");
    }
    protected void subi(String params) {
        int[] parsedParams  = instParser.parseRItype(params);
        long res = (long)registers[parsedParams[1]] - (long)parsedParams[2];
        if (res > Integer.MAX_VALUE || res < Integer.MIN_VALUE) {
            registers[parsedParams[0]] = Integer.parseInt(Long.toBinaryString(res).substring(1), 2);
            registers[30] = registers[30] | (1 << 26);
        } else {
            registers[parsedParams[0]] = registers[parsedParams[1]] - parsedParams[2];
        }
        setFlags(res);
        System.out.println("subi");

    }
    protected void mul(String params) {
        short[] parsedParams = instParser.parseRRtype(params);
        long res = registers[parsedParams[1]] * registers[parsedParams[2]];
        registers[parsedParams[0] + 1] = (int)(res & 0x00000000FFFFFFFFL);
        registers[parsedParams[0]] = (int)(res & 0xFFFFFFFF00000000L);
        setFlags(res);
        System.out.println("mul");
    }
    protected void muli(String params) {
        int[] parsedParams  = instParser.parseRItype(params);
        long res = registers[parsedParams[1]] * parsedParams[2];
        registers[parsedParams[0] + 1] = (int)(res & 0x00000000FFFFFFFFL);
        registers[parsedParams[0]] = (int)(res & 0xFFFFFFFF00000000L);
        setFlags(res);
        System.out.println("muli");
    }
    protected void div(String params) {
        short[] parsedParams = instParser.parseRRtype(params);
        registers[parsedParams[0]] = registers[parsedParams[1]] / registers[parsedParams[2]];
        long res = registers[parsedParams[0]];
        setFlags(res);
        System.out.println("div");
    }
    protected void divi(String params) {
        int[] parsedParams  = instParser.parseRItype(params);
        registers[parsedParams[0]] = registers[parsedParams[1]] / parsedParams[2];
        long res = registers[parsedParams[0]];
        setFlags(res);
        System.out.println("divi");
    }
    //============================================Logical operations====================================================

    protected void and(String params) {
        short[] parsedParams = instParser.parseRRtype(params);
        registers[parsedParams[0]] = registers[parsedParams[1]] & registers[parsedParams[2]];
        System.out.println("and");
    }
    protected void or(String params) {
        short[] parsedParams = instParser.parseRRtype(params);
        registers[parsedParams[0]] = registers[parsedParams[1]] | registers[parsedParams[2]];
        System.out.println("or");
    }
    protected void xor(String params) {
        short[] parsedParams = instParser.parseRRtype(params);
        registers[parsedParams[0]] = registers[parsedParams[1]] ^ registers[parsedParams[2]];
        System.out.println("xor");
    }
    protected void nand(String params) {
        short[] parsedParams = instParser.parseRRtype(params);
        registers[parsedParams[0]] = ~(registers[parsedParams[1]] & registers[parsedParams[2]]);
        System.out.println("nand");
    }

    //===========================================Shift operations=======================================================
    protected void shl(String params) {  // Think about carry reg
        short[] parsedParams = instParser.parseInRegisterOperation(params);
        registers[parsedParams[0]] = registers[parsedParams[0]] >>> 1;
        System.out.println("shl");
    }
    protected void shr(String params) {
        short[] parsedParams = instParser.parseInRegisterOperation(params);

        registers[parsedParams[0]] = registers[parsedParams[0]] << 1;
        System.out.println("shr");
    }
    protected void ror(String params) {
        short[] parsedParams = instParser.parseInRegisterOperation(params);
        long lsb = registers[parsedParams[0]] % 2;
        long res = registers[parsedParams[0]];
        int len = Long.toBinaryString(res).length();
        registers[parsedParams[0]] = Long.parseLong(
                Long.toBinaryString(res).charAt((int)lsb) + Long.toBinaryString(res).substring(0, len - 2), 2);
        System.out.println("ror");
    }
    protected void rol(String params) {
        short[] parsedParams = instParser.parseInRegisterOperation(params);
        long res = registers[parsedParams[0]];
        registers[parsedParams[0]] = Long.parseLong(Long.toBinaryString(res).substring(1) +
                Long.toBinaryString(res).charAt(0), 2);
        System.out.println("rol");
    }

    /**
     * compare values of two registers by subtracting, store result in the third register
     * and set corresponding flags: Zero, Negative or Overflow
     * @param params 24 binary string with all necessary arguments and info for instruction execution
     */
    protected void cmp(String params) { //TODO: add updates for all flags
        short[] parsedParams = instParser.parseRRtype(params);
        long res = (long)registers[parsedParams[1]] - (long)registers[parsedParams[2]];
        if (res > Integer.MAX_VALUE || res < Integer.MIN_VALUE) {
            registers[parsedParams[0]] = Long.parseLong(Long.toBinaryString(res).substring(1), 2);
            registers[30] = registers[30] | (1 << 26);

        } else {
            registers[parsedParams[0]] = registers[parsedParams[1]] - registers[parsedParams[2]];
            registers[30] = registers[30] & (~(1 << 26));
        }
        setFlags(res);
        System.out.println("cmp");
    }






//===================================================Auxiliary methods==================================================
    protected int addrToArrIndex(long addr) {
        return (int) (addr - RAM_OFFSET);
    }
    public String showRegisters(long[] prevReg) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32; i++) {
           if (prevReg[i] != registers[i]) {
                String temp = "$r" + i + ": " + registers[i] + "\n";
                sb.append(temp);
            }
        }
        return sb.toString();
    }

    public String showRAM(long[] prevRAM) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < RAM.length; i++) {
            if(prevRAM[i] != RAM[i]) {
                String temp = "memory addr 0x" + (Integer.toHexString(i + RAM_OFFSET)) + ": " + RAM[i] + "\n";
                sb.append(temp);
            }
        }
        return sb.toString();
    }
    void setFlags(long res) {
        if (res < 0) {
            registers[30] = registers[30] | (1 << 24);
        } else {
            registers[30] = registers[30] & (~ (1 << 24));
        }
        if (res == 0) {
            registers[30] = registers[30] | (1 << 25);
        } else {
            registers[30] = registers[30] & (~ (1 << 25));
        }
    }
    void dummy(String bin)
    {}


}
