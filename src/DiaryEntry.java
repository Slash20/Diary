import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DiaryEntry {
    private int id;
    private LocalDateTime timestamp;
    private String content;

    public DiaryEntry(int id, String content) {
        this.id = id;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public int getId() { return id; }
    public String getContent() { return content; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public void setContent(String content) { this.content = content; }

    @Override
    public String toString() {
        String date = timestamp.format(DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm"));
        return "[" + date + "]\n" + content;
    }
}
