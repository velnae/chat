package Client.Models;

import java.time.LocalDateTime;

public class Message {
    public static final String TYPE_TEXT = "message:type:text";
    public static final String TYPE_OBJECT = "message:type:object";
    public static final String TYPE_FILE = "message:type:file";
    private String type;
    private String message;
    private final String date;

    public Message(String message) {
        this.message = message;
        this.date = String.valueOf(LocalDateTime.now());
        this.type = TYPE_TEXT;
    }

    public Message(String message, String type) {
        this.message = message;
        this.date = String.valueOf(LocalDateTime.now());
        this.type = type;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

}
