package instructimplement;

/**
 * Created by dzvinka on 12.03.17.
 */
public class InstructionParser {
    public short[] parseRRtype(String binString) {
        binString = binString.replaceAll("\\s","");
        short[] params = new short[5];
        params[0] = Short.parseShort(binString.substring(0, 5), 2); // target r
        params[1] = Short.parseShort(binString.substring(5, 10), 2); //source r 1
        params[2] = Short.parseShort(binString.substring(10, 15), 2);//source r 2
        params[3] = Short.parseShort(binString.substring(15, 20), 2);  //ALU/FPU operation
        params[4] = Short.parseShort(binString.substring(23), 2);  //mark if the next instruction depends on the
        //result of this or previous ones.

        return params;
    }

    public int[] parseRItype(String binString) {
        binString = binString.replaceAll("\\s","");
        int[] params = new int[5];
        params[0] = Integer.parseInt(binString.substring(0, 5), 2); // target r
        params[1] = Integer.parseInt(binString.substring(5, 10), 2); //source r
        params[2] = Integer.parseInt(binString.substring(10, 22), 2);//source imvalue
        params[3] = Integer.parseInt(binString.substring(22, 23), 2);  // indicates if the next instruction is used for storing data for this one
        params[4] = Integer.parseInt(binString.substring(23), 2);  //mark if the next instruction depends on the
        //result of this or previous ones.

        return params;
    }

    public short[] parseLoadStoretype(String binString) {
        binString = binString.replaceAll("\\s","");
        short[] params = new short[3];
        params[0] = Short.parseShort(binString.substring(0, 5), 2); // source r of data
        params[1] = Short.parseShort(binString.substring(5, 10), 2); //source r of memory addr
        params[2] = Short.parseShort(binString.substring(23), 2);  //mark if the next instruction depends on the
        //result of this or previous ones.
        return params;

    }
    public int[] parseLoadStoretypeImValue(String binString) {
        binString = binString.replaceAll("\\s","");
        int[] params = new int[3];
        params[0] = Integer.parseInt(binString.substring(0, 5), 2); // source r of data/sourec r of memory addr
        params[1] = Integer.parseInt(binString.substring(5, 21), 2); //memory addr/ data to store in r
        params[2] = Integer.parseInt(binString.substring(23), 2);  //mark if the next instruction depends on the
        //result of this or previous ones.
        return params;
    }

    public short[] parseBtype(String binString) {
        binString = binString.replaceAll("\\s","");
        short[] params = new short[5];
        params[0] = Short.parseShort(binString.substring(0, 3), 2); // specify which comparison should be done
        params[1] = Short.parseShort(binString.substring(3, 8), 2); //source r 1 with data to be compared
        params[2] = Short.parseShort(binString.substring(8, 13), 2);//source r 2 with data to be compared
        params[3] = Short.parseShort(binString.substring(13, 18), 2);  // r with target addr
        params[4] = Short.parseShort(binString.substring(23), 2);  //mark if the next instruction depends on the
        //result of this or previous ones.

        return params;

    }

    public int[] parseBtypeImValue(String binString) {
        binString = binString.replaceAll("\\s","");
        int[] params = new int[5];
        params[0] = Integer.parseInt(binString.substring(0, 3), 2); // specify which comparison should be done
        params[1] = Integer.parseInt(binString.substring(3, 8), 2); //source r 1 with data to be compared
        params[2] = Integer.parseInt(binString.substring(8, 13), 2);//source r 2 with data to be compared
        params[3] = Integer.parseInt(binString.substring(13, 23), 2);  // im value
        params[4] = Integer.parseInt(binString.substring(23), 2);  //mark if the next instruction depends on the
        //result of this or previous ones.

        return params;

    }



    public short[] parseBtypeOnlyJump(String binString) {
        binString = binString.replaceAll("\\s","");
        short[] params = new short[3];
        params[0] = Short.parseShort(binString.substring(0, 3), 2); // specify which comparison should be done
        params[1] = Short.parseShort(binString.substring(3, 8), 2); // r with target addr

        params[2] = Short.parseShort(binString.substring(23), 2);  //mark if the next instruction depends on the
        //result of this or previous ones.

        return params;

    }

    public int[] parseBtypeOnlyJumpImValue(String binString) {
        binString = binString.replaceAll("\\s","");
        int[] params = new int[3];
        params[0] = Integer.parseInt(binString.substring(0, 3), 2); // specify which comparison should be done
        params[1] = Integer.parseInt(binString.substring(3, 19), 2); // target addr(offset value)

        params[2] = Integer.parseInt(binString.substring(23), 2);  //mark if the next instruction depends on the
        //result of this or previous ones.

        return params;

    }


    //Created by me for ror, rol, shl, shr
    public short[] parseInRegisterOperation(String binString) {
        binString = binString.replaceAll("\\s","");
        short [] params = new short[2];
        params[0] = Short.parseShort(binString.substring(0, 5), 2);
        params[1] = Short.parseShort(binString.substring(23), 2);  //mark if the next instruction depends on the
        //result of this or previous ones.
        return params;
    }

    public short[] parseInstructForALU(String binString) {
        short [] params = new short[1];
        params[0] = Short.parseShort(binString.substring(15, 20), 2); //ALU operation
        return params;
    }



    //ToDo: decide whether right parser for only one instruction - compare?



}