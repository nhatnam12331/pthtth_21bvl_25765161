import java.util.Scanner;
public class Baitap3PTHTTH {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nhập vào số a:");
        int a = scanner.nextInt();
        System.out.print("Nhập vào số b:");
        int b = scanner.nextInt();
        int tong = a+b;
        System.out.println("Tổng hai số là:" +tong);
        scanner.close();
    }
}