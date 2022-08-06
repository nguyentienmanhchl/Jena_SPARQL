package com.mlpj.ontology.controllers;

import com.mlpj.ontology.file.FileHelper;
import com.mlpj.ontology.jenawork.AnalysisOntology;
import com.mlpj.ontology.jenawork.InitJena;
import com.mlpj.ontology.util.Constant;
import org.apache.jena.vocabulary.RDFS;
import org.json.simple.JSONObject;

import org.springframework.web.bind.annotation.*;

import java.util.Random;


@RestController
public class Controller {


    @GetMapping("/one_condition")
    public String oneCondition(@RequestBody String data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Output RASA", data);
        String[] list = data.split("~");

        if (!list[2].contains(":")) {
            list[2] = "\"" + list[2] + "\"";
        }
        if (!list[0].contains(":")) {
            list[0] = "?A";
        }
        if (list[1].equals(".") || !list[1].contains(":")) {
            list[1] = "?B";
        }


        String queryString = "SELECT distinct ?X  " +
                "WHERE { ?X  " + list[1] + " " + list[2] + ". " +
                "?X rdf:type " + list[0] + " }";
        jsonObject.put("SPARQL", queryString);
        queryString = Constant.PREFIX_QUERY + queryString;
        String results = InitJena.getItems4(queryString);
        if (!results.equals("")) {
            jsonObject.put("Answer", "Đó là " + results);

        }
        if (!list[3].equals(".")) {
            String[] time = InitJena.convertDate(list[3]);
            String type;
            if (list[2].contains("âm lịch") || list[2].contains("Âm lịch")) {
                type = "vntourism:LunarCalendar";
            } else {
                type = "time:ChronometricGeologicTime";
            }
            String query = "SELECT distinct ?X  " +
                    "WHERE { ?X " + list[1] + " ?T. " +
                    "?X rdf:type " + list[0] + " . " +
                    "?T rdf:type time:DateTimeDescription . " +
                    "?T time:hasTRS " + type + " . " +
                    "?T time:month \"" + time[1] + "\" . " +
                    "optional{?T time:day \"" + time[2] + "\"}" +
                    "optional{?T time:year \"" + time[0] + "\"}}";


            jsonObject.put("SPARQL", query);
            query = Constant.PREFIX_QUERY + query;
            String results2 = InitJena.getItems4(query);
            if (!results2.equals("")) {
                results = results2;
                jsonObject.put("Answer", "Đó là " + results);

            }
        }
        if (results.equals("")) {

            jsonObject.put("Answer", "Không tìm được");
            return "Không tìm được" + "~" + jsonObject.get("SPARQL");
        }


        FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
        return "Đó là " + results + "~" + jsonObject.get("SPARQL");
    }

    @GetMapping("/two_conditions")
    public String twoConditions(@RequestBody String data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Output RASA", data);
        String[] list = data.split("~");


        if (!list[2].contains(":")) {
            list[2] = "\"" + list[2] + "\"";
        }

        if (!list[4].contains(":")) {
            list[4] = "\"" + list[4] + "\"";
        }

        if (list[0].equals(".") || !list[0].contains(":")) {
            list[0] = "?A";
        }
        if (!list[1].contains(":")) {
            list[1] = "?B";
        }
        if (!list[3].contains(":")) {
            list[3] = "?C";
        }

        String queryString = "SELECT distinct ?X  WHERE { ?X  " + list[1] + " " + list[2] + ". " +
                "?X " + list[3] + " " + list[4] + ". " +
                "?X rdf:type " + list[0] + "}";
        jsonObject.put("SPARQL", queryString);
        queryString = Constant.PREFIX_QUERY + queryString;
        String results = InitJena.getItems4(queryString);

        if (results.equals("")) {
            jsonObject.put("Answer", "Không tìm được");
            FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
            return "Không tìm được" + "~" + jsonObject.get("SPARQL");
        }
        jsonObject.put("Answer", "Đó là " + results);
        FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
        return "Đó là " + results + "~" + jsonObject.get("SPARQL");
    }

    @GetMapping("/ask_one_property")
    public String askOneProperty(@RequestBody String data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Output RASA", data);
        String[] list = data.split("~");

        if (!list[1].contains(":")) {
            list[1] = "?A";
        }
        if (!list[0].contains(":")) {
            list[0] = list[0].replaceAll(" ", "_");
            list[0] = "vntourism:" + list[0];
        }
        if (!list[2].contains(":")) {
            list[2] = list[2].replaceAll(" ", "_");
            list[2] = "vntourism:" + list[2];
        }

        String queryString1 = "SELECT ?X " +
                "WHERE {" + list[0] + " " + list[1] + " ?X.}";
        jsonObject.put("SPARQL", queryString1);

        queryString1 = Constant.PREFIX_QUERY + queryString1;
        String queryResult1 = InitJena.getItems4(queryString1);
        String queryString2 = Constant.PREFIX_QUERY + " SELECT ?X WHERE {" + list[1] + " rdfs:comment ?X}";
        String queryResult2 = InitJena.getItems5(queryString2);


        String results = InitJena.pretty2(list[0]) + " " + queryResult2 + " " + queryResult1;


        if (queryResult1.equals("")) {
            if (!list[2].equals("vntourism:x")) {
                queryString1 = "SELECT ?X " +
                        "WHERE {" + list[2] + " " + list[1] + " ?X.}";
                jsonObject.put("SPARQL", queryString1);

                queryString1 = Constant.PREFIX_QUERY + queryString1;
                queryResult1 = InitJena.getItems4(queryString1);
            }
            if (queryResult1.equals("")) {
                jsonObject.put("Answer", "Tôi không biết.");
                FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
                return "Tôi không biết." + "~" + jsonObject.get("SPARQL");
            } else {
                results = InitJena.pretty2(list[2]) + " " + queryResult2 + " " + queryResult1;
            }
        }
        jsonObject.put("Answer", results);
        FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
        return results + "~" + jsonObject.get("SPARQL");
    }

    @GetMapping("/all")
    public String all(@RequestBody String data) {
        Random random = new Random();
        int temp;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Output RASA", data);

        String[] list = data.split("~");
        String s = list[0];
        list[0] = list[0].replaceAll(" ", "_");
        list[0] = list[0].replaceAll("/", "");
        if (!list[0].contains(":")) {
            list[0] = "vntourism:" + list[0];
        }
        String queryString, queryString1, queryString2, queryResult1, queryResult2, results;
        if (list[0].equals("vntourism:Hà_Nội")) {
            queryString1 = "SELECT distinct ?X " +
                    "WHERE { ?X rdf:type vntourism:Person." +
                    "?Y rdf:type vntourism:Festival." +
                    "?Y vntourism:hasCommemorate ?X." +
                    "?Y vntourism:isHeldAt " + list[0] + "}";
            queryString2 = "SELECT  ?X  " +
                    "WHERE { {?X rdf:type vntourism:NaturalArea}union{?X rdf:type vntourism:Landscape-Place}" +
                    "?X vntourism:isApartOf " + list[0] + "}";
            jsonObject.put("SPARQL", queryString1 + "      " + queryString2);
            queryString1 = Constant.PREFIX_QUERY + queryString1;
            queryString2 = Constant.PREFIX_QUERY + queryString2;
            queryResult1 = InitJena.getItems4(queryString1);
            queryResult2 = InitJena.getItems4(queryString2);
            results = InitJena.pretty2(list[0]) + " có các lễ hội được tổ chức nhằm tưởng nhớ công lao của các " +
                    "anh hùng lịch sử như " + queryResult1 +
                    "... Ngoài ra, " + InitJena.pretty2(list[0]) + " còn có nhiều điểm đến thú vị như " + queryResult2 + "...  ";
            jsonObject.put("Answer", results);
            FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
            temp = random.nextInt(2);
            return results +(temp==0?" Hãy đến đây ngay nhé!":"") + "~" + jsonObject.get("SPARQL");
        }

        boolean check = false;
        if (list[1].contains(":")) {
            queryString = "SELECT ?X " +
                    "WHERE { " + list[1] + " vntourism:hasDescription ?X}";
            jsonObject.put("SPARQL", queryString);
            queryString = Constant.PREFIX_QUERY + queryString;
            results = InitJena.getItems4(queryString);
            if (!results.equals("") && !results.contains("description")) {
                jsonObject.put("Answer", results);
                FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
                check = true;
                //return results+"~"+jsonObject.get("SPARQL");

            }
        }
        queryString = "SELECT ?X " +
                "WHERE { " + list[0] + " vntourism:hasDescription ?X}";

        queryString = Constant.PREFIX_QUERY + queryString;
        results = InitJena.getItems4(queryString);
        if (!results.equals("") && !results.contains("description")) {
            if (check == false) {
                jsonObject.put("SPARQL",queryString.replaceAll(Constant.PREFIX_QUERY,""));
                jsonObject.put("Answer", results);
                FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
                temp = random.nextInt(2);
                return results+(temp==0?" Hãy đến đây ngay nhé!":"") + "~" + jsonObject.get("SPARQL");
            } else {
                jsonObject.put("SPARQL",jsonObject.get("SPARQL")+ "   "+queryString.replaceAll(Constant.PREFIX_QUERY,""));
                jsonObject.put("Answer","Nếu bạn hỏi về "+InitJena.pretty2(list[0]) +" thì nơi đây "+
                        results.replaceFirst(InitJena.pretty2(list[0]),"") +
                        " Nếu bạn muốn biết về "+
                        InitJena.pretty2(list[1])+", ở đó " +
                        jsonObject.get("Answer").toString().replaceFirst(InitJena.pretty2(list[1]),""));
                FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
                temp = random.nextInt(2);
                return jsonObject.get("Answer") +(temp==0?" Hãy đến đây ngay nhé!":"")+ "~" + jsonObject.get("SPARQL");
            }
        }else{
            if (check == true){
                temp = random.nextInt(2);
                return jsonObject.get("Answer")+(temp==0?" Hãy đến đây ngay nhé!":"")+"~"+jsonObject.get("SPARQL");
            }

        }
        queryString1 = "SELECT  ?X ?Y ?Z " +
                "WHERE { " + list[0] + "  ?Y  ?X ." +
                "?Y rdfs:comment ?Z}";
        queryString2 = "SELECT ?X ?Y  " +
                "WHERE {{?X ?Y " + list[0] + "}union{?X ?Y \"" + s + "\"}" +
                "}";
        jsonObject.put("SPARQL", queryString1 + "     " + queryString2);
        queryString1 = Constant.PREFIX_QUERY + queryString1;
        queryString2 = Constant.PREFIX_QUERY + queryString2;
        queryResult1 = InitJena.getItems7(queryString1);
        queryResult2 = InitJena.getItems8(queryString2);
        if (!queryResult1.equals("")) {
            results += InitJena.pretty2(s) + " " + queryResult1;
        }
        if (!queryResult2.equals("")) {
            results += InitJena.pretty2(s) + " " + queryResult2;
        }
        if (queryResult1.equals("") && queryResult2.equals("")) {
            jsonObject.put("Answer", "Tôi không biết.");
            FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
            return "Tôi không biết." + "~" + jsonObject.get("SPARQL");
        }
        jsonObject.put("Answer", results);
        FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
        return results + "~" + jsonObject.get("SPARQL");
    }

    @GetMapping("/ask_den_chua")
    public String askTemple(@RequestBody String data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Output RASA", data);
        String[] list = data.split("~");

        if (!list[0].contains(":")) {
            list[0] = "\"" + list[0] + "\"";
        }
        String queryString = "SELECT  ?X  " +
                "WHERE { {?X rdf:type vntourism:Temple}union {?X rdf:type vntourism:BuddhistTemple}" +
                "?X vntourism:isApartOf " + list[0] + "}";

        jsonObject.put("SPARQL", queryString);
        queryString = Constant.PREFIX_QUERY + queryString;
        String queryResult = InitJena.getItems4(queryString);
        String results = "Đó là " + queryResult;
        if (queryResult.equals("")) {
            jsonObject.put("Answer", "Tôi không biết.");
            FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
            return "Tôi không biết." + "~" + jsonObject.get("SPARQL");
        }
        jsonObject.put("Answer", results);
        FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
        return results + "~" + jsonObject.get("SPARQL");
    }

    @GetMapping("/ask_area")
    public String askArea(@RequestBody String data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Output RASA", data);
        String[] list = data.split("~");

        if (!list[0].contains(":")) {
            list[0] = "\"" + list[0] + "\"";
        }

        String queryString = "SELECT  ?X  " +
                "WHERE { {?X rdf:type vntourism:NaturalArea}union{?X rdf:type vntourism:Landscape-Place}" +
                "?X vntourism:isApartOf " + list[0] + "}";

        jsonObject.put("SPARQL", queryString);
        queryString = Constant.PREFIX_QUERY + queryString;
        String queryResult = InitJena.getItems4(queryString);
        String results = "Đó là " + queryResult;
        if (queryResult.equals("")) {
            jsonObject.put("Answer", "Tôi không biết.");
            FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
            return "Tôi không biết." + "~" + jsonObject.get("SPARQL");
        }
        jsonObject.put("Answer", results);
        FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
        return results + "~" + jsonObject.get("SPARQL");
    }

    @GetMapping("/ask_about_area")
    public String askAboutArea(@RequestBody String data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Output RASA", data);
        String[] list = data.split("~");


        if (!list[0].contains(":")) {
            list[0] = list[0].replaceAll(" ", "_");
            list[0] = "vntourism:" + list[0];
        }
        if (!list[1].contains(":")) {
            list[1] = list[1].replaceAll(" ", "_");
            list[1] = "vntourism:" + list[1];
        }
        if (!list[2].contains(":")) {
            list[2] = "vntourism:isApartOf";
        }

        String queryString = "SELECT  ?X  " +
                "WHERE { " + list[0] + " " + list[2] + " ?X}";

        jsonObject.put("SPARQL", queryString);
        queryString = Constant.PREFIX_QUERY + queryString;
        String queryResult = InitJena.getItems4(queryString);
        String results;
        if (list[2].equals("vntourism:isApartOf")) {
            results = InitJena.pretty2(list[0]) + " ở " + queryResult;
        } else {
            String queryString2 = Constant.PREFIX_QUERY + " SELECT ?X WHERE {" + list[2] + " rdfs:comment ?X}";
            String queryResult2 = InitJena.getItems5(queryString2);
            results = InitJena.pretty2(list[0]) + " " + queryResult2 + " " + queryResult;
        }
        if (queryResult.equals("")) {
            if (!list[1].equals("vntourism:x")) {
                queryString = "SELECT  ?X  " +
                        "WHERE { " + list[1] + " " + list[2] + " ?X}";
                jsonObject.put("SPARQL", queryString);
                queryString = Constant.PREFIX_QUERY + queryString;
                queryResult = InitJena.getItems4(queryString);
            }
            if (queryResult.equals("")) {
                jsonObject.put("Answer", "Tôi không biết.");
                FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
                return "Tôi không biết." + "~" + jsonObject.get("SPARQL");
            } else {
                results = InitJena.pretty2(list[1]) + " ở " + queryResult;
            }
        }
        jsonObject.put("Answer", results);
        FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
        return results + "~" + jsonObject.get("SPARQL");
    }

    @GetMapping("/ask_relate")
    public String askRelate(@RequestBody String data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Output RASA", data);
        String[] list = data.split("~");


        if (!list[0].contains(":")) {
            list[0] = "\"" + list[0] + "\"";
        }
        if (!list[1].contains(":")) {
            list[1] = "?A";
        }

        String queryString;
        if (list[1].equals("vntourism:Landscape-Place") || list[1].equals("vntourism:NaturalArea")) {
            queryString = "SELECT  ?X  " +
                    "WHERE { {?X rdf:type vntourism:NaturalArea}union{?X rdf:type vntourism:Landscape-Place}" +
                    "?X vntourism:related " + list[0] + "}";
        } else {
            queryString = "SELECT  ?X  " +
                    "WHERE { ?X rdf:type " + list[1] + "." +
                    "?X vntourism:related " + list[0] + "}";

        }
        jsonObject.put("SPARQL", queryString);
        queryString = Constant.PREFIX_QUERY + queryString;
        String queryResult = InitJena.getItems4(queryString);
        String results = "Đó là " + queryResult;
        if (queryResult.equals("")) {
            jsonObject.put("Answer", "Tôi không biết.");
            FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
            return "Tôi không biết." + "~" + jsonObject.get("SPARQL");
        }
        jsonObject.put("Answer", results);
        FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
        return results + "~" + jsonObject.get("SPARQL");
    }


    @GetMapping("/test")
    public String test() {

        String query = Constant.PREFIX_QUERY +
                "SELECT distinct ?X  WHERE { ?X rdf:type ?Y. ?Y rdfs:subClassOf vntourism:HeritageSite }";

        String results = InitJena.getItems4(query);

        return results;
    }

    @GetMapping("/test2")
    public String test2() {

        String query = Constant.PREFIX_QUERY +
                "SELECT distinct ?X  WHERE { ?X rdf:type vntourism:Person }";

        String results = InitJena.getItems4(query);

        return results.replaceAll(",","\n");
    }

    @GetMapping("/rule_temple")
    public String ruleTemple() {
        String query = Constant.PREFIX_QUERY +
                "SELECT distinct ?X ?Y WHERE {  {?X rdf:type vntourism:Temple}union {?X rdf:type vntourism:BuddhistTemple}" +
                "?X vntourism:isApartOf ?Y}";

        String results = InitJena.getItems(query);

        return results;
    }

    @GetMapping("/convert")
    public String convert() {
        String query = Constant.PREFIX_QUERY +
                " SELECT  ?X  WHERE { ?X rdf:type vntourism:TraditionalFestival }";
        String results = InitJena.getItems2(query);

        return results;
    }

    @GetMapping("/data_train")
    public String getDataTrain() {

        boolean b1 = AnalysisOntology.createAnswer();
        boolean b2 = AnalysisOntology.genQuestion();
        boolean b3 = AnalysisOntology.genQuestion2();
        boolean b4 = AnalysisOntology.genQuestion3();
        if (b1 && b2 && b3 && b4) {
            return "success";
        }
        return "fail";
    }


}
