import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {


    private static class LeitorMensagensRecebidas extends Thread{
        private BufferedReader bufferReader = null;
        private boolean ativo = true;

        public LeitorMensagensRecebidas (BufferedReader bf){
            this.setDaemon(true);
            bufferReader = bf;
        }
        public void run(){
            while(ativo){
                try{
                    String msg = bufferReader.readLine();

                    if(!msg.equals(null)){
                        System.out.println(msg);
                    }
                    sleep(1000);    //pra nao ficar lendo toda hora e ocupando a cpu

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

        public void desligar(){
            ativo=false;
        }
    }

    public static void main(String[] args){
        Socket socket =null;
        InputStreamReader inputLeitor = null;
        OutputStreamWriter outputEscritor = null;
        BufferedReader bufferReader = null;
        BufferedWriter bufferWriter = null;
        Scanner scan = new Scanner(System.in); //scan jogado pra junto das variaveis

        try{

            //System.out.println("Digite o IP do servidor: "); //mudei o ip pra ser configurado pelo usuario
            //String ipServidor = scan.nextLine();

            //System.out.println("Digite a porta do servidor: ");//mudei a porta pra ser configurada pelo usuario
           // int portaServer = Integer.parseInt(scan.nextLine());

            //TIRAR ISSO DEPOIS!!!!
            String ipServidor = "localhost"; int portaServer = 5000;    //so pra agilizar a vida nos testes aqui        <<-- vamo deiar isso aq, pq agiliza dms 

            boolean nomeAceito=false;
            do{
                socket = new Socket(ipServidor, portaServer); //mudado aq tbm

                inputLeitor = new InputStreamReader(socket.getInputStream());
                outputEscritor = new OutputStreamWriter(socket.getOutputStream());

                bufferReader = new BufferedReader(inputLeitor);
                bufferWriter = new BufferedWriter(outputEscritor);

                System.out.println("Digite seu apelido: "); //add pra o cliente anunciar de primeira seu nome pro servidor
                String nome = scan.nextLine();

                bufferWriter.write(nome); //primeira mensagem eh o nome do cliente
                bufferWriter.newLine(); 
                bufferWriter.flush();

                if(bufferReader.readLine().equals("Já existe um usuário com esse nome")){
                    System.out.println("Já existe um usuário com esse nome");
                    bufferWriter.close();
                    bufferReader.close();
                    inputLeitor.close();
                    outputEscritor.close();
                    socket.close();
                }else{
                    nomeAceito=true;
                }

            }while(!nomeAceito);

            LeitorMensagensRecebidas leitorDeMensagem = new LeitorMensagensRecebidas(bufferReader);
            leitorDeMensagem.start();

            //System.out.println(bufferReader.readLine()); 
            System.out.println("Para falar em privado com alguem digite \\nome");//barra normal é melhor n?
            System.out.println("Para listar os usuarios conectados digite /listar"); 
            System.out.println("Para sair digite SAIR");

            
            while(true){
                String mensagem = scan.nextLine();

                if(mensagem.equalsIgnoreCase("/listar")){
                    bufferWriter.write("LISTAR_USUARIOS");
                }
                else if(mensagem.charAt(0)=='\\'){
                    System.out.print("Digite sua mensagem para "+mensagem.substring(1)+": ");
                    String mensagemPrivada = scan.nextLine();
                    bufferWriter.write("PRIVADA|"+mensagem.substring(1)+"|"+mensagemPrivada);
                }else{
                    bufferWriter.write("CHAT GERAL|"+mensagem);
                }
                bufferWriter.newLine(); 
                bufferWriter.flush();

                //System.out.println("Servidor: "+ bufferReader.readLine()); 

                if(mensagem.equalsIgnoreCase("SAIR")){
                    leitorDeMensagem.desligar();
                    
                    bufferWriter.close();
                    bufferReader.close();
                    inputLeitor.close();
                    outputEscritor.close();
                    socket.close();
                    break;
                }
            }
        } catch (Exception e){
            System.out.println("Deu ruim :( Socket morreu");
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
