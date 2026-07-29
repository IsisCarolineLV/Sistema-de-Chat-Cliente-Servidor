import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class ThreadAtendente extends Thread{

    private Socket socket;
    private Semaphore semaforoTabela;

    public ThreadAtendente(Socket socket, Semaphore s){
        this.setDaemon(true);   //para finalizar o atendente junto com o servidor
        this.socket = socket;
        semaforoTabela = s;
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

                if(mensagemDoCliente.equalsIgnoreCase("SAIR")) {
                    semaforoTabela.acquire();
                    Servidor.socketCliente.remove(socket);  //remove o cliente da tabela de roteamento
                    semaforoTabela.release();
                    break;
                }
            }

            socket.close();
            inputLeitor.close();
            outputEscritor.close();
            bufferWriter.close(); 
            bufferReader.close();

        }catch(IOException e){
            e.printStackTrace();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        
    }
    
}
