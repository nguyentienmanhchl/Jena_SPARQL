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
            String u = pretty(solution.get("U").toString());//class
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
                            && !jsonObject.get("hasSuccessor").toString().equals("Triều đại sụp đổ")) {
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
                            && !jsonObject.get("hasPredecessor").toString().equals("Sáng lập triều đại")) {
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

    //Insert lễ hội
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
                            m.add(name, property1, date.get("lunar_calender").toString() + " Âm lịch");
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

    //Tao cạnh noi object voi object
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

    //predicate tùy chỉnh, không mặc định prefix là vntourism
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

    //predicate tùy chỉnh, không mặc định prefix là vntourism
    public static void insert3(OntModel m, String name, String predicate, String object) throws Exception {

        if (name == null || object == null || predicate == null || name == "" || object == "" || predicate == "") {
            return;
        }
        Resource a0 = m.getResource(Constant.PREFIX + name);
        Property property = m.getProperty(predicate);
        Resource a1 = m.getResource(Constant.PREFIX + object);
        m.add(a0, property, a1);


    }

    //insert comment cho predicate tùy chỉnh, không mặc định prefix là vntourism
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
        String[] list = s.split("#");
        String[] list2 = s.split("\\^");
        if (list2.length > 1) {
            return list2[0].replaceAll("_", " ");
        }

        if (list.length == 1) {
            return s.replaceAll("_", " ");
        } else {
            return list[1].replaceAll("_", " ");
        }

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
//            InitJena.insert("Lễ_hội_đền_Cửa_Ông", Constant.PREFIX + "Festival");
//            InitJena.insert("Lễ_hội_Bạch_Đằng", Constant.PREFIX + "Festival");
//            InitJena.insert("Lễ_hội_đền_Cửa_Ông", "hasTimeHappen", "tháng 2 Âm lịch");
//            InitJena.insert2("Lễ_hội_đền_Cửa_Ông", "isHeldAt", "Quảng_Ninh");
//            InitJena.insert2("Lễ_hội_Bạch_Đằng", "isHeldAt", "Quảng_Ninh");
//            InitJena.insert("Quảng_Ninh",Constant.PREFIX+"AdministrativeDivision");
//            InitJena.insert2("isApartOf", "nằm trong");
//            InitJena.insert2("hasChronology", "có niên đại");
//            InitJena.insert2("isHeldAt", "được tổ chức");
//            InitJena.insert2("hasDescription", "được mô tả là");
//            InitJena.insert2("wasBuiltIn", "được xây dựng vào");
//            InitJena.insert2("wasDerivedFrom", "có nguồn gốc từ");
//            InitJena.insert2("related", "liên quan");
//            InitJena.insert2("hasDied", "mất vào");
//            InitJena.insert2("hasBorn", "sinh vào");
//            InitJena.insert2("orKnownAs", "hay còn được gọi là");
//            InitJena.insert2("hasReignTo", "trị vì");
//            InitJena.insert2("hasTimeHappen", "diễn ra vào");
//            InitJena.insert2("hasPeriod", "ở thời kỳ là");
//            InitJena.insert2("wasBuiltBy", "được xây dựng bởi");
//            InitJena.insert2("hasJob", "giữ chức vụ");
//            InitJena.insert2("hasSuccessor", "có người kế vị là");
//            InitJena.insert2("hasBornAt", "sinh tại");
//            InitJena.insert2("chosenCapitalBy", "được chọn làm thủ đô bởi");
//            InitJena.insert4(Constant.PREFIX_TIME + "hasBeginning", "bắt đầu vào");
//            InitJena.insert4(Constant.PREFIX_TIME + "hasEnd", "kết thúc vào");
//            InitJena.insert2("Điện_Long_An", "isApartOf", "quần_thể_di_tích_Cố_đô_Huế");
//            InitJena.insert("Điện_Long_An", Constant.PREFIX + "CulturalHistoricalSite");
//            InitJena.insert("quần_thể_di_tích_Cố_đô_Huế", Constant.PREFIX + "CulturalHistoricalSite");
//            InitJena.insert("Đàn_đá_Bình_Đa", "hasChronology", "3000 năm");
//            InitJena.insert("Kinh_thành_Huế", "wasBuiltIn", "năm 1805");
//            InitJena.insert3("Lễ_hội_đền_Cửa_Ông", Constant.PREFIX_TIME + "hasBeginning", "3/2 Âm lịch");
//            InitJena.insert3("Lễ_hội_đền_Cửa_Ông", Constant.PREFIX_TIME + "hasEnd", "cuối tháng 3 Âm lịch");
//            InitJena.insert("Đàn_đá_Bình_Đa", Constant.PREFIX + "ArchaeologicalHistoricalSite");
//            InitJena.insert("Kinh_thành_Huế", Constant.PREFIX + "CitadelArchitecture");
//            InitJena.insert2("hasParent","có cha mẹ là");
//            InitJena.insert2("hasDegree","có học vị");
//            InitJena.insert2("hasReligion","có tôn giáo");
//            InitJena.insert2("buriedPlace","an nghỉ");
//            InitJena.insert2("hasCommemorate","tưởng nhớ");
//            InitJena.insert2("hasCommemorate","kỷ niệm");
//            InitJena.insert2("hasCommemorate","tri ân");

//            InitJena.insert("pretty_people_data_8.json");
////            InitJena.insert2("festival_combination3.json");


//            OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
//            InputStream in = FileManager.get().open(Constant.FILE);
//            m.read(in, null);
//            insert(m,"Phạm_Ngũ_Lão",Constant.PREFIX+"Person");
//            insert(m,"Chùa_Hương",Constant.PREFIX+"BuddhistTemple");
//            insert(m,"Hà_Nội",Constant.PREFIX+"AdministrativeDivision");
//            insert2(m,"Chùa_Hương","isApartOf","Hà_Nội");
//            insert(m,"Yên_Tử",Constant.PREFIX+"BuddhistTemple");
//            insert2(m,"Yên_Tử","isApartOf","Quảng_Ninh");
//            insert(m,"Chùa_Bái_Đính",Constant.PREFIX+"BuddhistTemple");
//            insert(m,"Ninh_Bình",Constant.PREFIX+"AdministrativeDivision");
//            insert2(m,"Chùa_Bái_Đính","isApartOf","Ninh_Bình");
//            insert(m,"Chùa_Hà",Constant.PREFIX+"BuddhistTemple");
//            insert2(m,"Chùa_Hà","isApartOf","Hà_Nội");
//            insert(m,"Chùa_Ba_Vàng",Constant.PREFIX+"BuddhistTemple");
//            insert2(m,"Chùa_Ba_Vàng","isApartOf","Quảng_Ninh");
//            insert(m,"Chùa_Cái_Bầu",Constant.PREFIX+"BuddhistTemple");
//            insert2(m,"Chùa_Cái_Bầu","isApartOf","Quảng_Ninh");
//            insert(m,"Đền_Cửa_Ông",Constant.PREFIX+"Temple");
//            insert2(m,"Đền_Cửa_Ông","isApartOf","Quảng_Ninh");
//            insert(m,"Đền_Trần",Constant.PREFIX+"Temple");
//            insert(m,"Nam_Định",Constant.PREFIX+"AdministrativeDivision");
//            insert2(m,"Đền_Trần","isApartOf","Nam_Định");
//            insert(m,"Vịnh_Hạ_Long",Constant.PREFIX+"NaturalArea");
//            insert2(m,"Vịnh_Hạ_Long","isApartOf","Quảng_Ninh");
//            insert(m,"Núi_Bài_Thơ",Constant.PREFIX+"NaturalArea");
//            insert2(m,"Núi_Bài_Thơ","isApartOf","Quảng_Ninh");
//            insert(m,"Bãi_biển_Trà_Cổ",Constant.PREFIX+"NaturalArea");
//            insert2(m,"Bãi_biển_Trà_Cổ","isApartOf","Quảng_Ninh");
//            insert(m,"Lăng_Bác",Constant.PREFIX+"Landscape-Place");
//            insert2(m,"Lăng_Bác","isApartOf","Hà_Nội");
//            insert(m,"Văn_Miếu",Constant.PREFIX+"Landscape-Place");
//            insert2(m,"Văn_Miếu","isApartOf","Hà_Nội");
//            insert(m,"Phố_cổ_Hà_Nội",Constant.PREFIX+"Landscape-Place");
//            insert2(m,"Phố_cổ_Hà_Nội","isApartOf","Hà_Nội");
//            insert(m,"Hồ_Tây",Constant.PREFIX+"NaturalArea");
//            insert2(m,"Hồ_Tây","isApartOf","Hà_Nội");
//            insert(m,"Hồ_Gươm",Constant.PREFIX+"NaturalArea");
//            insert2(m,"Hồ_Gươm","isApartOf","Hà_Nội");
//            insert(m,"Hà_Giang",Constant.PREFIX+"AdministrativeDivision");
//            insert(m,"Rừng_thông_Yên_Minh",Constant.PREFIX+"Landscape-Place");
//            insert2(m,"Rừng_thông_Yên_Minh","isApartOf","Hà_Giang");
//            insert(m,"Thung_lũng_Sủng_Là",Constant.PREFIX+"Landscape-Place");
//            insert2(m,"Thung_lũng_Sủng_Là","isApartOf","Hà_Giang");
//            insert(m,"Cột_cờ_Lũng_Cú",Constant.PREFIX+"Landscape-Place");
//            insert2(m,"Cột_cờ_Lũng_Cú","isApartOf","Hà_Giang");
//            insert2(m,"Trần_Nhân_Tông","related","Yên_Tử");
//            insert2(m,"Lê_Lợi","related","Hồ_Gươm");
//            insert(m,"Tràng_An",Constant.PREFIX+"Landscape-Place");
//            insert2(m,"Tràng_An","isApartOf","Ninh_Bình");
//            insert(m,"Hoa_Lư",Constant.PREFIX+"Landscape-Place");
//            insert2(m,"Hoa_Lư","isApartOf","Ninh_Bình");
//            insert(m,"Vườn_quốc_gia_Cúc_Phương",Constant.PREFIX+"Landscape-Place");
//            insert2(m,"Vườn_quốc_gia_Cúc_Phương","isApartOf","Ninh_Bình");
//            insert2(m,"Lễ_Hội_Tịch_Điền","hasCommemorate","Lê_Đại_Hành");
//            insert2(m,"Lễ_hội_Côn_Sơn_-_Kiếp_Bạc","hasCommemorate","Trần_Hưng_Đạo");
//            insert2(m,"Lễ_hội_đền_Cửa_Ông","hasCommemorate","Trần_Quốc_Tảng");

//              insert2(m,"Hội_đền_Xuân_Lai","hasCommemorate","Thánh_Gióng");
//              insert2(m,"Lễ_hội_Đống_Đa","hasCommemorate","Nguyễn_Huệ");
//              insert2(m,"Hội_Gióng_đền_Sóc","hasCommemorate","Thánh_Gióng");
//              insert2(m,"Lễ_hội_Cổ_Loa","hasCommemorate","An_Dương_Vương");
//              insert2(m,"Hội_đình_Thượng_Lão","hasCommemorate","An_Dương_Vương");
//              insert2(m,"Hội_Sơn_Thanh","hasCommemorate","Tô_Hiến_Thành");
//              insert2(m,"Hội_đình_Kim_Mã_Hạ","hasCommemorate","Phùng_Hưng");
//              insert2(m,"Hội_chợ_Chuông","hasCommemorate","Phùng_Hưng");
//              insert2(m,"Lễ_hội_làng_Triều_Khúc","hasCommemorate","Phùng_Hưng");
//              insert2(m,"Hội_làng_Tó","hasCommemorate","Lê_Hoàn");
//              insert2(m,"Lễ_hội_đình_Đông_Phù","hasCommemorate","Nguyễn_Siêu");
//              insert2(m,"Lễ_hội_làng_Bắc_Biên","hasCommemorate","Lý_Thường_Kiệt");
//              insert(m,"Lý_Thường_Kiệt","orKnownAs","Lý Nam Đế");
//              insert(m,"Nguyễn_Huệ","orKnownAs","Quang Trung");
//              insert(m,"3_tháng_2_Âm_lịch",Constant.PREFIX_TIME+"DateTimeDescription");
//              insert3(m,"3_tháng_2_Âm_lịch",Constant.PREFIX_TIME+"hasTRS","LunarCalendar");
//              insert2(m,"Lễ_hội_đền_Cửa_Ông","hasTimeHappen","3_tháng_2_Âm_lịch");
//            insert(m,"5_tháng_1_âm_lịch",Constant.PREFIX_TIME+"DateTimeDescription");
//            insert3(m,"5_tháng_1_âm_lịch",Constant.PREFIX_TIME+"hasTRS","LunarCalendar");
//
//            insert2(m,"Lễ_hội_Đống_Đa","hasTimeHappen","5_tháng_1_âm_lịch");
//
//            OutputStream output = new FileOutputStream(Constant.FILE);
//            m.write(output, "RDF/XML", null);
//            output.close();
//            insert3("3_tháng_2_Âm_lịch",Constant.PREFIX_TIME+"month","2");
//            insert3("3_tháng_2_Âm_lịch",Constant.PREFIX_TIME+"day","3");
//
//            insert3("5_tháng_1_âm_lịch",Constant.PREFIX_TIME+"month","1");
//            insert3("5_tháng_1_âm_lịch",Constant.PREFIX_TIME+"day","5");
//            insert("Lê_Lợi","hasJob","vua");
//            insert3("19-5-1980",Constant.PREFIX_TIME+"month","5");
//            insert3("19-5-1980",Constant.PREFIX_TIME+"day","19");
//            insert3("19-5-1980",Constant.PREFIX_TIME+"year","1980");
//            insert2("Hồ_Chí_Minh","hasBorn","19-5-1980");
//            insert("19-5-1980", Constant.PREFIX_TIME + "DateTimeDescription");
//            insert("Xây_thành_Cổ_Loa",Constant.PREFIX+"HistoricEvent");
//            insert("Xây_thành_Cổ_Loa","hasDescription","Xây thành Cổ Loa");
//            insert2("Xây_thành_Cổ_Loa","related","An_Dương_Vương");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
