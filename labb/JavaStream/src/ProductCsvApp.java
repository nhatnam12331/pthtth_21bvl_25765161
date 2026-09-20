import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProductCsvApp {
    public static void main(String[] args) {
        Path input = Path.of("JavaStream/data", "products.csv");
        Path report = Path.of("JavaStream/data", "report.txt");
        List<Product> products = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(
                input, StandardCharsets.UTF_8)) {
            reader.readLine(); // bỏ qua dòng tiêu đề
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank())
                    continue;
                String[] parts = line.split(",", -1);
                if (parts.length != 4) {
                    System.err.println("Bỏ qua dòng " + lineNumber);
                    continue;
                }
                try {
                    products.add(new Product(
                            parts[0].trim(), parts[1].trim(),
                            Double.parseDouble(parts[2].trim()),
                            Integer.parseInt(parts[3].trim())));
                } catch (IllegalArgumentException e) {
                    System.err.println("Dòng " + lineNumber
                            + " không hợp lệ: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Không đọc được CSV: " + e.getMessage());
            return;
        }
        double total = 0;
        for (Product product : products) {
            System.out.println(product);
            total += product.inventoryValue();
        }
        try (BufferedWriter writer = Files.newBufferedWriter(
                report, StandardCharsets.UTF_8)) {
            writer.write("Số sản phẩm: " + products.size());
            writer.newLine();
            writer.write("Tổng giá trị tồn kho: %,.0f VND".formatted(total));
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Không ghi được báo cáo: " + e.getMessage());
        }
    }
}