import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class ThreadAtendente extends Thread{

    private Socket socket;
    private Semaphore semaforoTabela;
    private String nomeDoAtendido;
    private InputStreamReader inputLeitor = null;
    private OutputStreamWriter outputEscritor = null;
    private BufferedReader bufferReader = null;
    private BufferedWriter bufferWriter = null;

    public ThreadAtendente(Socket socket, Semaphore s){
        this.setDaemon(true);   //para finalizar o atendente junto com o servidor
        this.socket = socket;
        semaforoTabela = s;
    }

    public void run(){
        
        try{
            inputLeitor = new InputStreamReader(socket.getInputStream());
            outputEscritor = new OutputStreamWriter(socket.getOutputStream());

            bufferReader = new BufferedReader(inputLeitor);
            bufferWriter = new BufferedWriter(outputEscritor);

            nomeDoAtendido = bufferReader.readLine();   //le o nome
            //adiciona o cliente novo e o socket dele na tabela
            semaforoTabela.acquire();
            Servidor.socketCliente.put(nomeDoAtendido, socket);
            semaforoTabela.release();

            bufferWriter.write(nomeDoAtendido + ", seja bem-vindo ao chat geral!");
            bufferWriter.newLine();
            bufferWriter.flush();

            while(true){
                String mensagemDoCliente = bufferReader.readLine();
                

                if (mensagemDoCliente == null) {
                    break;
                }
                Mensagem mensagem = new Mensagem(mensagemDoCliente);

                if(mensagemDoCliente.equalsIgnoreCase("SAIR")) {
                    semaforoTabela.acquire();
                    Servidor.socketCliente.remove(nomeDoAtendido);  //remove o cliente da tabela de roteamento
                    semaforoTabela.release();
                    break;
                }

                if(mensagem.getTipo()==1){
                    broadcast(mensagem.getConteudo());
                }else{
                    semaforoTabela.acquire();
                    System.out.println("DESTINO:"+mensagem.getDestino());
                    Socket socketDestino = Servidor.socketCliente.get(mensagem.getDestino());
                    semaforoTabela.release();
                    if(socketDestino== null){
                        bufferWriter.write("Usuario não encontrado");
                        bufferWriter.newLine();
                        bufferWriter.flush();
                    }else{
                        OutputStreamWriter outputEscritorDestino = new OutputStreamWriter(socketDestino.getOutputStream());
                        BufferedWriter bufferWriterDestino =new BufferedWriter(outputEscritorDestino);

                        bufferWriterDestino.write(nomeDoAtendido +"(privada): "+ mensagem.getConteudo());
                        bufferWriterDestino.newLine();
                        bufferWriterDestino.flush();
                    }
                        
                }

                System.out.println(nomeDoAtendido +": "+ mensagem.getConteudo());

                bufferWriter.write("Mensagem recebida");
                bufferWriter.newLine();
                bufferWriter.flush();

                
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

    public void broadcast(String mensagem) throws InterruptedException, IOException{

        semaforoTabela.acquire();
        for(Map.Entry<String, Socket> c: Servidor.socketCliente.entrySet()){
            OutputStreamWriter outputEscritor = new OutputStreamWriter(c.getValue().getOutputStream());
            BufferedWriter bufferWriter =new BufferedWriter(outputEscritor);

            bufferWriter.write(nomeDoAtendido +": "+ mensagem);
            bufferWriter.newLine();
            bufferWriter.flush();

        }
        semaforoTabela.release();

    }
    
}
