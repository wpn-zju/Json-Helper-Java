public class JsonCastException extends RuntimeException {
    public JsonCastException(JsonType casted, JsonType actual) {
        super(String.format("Invalid JSON type cast, expected %s, actual %s.", casted, actual));
    }
}
