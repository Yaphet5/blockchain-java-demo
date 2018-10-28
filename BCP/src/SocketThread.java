import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Socket���̴߳����� �����������˽��յ��Ŀͻ������󣨴���Socket����
 * 
 * Created on 2018/10/20.
 *
 * @author DragonW 
 * @since 1.0
 */

public class SocketThread extends Thread {
    private Socket socket;

    public SocketThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        // ��������������Ϳͻ�������
        try {
            InputStream inputStream = socket.getInputStream();
            // �õ�һ�������������տͻ��˴��ݵ���Ϣ
            InputStreamReader inputStreamReader = new InputStreamReader(
                    inputStream);// ���Ч�ʣ����Լ��ֽ���תΪ�ַ���
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);// ���뻺����
            String temp = null;
            String info = "";
            while ((temp = bufferedReader.readLine()) != null) {
                info += temp;
                System.out.println("�ѽ��յ��ͻ�������");
                System.out.println("����˽��յ��ͻ�����Ϣ��" + info + ",��ǰ�ͻ���ipΪ��"
                        + socket.getInetAddress().getHostAddress());
            }

            OutputStream outputStream = socket.getOutputStream();// ��ȡһ��������������˷�����Ϣ
            PrintWriter printWriter = new PrintWriter(outputStream);// ���������װ�ɴ�ӡ��
            printWriter.print("��ã�������ѽ��յ�������Ϣ");
            printWriter.flush();
            socket.shutdownOutput();// �ر������

            // �ر����Ӧ����Դ
            bufferedReader.close();
            inputStream.close();
            printWriter.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}