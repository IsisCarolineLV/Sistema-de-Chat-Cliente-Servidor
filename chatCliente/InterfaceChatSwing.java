//fiz a interiniciando a interfaceface no scene builder, mas como Javafx eh uma bomba nos pcs com java 22
//a gemini sugeriu fazer em swing, entao eu pedi pra ela adaptar o arquivo fxml que eu fiz
//para swing, assim roda, teoricamente em qualquer pc independente da versao do java

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class InterfaceChatSwing {

    private static ArrayList<String> botoesLaterais = new ArrayList<>();
    private static java.util.List<JPanel> todosPaineisLaterais = new java.util.ArrayList<>();
    private static CardLayout cardLayout=null;
    private static JPanel painelGerenciador=null;
    private static Socket socket;
    private static InputStreamReader inputLeitor = null;
    private static OutputStreamWriter outputEscritor = null;
    private static BufferedReader bufferReader = null;
    private static BufferedWriter bufferWriter = null;
    private static String ipServidor;
    private static int portaServer;
    private static String meuNome;
    //Scanner scan = new Scanner(System.in); //scan jogado pra junto das variaveis


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> criarTela());
    }

    private static void criarTela() {
        // Configuração da janela principal 
        JFrame janela = new JFrame("Sistema de Chat");
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        
        
        janela.getContentPane().setPreferredSize(new Dimension(810, 810));
        janela.pack(); 
        janela.setResizable(false); // Trava o redimensionamento

        //CardLayout que trata cada pane nele como uma carta, ajudando a trocar o pane visivel facilmente
        cardLayout = new CardLayout();
        painelGerenciador = new JPanel(cardLayout);

        /////////////////////////////////////////////////////
        // TELA DE CONEXAO
        PainelComFundo telaConexao = new PainelComFundo("chatCliente/imagens/telaConexao.png");
        telaConexao.setLayout(null); 

        // Texto exemplo IP
        JLabel lblPlaceholderIp = new JLabel("Ex: 10.0.0.0");
        lblPlaceholderIp.setBounds(151, 224, 551, 49);
        lblPlaceholderIp.setForeground(Color.decode("#88adb3"));
        lblPlaceholderIp.setFont(new Font("SansSerif", Font.PLAIN, 31));
        
        //Campo para Digitar o IP
        JTextField txtIp = new JTextField();
        txtIp.setBounds(151, 206, 532, 85);
        configurarCampoInvisivel(txtIp, lblPlaceholderIp);

        // Texto exemplo porta
        JLabel lblPlaceholderPorta = new JLabel("Ex: 1234");
        lblPlaceholderPorta.setBounds(151, 391, 351, 49);
        lblPlaceholderPorta.setForeground(Color.decode("#88adb3"));
        lblPlaceholderPorta.setFont(new Font("SansSerif", Font.PLAIN, 31));

        //Campo para Digitar a porta
        JTextField txtPorta = new JTextField();
        txtPorta.setBounds(151, 373, 423, 85);
        configurarCampoInvisivel(txtPorta, lblPlaceholderPorta);

        // Botão Conectar
        JButton btnConectar = new JButton();
        btnConectar.setBounds(248, 618, 312, 64);
        configurarBotaoInvisivel(btnConectar);

        telaConexao.add(txtIp);
        telaConexao.add(lblPlaceholderIp);
        telaConexao.add(txtPorta);
        telaConexao.add(lblPlaceholderPorta);
        telaConexao.add(btnConectar);

        /////////////////////////////////////////////////////
        // TELA DE DEFINICAO DO NOME
        PainelComFundo telaMenu = new PainelComFundo("chatCliente/imagens/Menu.png");
        telaMenu.setLayout(null); 

        //Placeholder indicando onde digitar
        JLabel lblPlaceholderNome = new JLabel("Digite seu nome aqui...");
        lblPlaceholderNome.setBounds(225, 562, 351, 49);
        lblPlaceholderNome.setForeground(Color.decode("#88adb3"));
        lblPlaceholderNome.setFont(new Font("SansSerif", Font.PLAIN, 31));

        //Campo para Digitar o nome
        JTextField txtNome = new JTextField();
        txtNome.setBounds(225, 543, 481, 85);
        configurarCampoInvisivel(txtNome, lblPlaceholderNome);

        //Botao nomeAleatorio
        JButton btnAleatorio = new JButton();
        btnAleatorio.setBounds(179, 521, 45, 43);
        configurarBotaoInvisivel(btnAleatorio);

        //Botao entrar
        JButton btnEntrar = new JButton();
        btnEntrar.setBounds(246, 653, 312, 64);
        configurarBotaoInvisivel(btnEntrar);

        telaMenu.add(txtNome);
        telaMenu.add(lblPlaceholderNome); 
        telaMenu.add(btnAleatorio);
        telaMenu.add(btnEntrar);

        /////////////////////////////////////////////////////
        // TELA DO CHAT
        PainelComFundo telaChat = criarTelaChat("chatCliente/imagens/TelaChat.png", "Chat Geral");

        //Adiciona as telas no gerenciador:

        //A primeira tela adicionada é a que aparece por padrão
        painelGerenciador.add(telaConexao, "TELA_CONEXAO");
        painelGerenciador.add(telaMenu, "TELA_MENU");
        painelGerenciador.add(telaChat, "TELA_CHAT");

        //Mudanca de tela:
        // Conexao -> Nome
        btnConectar.addActionListener(e -> {
            ipServidor = txtIp.getText();
            String porta = txtPorta.getText();
            //Socket socketNovo = null;
            
            // Aqui você futuramente adiciona a lógica do "new Socket(ip, porta)"
            if (!ipServidor.trim().isEmpty() && !porta.trim().isEmpty()) {
                socket =null;
                //try {
                    
                    try {
                        portaServer = Integer.parseInt(porta);
                        socket = new Socket(ipServidor,portaServer );
                        cardLayout.show(painelGerenciador, "TELA_MENU");
                    } catch(NumberFormatException n){
                        txtPorta.setText(""); 
                        lblPlaceholderPorta.setForeground(Color.RED);
                        lblPlaceholderPorta.setText("Porta inválida!"); 
                        lblPlaceholderPorta.setVisible(true);
                        janela.requestFocusInWindow(); 
                    } catch (Exception e1) {
                        //e1.printStackTrace();
                        txtIp.setText(""); 
                        lblPlaceholderIp.setForeground(Color.RED);
                        lblPlaceholderIp.setText("Servidor não encontrado"); 
                        lblPlaceholderIp.setVisible(true);
                        txtPorta.setText(""); 
                        lblPlaceholderPorta.setForeground(Color.RED);
                        lblPlaceholderPorta.setText("Porta não encontrada"); 
                        lblPlaceholderPorta.setVisible(true);
                        janela.requestFocusInWindow(); 
                    }
                    
                //} catch (IOException e1) {
                    //e1.printStackTrace();
                //}
            }

        });

        // Nome -> Chat Geral
        btnEntrar.addActionListener(e -> {
            String nome = txtNome.getText();
            if (nome.trim().isEmpty()) {    //se tentar enviar com o campo em branco
                txtNome.setText(""); 
                lblPlaceholderNome.setForeground(Color.RED);
                lblPlaceholderNome.setText("Nome obrigatório!"); 
                lblPlaceholderNome.setVisible(true);
                janela.requestFocusInWindow(); 
            }else {
                try {
                    socket = new Socket(ipServidor, portaServer); //mudado aq tbm

                    inputLeitor = new InputStreamReader(socket.getInputStream());
                    outputEscritor = new OutputStreamWriter(socket.getOutputStream());

                    bufferReader = new BufferedReader(inputLeitor);
                    bufferWriter = new BufferedWriter(outputEscritor);

                    bufferWriter.write(nome); //primeira mensagem eh o nome do cliente
                    bufferWriter.newLine(); 
                    bufferWriter.flush();

                    if(bufferReader.readLine().equals("Já existe um usuário com esse nome")){
                        
                        lblPlaceholderNome.setText("Nome indisponível!");
                        //txtNome.setVisible(false);
                        txtNome.setText("");
                        lblPlaceholderNome.setForeground(Color.RED);
                        lblPlaceholderNome.setVisible(true);
                        janela.requestFocusInWindow(); 
                        bufferWriter.close();
                        bufferReader.close();
                        inputLeitor.close();
                        outputEscritor.close();
                        socket.close();
                    }else{
                        meuNome = nome;
                        cardLayout.show(painelGerenciador, "TELA_CHAT");
                        LeitorMensagensRecebidas leitorDeMensagem = new LeitorMensagensRecebidas(bufferReader);
                        leitorDeMensagem.start();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        // Configuração final
        janela.add(painelGerenciador);
        janela.setLocationRelativeTo(null);
        janela.setVisible(true);
    }

    // Torna o botao "invisivel"
    private static void configurarBotaoInvisivel(JButton botao) {
        botao.setOpaque(false);
        botao.setContentAreaFilled(false);
        botao.setBorderPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
    }
    
    // Quando se escreve algo no campo o placeholder fica invisivel
    private static void configurarCampoInvisivel(JTextField campo, JLabel placeholder) {
        campo.setOpaque(false); 
        campo.setBorder(null);  
        campo.setFont(new Font("SansSerif", Font.PLAIN, 31));
        campo.setForeground(Color.decode("#500bc1")); 
        campo.setCaretColor(Color.BLACK); 
        
        campo.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { 
                placeholder.setVisible(false); 
                placeholder.setForeground(Color.decode("#88adb3"));
                placeholder.setText("Digite aqui");
            }
            public void focusLost(FocusEvent e) { if(campo.getText().isEmpty()) placeholder.setVisible(true); }
        });
    }

    //Cria Tela de Chat
    private static PainelComFundo criarTelaChat(String caminho, String titulo){
        PainelComFundo telaChat = new PainelComFundo(caminho);
        telaChat.setLayout(null);

        JPanel painelMensagens = new JPanel();
        painelMensagens.setOpaque(false);
        painelMensagens.setLayout(new BoxLayout(painelMensagens, BoxLayout.Y_AXIS));
        JPanel wrapperMensagens = new JPanel(new BorderLayout());
        wrapperMensagens.setOpaque(false);
        // O BorderLayout.NORTH é o que força as mensagens a ficarem espremidas em cima!
        wrapperMensagens.add(painelMensagens, BorderLayout.NORTH);

        /*MensagemPanel novoBalao = new MensagemPanel("Isis", "Oii! Essa é uma mensagem de teste");
        MensagemPanel novoBalao2 = new MensagemPanel("Oii! Essa é uma mensagem de teste minha :P");
        MensagemPanel novoBalao3 = new MensagemPanel("Isis", "Oii! Essa é uma mensagem de teste");

        for(int i=0; i<20; i++){
            MensagemPanel novoB = new MensagemPanel("Isis "+i, "Oii! Essa é uma mensagem de teste");
            painelMensagens.add(novoB);
        }

        // Adiciona o balão no painel que está dentro do JScrollPane
        painelMensagens.add(novoBalao);
        painelMensagens.add(novoBalao2);
        painelMensagens.add(novoBalao3);*/

        // Scroll para as mensagens da conversa
        JScrollPane scrollChat = new JScrollPane(wrapperMensagens);
        scrollChat.setBounds(186, 65, 612, 625);
        scrollChat.setOpaque(false);
        scrollChat.getViewport().setOpaque(false); 
        scrollChat.setBorder(null);
        scrollChat.getVerticalScrollBar().setUnitIncrement(16);
        scrollChat.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollChat.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        
        //Place holder indicando onde digitar a mensagem
        JLabel lblPlaceholderMsg = new JLabel("Digite aqui...");
        lblPlaceholderMsg.setBounds(219, 735, 471, 49);
        lblPlaceholderMsg.setForeground(Color.decode("#88adb3"));
        lblPlaceholderMsg.setFont(new Font("SansSerif", Font.PLAIN, 31));

        //Campo para digitar uma mensagem
        JTextField txtMensagem = new JTextField();
        txtMensagem.setBounds(219, 735, 471, 49);
        configurarCampoInvisivel(txtMensagem, lblPlaceholderMsg);
        txtMensagem.setFont(new Font("SansSerif", Font.PLAIN, 31)); // Reduzindo um pouco a fonte da msg

        //Botao enviar
        JButton btnEnviar = new JButton();
        btnEnviar.setBounds(704, 729, 65, 56);
        configurarBotaoInvisivel(btnEnviar);

        //Titulo do chat
        JLabel lblTituloChat = new JLabel(titulo, SwingConstants.CENTER);
        lblTituloChat.setBounds(334, 24, 257, 31);
        lblTituloChat.setFont(new Font("SansSerif", Font.PLAIN, 31));
        lblTituloChat.setForeground(Color.BLACK);

        //Botao do Chat Geral
        JButton btnChatLateral = new JButton("Chat Geral");
        btnChatLateral.setBounds(14, 48, 141, 45); 
        btnChatLateral.setFont(new Font("SansSerif", Font.PLAIN, 20));
        btnChatLateral.setFocusPainted(false);
        if(titulo.equals("Chat Geral")){
            btnChatLateral.setBackground(Color.decode("#b2bdff"));
        } else{
            btnChatLateral.setBackground(Color.decode("#458c98"));
        }
        btnChatLateral.addActionListener(e ->{
            cardLayout.show(painelGerenciador, "TELA_CHAT");
        });
        //Demais botoes pros chats privados
        JPanel painelLateral = new JPanel();
        painelLateral.setOpaque(false);
        painelLateral.setLayout(new BoxLayout(painelLateral, BoxLayout.Y_AXIS));

        for(String nomeAntigo : botoesLaterais){
            JButton btnAntigo = new JButton(nomeAntigo);
            btnAntigo.setPreferredSize(new Dimension(141, 45));
            btnAntigo.setMaximumSize(new Dimension(141, 45)); 
            btnAntigo.setBackground(Color.decode("#458c98"));
            btnAntigo.setOpaque(true);
            btnAntigo.setFocusPainted(false);
            btnAntigo.setFont(new Font("SansSerif", Font.PLAIN, 20));
            btnAntigo.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            btnAntigo.addActionListener(e -> {
                cardLayout.show(painelGerenciador, nomeAntigo);
            });

            painelLateral.add(btnAntigo);
            painelLateral.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        todosPaineisLaterais.add(painelLateral);

        //adicionando botao da tela privada
        if(!titulo.equals("Chat Geral")){
            adicionarNovoChatNaLateral(titulo);
        }

        //Scroll Lateral
        
        JScrollPane scrollLateral = new JScrollPane(painelLateral);
        scrollLateral.setBounds(14, 200, 160, 600);
        scrollLateral.setOpaque(false);
        scrollLateral.getViewport().setOpaque(false); 
        scrollLateral.setBorder(null);
        scrollLateral.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollLateral.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));


        //Enviar mensagem
        btnEnviar.addActionListener(e -> {
            String mensagem = txtMensagem.getText();
            if (!mensagem.trim().isEmpty()){
                MensagemPanel novaMsg = new MensagemPanel(mensagem);
                painelMensagens.add(novaMsg);
                txtMensagem.setText("");
                try {

                    if(titulo.equals("Chat Geral")){
                        if(mensagem.equalsIgnoreCase("/listar")){
                            bufferWriter.write("LISTAR_USUARIOS");
                        }else if(mensagem.equalsIgnoreCase("/ajuda")){
                            bufferWriter.write("AJUDA");
                        }else if(mensagem.charAt(0)=='@'){
                            bufferWriter.write(meuNome+"|"+mensagem.substring(1));
                            //faz isso quando receber a confirmacao da existencia do usuario
                            //PainelComFundo telaNova = criarTelaChat(caminho, mensagem.substring(1));
                            //painelGerenciador.add(telaNova, mensagem.substring(1));
                            //bufferWriter.write("PRIVADA|"+mensagem.substring(1)+"|"+mensagemPrivada);
                        }else{
                            bufferWriter.write("CHAT GERAL|"+mensagem);
                        }
                    }else{
                        bufferWriter.write("PRIVADA|"+titulo+"|"+mensagem);
                    }
                    bufferWriter.newLine(); 
                    bufferWriter.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        
        telaChat.add(scrollChat);
        telaChat.add(txtMensagem);
        telaChat.add(lblPlaceholderMsg);
        telaChat.add(btnEnviar);
        telaChat.add(lblTituloChat);
        telaChat.add(btnChatLateral);
        telaChat.add(scrollLateral);

        return telaChat;
    }

    // Novo método para adicionar botões de chat privado
    public static void adicionarNovoChatNaLateral(String tituloChat) {
        // Para cada painel lateral existente no nosso jogo, criamos um botão novo!
        JButton btnNovo = null;
        for (JPanel painel : todosPaineisLaterais) {
            btnNovo = new JButton(tituloChat);
            btnNovo.setPreferredSize(new Dimension(141, 45));
            btnNovo.setMaximumSize(new Dimension(141, 45)); 
            
            btnNovo.setBackground(Color.decode("#458c98"));
            btnNovo.setOpaque(true);
            btnNovo.setFocusPainted(false);
            btnNovo.setFont(new Font("SansSerif", Font.PLAIN, 20));
            btnNovo.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            btnNovo.addActionListener(e -> {
                cardLayout.show(painelGerenciador, tituloChat);
            });
            
            painel.add(btnNovo);
            painel.add(Box.createRigidArea(new Dimension(0, 10))); // Espaçamento
            
            // Avisa o painel que ele precisa atualizar o visual porque ganhou um botão
            painel.revalidate();
            painel.repaint();
            
        }
        botoesLaterais.add(tituloChat);
        btnNovo.setBackground(Color.decode("#b2bdff"));
    }

    //Thread pra ficar recebendo as mensagens
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
}

// Classe que desenha a imagem de fundo
class PainelComFundo extends JPanel {
    private Image imagemFundo;

    public PainelComFundo(String caminhoDaImagem) {
        try {
            this.imagemFundo = new ImageIcon(caminhoDaImagem).getImage();
        } catch (Exception e) {
            System.err.println("Não foi possível carregar a imagem: " + caminhoDaImagem);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagemFundo != null) {
            g.drawImage(imagemFundo, 0, 0, getWidth(), getHeight(), this);
        }
    }
}