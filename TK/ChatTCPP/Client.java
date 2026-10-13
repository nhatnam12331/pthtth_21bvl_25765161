import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket(Constants.HOST, Constants.PORT);
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            System.out.print("Nhập tên của bạn: ");
            String name = keyboard.readLine();
            out.println(name);

            // Tạo luồng phụ để chuyên nhận tin nhắn từ server đẩy về
            Thread receiveThread = new Thread(() -> {
                try {
                    String serverMsg;
                    while ((serverMsg = in.readLine()) != null) {
                        if (serverMsg.startsWith("DANH_SACH_PHONG:")) {
                            String[] rooms = serverMsg.replace("DANH_SACH_PHONG:", "").split(",");
                            System.out.println("\n--- DANH SÁCH PHÒNG CHAT ---");
                            for (String r : rooms) {
                                System.out.println("- " + r);
                            }
                            System.out.print("Nhập tên phòng muốn vào: ");
                        } else {
                            System.out.println("\n" + serverMsg);
                            System.out.print("Nhập tin nhắn: ");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("[!] Mất kết nối tới Server.");
                }
            });
            receiveThread.start();

            // Luồng chính dùng để gửi tin nhắn hoặc lệnh từ bàn phím
            String userInput;
            while ((userInput = keyboard.readLine()) != null) {
                out.println(userInput);
                if (userInput.equals(Constants.CMD_EXIT)) {
                    break;
                }
            }

            socket.close();
            System.exit(0);

        } catch (Exception e) {
            System.out.println("[!] Không thể kết nối tới Server!");
        }
    }
}
