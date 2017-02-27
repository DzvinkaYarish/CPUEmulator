package cpus;

import instructimplement.InstructionMap;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by dzvinka on 26.02.17.
 */
public class EU {
    private int[] registers = new int[32];
    private int[] memory;
    protected InstructionMap instTable = new InstructionMap();
    public EU(int[] memory) {
        this.memory = memory;
    }
    public int[] getMemory() {
        return memory;
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

    /**
     * execute a command, based on its opcode
     * @param opcode of the command to execute
     * @param args with which the command will be executed
     */
    public void execute(byte opcode, int... args) {
        try {
            instTable.getInstruction(opcode).invoke(this, args);
        } catch (IllegalAccessException|InvocationTargetException ex) {
            if (ex.getCause().getClass().equals(IllegalArgumentException.class)) {
                System.out.println("Illegal arguments were passed to" + instTable.getInstruction(opcode).getName());
            }else if (ex.getCause().getClass().equals(IndexOutOfBoundsException.class)) {
                System.out.println("Invalid main memory/register address in " + instTable.getInstruction(opcode).getName());
            }
        }
    }


    //Load/Store

    /**
     * Loads one word (basically  32-bit) from memory at the address maddr to the register raddr.
     * @param maddr memory cell address
     * @param raddr register address
     */
    private void lw(byte maddr, byte raddr) {
        registers[addrToArrIndex(raddr)] = memory[addrToArrIndex(maddr)];
    }

    /**
     * Loads one byte from memory at the ad-
     dress maddr to the 8 most significant bits of register raddr.
     * @param maddr memory cell address
     * @param raddr register address
     */
    private void lb(byte maddr, byte raddr) { //TODO: think about it

    }

    /**
     * stores a word from a register raddr1 to memory at the address stored in register raddr2
     * @param raddr1
     * @param raddr2
     */
    private void sw(byte raddr1, byte raddr2) { //
        memory[addrToArrIndex((byte)registers[addrToArrIndex(raddr2)])] = registers[addrToArrIndex(raddr1)];
    }

    /**
     * Stores a byte from 8 most signicant bits
     * of a register raddr1 to memory at the address stored in register raddr2
     * @param raddr1
     * @param raddr2
     */
    private void sb(byte raddr1, byte raddr2) {

    }

    /**
     * Stores sign extended value of value to the
     * register raddr
     * @param raddr
     * @param value
     */
    private void seti(byte raddr, int value) { //Stores sign extended value of val to the $t register
        registers[addrToArrIndex(raddr)] = value;
    }

    //Integer arithmetic

    private void add(byte raddrval1, byte raddrval2, byte raddr) {
        registers[addrToArrIndex(raddr)] = registers[addrToArrIndex(raddrval1)] + registers[addrToArrIndex(raddrval2)];
    }
    private void addi(byte raddrval1, int imvalue, byte raddr) {
        registers[addrToArrIndex(raddr)] = registers[addrToArrIndex(raddrval1)] + imvalue;
    }

    private void sub(byte raddrval1, byte raddrval2, byte raddr) {
        registers[addrToArrIndex(raddr)] = registers[addrToArrIndex(raddrval1)] - registers[addrToArrIndex(raddrval2)];
    }
    private void subi(byte raddrval1, int imvalue, byte raddr) {
        registers[addrToArrIndex(raddr)] = registers[addrToArrIndex(raddrval1)] - imvalue;
    }

    private void mul(byte raddrval1, byte raddrval2, byte raddr) {

        long res = registers[addrToArrIndex(raddrval1)] * registers[addrToArrIndex(raddrval2)];
        registers[addrToArrIndex(raddr) + 1] = (int)(res >> 32);
        registers[addrToArrIndex(raddr)] = (int)(res & 0xFFFFFFFF00000000L);
    }
    private void muli(byte raddrval1, int imvalue, byte raddr) {
        long res = registers[addrToArrIndex(raddrval1)] * imvalue;
        registers[addrToArrIndex(raddr) + 1] = (int)(res >> 32);
        registers[addrToArrIndex(raddr)] = (int)(res & 0xFFFFFFFF00000000L);
    }
    private void div(byte raddrval1, byte raddrval2, byte raddr) {
        registers[addrToArrIndex(raddr)] = registers[addrToArrIndex(raddrval1)] / registers[addrToArrIndex(raddrval2)];
    }
    private void divi(byte raddrval1, int imvalue, byte raddr) {
        registers[addrToArrIndex(raddr)] = registers[addrToArrIndex(raddrval1)] / imvalue;
    }
    //Logical operations
    private void and(byte raddrval1, byte raddrval2, byte raddr) {
        registers[addrToArrIndex(raddr)] = registers[addrToArrIndex(raddrval1)] & registers[addrToArrIndex(raddrval2)];
    }
    private void or(byte raddrval1, byte raddrval2, byte raddr) {
        registers[addrToArrIndex(raddr)] = registers[addrToArrIndex(raddrval1)] | registers[addrToArrIndex(raddrval2)];
    }
    private void xor(byte raddrval1, byte raddrval2, byte raddr) {
        registers[addrToArrIndex(raddr)] = registers[addrToArrIndex(raddrval1)] ^ registers[addrToArrIndex(raddrval2)];
    }
    private void nand(byte raddrval1, byte raddrval2, byte raddr) {
        registers[addrToArrIndex(raddr)] = ~(registers[addrToArrIndex(raddrval1)] & registers[addrToArrIndex(raddrval2)]);
    }

    //Shift operations
    private void shl(byte raddr) {  // Think about carry reg
        registers[addrToArrIndex(raddr)] = registers[addrToArrIndex(raddr)] >>> 1;
    }
    private void shr(byte raddr) {
        int res = registers[addrToArrIndex(raddr)] << 1;
        registers[addrToArrIndex(raddr)] = Integer.parseInt(Integer.toBinaryString(res).substring(1), 2);
    }
    protected void ror(byte raddr) {
        int lsb = registers[addrToArrIndex(raddr)] % 2;
        int res = registers[addrToArrIndex(raddr)] >> 1;
        registers[addrToArrIndex(raddr)] = Integer.parseInt(Integer.toString(lsb)
                                                            + Integer.toBinaryString(res).substring(1), 2);
    }
    protected void rol(byte raddr) {
        int res = registers[addrToArrIndex(raddr)] << 1;
        registers[addrToArrIndex(raddr)] = Integer.parseInt(Integer.toBinaryString(res).charAt(0)
                                                            + Integer.toBinaryString(res).substring(1), 2);
    }
















    private int addrToArrIndex(byte addr) { //TODO:
        return 0;
    }

}
