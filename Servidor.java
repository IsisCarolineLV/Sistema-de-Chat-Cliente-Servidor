import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
                // O servidor fica aguardando uma conexao
                socket = serverSocket.accept();

                //envia confirmacao 
                try{
                    OutputStreamWriter outputEscritor = new OutputStreamWriter(socket.getOutputStream());
                    BufferedWriter bufferWriter = new BufferedWriter(outputEscritor);

                    bufferWriter.write("Conexão aceita");
                    bufferWriter.newLine();
                    bufferWriter.flush();
                }catch(IOException e){
                    System.out.println("Não conseguiu enviar a confirmação");
                }

                VerificadorNomes verificaNovoCliente= new VerificadorNomes(socket);
                verificaNovoCliente.start();
                
            }
        }catch (Exception e){
            e.printStackTrace();
        } 
    
    }

    static class VerificadorNomes extends Thread{
        private Socket socketNovoCliente;

        public VerificadorNomes (Socket socket){
            socketNovoCliente = socket;
            this.setDaemon(true);
        }

        public void run(){
            try {
                
                //Se alguma conexao comecou ele segue executando isso:
                System.out.println("Novo cliente tentou conectar: " + socketNovoCliente.getInetAddress().getHostAddress());

                InputStreamReader inputLeitor = new InputStreamReader(socketNovoCliente.getInputStream());
                BufferedReader bufferReader = new BufferedReader(inputLeitor);
                String nomeDoCliente = bufferReader.readLine();   //le o nome
                
                    
                //verifica se o nome eh valido
                if(nomeUnico(nomeDoCliente)){
                    // Cria uma nova Thread para atender o cliente
                    ThreadAtendente tratador = new ThreadAtendente(socketNovoCliente, semaforoTabela, nomeDoCliente);
                    Thread threadDoCliente = new Thread(tratador);
                    threadDoCliente.start();

                    semaforoTabela.acquire();
                    socketCliente.put(nomeDoCliente, socketNovoCliente);
                    semaforoTabela.release();

                    System.out.println(nomeDoCliente+" foi conectado!");

                }else{
                    //Avisa pro Cliente que o nome nao foi aceito
                    OutputStreamWriter outputEscritor = new OutputStreamWriter(socketNovoCliente.getOutputStream());
                    BufferedWriter bufferWriter = new BufferedWriter(outputEscritor);

                    bufferWriter.write("Já existe um usuário com esse nome");
                    bufferWriter.newLine();
                    bufferWriter.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //verifica se ha alguma ocorrencia desse nome na nossa atual tabela de roteamento
        public boolean  nomeUnico(String nome) throws InterruptedException{
            boolean aceito = true;

            semaforoTabela.acquire();
            for(Map.Entry<String, Socket> c: Servidor.socketCliente.entrySet()){
                if(c.getKey().equals(nome)) aceito=false;
                //System.out.println(c.getKey()+ " x "+ nome);
            }
            semaforoTabela.release();

            return aceito;
        }
    }
    
}

