package com.mlpj.ontology.controllers;

import com.mlpj.ontology.data.Person;
import com.mlpj.ontology.file.FileHelper;
import com.mlpj.ontology.jenawork.InitJena;
import com.mlpj.ontology.util.Constant;
import org.json.simple.JSONObject;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class Controller {


    @GetMapping("/one_condition")
    public String oneCondition(@RequestBody String data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Output RASA", data);
        String[] list = data.split("~");

        String s = list[2];
        s = s.replaceAll(" ", "_");
        s = s.replaceAll("/", "");

        if (list[0].equals(".")) {
            list[0] = "?A";
        } else {
            //list[0] = "\"" + InitJena.upperFirstCharacter(list[0]) + "@vn\"";
            list[0] = Constant.map.get(list[0].toLowerCase());
            if (list[0] == null) list[0] = "?A";
        }

        if (list[1].equals(".")) {
            list[1] = "?B";
        } else {
            //list[1]= "\"" + list[1] + "\"";
            list[1] = Constant.map.get(list[1]);
            if (list[1] == null) list[1] = "?B";
        }

//        String queryString = "SELECT distinct ?X  " +
//                "WHERE { {?X  ?Y  vntourism:" + s + "} " +
//                "union {?X ?Y \"" + list[2] + "\"} " +
//                "?Y rdfs:comment " + list[1] + ". " +
//                "?X rdf:type ?Z. ?Z rdfs:label "+list[0]+" }";
        String queryString = "SELECT distinct ?X  " +
                "WHERE { {?X  " + list[1] + "  vntourism:" + s + "} " +
                "union {?X " + list[1] + " \"" + list[2] + "\"} " +
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
                type= "vntourism:LunarCalendar";
            } else {
                type="time:ChronometricGeologicTime";
            }
            String query = "SELECT distinct ?X  " +
                    "WHERE { ?X " + list[1] + " ?T. " +
                    "?X rdf:type " + list[0] + " . " +
                    "?T rdf:type time:DateTimeDescription . " +
                    "?T time:hasTRS "+type+" . "+
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

        String s1 = list[2];
        s1 = s1.replaceAll(" ", "_");
        s1 = s1.replaceAll("/", "");

        String s2 = list[4];
        s2 = s2.replaceAll(" ", "_");
        s2 = s2.replaceAll("/", "");

        if (list[0].equals(".")) {
            list[0] = "?A";
        } else {
            //list[0] = "\"" + InitJena.upperFirstCharacter(list[0]) + "@vn\"";
            list[0] = Constant.map.get(list[0].toLowerCase());
            if (list[0] == null) list[0] = "?A";
        }
        if (list[1].equals(".")) {
            list[1] = "?B";
        } else {
            //list[1]= "\"" + list[1] + "\"";
            list[1] = Constant.map.get(list[1]);
            if (list[1] == null) list[1] = "?B";
        }
        if (list[3].equals(".")) {
            list[3] = "?C";
        } else {
            //list[3]= "\"" + list[3] + "\"";
            list[3] = Constant.map.get(list[3]);
            if (list[3] == null) list[3] = "?C";
        }
//        String queryString = "SELECT distinct ?X  WHERE { {?X  ?Y1 vntourism:" + s1 + "} " +
//                "union {?X ?Y1 \"" + list[2] + "\"} " +
//                "?Y1 rdfs:comment " + list[1] + ". " +
//                "{?X ?Y2 vntourism:" + s2 + "} " +
//                "union {?X ?Y2 \"" + list[4] + "\"} " +
//                "?Y2 rdfs:comment " + list[3] + ". " +
//                "?X rdf:type ?Z. ?Z rdfs:label "+list[0]+" }";
        String queryString = "SELECT distinct ?X  WHERE { {?X  " + list[1] + " vntourism:" + s1 + "} " +
                "union {?X " + list[1] + " \"" + list[2] + "\"} " +
                "{?X " + list[3] + " vntourism:" + s2 + "} " +
                "union {?X " + list[3] + " \"" + list[4] + "\"} " +
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

        String s = list[0];
        s = s.replaceAll(" ", "_");
        s = s.replaceAll("/", "");

        String predicate = list[1];
        if (list[1].equals(".")) {
            list[1] = "?A";
        } else {
            //list[1]= "\"" + list[1] + "\"";
            list[1] = Constant.map.get(list[1]);
            if (list[1] == null) list[1] = "?A";
        }
//        String queryString = "SELECT distinct  ?X  " +
//                "WHERE { vntourism:" + s + "  ?Y  ?X. " +
//                "?Y rdfs:comment " + list[1] + "}";
        String queryString = "SELECT distinct  ?X " +
                "WHERE {?Z " + list[1] + " ?X." +
                "FILTER (lcase(str(?Z)) =\"" + Constant.PREFIX + s.toLowerCase() + "\" )}";

        jsonObject.put("SPARQL", queryString);
        queryString = Constant.PREFIX_QUERY + queryString;
        String queryResult = InitJena.getItems6(queryString);
        String results = list[0] + " " + predicate + " " + queryResult;
        if (queryResult.equals("")) {
            jsonObject.put("Answer", "Tôi không biết.");
            FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
            return "Tôi không biết." + "~" + jsonObject.get("SPARQL");
        }
        jsonObject.put("Answer", results);
        FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
        return results + "~" + jsonObject.get("SPARQL");
    }

    @GetMapping("/all")
    public String all(@RequestBody String data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Output RASA", data);

        String s = data;
        data = data.replaceAll(" ", "_");
        data = data.replaceAll("/", "");

        String queryString1 = "SELECT  ?X ?Y ?Z " +
                "WHERE { vntourism:" + data + "  ?Y  ?X ." +
                "?Y rdfs:comment ?Z}";
        String queryString2 = "SELECT ?X ?Y ?Z " +
                "WHERE {{?X ?Y vntourism:" + data + "}union{?X ?Y \"" + s + "\"}" +
                "?Y rdfs:comment ?Z}";
        jsonObject.put("SPARQL", queryString1 + "     " + queryString2);
        queryString1 = Constant.PREFIX_QUERY + queryString1;
        queryString2 = Constant.PREFIX_QUERY + queryString2;
        String queryResult1 = InitJena.getItems7(queryString1);
        String queryResult2 = InitJena.getItems8(queryString2, s);
        String results = queryResult1 + " " + queryResult2;
        if (queryResult1.equals("") && queryResult2.equals("")) {
            jsonObject.put("Answer", "Tôi không biết.");
            FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
            return "Tôi không biết." + "~" + jsonObject.get("SPARQL");
        }
        jsonObject.put("Answer", s + " : " + results);
        FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
        return s + " : " + results + "~" + jsonObject.get("SPARQL");
    }

    @GetMapping("/ask_den_chua")
    public String askTemple(@RequestBody String data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Output RASA", data);
        String[] list = data.split("~");

        String s = list[0];
        s = s.replaceAll(" ", "_");
        s = s.replaceAll("/", "");


        String queryString = "SELECT  ?X  " +
                "WHERE { {?X rdf:type vntourism:Temple}union {?X rdf:type vntourism:BuddhistTemple}" +
                "{?X vntourism:isApartOf \"" + list[0] + "\"}union{?X vntourism:isApartOf vntourism:" + s + "}}";

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

        String s = list[0];
        s = s.replaceAll(" ", "_");
        s = s.replaceAll("/", "");


        String queryString = "SELECT  ?X  " +
                "WHERE { {?X rdf:type vntourism:NaturalArea}union{?X rdf:type vntourism:Landscape-Place}" +
                "{?X vntourism:isApartOf \"" + list[0] + "\"}union{?X vntourism:isApartOf vntourism:" + s + "}}";

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

        String s = list[0];
        s = s.replaceAll(" ", "_");
        s = s.replaceAll("/", "");


        String queryString = "SELECT  ?X  " +
                "WHERE { vntourism:" + s + " vntourism:isApartOf ?X}";

        jsonObject.put("SPARQL", queryString);
        queryString = Constant.PREFIX_QUERY + queryString;
        String queryResult = InitJena.getItems4(queryString);
        String results = list[0] + " ở " + queryResult;
        if (queryResult.equals("")) {
            jsonObject.put("Answer", "Tôi không biết.");
            FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
            return "Tôi không biết." + "~" + jsonObject.get("SPARQL");
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

        String s = list[0];
        s = s.replaceAll(" ", "_");
        s = s.replaceAll("/", "");


        String queryString = "SELECT  ?X  " +
                "WHERE { ?X rdf:type vntourism:Person" +
                "{?X vntourism:related \"" + list[0] + "\"}union{?X vntourism:related vntourism:" + s + "}}";

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


    @GetMapping("/test2")
    public String test2() {
        String query = Constant.PREFIX_QUERY +
                "SELECT distinct ?X  WHERE { vntourism:Lễ_hội_Đống_Đa vntourism:hasTimeHappen ?X.  ?X rdf:type time:DateTimeDescription . ?X time:hasTRS vntourism:LunarCalendar . ?X time:month \"1\". optional{?T time:day \"5\"}optional{?T time:year \"2022\"}}";

        String results = InitJena.getItems4(query);

        return results;
    }

    @GetMapping("/convert")
    public String convert() {
        String query = Constant.PREFIX_QUERY +
                " SELECT  ?X  WHERE { ?X rdf:type vntourism:TraditionalFestival }";
        String results = InitJena.getItems2(query);

        return results;
    }


    //FILE2
//    @GetMapping("/toChucTai")
//    public String toChucTai() {
//        String query = Constant.PREFIX_QUERY +
//                "SELECT ?X  ?Z WHERE {?X1 ont:toChucTai  ?Z1." +
//                "?X1 ont:tenLeHoi ?X." +
//                "?Z1 ont:tenDiaDiem ?Z.}";
//        String results = InitJena.getItems5(query);
//        return results;
//    }


}
