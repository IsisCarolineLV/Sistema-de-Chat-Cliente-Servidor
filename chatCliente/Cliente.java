import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    private static final int PORTA = 5000; //porta escolhida    PS: nao é mais util com a mudanca atual kkk
    public static void main(String[] args){
        Socket socket =null;
        InputStreamReader inputLeitor = null;
        OutputStreamWriter outputEscritor = null;
        BufferedReader bufferReader = null;
        BufferedWriter bufferWriter = null;
        Scanner scan = new Scanner(System.in); //scan jogado pra junto das variaveis

        try{

            System.out.println("Digite o IP do servidor: "); //mudei o ip pra ser configurado pelo usuario
            String ipServidor = scan.nextLine();

            System.out.println("Digite a porta do servidor: ");//mudei a porta pra ser configurada pelo usuario
            int portaServer = Integer.parseInt(scan.nextLine());
            
            socket = new Socket(ipServidor, portaServer); //mudado aq tbm

            inputLeitor = new InputStreamReader(socket.getInputStream());
            outputEscritor = new OutputStreamWriter(socket.getOutputStream());

            bufferReader = new BufferedReader(inputLeitor);
            bufferWriter = new BufferedWriter(outputEscritor);

            while(true){
                String mensagem = scan.nextLine();
                bufferWriter.write(mensagem);
                bufferWriter.newLine(); 
                bufferWriter.flush();

                System.out.println("Servidor: "+ bufferReader.readLine()); 

                if(mensagem.equalsIgnoreCase("SAIR")){
                    break;
                }
            }
        } catch (Exception e){
            System.out.println("Deu ruim :(");
            e.printStackTrace();
        } finally {
            try{
                if (scan != null) scan.close(); //fechei o scanner
                if (socket != null) socket.close();
                if (inputLeitor != null) inputLeitor.close();
                if (outputEscritor != null) outputEscritor.close();
                if (bufferReader != null) bufferReader.close();
                if (bufferWriter != null) bufferWriter.close();
            } catch (IOException e){
                e.printStackTrace();
            }
            
        }
    }
}
