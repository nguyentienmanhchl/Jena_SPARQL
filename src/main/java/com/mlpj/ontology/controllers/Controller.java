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

//    @PostMapping("/person")
//    public boolean insert(@RequestBody Person person) {
//        return InitJena.insert(person);
//    }
//
//    @PostMapping("/people")
//    public boolean insertPeople(@RequestBody List<Person> list) {
//        boolean check;
//        for (Person person : list) {
//            check = InitJena.insert(person);
//            if (check == false) {
//                return false;
//            }
//        }
//        return true;
//    }


//    @GetMapping("/festival")
//    public String getFestival(@RequestBody String data) {
//        String[] list = data.split("~");
//        String name = list[1];
//
//        name = name.replaceAll(" ", "_");
//        String queryString = Constant.PREFIX_QUERY + "SELECT  ?X WHERE { ?X ?Y" + " foaf:" + name + "." +
//                "?X rdf:type foaf:" + Constant.map.get(list[0]) + "}";
//        String results = InitJena.getItems4(queryString);
//        return results;
//    }
//
//    @GetMapping("/festival2")
//    public String getFestival2(@RequestBody String data) {
//        String[] list = data.split("~");
//        String s1 = list[1];
//        s1 = s1.replaceAll(" ", "_");
//        String s2 = list[2];
//        s2 = s2.replaceAll(" ", "_");
//        String queryString = Constant.PREFIX_QUERY
//                + "SELECT  ?X  WHERE { {?X  ?Y foaf:" + s1 + "} union {?X ?Y \"" + list[1] + "\"}"
//                + "{?X  ?Z foaf:" + s2 + "} union {?X ?Z \"" + list[2] + "\"}"
//                + "?X rdf:type foaf:" + Constant.map.get(list[0]) + "}";
//        String results = InitJena.getItems4(queryString);
//        return results;
//    }

    @GetMapping("/one_condition")
    public String oneCondition(@RequestBody String data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Output RASA", data);
        String[] list = data.split("~");

        String s = list[2];
        s = s.replaceAll(" ", "_");
        s = s.replaceAll("/", "");

        if (list[0].equals(".")){
            list[0]="?A";
        }else{
            list[0] = "\"" + InitJena.upperFirstCharacter(list[0]) + "@vn\"";
        }

        if (list[1].equals(".")){
            list[1]="?B";
        }else{
            list[1]= "\"" + list[1] + "\"";
        }

        String queryString = "SELECT distinct ?X  " +
                "WHERE { {?X  ?Y  foaf:" + s + "} " +
                "union {?X ?Y \"" + list[2] + "\"} " +
                "?Y rdfs:comment " + list[1] + ". " +
                "?X rdf:type ?Z. ?Z rdfs:label "+list[0]+" }";
        jsonObject.put("SPARQL", queryString);
        queryString = Constant.PREFIX_QUERY + queryString;
        String results = InitJena.getItems4(queryString);
        if (results.equals("")) {
            jsonObject.put("Answer", "Không tìm được");
            FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
            return "Không tìm được";
        }
        jsonObject.put("Answer", "Đó là " + results);
        FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
        return "Đó là " + results+"~"+jsonObject.get("SPARQL");
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

        if (list[0].equals(".")){
            list[0]="?A";
        }else{
            list[0] = "\"" + InitJena.upperFirstCharacter(list[0]) + "@vn\"";
        }
        if (list[1].equals(".")){
            list[1]="?B";
        }else{
            list[1]= "\"" + list[1] + "\"";
        }
        if (list[3].equals(".")){
            list[3]="?C";
        }else{
            list[3]= "\"" + list[3] + "\"";
        }
        String queryString = "SELECT distinct ?X  WHERE { {?X  ?Y1 foaf:" + s1 + "} " +
                "union {?X ?Y1 \"" + list[2] + "\"} " +
                "?Y1 rdfs:comment " + list[1] + ". " +
                "{?X ?Y2 foaf:" + s2 + "} " +
                "union {?X ?Y2 \"" + list[4] + "\"} " +
                "?Y2 rdfs:comment " + list[3] + ". " +
                "?X rdf:type ?Z. ?Z rdfs:label "+list[0]+" }";
        jsonObject.put("SPARQL", queryString);
        queryString = Constant.PREFIX_QUERY + queryString;
        String results = InitJena.getItems4(queryString);

        if (results.equals("")) {
            jsonObject.put("Answer", "Không tìm được");
            FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
            return "Không tìm được";
        }
        jsonObject.put("Answer", "Đó là " + results);
        FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
        return "Đó là " + results+"~"+jsonObject.get("SPARQL");
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
        if (list[1].equals(".")){
            list[1]="?A";
        }else{
            list[1]= "\"" + list[1] + "\"";
        }
        String queryString = "SELECT distinct  ?X  " +
                "WHERE { foaf:" + s + "  ?Y  ?X. " +
                "?Y rdfs:comment " + list[1] + "}";

        jsonObject.put("SPARQL", queryString);
        queryString = Constant.PREFIX_QUERY + queryString;
        String queryResult = InitJena.getItems6(queryString);
        String results = list[0] + " " +predicate + " " + queryResult;
        if (queryResult.equals("")) {
            jsonObject.put("Answer", "Tôi không biết.");
            FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
            return "Tôi không biết.";
        }
        jsonObject.put("Answer", results);
        FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
        return results+"~"+jsonObject.get("SPARQL");
    }

    @GetMapping("/all")
    public String all(@RequestBody String data){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Output RASA", data);

        String s = data;
        data = data.replaceAll(" ", "_");
        data = data.replaceAll("/", "");

        String queryString1 = "SELECT  ?X ?Y ?Z " +
                "WHERE { foaf:" + data + "  ?Y  ?X ." +
                "?Y rdfs:comment ?Z}" ;
        String queryString2 ="SELECT ?X ?Y ?Z " +
                "WHERE {{?X ?Y foaf:"+data+"}union{?X ?Y \""+s+"\"}" +
                "?Y rdfs:comment ?Z}";
        jsonObject.put("SPARQL", queryString1+"     "+queryString2);
        queryString1 = Constant.PREFIX_QUERY + queryString1;
        queryString2 = Constant.PREFIX_QUERY + queryString2;
        String queryResult1 = InitJena.getItems7(queryString1);
        String queryResult2 = InitJena.getItems8(queryString2,s);
        String results = queryResult1+" "+queryResult2;
        if (queryResult1.equals("")&&queryResult2.equals("")) {
            jsonObject.put("Answer", "Tôi không biết.");
            FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
            return "Tôi không biết.";
        }
        jsonObject.put("Answer", s+" : "+results);
        FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
        return s+" : "+results+"~"+jsonObject.get("SPARQL");
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
                "WHERE { {?X rdf:type foaf:Temple}union {?X rdf:type foaf:BuddhistTemple}" +
                "{?X foaf:isApartOf \""+list[0]+"\"}union{?X foaf:isApartOf foaf:"+s+"}}";

        jsonObject.put("SPARQL", queryString);
        queryString = Constant.PREFIX_QUERY + queryString;
        String queryResult = InitJena.getItems4(queryString);
        String results = "Đó là " + queryResult;
        if (queryResult.equals("")) {
            jsonObject.put("Answer", "Tôi không biết.");
            FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
            return "Tôi không biết.";
        }
        jsonObject.put("Answer", results);
        FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
        return results+"~"+jsonObject.get("SPARQL");
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
                "WHERE { {?X rdf:type foaf:NaturalArea}union{?X rdf:type foaf:Landscape-Place}" +
                "{?X foaf:isApartOf \""+list[0]+"\"}union{?X foaf:isApartOf foaf:"+s+"}}";

        jsonObject.put("SPARQL", queryString);
        queryString = Constant.PREFIX_QUERY + queryString;
        String queryResult = InitJena.getItems4(queryString);
        String results = "Đó là " + queryResult;
        if (queryResult.equals("")) {
            jsonObject.put("Answer", "Tôi không biết.");
            FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
            return "Tôi không biết.";
        }
        jsonObject.put("Answer", results);
        FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
        return results+"~"+jsonObject.get("SPARQL");
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
                "WHERE { foaf:"+s+" foaf:isApartOf ?X}";

        jsonObject.put("SPARQL", queryString);
        queryString = Constant.PREFIX_QUERY + queryString;
        String queryResult = InitJena.getItems4(queryString);
        String results = list[0]+" ở " + queryResult;
        if (queryResult.equals("")) {
            jsonObject.put("Answer", "Tôi không biết.");
            FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
            return "Tôi không biết.";
        }
        jsonObject.put("Answer", results);
        FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
        return results+"~"+jsonObject.get("SPARQL");
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
                "WHERE { ?X rdf:type foaf:Person" +
                "{?X foaf:related \""+list[0]+"\"}union{?X foaf:related foaf:"+s+"}}";

        jsonObject.put("SPARQL", queryString);
        queryString = Constant.PREFIX_QUERY + queryString;
        String queryResult = InitJena.getItems4(queryString);
        String results = "Đó là " + queryResult;
        if (queryResult.equals("")) {
            jsonObject.put("Answer", "Tôi không biết.");
            FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
            return "Tôi không biết.";
        }
        jsonObject.put("Answer", results);
        FileHelper.saveToFile(jsonObject + ",\n", "history_log.txt");
        return results+"~"+jsonObject.get("SPARQL");
    }




//    @GetMapping("/test")
//    public String test() {
//        String query = Constant.PREFIX_QUERY +
//                "SELECT   ?X ?Y ?Z ?U ?U1 WHERE { ?X ?Y1 ?Z.?X rdf:type ?U.?Y1 rdfs:label ?Y.?U rdfs:label ?U1}";
//        String results = InitJena.getItems2(query);
//        FileHelper.saveToFile(results, "answer2.txt");
//        return results;
//    }

    @GetMapping("/test2")
    public String test2() {
        String query = Constant.PREFIX_QUERY +
                " SELECT distinct ?X ?Y WHERE {?X  foaf:buriedPlace  ?Y}";
        String results = InitJena.getItems4(query);

        return results;
    }
    @GetMapping("/convert")
    public String convert() {
        String query = Constant.PREFIX_QUERY +
                " SELECT  ?X  WHERE { ?X rdf:type foaf:TraditionalFestival }";
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
