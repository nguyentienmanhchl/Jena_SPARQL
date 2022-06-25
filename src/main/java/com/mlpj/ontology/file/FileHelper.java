package com.mlpj.ontology.file;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class FileHelper {
    public static void saveToFile(String data,String filename) {
        try {
            File file = new File(System.getProperty("user.dir")+"/"+filename);

            /* This logic is to create the file if the
             * file is not already present
             */
            if (!file.exists()) {
                file.createNewFile();
            }

            //Here true is to append the content to file
            FileWriter fw = new FileWriter(file, true);
            //BufferedWriter writer give better performance
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(data);
            //Closing BufferedWriter Stream
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
