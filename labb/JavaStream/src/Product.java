public class Product {
    private String code;
    private String name;
    private double unitPrice;
    private int quantity;

    public Product() {}

    public Product(String code, String name, double unitPrice, int quantity) {
        setCode(code);
        setName(name);
        setUnitPrice(unitPrice);
        setQuantity(quantity);
    }

    public String getCode() { return code; }
    public final void setCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sản phẩm không được rỗng.");
        }
        this.code = code.trim();
    }

    public String getName() { return name; }
    public final void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được rỗng.");
        }
        this.name = name.trim();
    }

    public double getUnitPrice() { return unitPrice; }
    public final void setUnitPrice(double unitPrice) {
        if (unitPrice <= 0) {
            throw new IllegalArgumentException("Đơn giá phải lớn hơn 0.");
        }
        this.unitPrice = unitPrice;
    }

    public int getQuantity() { return quantity; }
    public final void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Số lượng không được âm.");
        }
        this.quantity = quantity;
    }

    public double inventoryValue() {
        return unitPrice * quantity;
    }

    public String toCsvRow() {
        return String.format("%s,%s,%.2f,%d", code, name, unitPrice, quantity);
    }

    public static Product fromCsvRow(String line, int lineNumber) {
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("Dòng " + lineNumber + " rỗng.");
        }
        String[] parts = line.split(",");
        if (parts.length < 4) {
            throw new IllegalArgumentException("Dòng " + lineNumber + " thiếu cột dữ liệu (cần 4 cột, tìm thấy " + parts.length + ").");
        }
        try {
            String code = parts[0].trim();
            String name = parts[1].trim();
            double price = Double.parseDouble(parts[2].trim());
            int qnty = Integer.parseInt(parts[3].trim());
            return new Product(code, name, price, qnty);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Dòng " + lineNumber + " chứa dữ liệu số không hợp lệ.");
        }
    }

    @Override
    public String toString() {
        return String.format("Mã: %-6s | Tên: %-18s | Đơn giá: %,10.0f VNĐ | SL: %-3d | Giá trị: %,12.0f VNĐ",
                code, name, unitPrice, quantity, inventoryValue());
    }
}