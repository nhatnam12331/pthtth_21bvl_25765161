import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static ChatRoomManager roomManager = new ChatRoomManager();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(Constants.PORT)) {
            System.out.println("[SERVER] Đang chạy tại cổng " + Constants.PORT + "...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("[+] Có client kết nối mới: " + socket.getInetAddress());
                // Mỗi client là một luồng riêng biệt
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            System.out.println("[-] Lỗi Server: " + e.getMessage());
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String clientName;
        private String currentRoom;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // 1. Nhận tên client
                clientName = in.readLine();
                if (clientName == null) return;
                System.out.println("[*] Client đăng ký tên: " + clientName);

                // 2. Vòng lặp chọn phòng
                while (true) {
                    out.println("DANH_SACH_PHONG:" + roomManager.getRoomListString());
                    currentRoom = in.readLine();
                    if (currentRoom == null) return;
                    currentRoom = currentRoom.trim();

                    if (roomManager.roomExists(currentRoom)) {
                        roomManager.addClient(currentRoom, out, clientName);
                        break;
                    } else {
                        out.println("SYSTEM: Phòng không tồn tại. Vui lòng chọn lại!");
                    }
                }

                out.println("SYSTEM: Bạn đã vào phòng " + currentRoom + ". Gõ " + Constants.CMD_QUIT + " để đổi phòng, " + Constants.CMD_EXIT + " để thoát.");
                roomManager.broadcast(currentRoom, "SYSTEM: ==> " + clientName + " đã vào phòng.", out);

                // 3. Vòng lặp nhận tin nhắn chat
                String message;
                while ((message = in.readLine()) != null) {
                    message = message.trim();
                    if (message.equals(Constants.CMD_EXIT)) {
                        break;
                    } else if (message.equals(Constants.CMD_QUIT)) {
                        // Rời phòng cũ
                        roomManager.broadcast(currentRoom, "SYSTEM: <== " + clientName + " đã rời phòng.", out);
                        roomManager.removeClient(currentRoom, out, clientName);
                        
                        // Cho chọn lại phòng ngay
                        while (true) {
                            out.println("DANH_SACH_PHONG:" + roomManager.getRoomListString());
                            String chosen = in.readLine();
                            if (chosen != null && roomManager.roomExists(chosen.trim())) {
                                currentRoom = chosen.trim();
                                roomManager.addClient(currentRoom, out, clientName);
                                break;
                            } else {
                                out.println("SYSTEM: Phòng không hợp lệ!");
                            }
                        }
                        out.println("SYSTEM: Bạn đã vào phòng " + currentRoom + ".");
                        roomManager.broadcast(currentRoom, "SYSTEM: ==> " + clientName + " đã vào phòng.", out);
                    } else {
                        // Chat bình thường
                        roomManager.broadcast(currentRoom, clientName + ": " + message, out);
                    }
                }

            } catch (IOException e) {
                System.out.println("[-] Lỗi kết nối với " + clientName);
            } finally {
                // Dọn dẹp khi ngắt kết nối
                if (currentRoom != null && clientName != null) {
                    roomManager.removeClient(currentRoom, out, clientName);
                    roomManager.broadcast(currentRoom, "SYSTEM: <== " + clientName + " đã ngắt kết nối.", out);
                }
                try {
                    socket.close();
                } catch (IOException e) {}
                System.out.println("[-] Đã đóng kết nối của: " + clientName);
            }
        }
    }
}