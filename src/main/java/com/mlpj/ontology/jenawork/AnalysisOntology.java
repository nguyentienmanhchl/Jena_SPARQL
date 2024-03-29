package com.mlpj.ontology.jenawork;

import com.mlpj.ontology.file.FileHelper;
import com.mlpj.ontology.util.Constant;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class AnalysisOntology {
    private static String ontoFile = Constant.FILE;

    public static void getOntologyInfomation() {
        OntModel ontoModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        try {
            InputStream in = FileManager.get().open(ontoFile);
            try {
                ontoModel.read(in, "");
                ExtendedIterator<AnnotationProperty> iterator1 = ontoModel.listAnnotationProperties();
                ExtendedIterator<OntClass> iterator2 = ontoModel.listClasses();

                List<ExtendedIterator> list = new ArrayList<>();
                list.add(iterator1);
                list.add(iterator2);

                for (ExtendedIterator extendedIterator : list) {
                    while (extendedIterator.hasNext()) {
                        Object i = extendedIterator.next();
                        if (i.toString().contains("#")) {
                            FileHelper.saveToFile(i.toString().split("#")[1] + "\n", "property.txt",true);
                        }
                    }
                    FileHelper.saveToFile("---------------------------\n", "property.txt",true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (JenaException je) {
            System.err.println("ERROR" + je.getMessage());
            je.printStackTrace();
            System.exit(0);
        }

    }

    public static List<String> getProperties() {
        OntModel ontoModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        List<String> results = new ArrayList<>();
        try {
            InputStream in = FileManager.get().open(ontoFile);
            try {
                ontoModel.read(in, "");
                ExtendedIterator<AnnotationProperty> iterator = ontoModel.listAnnotationProperties();


                while (iterator.hasNext()) {
                    String i = iterator.next().toString();
                    String [] temp = i.split("/");

                    results.add(temp[temp.length-1].replaceAll("#",":"));


                }
                return results;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (JenaException je) {
            System.err.println("ERROR" + je.getMessage());
            je.printStackTrace();
            System.exit(0);
        }
        return null;

    }

    public static String answer(String predicate) {

        String query = Constant.PREFIX_QUERY +
                "SELECT   ?X ?Y ?Z ?U ?U1 WHERE { ?X " + predicate + " ?Z." +
                "?X rdf:type ?U." +
                predicate + " rdfs:comment ?Y." +
                "?U rdfs:label ?U1}";
        String results = InitJena.getItems3(query,predicate);
        FileHelper.saveToFile(results, "answer.txt",true);

        return results;
    }

    public static boolean createAnswer() {
        List<String> list = getProperties();
        for (String s : list) {
            if (s.contains("core")||s.contains("belongEthnic")||s.contains("hasCountry")||
            s.contains("hasSameEducationDegreeLevel")||s.contains("wasDerivedFrom")){
                continue;
            }
            answer(s);
        }
//        String[] list = {"vntourism:isApartOf",
//                "vntourism:isHeldAt",
//                "vntourism:wasBuiltIn",
//                "vntourism:hasCommemorate",
//                "time:hasBeginning",
//                "time:hasEnd",
//                "vntourism:related",
//                "vntourism:hasDied",
//                "vntourism:hasBorn",
//                "vntourism:hasPredecessor",
//                "vntourism:orKnownAs",
//                "vntourism:hasBirthName",
//                "vntourism:hasTimeHappen",
//                "vntourism:chosenCapitalBy",
//                "vntourism:wasBuiltBy",
//                "vntourism:buriedPlace",
//                "vntourism:hasSuccessor",
//                "vntourism:hasBornAt"
//        };
//        for (int i = 0; i < list.length; i++) {
//            answer(list[i]);
//        }
        return true;
    }

    public static boolean genQuestion() {
        try {
            File myObj = new File("answer.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                data = data.replaceAll("@en", "");
                data = data.replaceAll("@vn", "");
                String[] list = data.split("~");
                String result;
//                result = "    - [" + list[0].toLowerCase(Locale.ROOT) + "]{\"entity\":\"class\"} nào " + list[2] +
//                        " [" + InitJena.pretty2(list[3]) + "]{\"entity\":\"object\",\"value\":\""+list[3]+"\"}?\n";
                result = "    - [" + list[0].toLowerCase(Locale.ROOT) + "]{\"entity\":\"class\"} nào " +
                        "[" + list[2] + "]{\"entity\":\"predicate\",\"value\":\""+list[5]+"\"}" +
                        " [" + InitJena.pretty2(list[3]) + "]{\"entity\":\"object\",\"value\":\""+list[3]+"\"}?\n";
                FileHelper.saveToFile(result, "question/" + list[4] + "_question.txt",true);
//                result = list[0].toLowerCase(Locale.ROOT) + " nào " + list[2] + " " + InitJena.pretty2(list[3]) + "?\n";
//                FileHelper.saveToFile(result, "question.txt",true);

            }
            myReader.close();
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean genQuestion2() {
        try {
            File myObj = new File("answer.txt");
            Scanner myReader = new Scanner(myObj);
            List<String> stringList = new ArrayList<>();
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                data = data.replaceAll("@en", "");
                data = data.replaceAll("@vn", "");
                stringList.add(data);
            }
            for (String i : stringList) {
                for (String j : stringList) {
                    if (!i.equals(j)) {
                        String[] list1 = i.split("~");
                        String[] list2 = j.split("~");
                        if (list1[1].equals(list2[1])) {
                            String result;
//                            result = "    - [" + list1[0].toLowerCase(Locale.ROOT) + "]{\"entity\":\"X\"} nào " + "[" + list1[2] + "]{\"entity\":\"predicate\"} " + "[" + list1[3] + "]{\"entity\":\"object\"} " +
//                                    "và [" + list2[2] + "]{\"entity\":\"predicate\"} " + "[" + list2[3] + "]{\"entity\":\"object\"} ?\n";
                            result = "    - " + list1[0].toLowerCase(Locale.ROOT) + " nào " + list1[2] +
                                    " [" + InitJena.pretty2(list1[3]) + "]{\"entity\":\"object\",\"value\":\""+list1[3]+"\"} " +
                                    "và " + list2[2] + " [" + InitJena.pretty2(list2[3]) + "]{\"entity\":\"object\",\"value\":\""+list2[3]+"\"} ?\n";
                            FileHelper.saveToFile(result, "question/" + list1[4] + "_question2.txt",true);
//                            result = list1[0].toLowerCase(Locale.ROOT) + " nào " + list1[2] + " " + list1[3] + " và " + list2[2] + " " + list2[3] + "?\n";
//                            FileHelper.saveToFile(result, "question.txt",true);

                        }
                    }
                }
            }
            myReader.close();
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean genQuestion3() {
        try {
            File myObj = new File("answer.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                data = data.replaceAll("@en", "");
                data = data.replaceAll("@vn", "");
                String[] list = data.split("~");
                String result;
//                result = "    - [" + list[1] + "]{\"entity\":\"object\"}  " + "[" + list[2] + "]{\"entity\":\"predicate\"} " + "?\n";
                result = "    - [" + InitJena.pretty2(list[1]) + "]{\"entity\":\"object\",\"value\":\""+list[1]+"\"}  " + list[2] + " ?\n";
                FileHelper.saveToFile(result, "question/about_" + list[4] + "_question.txt",true);
//                result = InitJena.pretty2(list[1]) + " " + list[2] + " ?\n";
//                FileHelper.saveToFile(result, "question.txt",true);


            }
            myReader.close();
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return false;
    }


    public static void main(String[] args) {

        List<String> list = getProperties();
        for (String i : list) {
            System.out.println(i);
        }
//        AnalysisOntology.getOntologyInfomation();


////      AnalysisOntology.answer("hasBeginning");
////      AnalysisOntology.answer("hasEnd");

//        AnalysisOntology.createAnswer();
//        AnalysisOntology.genQuestion();
//        AnalysisOntology.genQuestion2();
//        AnalysisOntology.genQuestion3();


    }
}
