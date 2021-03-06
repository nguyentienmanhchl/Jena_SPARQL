package com.mlpj.ontology.controllers;

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
        if (!s.contains(":")) {
            s = "vntourism:" + s;
        }

        if (list[0].equals(".")) {
            list[0] = "?A";
        } else if (!list[0].contains(":")) {
            //list[0] = "\"" + InitJena.upperFirstCharacter(list[0]) + "@vn\"";
            list[0] = Constant.map.get(list[0].toLowerCase());
            if (list[0] == null) list[0] = "?A";
        }

        if (list[1].equals(".")) {
            list[1] = "?B";
        } else if (!list[1].contains(":")) {
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
                "WHERE { {?X  " + list[1] + " " + s + "} " +
                "union {?X " + list[1] + " \"" + list[2] + "\"} " +
                "?X rdf:type " + list[0] + " }";
        jsonObject.put("SPARQL", queryString);
        queryString = Constant.PREFIX_QUERY + queryString;
        String results = InitJena.getItems4(queryString);
        if (!results.equals("")) {
            jsonObject.put("Answer", "???? l?? " + results);

        }
        if (!list[3].equals(".")) {
            String[] time = InitJena.convertDate(list[3]);
            String type;
            if (list[2].contains("??m l???ch") || list[2].contains("??m l???ch")) {
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
                jsonObject.put("Answer", "???? l?? " + results);

            }
        }
        if (results.equals("")) {
            jsonObject.put("Answer", "Kh??ng t??m ???????c");
            return "Kh??ng t??m ???????c" + "~" + jsonObject.get("SPARQL");
        }


        FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
        return "???? l?? " + results + "~" + jsonObject.get("SPARQL");
    }

    @GetMapping("/two_conditions")
    public String twoConditions(@RequestBody String data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Output RASA", data);
        String[] list = data.split("~");

        String s1 = list[2];
        s1 = s1.replaceAll(" ", "_");
        s1 = s1.replaceAll("/", "");
        if (!s1.contains(":")) {
            s1 = "vntourism:" + s1;
        }

        String s2 = list[4];
        s2 = s2.replaceAll(" ", "_");
        s2 = s2.replaceAll("/", "");
        if (!s2.contains(":")) {
            s2 = "vntourism:" + s2;
        }

        if (list[0].equals(".")) {
            list[0] = "?A";
        } else if (!list[0].contains(":")) {
            //list[0] = "\"" + InitJena.upperFirstCharacter(list[0]) + "@vn\"";
            list[0] = Constant.map.get(list[0].toLowerCase());
            if (list[0] == null) list[0] = "?A";
        }
        if (list[1].equals(".")) {
            list[1] = "?B";
        } else if (!list[1].contains(":")) {
            //list[1]= "\"" + list[1] + "\"";
            list[1] = Constant.map.get(list[1]);
            if (list[1] == null) list[1] = "?B";
        }
        if (list[3].equals(".")) {
            list[3] = "?C";
        } else if (!list[3].contains(":")) {
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
        String queryString = "SELECT distinct ?X  WHERE { {?X  " + list[1] + " " + s1 + "} " +
                "union {?X " + list[1] + " \"" + list[2] + "\"} " +
                "{?X " + list[3] + " " + s2 + "} " +
                "union {?X " + list[3] + " \"" + list[4] + "\"} " +
                "?X rdf:type " + list[0] + "}";
        jsonObject.put("SPARQL", queryString);
        queryString = Constant.PREFIX_QUERY + queryString;
        String results = InitJena.getItems4(queryString);

        if (results.equals("")) {
            jsonObject.put("Answer", "Kh??ng t??m ???????c");
            FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
            return "Kh??ng t??m ???????c" + "~" + jsonObject.get("SPARQL");
        }
        jsonObject.put("Answer", "???? l?? " + results);
        FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
        return "???? l?? " + results + "~" + jsonObject.get("SPARQL");
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
        } else if (!list[1].contains(":")) {
            //list[1]= "\"" + list[1] + "\"";
            list[1] = Constant.map.get(list[1]);
            if (list[1] == null) list[1] = "?A";
        }
//        String queryString = "SELECT distinct  ?X  " +
//                "WHERE { vntourism:" + s + "  ?Y  ?X. " +
//                "?Y rdfs:comment " + list[1] + "}";
        String queryString = "SELECT   ?X ?Z  " +
                "WHERE {?Z " + list[1] + " ?X." +
                "FILTER (lcase(str(?Z)) =\"" + Constant.PREFIX + s.replaceAll("vntourism:","").replaceAll("time:","").toLowerCase() + "\" )}";


        queryString = Constant.PREFIX_QUERY + queryString;
        String[] queryResult = InitJena.getItems6(queryString);

        queryString = "SELECT ?X " +
                "WHERE {" + queryResult[1] + " " + list[1] + " ?X.}";
        if (predicate.equals("m???")||predicate.equals("cha")){
            predicate = "c?? cha m??? l??";
        }
        String results = queryResult[1].replaceAll("vntourism:", "").replaceAll("_", " ") + " " + predicate + " " + queryResult[0];
        if (!queryResult[1].equals("")) {
            jsonObject.put("SPARQL", queryString);
        } else {
            jsonObject.put("SPARQL", "");
        }
        if (queryResult[0].equals("")) {
            jsonObject.put("Answer", "T??i kh??ng bi???t.");
            FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
            return "T??i kh??ng bi???t." + "~" + jsonObject.get("SPARQL");
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
        if (!data.contains(":")) {
            data = "vntourism:" + data;
        }

        String queryString1 = "SELECT  ?X ?Y ?Z " +
                "WHERE { " + data + "  ?Y  ?X ." +
                "?Y rdfs:comment ?Z}";
        String queryString2 = "SELECT ?X ?Y ?Z " +
                "WHERE {{?X ?Y " + data + "}union{?X ?Y \"" + s + "\"}" +
                "?Y rdfs:comment ?Z}";
        jsonObject.put("SPARQL", queryString1 + "     " + queryString2);
        queryString1 = Constant.PREFIX_QUERY + queryString1;
        queryString2 = Constant.PREFIX_QUERY + queryString2;
        String queryResult1 = InitJena.getItems7(queryString1);
        String queryResult2 = InitJena.getItems8(queryString2, s);
        String results = queryResult1 + " " + queryResult2;
        if (queryResult1.equals("") && queryResult2.equals("")) {
            jsonObject.put("Answer", "T??i kh??ng bi???t.");
            FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
            return "T??i kh??ng bi???t." + "~" + jsonObject.get("SPARQL");
        }
        jsonObject.put("Answer", s + " : " + results);
        FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
        return s.replaceAll("vntourism:", "").replaceAll("_", " ") + " : " + results + "~" + jsonObject.get("SPARQL");
    }

    @GetMapping("/ask_den_chua")
    public String askTemple(@RequestBody String data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Output RASA", data);
        String[] list = data.split("~");

        String s = list[0];
        s = s.replaceAll(" ", "_");
        s = s.replaceAll("/", "");
        if (!s.contains(":")) {
            s = "vntourism:" + s;
        }


        String queryString = "SELECT  ?X  " +
                "WHERE { {?X rdf:type vntourism:Temple}union {?X rdf:type vntourism:BuddhistTemple}" +
                "{?X vntourism:isApartOf \"" + list[0] + "\"}union{?X vntourism:isApartOf " + s + "}}";

        jsonObject.put("SPARQL", queryString);
        queryString = Constant.PREFIX_QUERY + queryString;
        String queryResult = InitJena.getItems4(queryString);
        String results = "???? l?? " + queryResult;
        if (queryResult.equals("")) {
            jsonObject.put("Answer", "T??i kh??ng bi???t.");
            FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
            return "T??i kh??ng bi???t." + "~" + jsonObject.get("SPARQL");
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

        if (!s.contains(":")) {
            s = "vntourism:" + s;
        }

        String queryString = "SELECT  ?X  " +
                "WHERE { {?X rdf:type vntourism:NaturalArea}union{?X rdf:type vntourism:Landscape-Place}" +
                "{?X vntourism:isApartOf \"" + list[0] + "\"}union{?X vntourism:isApartOf " + s + "}}";

        jsonObject.put("SPARQL", queryString);
        queryString = Constant.PREFIX_QUERY + queryString;
        String queryResult = InitJena.getItems4(queryString);
        String results = "???? l?? " + queryResult;
        if (queryResult.equals("")) {
            jsonObject.put("Answer", "T??i kh??ng bi???t.");
            FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
            return "T??i kh??ng bi???t." + "~" + jsonObject.get("SPARQL");
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
        if (!s.contains(":")) {
            s = "vntourism:" + s;
        }

        String queryString = "SELECT  ?X  " +
                "WHERE { " + s + " vntourism:isApartOf ?X}";

        jsonObject.put("SPARQL", queryString);
        queryString = Constant.PREFIX_QUERY + queryString;
        String queryResult = InitJena.getItems4(queryString);
        String results = list[0].replaceAll("vntourism:", "").replaceAll("_", " ") + " ??? " + queryResult;
        if (queryResult.equals("")) {
            jsonObject.put("Answer", "T??i kh??ng bi???t.");
            FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
            return "T??i kh??ng bi???t." + "~" + jsonObject.get("SPARQL");
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
        if (!s.contains(":")) {
            s = "vntourism:" + s;
        }

        String queryString = "SELECT  ?X  " +
                "WHERE { ?X rdf:type vntourism:Person" +
                "{?X vntourism:related \"" + list[0] + "\"}union{?X vntourism:related " + s + "}}";

        jsonObject.put("SPARQL", queryString);
        queryString = Constant.PREFIX_QUERY + queryString;
        String queryResult = InitJena.getItems4(queryString);
        String results = "???? l?? " + queryResult;
        if (queryResult.equals("")) {
            jsonObject.put("Answer", "T??i kh??ng bi???t.");
            FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
            return "T??i kh??ng bi???t." + "~" + jsonObject.get("SPARQL");
        }
        jsonObject.put("Answer", results);
        FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
        return results + "~" + jsonObject.get("SPARQL");
    }


    @GetMapping("/test2")
    public String test2() {
        String query = Constant.PREFIX_QUERY +
                "SELECT distinct ?X  WHERE { ?X vntourism:hasTimeHappen ?Y}";

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
