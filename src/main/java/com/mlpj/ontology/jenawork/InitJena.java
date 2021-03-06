package com.mlpj.ontology.jenawork;

import java.io.FileReader;

import com.mlpj.ontology.util.Constant;
import org.apache.jena.ontology.*;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class InitJena {

    private static QueryExecution qe;
    private static String ontoFile = Constant.FILE;
    //private static String ontoFile = Constant.FILE2;

    public static ResultSet execQuery(String queryString) {
        OntModel ontoModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        try {
            InputStream in = FileManager.get().open(ontoFile);
            try {
                ontoModel.read(in, "");
                Query query = QueryFactory.create(queryString);
                //Execute the query and obtain results
                qe = QueryExecutionFactory.create(query, ontoModel);
                ResultSet results = qe.execSelect();
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

    public static String getItems2(String queryString) {
        ResultSet resultSet = execQuery(queryString);
        String result = "fail";
        try {
            OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
            InputStream in = FileManager.get().open(Constant.FILE);
            m.read(in, null);
            while (resultSet.hasNext()) {

                QuerySolution solution = resultSet.nextSolution();
                String x = solution.get("X").toString();
                if (x.contains("#")) {
                    insert(m, x.split("#")[1], Constant.PREFIX + "Festival");
                }
            }
            OutputStream output = new FileOutputStream(Constant.FILE);
            m.write(output, "RDF/XML", null);
            output.close();
            result = "ok";
        } catch (Exception e) {
            e.printStackTrace();
        }
        qe.close();
        return result;
    }

    public static String getItems3(String query) {
        ResultSet resultSet = execQuery(query);
        String result = "";
        List<String> listClass = new ArrayList<>();
        while (resultSet.hasNext()) {
            QuerySolution solution = resultSet.nextSolution();
            String u = pretty(solution.get("U").toString())
                    .replaceAll("vntourism:", "").replaceAll("time:", "");//class
            String y = pretty(solution.get("Y").toString());//label predicate
            String u1 = pretty(solution.get("U1").toString());//label class
            String z = pretty(solution.get("Z").toString());//object
            if (Constant.STRING_LIST.contains(u)) continue;
            if (u1.contains("@en") || y.contains("@en") || y.contains("@es") || z.contains("@en")) continue;
            //if (listClass.contains(u1)) continue;
            result += u1 + "~" + pretty(solution.get("X").toString()) + "~" + y + "~" + z + "~" + u + "\n";
            listClass.add(u1);
        }
        qe.close();
        return result;


    }

    public static String getItems4(String queryString) {
        ResultSet resultSet = execQuery(queryString);
        String result = "";

        while (resultSet.hasNext()) {

            QuerySolution solution = resultSet.nextSolution();
            String x = solution.get("X").toString();
            if (x.contains("#")) {
                result += x.split("#")[1] + ", ";
            } else {
                if (!x.contains("http")) {
                    result += x + ", ";
                }
            }
        }
        qe.close();
        return result.replaceAll("_", " ");
    }


    public static String[] getItems6(String queryString) {
        ResultSet resultSet = execQuery(queryString);
        String[] result = {"", ""};

        while (resultSet.hasNext()) {

            QuerySolution solution = resultSet.nextSolution();
            if (result[1].equals("")) {
                result[1] += "vntourism:" + solution.get("Z").toString().split("#")[1];
            }
            String[] s = solution.get("X").toString().split("#");
            result[0] += (s.length > 1 ? s[1] : s[0]) + ", ";
        }
        result[0] = result[0].replaceAll("_", " ");
        qe.close();
        return result;
    }

    public static String getItems7(String queryString) {
        ResultSet resultSet = execQuery(queryString);
        String result = "";

        while (resultSet.hasNext()) {

            QuerySolution solution = resultSet.nextSolution();
            String z = solution.get("Z").toString();
            if (z.contains("@en") || z.contains("@es")) {
                continue;
            }
            if (solution.get("Y").toString().equals(RDF.type.toString())) continue;
            z = z.replaceAll("@vn", "");
            String[] s = solution.get("X").toString().split("#");
            result += z + " " + (s.length > 1 ? s[1] : s[0]) + ", ";
        }
        qe.close();
        return result.replaceAll("_", " ");
    }

    public static String getItems8(String queryString, String object) {
        ResultSet resultSet = execQuery(queryString);
        String result = "";

        while (resultSet.hasNext()) {

            QuerySolution solution = resultSet.nextSolution();
            String z = solution.get("Z").toString();
            if (z.contains("@en") || z.contains("@es")) {
                continue;
            }
            z = z.replaceAll("@vn", "");
            String[] s = solution.get("X").toString().split("#");
            result += (s.length > 1 ? s[1] : s[0]) + " " + z + " " + object + ", ";
        }
        qe.close();
        return result.replaceAll("_", " ");
    }


    public static void insert(String filename) {
        try {
            OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
            InputStream in = FileManager.get().open(Constant.FILE);
            m.read(in, null);
            JSONParser jsonParser = new JSONParser();
            FileReader reader = new FileReader(System.getProperty("user.dir") + "/" + filename);
            JSONObject object = (JSONObject) jsonParser.parse(reader);
            Set<String> set = object.keySet();
            Iterator<String> iterator = set.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                JSONObject jsonObject = (JSONObject) object.get(key);
                key = key.replaceAll(" ", "_");
                insert(m, key, Constant.PREFIX + "Person");

                if (jsonObject.containsKey("hasBorn")) {
                    JSONObject sinh = (JSONObject) jsonObject.get("hasBorn");
                    Set<String> set1 = sinh.keySet();
                    Iterator<String> iterator1 = set1.iterator();
                    while (iterator1.hasNext()) {
                        String key1 = iterator1.next();
                        insert(m, key, "hasBorn", sinh.get(key1) + " (theo " + key1 + ")");
                    }
                }

                if (jsonObject.containsKey("hasDied")) {
                    JSONObject mat = (JSONObject) jsonObject.get("hasDied");
                    Set<String> set2 = mat.keySet();
                    Iterator<String> iterator2 = set2.iterator();
                    while (iterator2.hasNext()) {
                        String key2 = iterator2.next();
                        insert(m, key, "hasDied", mat.get(key2) + " (theo " + key2 + ")");
                    }
                }


                if (jsonObject.containsKey("hasBornAt")) {
                    JSONObject jsonObject1 = (JSONObject) jsonObject.get("hasBornAt");
                    Set<String> set3 = jsonObject1.keySet();
                    set3.remove("dbpedia");
                    Iterator<String> iterator3 = set3.iterator();
                    while (iterator3.hasNext()) {
                        String key3 = iterator3.next();
                        String noiSinh = jsonObject1.get(key3).toString();
                        if (noiSinh.contains(",") || noiSinh.contains("(") || noiSinh.contains("?")) {
                            insert(m, key, "hasBornAt", noiSinh);

                        } else {
                            noiSinh = noiSinh.replaceAll(" ", "_");
                            insert2(m, key, "hasBornAt", noiSinh);
                            insert(m, noiSinh, Constant.PREFIX + "AdministrativeDivision");
                        }
                    }
                }
                if (jsonObject.containsKey("hasJob")) {
                    JSONObject jsonObject2 = (JSONObject) jsonObject.get("hasJob");
                    Set<String> set4 = jsonObject2.keySet();
                    set4.remove("dbpedia");
                    Iterator<String> iterator4 = set4.iterator();
                    while (iterator4.hasNext()) {
                        String key4 = iterator4.next();
                        if (!jsonObject2.get(key4).toString().isEmpty()) {
                            String[] nghe = jsonObject2.get(key4).toString().split("~");
                            for (int i = 0; i < nghe.length; i++) {
                                insert2(m, key, "hasJob", nghe[i].replaceAll(" ", "_"));
                                insert(m, nghe[i].replaceAll(" ", "_"), Constant.PREFIX + "PositionTitle");
                            }
                        }
                    }
                }

                if (jsonObject.containsKey("hasChild")) {
                    if (!jsonObject.get("hasChild").toString().isEmpty()) {
                        String[] con = jsonObject.get("hasChild").toString().split("~");
                        for (int i = 0; i < con.length; i++) {
                            String child = con[i];
                            if (child.contains("(") || child.contains("?")) {
                                insert(m, key, "hasChild", child);

                            } else {
                                child = child.replaceAll(" ", "_");
                                insert2(m, key, "hasChild", child);
                                insert(m, child, Constant.PREFIX + "Person");
                            }
                        }
                    }

                }
                if (jsonObject.containsKey("byMother")) {
                    if (jsonObject.get("byMother").toString() != null && !jsonObject.get("byMother").toString().isEmpty()) {
                        String mother = jsonObject.get("byMother").toString();
                        if (mother.contains("(") || mother.contains("?")) {
                            insert(m, key, "byMother", mother);
                            insert(m, key, "hasParent", mother);
                        } else {
                            mother = mother.replaceAll(" ", "_");
                            insert2(m, key, "byMother", mother);
                            insert2(m, key, "hasParent", mother);
                            insert(m, mother, Constant.PREFIX + "Person");
                        }


                    }
                }
                if (jsonObject.containsKey("hasSuccessor")) {
                    if (jsonObject.get("hasSuccessor").toString() != null && !jsonObject.get("hasSuccessor").toString().isEmpty()
                            && !jsonObject.get("hasSuccessor").toString().equals("Tri???u ?????i s???p ?????")) {
                        String suc = jsonObject.get("hasSuccessor").toString();
                        if (suc.contains("(") || suc.contains("?")) {
                            insert(m, key, "hasSuccessor", suc);
                        } else {
                            suc = suc.replaceAll(" ", "_");
                            insert2(m, key, "hasSuccessor", suc);
                            insert(m, suc, Constant.PREFIX + "Person");
                        }
                    }
                }
                if (jsonObject.containsKey("hasPredecessor")) {
                    if (jsonObject.get("hasPredecessor").toString() != null && !jsonObject.get("hasPredecessor").toString().isEmpty()
                            && !jsonObject.get("hasPredecessor").toString().equals("S??ng l???p tri???u ?????i")) {
                        String pre = jsonObject.get("hasPredecessor").toString();
                        if (pre.contains("(") || pre.contains("?")) {
                            insert(m, key, "hasPredecessor", pre);
                        } else {
                            pre = pre.replaceAll(" ", "_");
                            insert2(m, key, "hasPredecessor", pre);
                            insert(m, pre, Constant.PREFIX + "Person");
                        }
                    }
                }
                insert(jsonObject, key, m, "buriedPlace", "HeritageSite");
                insert(jsonObject, key, m, "belongEthnic", "Ethnic");
                insert(jsonObject, key, m, "hasBirthName");
                insert(jsonObject, key, m, "hasHusband", "Person");
                insert(jsonObject, key, m, "hasWife", "Person");
                insert(jsonObject, key, m, "hasParent", "Person");
                insert(jsonObject, key, m, "orKnownAs");


            }
            OutputStream output = new FileOutputStream(Constant.FILE);
            m.write(output, "RDF/XML", null);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insert(JSONObject jsonObject, String key, OntModel m, String predicate) throws Exception {
        if (jsonObject.containsKey(predicate)) {
            if (jsonObject.get(predicate).toString() != null && !jsonObject.get(predicate).toString().isEmpty()) {
                insert(m, key, predicate, jsonObject.get(predicate).toString());
            }
        }
    }

    public static void insert(JSONObject jsonObject, String key, OntModel m, String predicate, String type) throws Exception {
        if (jsonObject.containsKey(predicate)) {
            if (jsonObject.get(predicate).toString() != null && !jsonObject.get(predicate).toString().isEmpty()) {
                String obj = jsonObject.get(predicate).toString();
                if (obj.contains("(") || obj.contains(",") || obj.contains("?")) {
                    insert(m, key, predicate, jsonObject.get(predicate).toString());
                } else {
                    obj = obj.replaceAll(" ", "_");
                    insert2(m, key, predicate, obj);
                    insert(m, obj, Constant.PREFIX + type);

                }
            }
        }
    }


    //khai bao type cho instant
    public static void insert(OntModel m, String name, String object) throws Exception {
        if (name == null || object == null || name == "" || object == "") {
            return;
        }
        Resource a0 = m.getResource(Constant.PREFIX + name);
        Resource a1 = m.getResource(object);
        // m.add(a1, RDF.type, OWL.Class);
        m.add(a0, RDF.type, a1);
    }

    //Insert l??? h???i
    public static void insert2(String filename) {
        try {
            OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
            InputStream in = FileManager.get().open(Constant.FILE);
            m.read(in, null);
            JSONParser jsonParser = new JSONParser();
            FileReader reader = new FileReader(System.getProperty("user.dir") + "/" + filename);
            JSONArray jsonArray = (JSONArray) jsonParser.parse(reader);
            Property property1 = m.getProperty(Constant.PREFIX + "hasTimeHappen");
            Property property2 = m.getProperty(Constant.PREFIX + "isHeldAt");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                if (!jsonObject.get("name").toString().contains("(")) {
                    Resource name = m.getResource(Constant.PREFIX + jsonObject.get("name").toString().replaceAll(" ", "_"));
                    if (jsonObject.get("date") != null) {

                        JSONObject date = (JSONObject) jsonObject.get("date");
                        if (date.containsKey("lunar_calendar")) {
                            m.add(name, property1, date.get("lunar_calendar").toString());
                        }
                        if (date.containsKey("lunar_calender")) {
                            m.add(name, property1, date.get("lunar_calender").toString() + " ??m l???ch");
                        }
                        if (date.containsKey("solar_calendar")) {
                            m.add(name, property1, date.get("solar_calendar").toString());
                        }
                    }

                    if (jsonObject.get("province") != null && !jsonObject.get("province").toString().contains("[")) {
                        m.add(name, property2, jsonObject.get("province").toString());
                    }
                }
            }
            OutputStream output = new FileOutputStream(Constant.FILE);
            m.write(output, "RDF/XML", null);
            output.close();
        } catch (
                Exception e) {
            e.printStackTrace();
        }

    }
    //insert su kien
    public static void insert3(String filename){
        try{
            OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
            InputStream in = FileManager.get().open(Constant.FILE);
            m.read(in, null);

            JSONParser jsonParser = new JSONParser();
            FileReader reader = new FileReader(System.getProperty("user.dir") + "/"+filename);
            Object obj = jsonParser.parse(reader);
            JSONArray jsonArray = (JSONArray) obj;

            Resource classPerson = m.getResource(Constant.PREFIX+"Person");
            Resource classEvent = m.getResource(Constant.PREFIX+"HistoricEvent");
            Property moTa = m.getProperty(Constant.PREFIX+"hasDescription");
            Property relate = m.getProperty(Constant.PREFIX+"related");

            for (int i = 0; i<jsonArray.size();i++){
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String event = jsonObject.get("su_kien").toString().replaceAll(" ","_");
                String person = jsonObject.get("name").toString().replaceAll(" ","_");
                Resource objectEvent = m.getResource(Constant.PREFIX+event);
                Resource objectPerson = m.getResource(Constant.PREFIX+person);
                m.add(objectEvent,RDF.type,classEvent);
                m.add(objectPerson,RDF.type,classPerson);
                m.add(objectEvent,moTa,jsonObject.get("su_kien").toString());
                m.add(objectEvent,relate,objectPerson);
                m.add(objectPerson,relate,objectEvent);

            }

            OutputStream output = new FileOutputStream(Constant.FILE);
            m.write(output, "RDF/XML", null);
            output.close();

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    //khai bao type cho instant
    public static void insert(String name, String object) throws Exception {

        if (name == null || object == null || name == "" || object == "") {
            return;
        }
        OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        InputStream in = FileManager.get().open(Constant.FILE);
        m.read(in, null);
        Resource a0 = m.getResource(Constant.PREFIX + name);
        Resource a1 = m.getResource(object);

        m.add(a1, RDF.type, OWL.Class);
        m.add(a0, RDF.type, a1);

        OutputStream output = new FileOutputStream(Constant.FILE);
        m.write(output, "RDF/XML", null);
        output.close();


    }

    //insert comment cho property
    public static void insert2(String name, String object) throws Exception {

        if (name == null || object == null || name == "" || object == "") {
            return;
        }
        OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        InputStream in = FileManager.get().open(Constant.FILE);
        m.read(in, null);
        Resource a0 = m.getResource(Constant.PREFIX + name);
        m.add(a0, RDF.type, OWL.AnnotationProperty);
        m.add(a0, RDFS.comment, object);

        OutputStream output = new FileOutputStream(Constant.FILE);
        m.write(output, "RDF/XML", null);
        output.close();


    }
    //Tao canh noi object voi thuoc tinh cua no

    public static void insert(OntModel m, String name, String predicate, String object) throws Exception {
        if (name == null || object == null || predicate == null || name == "" || object == "" || predicate == "") {
            return;
        }
        Resource a0 = m.getResource(Constant.PREFIX + name);
        Property property = m.getProperty(Constant.PREFIX + predicate);
        m.add(a0, property, object);
    }

    //Tao c???nh noi object voi object
    public static void insert2(OntModel m, String name, String predicate, String object) throws Exception {

        if (name == null || object == null || predicate == null || name == "" || object == "" || predicate == "") {
            return;
        }
        Resource a0 = m.getResource(Constant.PREFIX + name);
        Property property = m.getProperty(Constant.PREFIX + predicate);
        Resource node = m.getResource(Constant.PREFIX + object);
        m.add(a0, property, node);


    }


    //Tao canh noi object voi thuoc tinh cua no
    public static void insert(String name, String predicate, String object) throws Exception {

        if (name == null || object == null || predicate == null || name == "" || object == "" || predicate == "") {
            return;
        }

        OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        InputStream in = FileManager.get().open(Constant.FILE);
        m.read(in, null);
        Resource a0 = m.getResource(Constant.PREFIX + name);

        Property property = m.getProperty(Constant.PREFIX + predicate);


        m.add(a0, property, object);
        OutputStream output = new FileOutputStream(Constant.FILE);
        m.write(output, "RDF/XML", null);
        output.close();


    }


    //Tao canh noi 2 object
    public static void insert2(String name, String predicate, String object) throws Exception {

        if (name == null || object == null || predicate == null || name == "" || object == "" || predicate == "") {
            return;
        }

        OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        InputStream in = FileManager.get().open(Constant.FILE);
        m.read(in, null);
        Resource a0 = m.getResource(Constant.PREFIX + name);

        Property property = m.getProperty(Constant.PREFIX + predicate);
        Resource node = m.getResource(Constant.PREFIX + object);

        m.add(a0, property, node);
        OutputStream output = new FileOutputStream(Constant.FILE);
        m.write(output, "RDF/XML", null);
        output.close();


    }

    //predicate t??y ch???nh, kh??ng m???c ?????nh prefix l?? vntourism
    public static void insert3(String name, String predicate, String object) throws Exception {

        if (name == null || object == null || predicate == null || name == "" || object == "" || predicate == "") {
            return;
        }

        OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        InputStream in = FileManager.get().open(Constant.FILE);
        m.read(in, null);
        Resource a0 = m.getResource(Constant.PREFIX + name);

        Property property = m.getProperty(predicate);


        m.add(a0, property, object);
        OutputStream output = new FileOutputStream(Constant.FILE);
        m.write(output, "RDF/XML", null);
        output.close();


    }

    //predicate t??y ch???nh, kh??ng m???c ?????nh prefix l?? vntourism
    public static void insert3(OntModel m, String name, String predicate, String object) throws Exception {

        if (name == null || object == null || predicate == null || name == "" || object == "" || predicate == "") {
            return;
        }
        Resource a0 = m.getResource(Constant.PREFIX + name);
        Property property = m.getProperty(predicate);
        Resource a1 = m.getResource(Constant.PREFIX + object);
        m.add(a0, property, a1);


    }

    //insert comment cho predicate t??y ch???nh, kh??ng m???c ?????nh prefix l?? vntourism
    public static void insert4(String name, String object) throws Exception {

        if (name == null || object == null || name == "" || object == "") {
            return;
        }
        OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        InputStream in = FileManager.get().open(Constant.FILE);
        m.read(in, null);
        Resource a0 = m.getResource(name);
        m.add(a0, RDF.type, OWL.AnnotationProperty);
        m.add(a0, RDFS.comment, object);

        OutputStream output = new FileOutputStream(Constant.FILE);
        m.write(output, "RDF/XML", null);
        output.close();


    }

    public static void get() {
        OntModel ontoModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        try {
            InputStream in = FileManager.get().open(ontoFile);
            try {
                ontoModel.read(in, "");
                ExtendedIterator<OntProperty> iterator1 = ontoModel.listAllOntProperties();
                ExtendedIterator<OntClass> iterator2 = ontoModel.listClasses();

                List<ExtendedIterator> list = new ArrayList<>();
                list.add(iterator1);
                list.add(iterator2);

                for (ExtendedIterator extendedIterator : list) {
                    while (extendedIterator.hasNext()) {
                        Object i = extendedIterator.next();
                        if (i.toString().contains("#")) {
                            System.out.println(i.toString().split("#")[1]);
                        }
                    }
                    System.out.println("----------------------");
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

    public static String pretty(String s) {
        String[] list = s.split("\\^");
        if (list.length > 1) {
            return list[0];
        }
        return s.replaceAll("http://www.semanticweb.org/minhn/ontologies/2021/0/vntourism#", "vntourism:")
                .replaceAll("http://www.w3.org/2006/time#", "time:");

    }

    public static String pretty2(String s) {
        return s.replaceAll("vntourism:", "").replaceAll("time:", "").replaceAll("_", " ");
    }

    public static String[] convertDate(String s) {
        String[] list = s.split("T");
        String[] result = list[0].split("-");
        if (!result[1].equals("10"))
            result[1] = result[1].replaceAll("0", "");
        if (!result[2].equals("10") && !result[2].equals("20") && !result[2].equals("30"))
            result[2] = result[2].replaceAll("0", "");
        return result;
    }

    public static String upperFirstCharacter(String s) {
        String first = s.substring(0, 1);
        String remain = s.substring(1);
        return first.toUpperCase() + remain;
    }

    public static void main(String[] args) {
        try {
//            InitJena.insert("L???_h???i_?????n_C???a_??ng", Constant.PREFIX + "Festival");
//            InitJena.insert("L???_h???i_B???ch_?????ng", Constant.PREFIX + "Festival");
//            InitJena.insert("L???_h???i_?????n_C???a_??ng", "hasTimeHappen", "th??ng 2 ??m l???ch");
//            InitJena.insert2("L???_h???i_?????n_C???a_??ng", "isHeldAt", "Qu???ng_Ninh");
//            InitJena.insert2("L???_h???i_B???ch_?????ng", "isHeldAt", "Qu???ng_Ninh");
//            InitJena.insert("Qu???ng_Ninh",Constant.PREFIX+"AdministrativeDivision");
//            InitJena.insert2("isApartOf", "n???m trong");
//            InitJena.insert2("hasChronology", "c?? ni??n ?????i");
//            InitJena.insert2("isHeldAt", "???????c t??? ch???c");
//            InitJena.insert2("hasDescription", "???????c m?? t??? l??");
//            InitJena.insert2("wasBuiltIn", "???????c x??y d???ng v??o");
//            InitJena.insert2("wasDerivedFrom", "c?? ngu???n g???c t???");
//            InitJena.insert2("related", "li??n quan");
//            InitJena.insert2("hasDied", "m???t v??o");
//            InitJena.insert2("hasBorn", "sinh v??o");
//            InitJena.insert2("orKnownAs", "hay c??n ???????c g???i l??");
//            InitJena.insert2("hasReignTo", "tr??? v??");
//            InitJena.insert2("hasTimeHappen", "di???n ra v??o");
//            InitJena.insert2("hasPeriod", "??? th???i k??? l??");
//            InitJena.insert2("wasBuiltBy", "???????c x??y d???ng b???i");
//            InitJena.insert2("hasJob", "gi??? ch???c v???");
//            InitJena.insert2("hasSuccessor", "c?? ng?????i k??? v??? l??");
//            InitJena.insert2("hasBornAt", "sinh t???i");
//            InitJena.insert2("chosenCapitalBy", "???????c ch???n l??m th??? ???? b???i");
//            InitJena.insert4(Constant.PREFIX_TIME + "hasBeginning", "b???t ?????u v??o");
//            InitJena.insert4(Constant.PREFIX_TIME + "hasEnd", "k???t th??c v??o");
//            InitJena.insert2("??i???n_Long_An", "isApartOf", "qu???n_th???_di_t??ch_C???_????_Hu???");
//            InitJena.insert("??i???n_Long_An", Constant.PREFIX + "CulturalHistoricalSite");
//            InitJena.insert("qu???n_th???_di_t??ch_C???_????_Hu???", Constant.PREFIX + "CulturalHistoricalSite");
//            InitJena.insert("????n_????_B??nh_??a", "hasChronology", "3000 n??m");
//            InitJena.insert("Kinh_th??nh_Hu???", "wasBuiltIn", "n??m 1805");
//            InitJena.insert3("L???_h???i_?????n_C???a_??ng", Constant.PREFIX_TIME + "hasBeginning", "3/2 ??m l???ch");
//            InitJena.insert3("L???_h???i_?????n_C???a_??ng", Constant.PREFIX_TIME + "hasEnd", "cu???i th??ng 3 ??m l???ch");
//            InitJena.insert("????n_????_B??nh_??a", Constant.PREFIX + "ArchaeologicalHistoricalSite");
//            InitJena.insert("Kinh_th??nh_Hu???", Constant.PREFIX + "CitadelArchitecture");
//            InitJena.insert2("hasParent","c?? cha m??? l??");
//            InitJena.insert2("hasDegree","c?? h???c v???");
//            InitJena.insert2("hasReligion","c?? t??n gi??o");
//            InitJena.insert2("buriedPlace","an ngh???");
//            InitJena.insert2("hasCommemorate","t?????ng nh???");
//            InitJena.insert2("hasCommemorate","k??? ni???m");
//            InitJena.insert2("hasCommemorate","tri ??n");

//            InitJena.insert("pretty_people_data_8.json");
////            InitJena.insert2("festival_combination3.json");


//            OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
//            InputStream in = FileManager.get().open(Constant.FILE);
//            m.read(in, null);
//            insert(m,"Ph???m_Ng??_L??o",Constant.PREFIX+"Person");
//            insert(m,"Ch??a_H????ng",Constant.PREFIX+"BuddhistTemple");
//            insert(m,"H??_N???i",Constant.PREFIX+"AdministrativeDivision");
//            insert2(m,"Ch??a_H????ng","isApartOf","H??_N???i");
//            insert(m,"Y??n_T???",Constant.PREFIX+"BuddhistTemple");
//            insert2(m,"Y??n_T???","isApartOf","Qu???ng_Ninh");
//            insert(m,"Ch??a_B??i_????nh",Constant.PREFIX+"BuddhistTemple");
//            insert(m,"Ninh_B??nh",Constant.PREFIX+"AdministrativeDivision");
//            insert2(m,"Ch??a_B??i_????nh","isApartOf","Ninh_B??nh");
//            insert(m,"Ch??a_H??",Constant.PREFIX+"BuddhistTemple");
//            insert2(m,"Ch??a_H??","isApartOf","H??_N???i");
//            insert(m,"Ch??a_Ba_V??ng",Constant.PREFIX+"BuddhistTemple");
//            insert2(m,"Ch??a_Ba_V??ng","isApartOf","Qu???ng_Ninh");
//            insert(m,"Ch??a_C??i_B???u",Constant.PREFIX+"BuddhistTemple");
//            insert2(m,"Ch??a_C??i_B???u","isApartOf","Qu???ng_Ninh");
//            insert(m,"?????n_C???a_??ng",Constant.PREFIX+"Temple");
//            insert2(m,"?????n_C???a_??ng","isApartOf","Qu???ng_Ninh");
//            insert(m,"?????n_Tr???n",Constant.PREFIX+"Temple");
//            insert(m,"Nam_?????nh",Constant.PREFIX+"AdministrativeDivision");
//            insert2(m,"?????n_Tr???n","isApartOf","Nam_?????nh");
//            insert(m,"V???nh_H???_Long",Constant.PREFIX+"NaturalArea");
//            insert2(m,"V???nh_H???_Long","isApartOf","Qu???ng_Ninh");
//            insert(m,"N??i_B??i_Th??",Constant.PREFIX+"NaturalArea");
//            insert2(m,"N??i_B??i_Th??","isApartOf","Qu???ng_Ninh");
//            insert(m,"B??i_bi???n_Tr??_C???",Constant.PREFIX+"NaturalArea");
//            insert2(m,"B??i_bi???n_Tr??_C???","isApartOf","Qu???ng_Ninh");
//            insert(m,"L??ng_B??c",Constant.PREFIX+"Landscape-Place");
//            insert2(m,"L??ng_B??c","isApartOf","H??_N???i");
//            insert(m,"V??n_Mi???u",Constant.PREFIX+"Landscape-Place");
//            insert2(m,"V??n_Mi???u","isApartOf","H??_N???i");
//            insert(m,"Ph???_c???_H??_N???i",Constant.PREFIX+"Landscape-Place");
//            insert2(m,"Ph???_c???_H??_N???i","isApartOf","H??_N???i");
//            insert(m,"H???_T??y",Constant.PREFIX+"NaturalArea");
//            insert2(m,"H???_T??y","isApartOf","H??_N???i");
//            insert(m,"H???_G????m",Constant.PREFIX+"NaturalArea");
//            insert2(m,"H???_G????m","isApartOf","H??_N???i");
//            insert(m,"H??_Giang",Constant.PREFIX+"AdministrativeDivision");
//            insert(m,"R???ng_th??ng_Y??n_Minh",Constant.PREFIX+"Landscape-Place");
//            insert2(m,"R???ng_th??ng_Y??n_Minh","isApartOf","H??_Giang");
//            insert(m,"Thung_l??ng_S???ng_L??",Constant.PREFIX+"Landscape-Place");
//            insert2(m,"Thung_l??ng_S???ng_L??","isApartOf","H??_Giang");
//            insert(m,"C???t_c???_L??ng_C??",Constant.PREFIX+"Landscape-Place");
//            insert2(m,"C???t_c???_L??ng_C??","isApartOf","H??_Giang");
//            insert2(m,"Tr???n_Nh??n_T??ng","related","Y??n_T???");
//            insert2(m,"L??_L???i","related","H???_G????m");
//            insert(m,"Tr??ng_An",Constant.PREFIX+"Landscape-Place");
//            insert2(m,"Tr??ng_An","isApartOf","Ninh_B??nh");
//            insert(m,"Hoa_L??",Constant.PREFIX+"Landscape-Place");
//            insert2(m,"Hoa_L??","isApartOf","Ninh_B??nh");
//            insert(m,"V?????n_qu???c_gia_C??c_Ph????ng",Constant.PREFIX+"Landscape-Place");
//            insert2(m,"V?????n_qu???c_gia_C??c_Ph????ng","isApartOf","Ninh_B??nh");
//            insert2(m,"L???_H???i_T???ch_??i???n","hasCommemorate","L??_?????i_H??nh");
//            insert2(m,"L????_h????i_C??n_S??n_-_Ki????p_Ba??c","hasCommemorate","Tr???n_H??ng_?????o");
//            insert2(m,"L???_h???i_?????n_C???a_??ng","hasCommemorate","Tr???n_Qu???c_T???ng");

//              insert2(m,"H???i_?????n_Xu??n_Lai","hasCommemorate","Th??nh_Gi??ng");
//              insert2(m,"L???_h???i_?????ng_??a","hasCommemorate","Nguy???n_Hu???");
//              insert2(m,"H???i_Gi??ng_?????n_S??c","hasCommemorate","Th??nh_Gi??ng");
//              insert2(m,"L???_h???i_C???_Loa","hasCommemorate","An_D????ng_V????ng");
//              insert2(m,"H???i_????nh_Th?????ng_L??o","hasCommemorate","An_D????ng_V????ng");
//              insert2(m,"H???i_S??n_Thanh","hasCommemorate","T??_Hi???n_Th??nh");
//              insert2(m,"H???i_????nh_Kim_M??_H???","hasCommemorate","Ph??ng_H??ng");
//              insert2(m,"H???i_ch???_Chu??ng","hasCommemorate","Ph??ng_H??ng");
//              insert2(m,"L???_h???i_l??ng_Tri???u_Kh??c","hasCommemorate","Ph??ng_H??ng");
//              insert2(m,"H???i_l??ng_T??","hasCommemorate","L??_Ho??n");
//              insert2(m,"L???_h???i_????nh_????ng_Ph??","hasCommemorate","Nguy???n_Si??u");
//              insert2(m,"L???_h???i_l??ng_B???c_Bi??n","hasCommemorate","L??_Th?????ng_Ki???t");
//              insert(m,"L??_Th?????ng_Ki???t","orKnownAs","L?? Nam ?????");
//              insert(m,"Nguy???n_Hu???","orKnownAs","Quang Trung");
//              insert(m,"3_th??ng_2_??m_l???ch",Constant.PREFIX_TIME+"DateTimeDescription");
//              insert3(m,"3_th??ng_2_??m_l???ch",Constant.PREFIX_TIME+"hasTRS","LunarCalendar");
//              insert2(m,"L???_h???i_?????n_C???a_??ng","hasTimeHappen","3_th??ng_2_??m_l???ch");
//            insert(m,"5_th??ng_1_??m_l???ch",Constant.PREFIX_TIME+"DateTimeDescription");
//            insert3(m,"5_th??ng_1_??m_l???ch",Constant.PREFIX_TIME+"hasTRS","LunarCalendar");
//
//            insert2(m,"L???_h???i_?????ng_??a","hasTimeHappen","5_th??ng_1_??m_l???ch");
//
//            OutputStream output = new FileOutputStream(Constant.FILE);
//            m.write(output, "RDF/XML", null);
//            output.close();
//            insert3("3_th??ng_2_??m_l???ch",Constant.PREFIX_TIME+"month","2");
//            insert3("3_th??ng_2_??m_l???ch",Constant.PREFIX_TIME+"day","3");
//
//            insert3("5_th??ng_1_??m_l???ch",Constant.PREFIX_TIME+"month","1");
//            insert3("5_th??ng_1_??m_l???ch",Constant.PREFIX_TIME+"day","5");
//            insert("L??_L???i","hasJob","vua");
//            insert3("19-5-1980",Constant.PREFIX_TIME+"month","5");
//            insert3("19-5-1980",Constant.PREFIX_TIME+"day","19");
//            insert3("19-5-1980",Constant.PREFIX_TIME+"year","1980");
//            insert2("H???_Ch??_Minh","hasBorn","19-5-1980");
//            insert("19-5-1980", Constant.PREFIX_TIME + "DateTimeDescription");
//            insert("X??y_th??nh_C???_Loa",Constant.PREFIX+"HistoricEvent");
//            insert("X??y_th??nh_C???_Loa","hasDescription","X??y th??nh C??? Loa");
//            insert2("X??y_th??nh_C???_Loa","related","An_D????ng_V????ng");

//             insert3("su_kien.json");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
