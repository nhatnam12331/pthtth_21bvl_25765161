import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class TextFileDemo {

    public static void main(String[] args) {
        Path filePath = Paths.get("data/ghi_chu.txt");

        System.out.println("=== 1. THỰC HIỆN GHI NỐI TIẾP (APPEND) ===");
        appendData(filePath, "Dòng mới 1: Thử nghiệm ghi nối tiếp vào tệp.");
        appendData(filePath, "Dòng mới 2: Tiếng Việt có dấu - Lập trình Java I/O.");

        System.out.println("\n=== 2. IN ĐƯỜNG DẪN TUYỆT ĐỐI ===");
        System.out.println("Đường dẫn tuyệt đối của tệp: " + filePath.toAbsolutePath());

        System.out.println("\n=== 3. ĐỌC TỆP BẰNG CÁC CHARSET KHÁC NHAU ===");
        
        System.out.println("\n--- Đọc bằng UTF-8 (Đúng chuẩn) ---");
        readFileWithCharset(filePath, StandardCharsets.UTF_8);

        System.out.println("\n--- Đọc bằng ISO-8859-1 (Sai chuẩn) ---");
        readFileWithCharset(filePath, StandardCharsets.ISO_8859_1);

        System.out.println("\n--- Đọc bằng US-ASCII (Sai chuẩn) ---");
        readFileWithCharset(filePath, StandardCharsets.US_ASCII);
    }

    private static void appendData(Path path, String content) {
        try (BufferedWriter writer = Files.newBufferedWriter(
                path,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            writer.write(content);
            writer.newLine();
            System.out.println("Đã ghi thêm thành công: \"" + content + "\"");

        } catch (IOException e) {
            System.err.println("Lỗi khi ghi tệp: " + e.getMessage());
        }
    }

    private static void readFileWithCharset(Path path, java.nio.charset.Charset charset) {
        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                System.out.printf("[%s - Dòng %d]: %s%n", charset.name(), lineNumber++, line);
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc tệp với " + charset.name() + ": " + e.getMessage());
        }
    }
}