import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    private static final int PORTA = 5000; //porta escolhida
    public static void main(String[] args){
        Socket socket =null;
        InputStreamReader inputLeitor = null;
        OutputStreamWriter outputEscritor = null;
        BufferedReader bufferReader = null;
        BufferedWriter bufferWriter = null;

        try{

            socket = new Socket("localhost", PORTA);
            inputLeitor = new InputStreamReader(socket.getInputStream());
            outputEscritor = new OutputStreamWriter(socket.getOutputStream());

            bufferReader = new BufferedReader(inputLeitor);
            bufferWriter = new BufferedWriter(outputEscritor);

            Scanner scan = new Scanner(System.in);

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
