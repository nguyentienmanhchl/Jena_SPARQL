package com.mlpj.ontology.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Constant {
    public final static String FILE = "vntourism.owl";
    public final static String FILE2 = "festivalVietNam.owl";

    public final static String PREFIX = "http://www.semanticweb.org/minhn/ontologies/2021/0/vntourism#";
    public final static String PREFIX_TIME ="http://www.w3.org/2006/time#";
    public final static String PREFIX_QUERY = "PREFIX foaf: <http://www.semanticweb.org/minhn/ontologies/2021/0/vntourism#>" +
            "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
            "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>" +
            "PREFIX ont:<http://www.semanticweb.org/admin/ontologies/2020/2/untitled-ontology-5#>" +
            "PREFIX owl:<http://www.w3.org/2002/07/owl#>" +
            "PREFIX time:<http://www.w3.org/2006/time#>";
    public final static String PREFIX_OWL = "http://www.w3.org/2002/07/owl#";
    public final static HashMap<String, String> map = new HashMap<>();

    public final static String[] _STRING_LIST = {"Ontology", "Restriction", "Class", "NamedIndividual", "DeprecatedClass",
            "DatatypeProperty", "ObjectProperty", "FunctionalProperty", "DayOfWeek", "TemporalUnit", "Datatype",
            "TransitiveProperty", "DeprecatedProperty", "Period"};
    public final static List<String> STRING_LIST = new ArrayList<>();

    static {
        for (int i = 0; i < _STRING_LIST.length; i++) {
            STRING_LIST.add(_STRING_LIST[i]);
        }
        map.put("lễ hội", "Festival");
        map.put("nằm trong", "isApartOf");
        map.put("có niên đại", "hasChronology");
        map.put("được tổ chức tại", "hasOrganization");
        map.put("được mô tả là", "hasDescription");
        map.put("được xây dựng vào", "hasBuildTime");
        map.put("có nguồn gốc từ", "wasDerivedFrom");
        map.put("có liên quan", "related");
        map.put("mất vào", "hasDied");
        map.put("sinh vào", "hasBorn");
        map.put("hay còn được gọi là", "orKnownAs");
        map.put("trị vì đến", "hasReignTo");
        map.put("trị vì từ","hasReignFrom");
        map.put("diễn ra vào", "hasTimeHappen");
        map.put("ở thời kỳ là", "hasPeriod");
        map.put("được xây dựng bởi", "hasBuildBy");
        map.put("giữ chức vụ", "hasJob");
        map.put("có người kế vị là", "hasSuccessor");
        map.put("sinh tại", "hasBornAt");
        map.put("bắt đầu vào","hasBeginning");
        map.put("kết thúc vào","hasEnd");
        map.put("được chọn làm thủ đô bởi","chosenCapitalBy");
    }

}
