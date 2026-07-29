import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class Servidor {
    private static final int PORTA = 5000; //porta escolhida
    public static Map<String, Socket> socketCliente = new HashMap<>();  //tabela de roteamento
    private static Semaphore semaforoTabela = new Semaphore(1);

    public static void main(String[] args){

        //pra inicializar eles tem que ser dentro do try/catch por isso criamos a referencia nula
        Socket socket =null;
        ServerSocket serverSocket = null;
        
        try{
            serverSocket = new ServerSocket(PORTA);

            while (true) {
                // O servidor fica aguardando uma conexão
                socket = serverSocket.accept();
                System.out.println("Novo cliente conectado: " + socket.getInetAddress().getHostAddress());

                // Cria uma nova Thread para atender o cliente
                ThreadAtendente tratador = new ThreadAtendente(socket, semaforoTabela);
                Thread threadDoCliente = new Thread(tratador);
                threadDoCliente.start();
            }
        }catch (Exception e){
            e.printStackTrace();
        } 
    
    }
}