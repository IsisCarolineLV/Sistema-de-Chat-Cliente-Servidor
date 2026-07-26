import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor {
    private static final int PORTA = 5000; //porta escolhida
    public static void main(String[] args) throws Exception {
        System.out.println("O servidor de chat foi iniciado na porta " + PORTA + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            //servidor ligado
            while (true) {
                // O método .accept() trava o código aqui até um cliente se conectar
                Socket socketDoCliente = serverSocket.accept();
                System.out.println("Um novo cliente se conectou ao servidor!");
                
                // TODO: Aqui vamos criar uma Thread (TratadorDeCliente) para cuidar desse cliente
                // de forma independente, permitindo múltiplos clientes simultâneos.
            }
            
        } catch (IOException e) {
            System.out.println("Erro no servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
