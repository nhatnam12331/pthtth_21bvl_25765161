import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class InventoryManager {
    private static final Path CSV_PATH = Paths.get("data/inventory.csv");
    private static final Path REPORT_PATH = Paths.get("data/inventory-report.txt");

    public static void main(String[] args) {
        List<Product> inputList = inputFromConsole();

        if (!inputList.isEmpty()) {
            saveToCsv(CSV_PATH, inputList);
        }

        System.out.println("\n--- ĐỌC VÀ TÁI TẠO DỮ LIỆU TỪ TỆP CSV ---");
        List<Product> loadedList = readFromCsv(CSV_PATH);

        displayAndAnalyze(loadedList);

        if (!loadedList.isEmpty()) {
            writeReport(REPORT_PATH, loadedList);
        }
    }

    // Nhập dữ liệu từ bàn phím
    private static List<Product> inputFromConsole() {
        List<Product> list = new ArrayList<>();
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        System.out.println("=== NHẬP DANH SÁCH SẢN PHẨM (Nhập mã 'q' để hoàn tất) ===");

        int count = 1;
        while (true) {
            try {
                System.out.println("\n[Sản phẩm " + count + "]");
                System.out.print("Mã SP (q để dừng): ");
                String code = console.readLine();
                if (code == null || code.trim().equalsIgnoreCase("q")) break;

                System.out.print("Tên SP: ");
                String name = console.readLine();

                System.out.print("Đơn giá: ");
                double price = Double.parseDouble(console.readLine().trim());

                System.out.print("Số lượng: ");
                int quantity = Integer.parseInt(console.readLine().trim());

                list.add(new Product(code, name, price, quantity));
                System.out.println("-> Thêm thành công!");
                count++;
            } catch (IllegalArgumentException e) {
                System.err.println("Lỗi dữ liệu: " + e.getMessage() + " - Mời nhập lại sản phẩm này!");
            } catch (IOException e) {
                System.err.println("Lỗi đọc bàn phím: " + e.getMessage());
            }
        }
        return list;
    }

    // Lưu danh sách vào CSV
    public static void saveToCsv(Path path, List<Product> products) {
        try {
            if (path.getParent() != null) Files.createDirectories(path.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                for (Product p : products) {
                    writer.write(p.toCsvRow());
                    writer.newLine();
                }
                System.out.println("-> Đã ghi " + products.size() + " sản phẩm vào " + path.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Lỗi ghi tệp " + path + ": " + e.getMessage());
        }
    }

    // Đọc CSV và xử lý lỗi dòng
    public static List<Product> readFromCsv(Path path) {
        List<Product> products = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;
                try {
                    products.add(Product.fromCsvRow(line, lineNumber));
                } catch (IllegalArgumentException e) {
                    System.err.println("[CẢNH BÁO TỆP " + path.getFileName() + "]: " + e.getMessage());
                }
            }
        } catch (NoSuchFileException e) {
            System.err.println("[LỖI]: Không tìm thấy tệp " + path.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("[LỖI]: Lỗi khi đọc tệp " + path + ": " + e.getMessage());
        }
        return products;
    }

    // Hiển thị sản phẩm, tính tổng tồn kho & tìm SP cao nhất
    public static void displayAndAnalyze(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("Danh sách trống.");
            return;
        }
        System.out.println("\n=== BẢNG THỐNG KÊ TỒN KHO ===");
        double totalValue = 0;
        Product maxProduct = products.get(0);

        for (Product p : products) {
            System.out.println(p);
            totalValue += p.inventoryValue();
            if (p.inventoryValue() > maxProduct.inventoryValue()) {
                maxProduct = p;
            }
        }
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.printf("TỔNG GIÁ TRỊ TỒN KHO : %,.0f VNĐ%n", totalValue);
        System.out.printf("SẢN PHẨM TỒN CAO NHẤT: %s (%,.0f VNĐ)%n", maxProduct.getName(), maxProduct.inventoryValue());
    }

    // Ghi báo cáo tổng hợp vào file txt
    public static void writeReport(Path reportPath, List<Product> products) {
        double totalValue = 0;
        Product maxProduct = products.get(0);
        for (Product p : products) {
            totalValue += p.inventoryValue();
            if (p.inventoryValue() > maxProduct.inventoryValue()) maxProduct = p;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(reportPath, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write("===============================================\n");
            writer.write("           BÁO CÁO TỔNG HỢP TỒN KHO            \n");
            writer.write("===============================================\n");
            writer.write("Tổng số mặt hàng      : " + products.size() + "\n");
            writer.write(String.format("Tổng giá trị tồn kho   : %,.0f VNĐ\n", totalValue));
            writer.write(String.format("Mặt hàng tồn cao nhất  : %s (%,.0f VNĐ)\n", maxProduct.getName(), maxProduct.inventoryValue()));
            writer.write("===============================================\n");
            System.out.println("-> Đã tạo báo cáo tại: " + reportPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Lỗi ghi báo cáo: " + e.getMessage());
        }
    }
}