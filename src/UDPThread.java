import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class UDPThread extends Thread {
    private int countSockets;
    private int lastSending;
    private List<byte[]> receivingDataBuffer;
    private List<byte[]> sendingDataBuffer;
    private List<DatagramPacket> input;
    private List<DatagramPacket> output;
    private DatagramSocket serverSocket;
    private int port = 8080;

    public UDPThread() throws SocketException {
        serverSocket = new DatagramSocket(port);
        countSockets = 0;
    }

    public void run() {
        receivingDataBuffer = new ArrayList<>();
        input = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            receivingDataBuffer.add(new byte[1024]);
            input.add(new DatagramPacket(receivingDataBuffer.get(i), receivingDataBuffer.get(i).length));
            try {
                serverSocket.receive(input.get(i));
                countSockets++;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendMessages(double min, double max, double step) throws IOException {
        lastSending = 0;
        sendingDataBuffer = new ArrayList<>();
        int lenModStep = (int) ((int) (max - Math.abs(min)) % step);
        int stepsCount = (int) ((int) (max - min) / step);
        int threadCount = (lenModStep == 0 ? Math.min(stepsCount, 3) : stepsCount + 1 > 3 ? 3 : stepsCount);
        if (threadCount == 3) {
            int steps = stepsCount / 3;
            sendingDataBuffer.add((min + " " + (min + steps * step) + " " + step).getBytes());
            sendingDataBuffer.add(((min + steps * step) + " " + (min + steps * 2 * step) + " " + step).getBytes());
            sendingDataBuffer.add(((min + steps * 2 * step) + " " + max + " " + step).getBytes());
            for (int i = 0; i < 3; i++) {
                InetAddress senderAddress = input.get(i).getAddress();
                int senderPort = input.get(i).getPort();
                DatagramPacket outputPacket = new DatagramPacket(
                        sendingDataBuffer.get(i), sendingDataBuffer.get(i).length,
                        senderAddress, senderPort);
                serverSocket.send(outputPacket);
            }
            lastSending = 3;
        } else if (threadCount == 2) {
            int steps = stepsCount / 2;
            sendingDataBuffer.add((min + " " + steps * step + " " + step).getBytes());
            sendingDataBuffer.add((min + steps * step + " " + max + " " + step).getBytes());
            for (int i = 0; i < 2; i++) {
                InetAddress senderAddress = input.get(i).getAddress();
                int senderPort = input.get(i).getPort();
                DatagramPacket outputPacket = new DatagramPacket(
                        sendingDataBuffer.get(i), sendingDataBuffer.get(i).length,
                        senderAddress, senderPort);
                serverSocket.send(outputPacket);
            }
            lastSending = 2;
        } else {
            sendingDataBuffer.add((min + " " + max + " " + step).getBytes());
            InetAddress senderAddress = input.get(0).getAddress();
            int senderPort = input.get(0).getPort();
            DatagramPacket outputPacket = new DatagramPacket(
                    sendingDataBuffer.get(0), sendingDataBuffer.get(0).length,
                    senderAddress, senderPort);
            serverSocket.send(outputPacket);
            lastSending = 1;
        }
    }

    public Double getResults() throws IOException {
        double result = 0.0;
        for (int i = 0; i< lastSending; i++){
            input.get(i).setData(new byte[100]);
            serverSocket.receive(input.get(i));
            result += Double.parseDouble(new String(input.get(i).getData()));
        }
        return result;
    }

    public void sendEnd() throws IOException {
        for (DatagramPacket dp:input) {
            InetAddress senderAddress = dp.getAddress();
            int senderPort = dp.getPort();
            byte[] end = "0 0 0 0".getBytes();
            DatagramPacket outputPacket = new DatagramPacket(
                    end, end.length,
                    senderAddress, senderPort);
            serverSocket.send(outputPacket);

        }
    }

    public int getCountSockets() {
        return countSockets;
    }

    public void setCountSockets(int countSockets) {
        this.countSockets = countSockets;
    }
}
