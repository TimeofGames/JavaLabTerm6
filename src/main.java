import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class main {
    private static final int port = 8080;

    public static void main(String[] args) throws IOException {
        try{
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName("localhost");
            byte[] sendingDataBuffer = new byte[1024];
            byte[] receivingDataBuffer = new byte[1024];

            String sentence = "Hello from UDP client";
            sendingDataBuffer = sentence.getBytes();
            DatagramPacket sendingPacket = new DatagramPacket(sendingDataBuffer,sendingDataBuffer.length,IPAddress, port);
            clientSocket.send(sendingPacket);

            DatagramPacket receivingPacket = new DatagramPacket(receivingDataBuffer,receivingDataBuffer.length);
            clientSocket.receive(receivingPacket);

            while (true) {
                Double[] request = Arrays.stream(new String(receivingPacket.getData()).split(" "))
                        .map(Double::parseDouble)
                        .toArray(Double[]::new);
                for (Double d:request) {
                    System.out.println(d);
                }

                double min = request[0];
                double max = request[1];
                double step = request[2];

                if(min == 0 && max == 0 && step == 0){break;}

                int lenModStep = (int) ((int) (max - Math.abs(min)) % step);
                int stepsCount = (int) ((int) (max - Math.abs(min)) / step);
                int threadCount = (lenModStep == 0 ? Math.min(stepsCount, 3) : stepsCount + 1 > 3 ? 3 : stepsCount);


                ResultResource rs = new ResultResource();


                List<CalculationThread> threads = new ArrayList<>();
                if (threadCount == 3) {
                    int steps = stepsCount / 3;
                    threads.add(new CalculationThread(min, min + steps * step, step, rs));
                    threads.add(
                            new CalculationThread(min + steps * step, min + steps * 2 * step, step,
                                    rs));
                    threads.add(new CalculationThread(min + steps * 2 * step, max, step, rs));
                } else if (threadCount == 2) {
                    int steps = stepsCount / 2;
                    threads.add(new CalculationThread(min, min + steps * step, step, rs));
                    threads.add(new CalculationThread(min + steps * step, max, step, rs));
                } else {
                    threads.add(new CalculationThread(min, max, step, rs));
                }

                threads.forEach(CalculationThread::start);

                while (rs.getChangeCount() != threadCount) {
                    Thread.sleep(100);
                }

                sendingDataBuffer = String.valueOf(rs.getResult())
                        .getBytes();
                sendingPacket = new DatagramPacket(sendingDataBuffer, sendingDataBuffer.length, IPAddress, port);
                clientSocket.send(sendingPacket);
                System.out.println("sended");
                clientSocket.receive(receivingPacket);
            }
            clientSocket.close();
        }
        catch(SocketException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
