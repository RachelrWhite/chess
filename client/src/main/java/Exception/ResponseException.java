//package Exception;
//
//import com.google.gson.Gson;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Indicates there was an error connecting to the database
// */
//public class ResponseException extends Exception{
//    public enum Code {
//        ServerError,
//        ClientError,
//    }
//
//    final private Exception.ResponseException.Code code;
//
//    public ResponseException(Exception.ResponseException.Code code, String message) {
//        super(message);
//        this.code = code;
//    }
//
//    public String toJson() {
//        return new Gson().toJson(Map.of("message", getMessage(), "status", code));
//    }
//
//    public static ResponseException fromJson(String json) {
//        var map = new Gson().fromJson(json, HashMap.class);
//        var status = Exception.ResponseException.Code.valueOf(map.get("status").toString());
//        String message = map.get("message").toString();
//        return new ResponseException(status, message);
//    }
//
//    public Exception.ResponseException.Code code() {
//        return code;
//    }
//
//    public static Exception.ResponseException.Code fromHttpStatusCode(int httpStatusCode) {
//        return switch (httpStatusCode) {
//            case 500 -> Exception.ResponseException.Code.ServerError;
//            case 400 -> Exception.ResponseException.Code.ClientError;
//            default -> throw new IllegalArgumentException("Unknown HTTP status code: " + httpStatusCode);
//        };
//    }
//
//    public int toHttpStatusCode() {
//        return switch (code) {
//            case ServerError -> 500;
//            case ClientError -> 400;
//        };
//    }
//}
//
//

