package com.mlpj.ontology.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Constant {
    public final static String FILE = "xmlowlv9.owl";
    public final static String FILE2 = "festivalVietNam.owl";

    public final static String PREFIX = "http://www.semanticweb.org/minhn/ontologies/2021/0/vntourism#";
    public final static String PREFIX_TIME = "http://www.w3.org/2006/time#";
    public final static String PREFIX_QUERY = "PREFIX vntourism:<http://www.semanticweb.org/minhn/ontologies/2021/0/vntourism#>" +
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
        map.put("lễ hội", "vntourism:Festival");
        map.put("con người", "vntourism:Person");
        map.put("đơn vị hành chính", "vntourism:AdministrativeDivision");
        map.put("di tích khảo cổ", "vntourism:ArchaeologicalHistoricalSite");
        map.put("kiến trúc thành quách", "vntourism:CitadelArchitecture");
        map.put("di tích lịch sử văn hóa", "vntourism:CulturalHistoricalSite");

        map.put("bắt đầu vào", "time:hasBeginning");
        map.put("kết thúc vào", "time:hasEnd");
        map.put("nằm trong", "vntourism:isApartOf");
        map.put("có niên đại", "vntourism:hasChronology");
        map.put("được tổ chức", "vntourism:isHeldAt");
        map.put("được xây dựng vào", "vntourism:wasBuiltIn");
        map.put("liên quan", "vntourism:related");
        map.put("mất vào", "vntourism:hasDied");
        map.put("sinh vào", "vntourism:hasBorn");
        map.put("diễn ra vào", "vntourism:hasTimeHappen");
        map.put("ở thời kỳ", "vntourism:hasPeriod");
        map.put("được xây dựng bởi", "vntourism:wasBuiltBy");
        map.put("giữ chức vụ", "vntourism:hasJob");
        map.put("có người kế vị là", "vntourism:hasSuccessor");
        map.put("sinh tại", "vntourism:hasBornAt");
        map.put("được chọn làm thủ đô bởi", "vntourism:chosenCapitalBy");
        map.put("tri ân", "vntourism:hasCommemorate");
        map.put("tưởng nhớ", "vntourism:hasCommemorate");
        map.put("kỷ niệm", "vntourism:hasCommemorate");
        map.put("an nghỉ", "vntourism:buriedPlace");

        map.put("được mô tả là", "vntourism:hasDescription");
        map.put("có nguồn gốc từ", "vntourism:wasDerivedFrom");
        map.put("hay còn được gọi là", "vntourism:orKnownAs");

        map.put("cha","vntourism:hasParent");
        map.put("mẹ","vntourism:hasParent");
    }

}
