import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.time.Instant;
import java.util.Map;

public class Main {
    public static final String CASE_1 = "{ \"field\" : [[-1 ,true,-3, false,\"er\\\"\\\\\\\"ewqe\"], [{\"id\":123456 , \"name\" : \"Jack\"}, {\"strings\"  :{\"string1\": \"I am 1\", \"string2\" :\"I am 2\", \"string3\" :\"I am 3\"  } } ]]}";
    public static final String CASE_2 = "{ \"name\" : null , \"alexa\":10000,\"sites\":{\"site1\":\"www.runoob.com\",\"site2\":\"m.runoob.com\",\"site3\" : \"c.runoob.com\" } }";
    public static final String CASE_3 = "{ \"send\\\"_id\":10001,\"send_name\":\"Pei\\\"nan\"}";
    public static final String CASE_4 = "{ \"field\" : [null, true, false, null, -3, null, [null, [], null]]}";
    public static final String CASE_5 = "{ \"field\": \"\\/Hello\\u0026\\u0026\\u0026\\u0026\\u0026\\u0026\\u0026\\u0026 \\\"World\\\" T\"}";
    public static final String FORMATTED_1 = "{\"field\":[[-1,true,-3,false,\"er\\\"\\\\\\\"ewqe\"],[{\"id\":123456,\"name\":\"Jack\"},{\"strings\":{\"string1\":\"I am 1\",\"string2\":\"I am 2\",\"string3\":\"I am 3\"}}]]}";
    public static final String FORMATTED_2 = "{\"name\":null,\"alexa\":10000,\"sites\":{\"site1\":\"www.runoob.com\",\"site2\":\"m.runoob.com\",\"site3\":\"c.runoob.com\"}}";
    public static final String FORMATTED_3 = "{\"send\\\"_id\":10001,\"send_name\":\"Pei\\\"nan\"}";
    public static final String FORMATTED_4 = "{\"field\":[null,true,false,null,-3,null,[null,[],null]]}";
    public static final String FORMATTED_5 = "{\"field\":\"/Hello&&&&&&&& \\\"World\\\" T\"}";

    public static void main(String[] args) {
        try {
            testJsonHelper();
            testJackson();
            testGson();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testJsonHelper() throws Exception {
        JsonObject test1 = JsonObject.create(CASE_1);
        JsonObject test2 = JsonObject.create(CASE_2);
        JsonObject test3 = JsonObject.create(CASE_3);
        JsonObject test4 = JsonObject.create(CASE_4);
        JsonObject test5 = JsonObject.create(CASE_5);

        String str1 = test1.toString();
        String str2 = test2.toString();
        String str3 = test3.toString();
        String str4 = test4.toString();
        String str5 = test5.toString();

        if (!str1.equals(FORMATTED_1)) { throw new Exception(String.format("\n%s\n%s", str1, FORMATTED_1)); }
        if (!str2.equals(FORMATTED_2)) { throw new Exception(String.format("\n%s\n%s", str2, FORMATTED_2)); }
        if (!str3.equals(FORMATTED_3)) { throw new Exception(String.format("\n%s\n%s", str3, FORMATTED_3)); }
        if (!str4.equals(FORMATTED_4)) { throw new Exception(String.format("\n%s\n%s", str4, FORMATTED_4)); }
        if (!str5.equals(FORMATTED_5)) { throw new Exception(String.format("\n%s\n%s", str5, FORMATTED_5)); }

        Instant start = Instant.now();
        for (int i = 0; i < 2000000; ++i) {
            str1 = test1.toString();
            str2 = test2.toString();
            str3 = test3.toString();
            str4 = test4.toString();
            str5 = test5.toString();
        }
        Instant end = Instant.now();
        System.out.println("Json Helper - Serialize Performance: " + (end.toEpochMilli() - start.toEpochMilli()) + " ms");

        start = Instant.now();
        for (int i = 0; i < 2000000; ++i) {
            test1 = JsonObject.create(CASE_1);
            test2 = JsonObject.create(CASE_2);
            test3 = JsonObject.create(CASE_3);
            test4 = JsonObject.create(CASE_4);
            test5 = JsonObject.create(CASE_5);
        }
        end = Instant.now();
        System.out.println("Json Helper - Deserialize Performance: " + (end.toEpochMilli() - start.toEpochMilli()) + " ms");
    }

    private static void testJackson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map map1 = mapper.readValue(CASE_1, Map.class);
        Map map2 = mapper.readValue(CASE_2, Map.class);
        Map map3 = mapper.readValue(CASE_3, Map.class);
        Map map4 = mapper.readValue(CASE_4, Map.class);
        Map map5 = mapper.readValue(CASE_5, Map.class);

        String str1 = mapper.writeValueAsString(map1);
        String str2 = mapper.writeValueAsString(map2);
        String str3 = mapper.writeValueAsString(map3);
        String str4 = mapper.writeValueAsString(map4);
        String str5 = mapper.writeValueAsString(map5);

        if (!str1.equals(FORMATTED_1)) { throw new Exception(String.format("\n%s\n%s", str1, FORMATTED_1)); }
        if (!str2.equals(FORMATTED_2)) { throw new Exception(String.format("\n%s\n%s", str2, FORMATTED_2)); }
        if (!str3.equals(FORMATTED_3)) { throw new Exception(String.format("\n%s\n%s", str3, FORMATTED_3)); }
        if (!str4.equals(FORMATTED_4)) { throw new Exception(String.format("\n%s\n%s", str4, FORMATTED_4)); }
        if (!str5.equals(FORMATTED_5)) { throw new Exception(String.format("\n%s\n%s", str5, FORMATTED_5)); }

        Instant start = Instant.now();
        for (int i = 0; i < 2000000; ++i) {
            str1 = mapper.writeValueAsString(map1);
            str2 = mapper.writeValueAsString(map2);
            str3 = mapper.writeValueAsString(map3);
            str4 = mapper.writeValueAsString(map4);
            str5 = mapper.writeValueAsString(map5);
        }
        Instant end = Instant.now();
        System.out.println("Jackson - Serialize Performance: " + (end.toEpochMilli() - start.toEpochMilli()) + " ms");

        start = Instant.now();
        for (int i = 0; i < 2000000; ++i) {
            map1 = mapper.readValue(CASE_1, Map.class);
            map2 = mapper.readValue(CASE_2, Map.class);
            map3 = mapper.readValue(CASE_3, Map.class);
            map4 = mapper.readValue(CASE_4, Map.class);
            map5 = mapper.readValue(CASE_5, Map.class);
        }
        end = Instant.now();
        System.out.println("Jackson - Deserialize Performance: " + (end.toEpochMilli() - start.toEpochMilli()) + " ms");
    }

    private static void testGson() throws Exception {
        JsonElement element1 = JsonParser.parseString(CASE_1);
        JsonElement element2 = JsonParser.parseString(CASE_2);
        JsonElement element3 = JsonParser.parseString(CASE_3);
        JsonElement element4 = JsonParser.parseString(CASE_4);
        JsonElement element5 = JsonParser.parseString(CASE_5);

        String str1 = element1.toString();
        String str2 = element2.toString();
        String str3 = element3.toString();
        String str4 = element4.toString();
        String str5 = element5.toString();

        if (!str1.equals(FORMATTED_1)) { throw new Exception(String.format("\n%s\n%s", str1, FORMATTED_1)); }
        if (!str2.equals(FORMATTED_2)) { throw new Exception(String.format("\n%s\n%s", str2, FORMATTED_2)); }
        if (!str3.equals(FORMATTED_3)) { throw new Exception(String.format("\n%s\n%s", str3, FORMATTED_3)); }
        if (!str4.equals(FORMATTED_4)) { throw new Exception(String.format("\n%s\n%s", str4, FORMATTED_4)); }
        if (!str5.equals(FORMATTED_5)) { throw new Exception(String.format("\n%s\n%s", str5, FORMATTED_5)); }

        Instant start = Instant.now();
        for (int i = 0; i < 2000000; ++i) {
            str1 = element1.toString();
            str2 = element2.toString();
            str3 = element3.toString();
            str4 = element4.toString();
            str5 = element5.toString();
        }
        Instant end = Instant.now();
        System.out.println("Gson - Serialize Performance: " + (end.toEpochMilli() - start.toEpochMilli()) + " ms");

        start = Instant.now();
        for (int i = 0; i < 2000000; ++i) {
            element1 = JsonParser.parseString(CASE_1);
            element2 = JsonParser.parseString(CASE_2);
            element3 = JsonParser.parseString(CASE_3);
            element4 = JsonParser.parseString(CASE_4);
            element5 = JsonParser.parseString(CASE_5);
        }
        end = Instant.now();
        System.out.println("Gson - Deserialize Performance: " + (end.toEpochMilli() - start.toEpochMilli()) + " ms");
    }
}
