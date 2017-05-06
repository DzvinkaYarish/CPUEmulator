package instructimplement;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by dzvinka on 26.02.17.
 */
public class InstructionMap {
    private HashMap<Integer, Method> instructionTable= new HashMap<>();
    public void addInstruction(int opcode, Method action) {
        instructionTable.put(opcode, action);
    }
    public Method getInstruction(int opcode) {
        return instructionTable.get(opcode);
    }
    public int size() {
        return instructionTable.size();
    }
}
