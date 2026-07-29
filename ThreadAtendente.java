import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ThreadAtendente extends Thread{

    private Socket socket;

    public ThreadAtendente(Socket socket){
        this.setDaemon(true);   //para finalizar o atendente junto com o servidor
        this.socket = socket;
    }

    public void run(){

        InputStreamReader inputLeitor = null;
        OutputStreamWriter outputEscritor = null;
        BufferedReader bufferReader = null;
        BufferedWriter bufferWriter = null;
        
        try{
            inputLeitor = new InputStreamReader(socket.getInputStream());
            outputEscritor = new OutputStreamWriter(socket.getOutputStream());

            bufferReader = new BufferedReader(inputLeitor);
            bufferWriter = new BufferedWriter(outputEscritor);

            while(true){
                String mensagemDoCliente = bufferReader.readLine();

                if (mensagemDoCliente == null) {
                    break;
                }

                System.out.println("Cliente: "+ mensagemDoCliente);

                bufferWriter.write("Mensagem recebida");
                bufferWriter.newLine();
                bufferWriter.flush();

                if(mensagemDoCliente.equalsIgnoreCase("SAIR")) break;
            }

            socket.close();
            inputLeitor.close();
            outputEscritor.close();
            bufferReader.close(); // nao seria bufferWriter.close?
            bufferReader.close();

        }catch(IOException e){
            e.printStackTrace();
        }
        
    }
    
}
