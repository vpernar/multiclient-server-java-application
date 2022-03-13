package pkg;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class ChatMessage {
    private String username;
    private String message;
    private LocalDateTime time;
    private static List<String> censored = Arrays.asList("string");
    private static DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    ;

    public ChatMessage(String username, String message) {
        this.username = username;
        this.message = message;
        this.time = LocalDateTime.now();
        censorWord();
    }

    private void censorWord() {
        String censoredMessage = "";
        String[] words = message.split(" ");
        for (String word : words) {
            if (censored.contains(word)) {
                word = "s****g";
            }
            censoredMessage += word + " ";
        }
        message = censoredMessage;
    }

    @Override
    public String toString() {
        return "[" + time.format(format) + "] " + username + " :" + message;
    }
}
