import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DiaryManager {
    private final File file;
    private final ObjectMapper mapper;
    private List<DiaryEntry> entries;

    public DiaryManager(String filePath) {
        this.file = new File(filePath);
        this.mapper = new ObjectMapper();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
        this.mapper.registerModule(module);

        this.mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        loadEntries();
    }

    private void loadEntries() {
        try {
            if (!file.exists()) {
                entries = new ArrayList<>();
                saveEntries();
                return;
            }
            entries = mapper.readValue(file, new TypeReference<>() {});
        } catch (IOException e) {
            entries = new ArrayList<>();
        }
    }

    private void saveEntries() throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, entries);
    }

    public void addEntry(String content) throws IOException {
        int newId = entries.isEmpty() ? 1 : entries.stream().mapToInt(DiaryEntry::getId).max().getAsInt() + 1;
        DiaryEntry entry = new DiaryEntry(newId, content);
        entries.add(entry);
        saveEntries();
    }

    public List<DiaryEntry> getEntries() {
        return entries.stream()
                .sorted(Comparator.comparing(DiaryEntry::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public List<DiaryEntry> searchEntries(String keyword) {
        String lower = keyword.toLowerCase();
        return entries.stream()
                .filter(e -> e.getContent().toLowerCase().contains(lower))
                .sorted(Comparator.comparing(DiaryEntry::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public boolean deleteEntry(int id) throws IOException {
        boolean removed = entries.removeIf(e -> e.getId() == id);
        if (removed) saveEntries();
        return removed;
    }

    public void editEntry(int id, String newText) throws IOException {
        for (DiaryEntry e : entries) {
            if (e.getId() == id) {
                e.setContent(newText);
                saveEntries();
                return;
            }
        }
    }

    public DiaryEntry getEntryById(int id) {
        return entries.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }
}
