package com.mlpj.ontology.file;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.List;

public class FileHelper {
    public static void saveToFile(String data, String filename, boolean append) {
        try {
            File file = new File(System.getProperty("user.dir") + "/" + filename);

            /* This logic is to create the file if the
             * file is not already present
             */
            if (!file.exists()) {
                file.createNewFile();
            }

            //Here true is to append the content to file
            FileWriter fw = new FileWriter(file, append);
            //BufferedWriter writer give better performance
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(data);
            //Closing BufferedWriter Stream
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(File file1, File file2) {
        InputStream inStream;
        OutputStream outStream;

        try {
            inStream = new FileInputStream(file1);
            outStream = new FileOutputStream(file2, true);

            int length;
            byte[] buffer = new byte[1024];

            // copy the file content in bytes
            while ((length = inStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, length);
            }
            System.out.println("File is copied successful!");
            inStream.close();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveToJSONFile(JSONObject jsonObject, String filename) {
        try {
            JSONParser jsonParser = new JSONParser();
            File file = new File(System.getProperty("user.dir") + "/" + filename);


            if (!file.exists()) {
                file.createNewFile();
                saveToFile("[]", filename, false);
            }
            FileReader reader = new FileReader(System.getProperty("user.dir") + "/" + filename);
            JSONArray object;
            try {
                Object obj = jsonParser.parse(reader);

                object = (JSONArray) obj;
            } catch (Exception e) {

                object = new JSONArray();
            }

            object.add(jsonObject);
            saveToFile(object.toString(), filename, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            File file = new File(System.getProperty("user.dir") + "/" + System.currentTimeMillis() + "_nlu.yml");
            if (!file.exists()) {
                file.createNewFile();
            }
            copyFile(new File(System.getProperty("user.dir") + "/question/Person_question2.txt"), file);
            saveToFile("\n", file.getName(), true);
            copyFile(new File(System.getProperty("user.dir") + "/question/Person_question.txt"), file);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
