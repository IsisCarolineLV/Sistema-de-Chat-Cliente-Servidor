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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class InterfaceChatSwing {

    private static ArrayList<String> botoesLaterais = new ArrayList<>();
    private static java.util.List<JPanel> todosPaineisLaterais = new java.util.ArrayList<>();
    private static Map<String, JButton> botaoEnviarDeCadaChat = new HashMap<>();
    
    private static String chatAtivo = "Chat Geral";
    private static Map<String, java.util.List<JButton>> botoesDaBarraLateral = new HashMap<>();
    private static Map<String, Integer> mensagensNaoLidas = new HashMap<>();

    private static Map<String,JPanel> PanelsMensagemTelas = new HashMap<>();
    private static CardLayout cardLayout=null;
    private static JPanel painelGerenciador=null;
    private static JFrame janela=null;
    private static Socket socket;
    private static InputStreamReader inputLeitor = null;
    private static OutputStreamWriter outputEscritor = null;
    private static BufferedReader bufferReader = null;
    private static BufferedWriter bufferWriter = null;
    private static LeitorMensagensRecebidas leitorDeMensagem =null;
    private static String ipServidor;
    private static int portaServer;
    private static String meuNome;

    private static JButton btnConectar;

    private static Semaphore semaforoMapPanels = new Semaphore(1);

    //Scanner scan = new Scanner(System.in); //scan jogado pra junto das variaveis


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> criarTela());
    }

    private static void criarTela() {
        // Configuração da janela principal 
        janela = new JFrame("Chat Maneiro");
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        janela.getContentPane().setPreferredSize(new Dimension(810, 810));
        janela.pack(); 
        janela.setResizable(false); // Trava o redimensionamento

        //CardLayout que trata cada pane nele como uma carta, ajudando a trocar o pane visivel facilmente
        cardLayout = new CardLayout();
        painelGerenciador = new JPanel(cardLayout);

        /////////////////////////////////////////////////////
        // TELA DE CONEXAO
        //PainelComFundo telaConexao = new PainelComFundo("chatCliente/imagens/telaConexao.png");
        PainelComFundo telaConexao = new PainelComFundo("imagens/telaConexao.png");
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
        btnConectar = new JButton();
        btnConectar.setBounds(248, 618, 312, 64);
        configurarBotaoInvisivel(btnConectar);

        telaConexao.add(txtIp);
        telaConexao.add(lblPlaceholderIp);
        telaConexao.add(txtPorta);
        telaConexao.add(lblPlaceholderPorta);
        telaConexao.add(btnConectar);

        /////////////////////////////////////////////////////
        // TELA DE DEFINICAO DO NOME
        //PainelComFundo telaMenu = new PainelComFundo("chatCliente/imagens/Menu.png");
        PainelComFundo telaMenu = new PainelComFundo("imagens/Menu.png");
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
        //PainelComFundo telaChat = criarTelaChat("chatCliente/imagens/TelaChat.png", "Chat Geral");
        PainelComFundo telaChat = criarTelaChat("imagens/TelaChat.png", "Chat Geral");

        //so pra testas as telas laterais, depois tem que tirar
        //PainelComFundo telaIsis = criarTelaChat("chatCliente/imagens/TelaChat.png", "Isis");
        //PainelComFundo telaLevi = criarTelaChat("chatCliente/imagens/TelaChat.png", "Levi");

        //Adiciona as telas no gerenciador:

        //A primeira tela adicionada é a que aparece por padrão
        painelGerenciador.add(telaConexao, "TELA_CONEXAO");
        painelGerenciador.add(telaMenu, "TELA_MENU");
        painelGerenciador.add(telaChat, "Chat Geral");
        //painelGerenciador.add(telaIsis, "Isis");
        //painelGerenciador.add(telaLevi, "Levi");

        janela.getRootPane().setDefaultButton(btnConectar);

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
                        janela.getRootPane().setDefaultButton(btnEntrar);
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
                    if (socket == null || socket.isClosed()) {
                        socket = new Socket(ipServidor, portaServer);
                    }

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
                        cardLayout.show(painelGerenciador, "Chat Geral");
                        janela.getRootPane().setDefaultButton(botaoEnviarDeCadaChat.get("Chat Geral"));
                        leitorDeMensagem = new LeitorMensagensRecebidas(bufferReader);
                        leitorDeMensagem.start();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        btnAleatorio.addActionListener(e ->{
            String nome = gerarNickAleatorio();
            txtNome.setText(nome);
            lblPlaceholderNome.setVisible(false);
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

        try {
            semaforoMapPanels.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PanelsMensagemTelas.put(titulo, painelMensagens);
        semaforoMapPanels.release();

        JPanel wrapperMensagens = new JPanel(new BorderLayout());
        wrapperMensagens.setOpaque(false);
        // O BorderLayout.NORTH é o que força as mensagens a ficarem espremidas em cima!
        wrapperMensagens.add(painelMensagens, BorderLayout.NORTH);

        // Scroll para as mensagens da conversa
        JScrollPane scrollChat = new JScrollPane(wrapperMensagens);
        scrollChat.setBounds(186, 65, 612, 600);
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
        btnChatLateral.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnChatLateral.setFocusPainted(false);
        if(titulo.equals("Chat Geral")){
            btnChatLateral.setBackground(Color.decode("#b2bdff"));
        } else{
            btnChatLateral.setBackground(Color.decode("#458c98"));
        }
        
        botoesDaBarraLateral.putIfAbsent("Chat Geral", new ArrayList<>());
        botoesDaBarraLateral.get("Chat Geral").add(btnChatLateral);

        btnChatLateral.addActionListener(e ->{
            cardLayout.show(painelGerenciador, "Chat Geral");
            janela.getRootPane().setDefaultButton(botaoEnviarDeCadaChat.get("Chat Geral")); // Arruma o Enter
            
            limparNotificacao("Chat Geral");
        });
        
        //Demais botoes pros chats privados antigos
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
            btnAntigo.setFont(new Font("SansSerif", Font.PLAIN, 17));
            btnAntigo.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            botoesDaBarraLateral.putIfAbsent(nomeAntigo, new ArrayList<>());
            botoesDaBarraLateral.get(nomeAntigo).add(btnAntigo);
            
            // Restaura o visual da notificação se a tela for recriada
            int unread = mensagensNaoLidas.getOrDefault(nomeAntigo, 0);
            if(unread > 0 && !chatAtivo.equals(nomeAntigo)) {
                btnAntigo.setText(nomeAntigo + " (" + unread + ")");
            }
            
            btnAntigo.addActionListener(e -> {
                cardLayout.show(painelGerenciador, nomeAntigo);
                janela.getRootPane().setDefaultButton(botaoEnviarDeCadaChat.get(nomeAntigo)); // Arruma o Enter
                
                limparNotificacao(nomeAntigo);
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
                
                try {

                    if(titulo.equals("Chat Geral")){
                        MensagemPanel novaMsg = new MensagemPanel(mensagem);
                        SwingUtilities.invokeLater(() -> {
                            painelMensagens.add(novaMsg);
                        });
                        scrollarPraBaixo(painelMensagens);
                        txtMensagem.setText("");
                        if(mensagem.equalsIgnoreCase("/listar")){
                            bufferWriter.write("LISTAR_USUARIOS");
                        }else if(mensagem.equalsIgnoreCase("/ajuda")){
                            bufferWriter.write("AJUDA");
                        }else if(mensagem.equalsIgnoreCase("/sair")){
                            bufferWriter.write("SAIR");
                            leitorDeMensagem.desligar();
                            cardLayout.show(painelGerenciador, "TELA_CONEXAO");
                        }else if(mensagem.charAt(0)=='@'){
                            bufferWriter.write("PRIVADA|"+meuNome+"|"+ mensagem.trim().split(" ")[0].substring(1) +"|"+mensagem); //tripla
                        }else{
                            bufferWriter.write("CHAT GERAL|"+meuNome+"|"+mensagem); //tripla
                        }
                    }else{
                        txtMensagem.setText("");
                        bufferWriter.write("PRIVADA|"+meuNome+"|"+titulo+"|"+mensagem);
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
        botaoEnviarDeCadaChat.put(titulo, btnEnviar);

        return telaChat;
    }

    // Novo método para adicionar botões de chat privado
    public static void adicionarNovoChatNaLateral(String tituloChat) {
        // Para cada painel lateral existente no nosso jogo, criamos um botão novo!
        JButton btnNovo=null;
        for (JPanel painel : todosPaineisLaterais) {
            btnNovo = new JButton(tituloChat);
            btnNovo.setPreferredSize(new Dimension(141, 45));
            btnNovo.setMaximumSize(new Dimension(141, 45)); 
            
            btnNovo.setBackground(Color.decode("#458c98"));
            btnNovo.setOpaque(true);
            btnNovo.setFocusPainted(false);
            btnNovo.setFont(new Font("SansSerif", Font.PLAIN, 17));
            btnNovo.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            botoesDaBarraLateral.putIfAbsent(tituloChat, new ArrayList<>());
            botoesDaBarraLateral.get(tituloChat).add(btnNovo);

            btnNovo.addActionListener(e -> { 
                cardLayout.show(painelGerenciador, tituloChat);
                janela.getRootPane().setDefaultButton(botaoEnviarDeCadaChat.get(tituloChat)); // Arruma o Enter
                
                limparNotificacao(tituloChat);
            });
            
            painel.add(btnNovo);
            painel.add(Box.createRigidArea(new Dimension(0, 10))); 
            
            painel.revalidate();
            painel.repaint();
        }
        botoesLaterais.add(tituloChat);
        if(btnNovo != null) btnNovo.setBackground(Color.decode("#b2bdff"));
    }

    private static void scrollarPraBaixo(JPanel panel){
        final JPanel painelFinal = panel; 
    
        SwingUtilities.invokeLater(() -> {
            // 1. Pede para o Java recalcular o tamanho da tela
            painelFinal.revalidate();
            painelFinal.repaint();
            
            // 2. Coloca a rolagem no FINAL da fila de tarefas do Swing
            SwingUtilities.invokeLater(() -> {
                JScrollPane scroll = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, painelFinal);
                if (scroll != null) {
                    JScrollBar barraVertical = scroll.getVerticalScrollBar();
                    barraVertical.setValue(barraVertical.getMaximum());
                }
            });
        });
    }

    private static String gerarNickAleatorio() {
        // Encontros vocalicos e vogais simples
        String[] vogais = {"a", "e", "i", "o", "u", "ia", "ou", "io", "ei", "au"};
        
        // Consoantes simples e encontros consonantais muito comuns em PT/EN
        String[] consoantes = {
            "b", "c", "d", "f", "g", "j", "k", "l", "m", "n", "p", "r", "s", "t", "v", "z", 
            "br", "cr", "dr", "fr", "gr", "pr", "tr", "vr", 
            "bl", "cl", "fl", "gl", "pl", 
            "ch", "nh", "lh", "sh", "st", "th"
        };
        
        java.util.Random random = new java.util.Random();
        
        // Define que o nome tera entre 3 e 5 "partes" (ex: C-V-C-V)
        int numPartes = random.nextInt(3) + 3; 
        StringBuilder nick = new StringBuilder();
        
        // Sorteia se o nome vai começar com vogal (true) ou consoante (false)
        boolean usarVogal = random.nextBoolean();
        
        for (int i = 0; i < numPartes; i++) {
            if (usarVogal) {
                nick.append(vogais[random.nextInt(vogais.length)]);
            } else {
                nick.append(consoantes[random.nextInt(consoantes.length)]);
            }
            // Alterna para o proximo pedaço! Se foi vogal, agora eh consoante, e vice-versa.
            usarVogal = !usarVogal; 
        }
        
        // (Opcional) Adiciona um numero aleatório no final
        if (random.nextBoolean()) {
            nick.append(random.nextInt(100));
        }
        
        // Coloca a primeira letra em maiusculo para ficar bonito na tela
        String resultado = nick.toString();
        return resultado.substring(0, 1).toUpperCase() + resultado.substring(1);
    }

    private static void limparNotificacao(String nomeChat) {
        chatAtivo = nomeChat;
        mensagensNaoLidas.put(nomeChat, 0);
        
        java.util.List<JButton> botoes = botoesDaBarraLateral.get(nomeChat);
        if (botoes != null) {
            for (JButton b : botoes) {
                b.setText(nomeChat); // Remove o "(1)" de todas as telas
            }
        }
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

                    if(msg != null){
                        final Mensagem novaMensagem = new Mensagem(msg);
                        if(novaMensagem.getRemetente().equals(meuNome)) continue;
                        
                        SwingUtilities.invokeLater(() -> {
                            try {
                                if(novaMensagem.getRemetente().equals("Servidor")) {
                                    if(novaMensagem.getTipo().equals("PRIVADA") && 
                                    !novaMensagem.getConteudo().equals("Usuario não encontrado")){
                                        semaforoMapPanels.acquire();
                                        JPanel panel = PanelsMensagemTelas.get(novaMensagem.getDestino());
                                        semaforoMapPanels.release();
                                        if(panel==null){
                                            PainelComFundo novaTela = criarTelaChat("imagens/TelaChat.png", novaMensagem.getDestino());
                                            painelGerenciador.add(novaTela, novaMensagem.getDestino());
                                            cardLayout.show(painelGerenciador, novaMensagem.getDestino());
                                            semaforoMapPanels.acquire();
                                            panel = PanelsMensagemTelas.get(novaMensagem.getDestino());
                                            semaforoMapPanels.release();
                                        }

                                        MensagemPanel novaMensagemPanel = new MensagemPanel(novaMensagem.getConteudo());
                                        panel.add(novaMensagemPanel);
                                        scrollarPraBaixo(panel);
                                        atualizarNotificacao(novaMensagem.getRemetente());
                                    }else{
                                        MensagemPanel novaMensagemPanel = new MensagemPanel(novaMensagem.getConteudo(), true);
                                        semaforoMapPanels.acquire();
                                        JPanel panel = PanelsMensagemTelas.get("Chat Geral");
                                        semaforoMapPanels.release();
                                        panel.add(novaMensagemPanel);
                                        scrollarPraBaixo(panel);
                                        atualizarNotificacao("Chat Geral");
                                    }
                                }
                                else if(novaMensagem.getTipo().equals("CHAT GERAL")){
                                    MensagemPanel novaMensagemPanel = new MensagemPanel( novaMensagem.getRemetente(), novaMensagem.getConteudo());
                                    semaforoMapPanels.acquire();
                                    JPanel panel = PanelsMensagemTelas.get("Chat Geral");
                                    semaforoMapPanels.release();
                                    panel.add(novaMensagemPanel);
                                    scrollarPraBaixo(panel);
                                    atualizarNotificacao("Chat Geral");
                                    
                                }
                                else if(novaMensagem.getTipo().equals("PRIVADA")){

                                    semaforoMapPanels.acquire();
                                    JPanel panel = PanelsMensagemTelas.get(novaMensagem.getRemetente());
                                    semaforoMapPanels.release();
                                    if(panel==null){
                                        //System.out.println("É null coitado");
                                        PainelComFundo novaTela = criarTelaChat("imagens/TelaChat.png", novaMensagem.getRemetente());
                                        painelGerenciador.add(novaTela, novaMensagem.getRemetente());
                                        //cardLayout.show(painelGerenciador, novaMensagem.getConteudo());
                                        semaforoMapPanels.acquire();
                                        panel = PanelsMensagemTelas.get(novaMensagem.getRemetente());
                                        semaforoMapPanels.release();
                                    }

                                    MensagemPanel novaMensagemPanel = new MensagemPanel( novaMensagem.getRemetente(), novaMensagem.getConteudo());
                                    panel.add(novaMensagemPanel);
                                    scrollarPraBaixo(panel);
                                    atualizarNotificacao(novaMensagem.getRemetente());
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                    sleep(300);    //pra nao ficar lendo toda hora e ocupando a cpu

                }catch(Exception e){
                    e.printStackTrace();
                    desligar();
                    SwingUtilities.invokeLater(() -> cardLayout.show(painelGerenciador, "TELA_CONEXAO"));
                    //btnConectar.doClick();
                }
            }
        }

        public void desligar(){
            ativo=false;
        }

        private static void scrollarPraBaixo(JPanel panel){
            final JPanel painelFinal = panel; 
        
            SwingUtilities.invokeLater(() -> {
                // 1. Pede para o Java recalcular o tamanho da tela
                painelFinal.revalidate();
                painelFinal.repaint();
                
                // 2. Coloca a rolagem no FINAL da fila de tarefas do Swing
                SwingUtilities.invokeLater(() -> {
                    JScrollPane scroll = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, painelFinal);
                    if (scroll != null) {
                        JScrollBar barraVertical = scroll.getVerticalScrollBar();
                        barraVertical.setValue(barraVertical.getMaximum());
                    }
                });
            });
        }

        private void atualizarNotificacao(String nomeChat) {
        if (!chatAtivo.equals(nomeChat)) {
            int contagem = mensagensNaoLidas.getOrDefault(nomeChat, 0) + 1;
            mensagensNaoLidas.put(nomeChat, contagem);
            
            // MUDOU AQUI: Puxa a lista e atualiza todos
            java.util.List<JButton> botoes = botoesDaBarraLateral.get(nomeChat);
            if (botoes != null) {
                for (JButton btn : botoes) {
                    btn.setText(nomeChat + " (" + contagem + ")");
                }
            }
        }
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