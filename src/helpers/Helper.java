package helpers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by dzvinka on 06.04.17.
 */
public class Helper {

    public ArrayList<Long> readFile(String filename) {
        BufferedReader br = null;
        FileReader fr = null;
        ArrayList<Long> arl = new ArrayList<>();

        try {

            fr = new FileReader(filename);
            br = new BufferedReader(fr);

            String sCurrentLine;

            br = new BufferedReader(new FileReader(filename));

            while ((sCurrentLine = br.readLine()) != null) {
               // System.out.println(sCurrentLine);
                arl.add(Long.valueOf(sCurrentLine.substring(0, 32), 2));
               // arl.add(Long.valueOf(sCurrentLine.substring(32), 2));
            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException ex) {

                ex.printStackTrace();
            }
        }

        return arl;
    }

    public String intToBinString(long n) {
        String binStr = Long.toBinaryString(n);
        while (binStr.length() < 32) {

            binStr = '0' + binStr;
        }
        return binStr;
    }
}
