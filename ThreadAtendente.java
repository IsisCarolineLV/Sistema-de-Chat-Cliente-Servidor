import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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

    public ThreadAtendente(Socket socket, Semaphore s, String nome){
        this.setDaemon(true);   //para finalizar o atendente junto com o servidor
        this.socket = socket;
        semaforoTabela = s;
        nomeDoAtendido = nome;
    }

    public void run(){
        
        try{
            inputLeitor = new InputStreamReader(socket.getInputStream());
            outputEscritor = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);

            bufferReader = new BufferedReader(inputLeitor);
            bufferWriter = new BufferedWriter(outputEscritor);

            broadcast(nomeDoAtendido + " entrou do chat");
            bufferWriter.write("Servidor|"+nomeDoAtendido + ", seja bem-vindo(a) ao chat geral!");
            bufferWriter.newLine();
            bufferWriter.flush();
            mandaAjuda(bufferWriter);

            //reconstituindo mensagens armazenadas
            java.io.File arquivoHistorico = new java.io.File("arquivos/"+nomeDoAtendido + ".txt");
            if(arquivoHistorico.exists()) {
                java.io.BufferedReader leitorArquivo = new java.io.BufferedReader(new java.io.FileReader(arquivoHistorico));
                String linhaAntiga;
                while((linhaAntiga = leitorArquivo.readLine()) != null) {
                    bufferWriter.write(linhaAntiga);
                    bufferWriter.newLine();
                }
                bufferWriter.flush();
                leitorArquivo.close();
            }

            bufferWriter.write("Servidor|FIM_HISTORICO");
            bufferWriter.newLine();
            bufferWriter.flush();

            while(true){
                String mensagemDoCliente = bufferReader.readLine();

                if (mensagemDoCliente == null) {
                    break;
                }

                if(mensagemDoCliente.equalsIgnoreCase("SAIR")) {
                    semaforoTabela.acquire();
                    Servidor.socketCliente.remove(nomeDoAtendido);  //remove o cliente da tabela de roteamento
                    semaforoTabela.release();
                    break;
                }

                Mensagem mensagem = new Mensagem(mensagemDoCliente);    //mudei a estrutura
                                                                        // tipo | remetende | destino | mensagem

                // deixei todas as funcoes num unico if
                if(mensagem.getTipo().equals("LISTAR_USUARIOS")){
                    StringBuilder lista = new StringBuilder("Servidor|Usuarios conectados: ");
                    semaforoTabela.acquire();
                    for(String nomeConectado : Servidor.socketCliente.keySet()){
                        lista.append(nomeConectado).append(", ");//coloca os clientes conecotados na lista 
                    }
                    semaforoTabela.release();

                    if(lista.length()>21){
                        lista.setLength(lista.length() - 2);//tira a virgula 
                    }
                    bufferWriter.write(lista.toString());
                    bufferWriter.newLine();
                    bufferWriter.flush();

                    continue;
                }else if(mensagem.getTipo().equals("AJUDA")){
                    mandaAjuda(bufferWriter);
                    continue;
                }else  if(mensagem.getTipo().equals("CHAT GERAL")){
                    //manda pra todo mundo no chat geral
                    broadcast(mensagem.getRemetente(), mensagem.getConteudo());
                }else{
                    //manda pra uma pessoa especifica
                    semaforoTabela.acquire();
                    //System.out.println("DESTINO:"+mensagem.getDestino());
                    Socket socketDestino = Servidor.socketCliente.get(mensagem.getDestino());
                    semaforoTabela.release();
                    if(socketDestino == null){
                        bufferWriter.write("PRIVADA|Servidor|Usuario não encontrado");
                        bufferWriter.newLine();
                        bufferWriter.flush();
                        
                    }else{
                        OutputStreamWriter outputEscritorDestino = new OutputStreamWriter(socketDestino.getOutputStream());
                        BufferedWriter bufferWriterDestino =new BufferedWriter(outputEscritorDestino);
                        String msgProRemetente = "PRIVADA|Servidor|"+mensagem.getDestino()+"|"+ mensagem.getConteudo();
                        bufferWriter.write(msgProRemetente);
                        bufferWriter.newLine();
                        bufferWriter.flush();
                        salvarNoHistorico(nomeDoAtendido, msgProRemetente);

                        String msgProDestino = "PRIVADA|"+nomeDoAtendido +"|"+mensagem.getDestino()+"|"+ mensagem.getConteudo();
                        bufferWriterDestino.write(msgProDestino);
                        bufferWriterDestino.newLine();
                        bufferWriterDestino.flush();
                        salvarNoHistorico(mensagem.getDestino(), msgProDestino);
                    }
                        
                }
                
            }

            socket.close();
            inputLeitor.close();
            outputEscritor.close();
            bufferWriter.close(); 
            bufferReader.close();

        }catch(IOException e){
            //e.printStackTrace();
        }catch (InterruptedException e) {
            //e.printStackTrace();
        }finally {
            try {
                if (nomeDoAtendido != null) {
                    semaforoTabela.acquire();
                    Servidor.socketCliente.remove(nomeDoAtendido); // Remove o cliente que deu erro da tabela
                    semaforoTabela.release();
                    System.out.println(nomeDoAtendido + " foi desconectado");
                }

                // Fechando os recursos de forma segura
                // (se o cliente saiu de forma correta nao vai fechar duas vezes)
                if (socket != null && !socket.isClosed()) socket.close();
                if (inputLeitor != null) inputLeitor.close();
                if (outputEscritor != null) outputEscritor.close();
                if (bufferWriter != null) bufferWriter.close();
                if (bufferReader != null) bufferReader.close();

                broadcast( nomeDoAtendido + " saiu do chat");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }

    public void broadcast(String autor, String mensagem) throws InterruptedException, IOException{

        semaforoTabela.acquire();
        for(Map.Entry<String, Socket> c: Servidor.socketCliente.entrySet()){
            OutputStreamWriter outputEscritorC = new OutputStreamWriter(c.getValue().getOutputStream());
            BufferedWriter bufferWriterC =new BufferedWriter(outputEscritorC);

            bufferWriterC.write("CHAT GERAL|"+autor +"|"+ mensagem);
            bufferWriterC.newLine();
            bufferWriterC.flush();

            salvarNoHistorico(c.getKey(), "CHAT GERAL|"+autor +"|"+ mensagem);

        }
        semaforoTabela.release();

    }

    public void broadcast( String mensagem) throws InterruptedException, IOException{

        semaforoTabela.acquire();
        for(Map.Entry<String, Socket> c: Servidor.socketCliente.entrySet()){
            OutputStreamWriter outputEscritorC = new OutputStreamWriter(c.getValue().getOutputStream());
            BufferedWriter bufferWriterC =new BufferedWriter(outputEscritorC);

            bufferWriterC.write("Servidor|"+ mensagem);
            bufferWriterC.newLine();
            bufferWriterC.flush();

        }
        semaforoTabela.release();

    }

    public void mandaAjuda(BufferedWriter bufferedWriter) throws IOException{
        bufferWriter.write("Servidor|/ajuda: exibe comandos");
        bufferWriter.newLine();
        bufferWriter.write("Servidor|/listar: exibe uma lista de todos os usuarios online");
        bufferWriter.newLine();
        bufferWriter.write("Servidor|@nomeUsuario: inicia um chat privado com o usuario");
        bufferWriter.newLine();
        bufferWriter.write("Servidor|/sair: fecha a conexão com os servidor");
        bufferWriter.newLine();
        bufferWriter.flush();
    }

    // Método para salvar as mensagens no arquivo de texto do usuário
    public void salvarNoHistorico(String nomeDonoDoArquivo, String linha) {
        // O 'true' garante que ele vai adicionar no final do arquivo (append)
        try (java.io.FileWriter fw = new java.io.FileWriter("arquivos/"+nomeDonoDoArquivo + ".txt", true);
             java.io.BufferedWriter bw = new java.io.BufferedWriter(fw)) {
            bw.write(linha);
            bw.newLine();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        
    }
    
}
