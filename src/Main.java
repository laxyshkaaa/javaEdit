import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

class CodeProcessor {

    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);

        System.out.print("Введите путь к файлу: ");
        String folderPath = input.nextLine();

        System.out.print("Введите старое название класса: ");
        String oldName = input.nextLine();

        System.out.print("Введите новое название класса: ");
        String newName = input.nextLine();

        String javaFile = oldName + ".java";
        Path pathToFile = Paths.get(folderPath, javaFile);

        if (!Files.exists(pathToFile)) {
            System.out.println("Файл отсутствует: " + pathToFile);
            return;
        }

        String fileContent = new String(Files.readAllBytes(pathToFile));

        fileContent = removeCommentsAndSpaces(fileContent);
        fileContent = renameClassOnly(fileContent, oldName, newName);
        fileContent = shortenIdentifiers(fileContent);

        Path newFilePath = Paths.get(folderPath, newName + ".java");
        Files.write(newFilePath, fileContent.getBytes());

        System.out.println("Готовый файл: " + newFilePath);
    }

    private static String removeCommentsAndSpaces(String code) {
        code = code.replaceAll("//.*", "");
        code = code.replaceAll("/\\*.*?\\*/", "");

        code = code.replaceAll("\\s*([{};])\\s*", "$1\n");
        code = code.replaceAll("\\s+", " ");
        code = code.replaceAll("(?<=\\{)\\s*(?=\\S)", "\n    ");
        code = code.replaceAll("(?<=\\})\\s*(?=\\S)", "\n");

        return code;
    }

    private static String renameClassOnly(String code, String oldClass, String newClass) {
        code = code.replaceAll("\\b" + oldClass + "\\b", newClass);
        return code;
    }


    private static String shortenIdentifiers(String code) {
        Set<String> foundIdentifiers = collectIdentifiers(code);

        Map<String, String> identifierMap = new HashMap<>();
        char currentChar = 'a'; // Начинаем с буквы 'a'
        int currentNum = 0; // Счетчик для имен в формате v1, v2 и т.д.

        for (String identifier : foundIdentifiers) {
            String shortName;
            if (currentChar <= 'z') {
                shortName = String.valueOf(currentChar++);
            } else {
                shortName = "v" + currentNum++;
            }
            identifierMap.put(identifier, shortName);
        }

        for (Map.Entry<String, String> entry : identifierMap.entrySet()) {
            if (!entry.getKey().equals("main")) { // Не заменяем метод main
                code = code.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
            }
        }

        return code;
    }

    private static Set<String> collectIdentifiers(String code) {
        Set<String> identifiers = new HashSet<>();

        // Шаблоны для нахождения переменных, методов и классов
        Pattern variablePattern = Pattern.compile("\\b(int|double|float|String|boolean|char|long|short|byte)\\s+(\\w+)\\b");
        Pattern methodPattern = Pattern.compile("\\b(\\w+)\\s*\\(");
        Pattern classPattern = Pattern.compile("\\bclass\\s+(\\w+)\\b");

        // Находим переменные
        Matcher variableMatcher = variablePattern.matcher(code);
        while (variableMatcher.find()) {
            String identifier = variableMatcher.group(2);
            if (!isJavaKeyword(identifier)) {
                identifiers.add(identifier);
            }
        }

        // Находим методы
        Matcher methodMatcher = methodPattern.matcher(code);
        while (methodMatcher.find()) {
            String identifier = methodMatcher.group(1);
            if (!isJavaKeyword(identifier)) {
                identifiers.add(identifier);
            }
        }

        // Находим классы
        Matcher classMatcher = classPattern.matcher(code);
        while (classMatcher.find()) {
            String identifier = classMatcher.group(1);
            if (!isJavaKeyword(identifier)) {
                identifiers.add(identifier);
            }
        }

        return identifiers;
    }

    private static boolean isJavaKeyword(String word) {
        // Проверяем, является ли слово ключевым словом Java
        String[] keywords = {
                "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
                "class", "const", "continue", "default", "do", "double", "else", "enum",
                "extends", "final", "finally", "float", "for", "goto", "if", "implements",
                "import", "instanceof", "int", "interface", "long", "native", "new", "null",
                "package", "private", "protected", "public", "return", "short", "static",
                "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
                "transient", "try", "void", "volatile", "while"
        };
        return Arrays.asList(keywords).contains(word);
    }
}

