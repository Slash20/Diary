import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class DiaryApp {
    private static final String FILE_PATH = "diary.json";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DiaryManager diaryManager = new DiaryManager(FILE_PATH);

        System.out.println("Добро пожаловать в консольный дневник!");
        System.out.println("Введите 'help' для списка доступных команд.");

        while (true) {
            System.out.print("\n> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            String[] parts = input.split(" ", 2);
            String command = parts[0].toLowerCase();
            String argument = parts.length > 1 ? parts[1] : "";

            try {
                switch (command) {
                    case "new":
                        System.out.println("Введите текст новой записи (пустая строка — отмена):");
                        String content = scanner.nextLine().trim();
                        if (content.isEmpty()) {
                            System.out.println("Отменено.");
                            break;
                        }
                        diaryManager.addEntry(content);
                        System.out.println("Запись добавлена.");
                        break;

                    case "list":
                        List<DiaryEntry> entries = diaryManager.getEntries();
                        if (entries.isEmpty()) {
                            System.out.println("Нет записей.");
                        } else {
                            for (DiaryEntry entry : entries) {
                                System.out.println(entry);
                                System.out.println("---");
                            }
                        }
                        break;

                    case "search":
                        if (argument.isEmpty()) {
                            System.out.println("Укажите слово для поиска: search <слово>");
                            break;
                        }
                        List<DiaryEntry> found = diaryManager.searchEntries(argument);
                        if (found.isEmpty()) {
                            System.out.println("Совпадений не найдено.");
                        } else {
                            for (DiaryEntry entry : found) {
                                System.out.println(entry);
                                System.out.println("---");
                            }
                        }
                        break;

                    case "delete":
                        try {
                            int id = Integer.parseInt(argument);
                            if (diaryManager.deleteEntry(id)) {
                                System.out.println("Запись удалена.");
                            } else {
                                System.out.println("Запись с таким ID не найдена.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Используйте: delete <ID>");
                        }
                        break;

                    case "edit":
                        try {
                            int id = Integer.parseInt(argument);
                            DiaryEntry entry = diaryManager.getEntryById(id);
                            if (entry == null) {
                                System.out.println("Запись не найдена.");
                                break;
                            }
                            System.out.println("Текущий текст:\n" + entry.getContent());
                            System.out.println("Введите новый текст (пустая строка — отмена):");
                            String newText = scanner.nextLine().trim();
                            if (newText.isEmpty()) {
                                System.out.println("Отменено.");
                                break;
                            }
                            diaryManager.editEntry(id, newText);
                            System.out.println("Запись обновлена.");
                        } catch (NumberFormatException e) {
                            System.out.println("Используйте: edit <ID>");
                        }
                        break;

                    case "help":
                        printHelp();
                        break;

                    case "exit":
                        System.out.println("До свидания!");
                        return;

                    default:
                        System.out.println("Неизвестная команда. Введите 'help' для справки.");
                }
            } catch (IOException e) {
                System.out.println("Ошибка доступа к файлу: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private static void printHelp() {
        System.out.println("""
        Доступные команды:
          new               — создать новую запись
          list              — показать все записи
          search <слово>    — поиск записей по слову
          delete <ID>       — удалить запись по ID
          edit <ID>         — редактировать запись
          help              — показать это сообщение
          exit              — выйти из программы
        """);
    }
}
