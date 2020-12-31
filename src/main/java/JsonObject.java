import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"DuplicateBranchesInSwitch", "unused"})
public class JsonObject {
    static class Index {
        private int value;
        private final int length;

        public Index(int value, int length) {
            this.value = value;
            this.length = length;
        }

        public int intValue() throws JsonParseException {
            if (value >= length) {
                throw new JsonParseException("Parse Error - Index overflow.");
            } else {
                return value;
            }
        }

        public void increment() throws JsonParseException {
            plus(1);
        }

        public void plus(int addend) throws JsonParseException {
            if (value + addend > length) {
                throw new JsonParseException("Parse Error - Index overflow.");
            } else {
                value = value + addend;
            }
        }
    }

    static class JsonParseException extends Exception {
        public JsonParseException() {
            super();
        }

        public JsonParseException(String message) {
            super(message);
        }
    }

    private final JsonType jsonType;
    private final Object object;

    public JsonObject() {
        jsonType = JsonType.Null;
        object = null;
    }

    public JsonObject(boolean value) {
        jsonType = JsonType.Boolean;
        object = value;
    }

    public JsonObject(int value) {
        jsonType = JsonType.Number;
        object = value;
    }

    public JsonObject(String value) {
        jsonType = JsonType.String;
        object = value;
    }

    public JsonObject(List<JsonObject> list) {
        jsonType = JsonType.Array;
        object = list;
    }

    public JsonObject(Map<String, JsonObject> kvMap) {
        jsonType = JsonType.Object;
        object = kvMap;
    }

    public JsonObject(JsonType jsonType, Object object) {
        this.jsonType = jsonType;
        this.object = object;
    }

    public JsonObject(JsonObject that) {
        this.jsonType = that.jsonType;

        switch (this.jsonType) {
            case Null:
                this.object = null;
                break;
            case Boolean:
                this.object = that.getBool();
                break;
            case Number:
                this.object = that.getInt();
                break;
            case String:
                this.object = that.getString();
                break;
            case Array:
                this.object = new ArrayList<>(that.getList());
                break;
            case Object:
                this.object = new LinkedHashMap<>(that.getObject());
                break;
            default:
                throw new JsonIOException("Parse Error - Undefined JSON type.");
        }
    }

    public static JsonObject create(String input) {
        try {
            Index index = new Index(0, input.length());

            switch (readType(input, index)) {
                case Null:
                    return new JsonObject(JsonType.Null, nullParser(input, index));
                case Boolean:
                    return new JsonObject(JsonType.Boolean, booleanParser(input, index));
                case Number:
                    return new JsonObject(JsonType.Number, numberParser(input, index));
                case String:
                    return new JsonObject(JsonType.String, stringParser(input, index));
                case Array:
                    return new JsonObject(JsonType.Array, listParser(input, index));
                case Object:
                    return new JsonObject(JsonType.Object, objectParser(input, index));
                default:
                    throw new JsonParseException("Parse Error - Undefined JSON type.");
            }
        } catch (JsonParseException e) {
            throw new JsonIOException(e.getMessage());
        }
    }

    private static JsonType readType(String input, Index index) throws JsonParseException {
        while (input.charAt(index.intValue()) == ' ' ||
                input.charAt(index.intValue()) == '\r' ||
                input.charAt(index.intValue()) == '\n' ||
                input.charAt(index.intValue()) == '\t') index.increment();

        switch (input.charAt(index.intValue())) {
            case '"':
                return JsonType.String;
            case 't':
                return JsonType.Boolean;
            case 'f':
                return JsonType.Boolean;
            case '[':
                return JsonType.Array;
            case '{':
                return JsonType.Object;
            case 'n':
                return JsonType.Null;
            case '-':
                return JsonType.Number;
            default:
                if (input.charAt(index.intValue()) >= '0' && input.charAt(index.intValue()) <= '9') {
                    return JsonType.Number;
                } else {
                    throw new JsonParseException(String.format("Parse Error - Read type error at position %d, source %s.", index.intValue(), input));
                }
        }
    }

    @Override
    public String toString() {
        return toStringBuilder().toString();
    }

    public StringBuilder toStringBuilder() {
        StringBuilder sb = new StringBuilder();
        appendWith(sb);
        return sb;
    }

    public JsonType getJsonType() {
        return jsonType;
    }

    public boolean getBool() {
        if (jsonType != JsonType.Boolean) {
            throw new ClassCastException();
        }

        return (boolean) object;
    }

    public int getInt() {
        if (jsonType != JsonType.Number) {
            throw new ClassCastException();
        }

        return (int) object;
    }

    public String getString() {
        if (jsonType != JsonType.String) {
            throw new ClassCastException();
        }

        return (String) object;
    }


    @SuppressWarnings("unchecked")
    public List<JsonObject> getList() {
        if (jsonType != JsonType.Array) {
            throw new ClassCastException();
        }

        return (List<JsonObject>) object;
    }

    public boolean containsKey(String key) {
        if (jsonType != JsonType.Object) {
            throw new ClassCastException();
        }

        return getObject().containsKey(key);
    }

    @SuppressWarnings("unchecked")
    public Map<String, JsonObject> getObject() {
        if (jsonType != JsonType.Object) {
            throw new ClassCastException();
        }

        return (Map<String, JsonObject>) object;
    }

    public JsonObject get(int index) {
        return getList().get(index);
    }

    public JsonObject get(String index) {
        return getObject().get(index);
    }

    public void add(JsonObject value) {
        getList().add(value);
    }

    public void put(String key, JsonObject value) {
        getObject().put(key, value);
    }

    private static Object nullParser(String input, Index index) throws JsonParseException {
        int startIndex = index.intValue();
        index.plus("null".length());
        if (input.charAt(startIndex) != 'n') throw new JsonParseException(
                String.format("Parse Error - Invalid null node at position %d, source %s.", startIndex, input));
        if (input.charAt(startIndex + 1) != 'u') throw new JsonParseException(
                String.format("Parse Error - Invalid null node at position %d, source %s.", startIndex + 1, input));
        if (input.charAt(startIndex + 2) != 'l') throw new JsonParseException(
                String.format("Parse Error - Invalid null node at position %d, source %s.", startIndex + 2, input));
        if (input.charAt(startIndex + 3) != 'l') throw new JsonParseException(
                String.format("Parse Error - Invalid null node at position %d, source %s.", startIndex + 3, input));
        return null;
    }

    // Floating Point not supported
    private static int numberParser(String input, Index index) throws JsonParseException {
        boolean neg = input.charAt(index.intValue()) == '-';
        if (neg) { index.increment(); }
        if (input.charAt(index.intValue()) > '9'
                || input.charAt(index.intValue()) < '0') { throw new JsonParseException(
                String.format("Parse Error - Invalid number node at position %d, source %s.", index.intValue(), input)); }
        int ret = 0;
        while (input.charAt(index.intValue()) <= '9' && input.charAt(index.intValue()) >= '0') {
            ret *= 10;
            ret += input.charAt(index.intValue()) - '0';
            index.increment();
        }
        if (neg) { ret = -ret; }
        return ret;
    }

    private static boolean booleanParser(String input, Index index) throws JsonParseException {
        boolean ret = input.charAt(index.intValue()) == 't';
        int startIndex = index.intValue();
        if (ret) {
            index.plus("true".length());
            if (input.charAt(startIndex) != 't') throw new JsonParseException(
                    String.format("Parse Error - Invalid boolean node at position %d, source %s.", startIndex, input));
            if (input.charAt(startIndex + 1) != 'r') throw new JsonParseException(
                    String.format("Parse Error - Invalid boolean node at position %d, source %s.", startIndex + 1, input));
            if (input.charAt(startIndex + 2) != 'u') throw new JsonParseException(
                    String.format("Parse Error - Invalid boolean node at position %d, source %s.", startIndex + 2, input));
            if (input.charAt(startIndex + 3) != 'e') throw new JsonParseException(
                    String.format("Parse Error - Invalid boolean node at position %d, source %s.", startIndex + 3, input));
        } else {
            index.plus("false".length());
            if (input.charAt(startIndex) != 'f') throw new JsonParseException(
                    String.format("Parse Error - Invalid boolean node at position %d, source %s.", startIndex, input));
            if (input.charAt(startIndex + 1) != 'a') throw new JsonParseException(
                    String.format("Parse Error - Invalid boolean node at position %d, source %s.", startIndex + 1, input));
            if (input.charAt(startIndex + 2) != 'l') throw new JsonParseException(
                    String.format("Parse Error - Invalid boolean node at position %d, source %s.", startIndex + 2, input));
            if (input.charAt(startIndex + 3) != 's') throw new JsonParseException(
                    String.format("Parse Error - Invalid boolean node at position %d, source %s.", startIndex + 3, input));
            if (input.charAt(startIndex + 4) != 'e') throw new JsonParseException(
                    String.format("Parse Error - Invalid boolean node at position %d, source %s.", startIndex + 4, input));
        }
        return ret;
    }

    private static char hexCharToUChar(char input) throws JsonParseException {
        if (input >= 'A' && input <= 'F') {
            return (char) (input - 'A' + 10);
        } else if (input >= 'a' && input <= 'f') {
            return (char) (input - 'a' + 10);
        } else if (input >= '0' && input <= '9') {
            return (char) (input - '0');
        } else {
            throw new JsonParseException();
        }
    }

    private static char readUnicode(String input, int startIndex) throws JsonParseException {
        try {
            char h1 = hexCharToUChar(input.charAt(startIndex));
            char h2 = hexCharToUChar(input.charAt(startIndex + 1));
            char h3 = hexCharToUChar(input.charAt(startIndex + 2));
            char h4 = hexCharToUChar(input.charAt(startIndex + 3));
            return (char) ((h1 << 12) + (h2 << 8) + (h3 << 4) + h4);
        } catch (JsonParseException e) {
            throw new JsonParseException(
                    String.format("Parse Error - Invalid Unicode escaped character at position %d, source %s.", startIndex, input));
        }
    }

    private static String stringParser(String input, Index index) throws JsonParseException {
        StringBuilder sb = new StringBuilder();
        index.increment();
        while (input.charAt(index.intValue()) != '"') {
            if (input.charAt(index.intValue()) == '\\') {
                index.increment();
                switch (input.charAt(index.intValue())) {
                    case '"':
                        sb.append('"');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    // Front Slash Rules
                    // - Front slash will NOT be escaped in serialization.
                    // - However, an escaped front slash is acceptable '\/' in deserialization.
                    // - The behavior is the same as most third-party JSON libs like Jackson and Gson.
                    case '/':
                        sb.append('/');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'n':
                        sb.append('\f');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'u': {
                        int startIndex = index.intValue() + 1;
                        index.plus(4);
                        sb.append(readUnicode(input, startIndex));
                        break;
                    }
                    default:
                        throw new JsonParseException(
                                String.format("Parse Error - Invalid escaped character at position %d, source %s.", index.intValue(), input));
                }
            } else {
                sb.append(input.charAt(index.intValue()));
            }
            index.increment();
        }
        index.increment();
        return sb.toString();
    }

    private static List<JsonObject> listParser(String input, Index index) throws JsonParseException {
        List<JsonObject> list = new ArrayList<>();

        index.increment();

        while (input.charAt(index.intValue()) != ']') {
            switch (input.charAt(index.intValue())) {
                case ' ':
                    index.increment();
                    break;
                case '\r':
                    index.increment();
                    break;
                case '\n':
                    index.increment();
                    break;
                case '\t':
                    index.increment();
                    break;
                case ',':
                    index.increment();
                    break;
                case '[':
                    list.add(new JsonObject(JsonType.Array, listParser(input, index)));
                    break;
                case '{':
                    list.add(new JsonObject(JsonType.Object, objectParser(input, index)));
                    break;
                case 't':
                    list.add(new JsonObject(JsonType.Boolean, booleanParser(input, index)));
                    break;
                case 'f':
                    list.add(new JsonObject(JsonType.Boolean, booleanParser(input, index)));
                    break;
                case '"':
                    list.add(new JsonObject(JsonType.String, stringParser(input, index)));
                    break;
                case 'n':
                    list.add(new JsonObject(JsonType.Null, nullParser(input, index)));
                    break;
                case '-':
                    list.add(new JsonObject(JsonType.Number, numberParser(input, index)));
                    break;
                default:
                    list.add(new JsonObject(JsonType.Number, numberParser(input, index)));
                    break;
            }
        }

        index.increment();

        return list;
    }

    private static Map<String, JsonObject> objectParser(String input, Index index) throws JsonParseException {
        Map<String, JsonObject> map = new LinkedHashMap<>();

        index.increment();

        int flag = 0;
        String column = "";
        while (input.charAt(index.intValue()) != '}') {
            if (flag == 1) {
                switch (input.charAt(index.intValue())) {
                    case ' ':
                        index.increment();
                        break;
                    case '\r':
                        index.increment();
                        break;
                    case '\n':
                        index.increment();
                        break;
                    case '\t':
                        index.increment();
                        break;
                    case ',':
                        index.increment();
                        break;
                    case ':':
                        index.increment();
                        break;
                    case '[':
                        map.put(column, new JsonObject(JsonType.Array, listParser(input, index)));
                        flag = 0; column = "";
                        break;
                    case '{':
                        map.put(column, new JsonObject(JsonType.Object, objectParser(input, index)));
                        flag = 0; column = "";
                        break;
                    case 't':
                        map.put(column, new JsonObject(JsonType.Boolean, booleanParser(input, index)));
                        flag = 0; column = "";
                        break;
                    case 'f':
                        map.put(column, new JsonObject(JsonType.Boolean, booleanParser(input, index)));
                        flag = 0; column = "";
                        break;
                    case '"':
                        map.put(column, new JsonObject(JsonType.String, stringParser(input, index)));
                        flag = 0; column = "";
                        break;
                    case 'n':
                        map.put(column, new JsonObject(JsonType.Null, nullParser(input, index)));
                        flag = 0; column = "";
                        break;
                    case '-':
                        map.put(column, new JsonObject(JsonType.Number, numberParser(input, index)));
                        flag = 0; column = "";
                        break;
                    default:
                        map.put(column, new JsonObject(JsonType.Number, numberParser(input, index)));
                        flag = 0; column = "";
                        break;
                }
            }
            else {
                if (input.charAt(index.intValue()) == '"') {
                    column = stringParser(input, index);
                    flag = 1;
                }
                else {
                    index.increment();
                }
            }
        }

        index.increment();

        return map;
    }

    private void appendWith(StringBuilder sb) {
        switch (jsonType) {
            case Null:
                sb.append("null");
                break;
            case Boolean:
                sb.append(getBool());
                break;
            case Number:
                sb.append(getInt());
                break;
            case String:
                sb.append('"');
                appendStringWithEscape(sb, getString());
                sb.append('"');
                break;
            case Array:
                sb.append('[');
                getList().forEach(e -> { e.appendWith(sb); sb.append(','); });
                if (getList().size() > 0) sb.setLength(sb.length() - 1);
                sb.append(']');
                break;
            case Object:
                sb.append('{');
                getObject().forEach((key, value) -> { sb.append('"'); appendStringWithEscape(sb, key); sb.append('"').append(':'); value.appendWith(sb); sb.append(','); });
                if (getObject().size() > 0) sb.setLength(sb.length() - 1);
                sb.append('}');
                break;
        }
    }

    private static void appendStringWithEscape(StringBuilder sb, String input) {
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
    }
}
