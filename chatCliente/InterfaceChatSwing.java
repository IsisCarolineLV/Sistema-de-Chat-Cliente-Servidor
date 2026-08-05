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
import java.nio.charset.StandardCharsets;
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
    private static Map<String, MensagemPanel> divisoresChat = new HashMap<>();

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
    private static int tamanho = 810;

    // Controle do fluxo de login (nome + senha)
    private static boolean aguardandoSenha = false;
    private static String nomeAtual = null;
    private static JTextField txtNome;
    private static JPasswordField txtSenha;

    private static JButton btnConectar;

    private static Semaphore semaforoMapPanels = new Semaphore(1);

    //Scanner scan = new Scanner(System.in); //scan jogado pra junto das variaveis


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> criarTela());
    }

    private static void criarTela() {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // Configuração da janela principal 
        janela = new JFrame("Chat Maneiro");
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        
        if(tamanho>screenSize.height){
            tamanho = screenSize.height-100;
        }

        janela.getContentPane().setPreferredSize(new Dimension(tamanho, tamanho));
        janela.pack(); 
        janela.setResizable(false); // Trava o redimensionamento

        //CardLayout que trata cada pane nele como uma carta, ajudando a trocar o pane visivel facilmente
        cardLayout = new CardLayout();
        painelGerenciador = new JPanel(cardLayout);

        /////////////////////////////////////////////////////
        // TELA DE CONEXAO
        //PainelComFundo telaConexao = new PainelComFundo("chatCliente/imagens/telaConexao.png");
        PainelComFundo telaConexao = new PainelComFundo("imagens/telaConexao.png", tamanho);
        telaConexao.setLayout(null); 

        // Texto exemplo IP
        JLabel lblPlaceholderIp = new JLabel("Ex: 10.0.0.0");
        lblPlaceholderIp.setBounds(novaPosicao(151),novaPosicao( 224), novaPosicao(551),novaPosicao( 49));
        lblPlaceholderIp.setForeground(Color.decode("#88adb3"));
        lblPlaceholderIp.setFont(new Font("SansSerif", Font.PLAIN, 31));
        
        //Campo para Digitar o IP
        JTextField txtIp = new JTextField();
        txtIp.setBounds(novaPosicao(151), novaPosicao(206), novaPosicao(470), 85);
        configurarCampoInvisivel(txtIp, lblPlaceholderIp);

        // Texto exemplo porta
        JLabel lblPlaceholderPorta = new JLabel("Ex: 1234");
        lblPlaceholderPorta.setBounds(novaPosicao(151), novaPosicao(391), novaPosicao(351), novaPosicao(49));
        lblPlaceholderPorta.setForeground(Color.decode("#88adb3"));
        lblPlaceholderPorta.setFont(new Font("SansSerif", Font.PLAIN, 31));

        //Campo para Digitar a porta
        JTextField txtPorta = new JTextField();
        txtPorta.setBounds(novaPosicao(151), novaPosicao(373), novaPosicao(400),novaPosicao( 85));
        configurarCampoInvisivel(txtPorta, lblPlaceholderPorta);

        // Botão Conectar
        btnConectar = new JButton();
        btnConectar.setBounds(novaPosicao(248), novaPosicao(618), 312, 64);
        configurarBotaoInvisivel(btnConectar);

        telaConexao.add(txtIp);
        telaConexao.add(lblPlaceholderIp);
        telaConexao.add(txtPorta);
        telaConexao.add(lblPlaceholderPorta);
        telaConexao.add(btnConectar);

        /////////////////////////////////////////////////////
        // TELA DE DEFINICAO DO NOME E SENHA (Com configuração especifica aplicada)
        PainelComFundo telaMenu = new PainelComFundo("imagens/MenuComSenha.png", tamanho);
        telaMenu.setLayout(null); 

        //Placeholder indicando onde digitar
        JLabel lblPlaceholderNome = new JLabel("Digite seu nome");
        lblPlaceholderNome.setBounds(novaPosicao(225), novaPosicao(420), novaPosicao(351), 49);
        lblPlaceholderNome.setForeground(Color.decode("#88adb3"));
        lblPlaceholderNome.setFont(new Font("SansSerif", Font.PLAIN, 31));

        //Campo para Digitar o nome
        txtNome = new JTextField();
        txtNome.setBounds(novaPosicao(225),novaPosicao(400), novaPosicao(401), 85);
        txtNome.setDocument(new LimitadorTexto(30));
        configurarCampoInvisivel(txtNome, lblPlaceholderNome);

        //Botao nomeAleatorio
        JButton btnAleatorio = new JButton();
        btnAleatorio.setBounds(novaPosicao(179), novaPosicao(380), 45, 43);
        configurarBotaoInvisivel(btnAleatorio);

        // Texto exemplo senha
        JLabel lblPlaceholderSenha = new JLabel("Digite sua senha");
        lblPlaceholderSenha.setBounds(novaPosicao(225), novaPosicao(562), novaPosicao(331), novaPosicao(49));
        lblPlaceholderSenha.setForeground(Color.decode("#88adb3"));
        lblPlaceholderSenha.setFont(new Font("SansSerif", Font.PLAIN, 31));

        //Campo para Digitar a senha
        txtSenha = new JPasswordField();
        txtSenha.setDocument(new LimitadorTexto(100));
        txtSenha.setBounds(novaPosicao(225), novaPosicao(543), novaPosicao(401), novaPosicao(85));
        
        configurarCampoInvisivel(txtSenha, lblPlaceholderSenha);

        //Botao entrar
        JButton btnEntrar = new JButton();
        btnEntrar.setBounds(novaPosicao(246), novaPosicao(653), 312, 64);
        configurarBotaoInvisivel(btnEntrar);

        telaMenu.add(txtNome);
        telaMenu.add(lblPlaceholderNome); 
        telaMenu.add(btnAleatorio);
        telaMenu.add(txtSenha);
        telaMenu.add(lblPlaceholderSenha);
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
                        InputStreamReader inputLeitor = new InputStreamReader(socket.getInputStream());
                        BufferedReader bufferReader = new BufferedReader(inputLeitor);
                        String resposta = bufferReader.readLine();   //le a confirmacao
                        if(resposta.equals("Conexão aceita")){
                            cardLayout.show(painelGerenciador, "TELA_MENU");
                            janela.getRootPane().setDefaultButton(btnEntrar);
                        }else{
                            throw new Exception("Conexão negada");
                        }
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

            // Já mandamos o nome antes e o servidor está esperando só a senha de novo (retry)
            if(aguardandoSenha){
                String senha = new String(txtSenha.getPassword());
                if(senha.trim().isEmpty()){
                    lblPlaceholderSenha.setForeground(Color.RED);
                    lblPlaceholderSenha.setText("Senha obrigatória!");
                    lblPlaceholderSenha.setVisible(true);
                    janela.requestFocusInWindow();
                    return;
                }
                try {
                    bufferWriter.write(senha);
                    bufferWriter.newLine();
                    bufferWriter.flush();

                    String resultado = bufferReader.readLine();

                    if(resultado.equals("autenticado")){
                        aguardandoSenha = false;
                        txtNome.setEditable(true);
                        meuNome = nomeAtual;
                        cardLayout.show(painelGerenciador, "Chat Geral");
                        janela.getRootPane().setDefaultButton(botaoEnviarDeCadaChat.get("Chat Geral"));
                        leitorDeMensagem = new LeitorMensagensRecebidas(bufferReader);
                        leitorDeMensagem.start();
                    } else if(resultado.equals("senha_errada")){
                        txtSenha.setText("");
                        lblPlaceholderSenha.setForeground(Color.RED);
                        lblPlaceholderSenha.setText("Senha incorreta!");
                        lblPlaceholderSenha.setVisible(true);
                        janela.requestFocusInWindow();
                        // continua aguardandoSenha = true, servidor já está esperando outra tentativa
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return;
            }

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
                    outputEscritor = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);

                    bufferReader = new BufferedReader(inputLeitor);
                    bufferWriter = new BufferedWriter(outputEscritor);

                    bufferWriter.write(nome); //primeira mensagem eh o nome do cliente
                    bufferWriter.newLine(); 
                    bufferWriter.flush();

                    String resposta = bufferReader.readLine();

                    if(resposta.equals("Já existe um usuário com esse nome")){
                        
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
                    } else if(resposta.equals("novo_usuario")){
                        String senha = new String(txtSenha.getPassword());
                        if(senha.trim().isEmpty()){
                            lblPlaceholderSenha.setForeground(Color.RED);
                            lblPlaceholderSenha.setText("Cadastre uma senha!");
                            lblPlaceholderSenha.setVisible(true);
                            janela.requestFocusInWindow();
                            return;
                        }
                        bufferWriter.write(senha);
                        bufferWriter.newLine();
                        bufferWriter.flush();

                        if(bufferReader.readLine().equals("autenticado")){
                            meuNome = nome;
                            cardLayout.show(painelGerenciador, "Chat Geral");
                            janela.getRootPane().setDefaultButton(botaoEnviarDeCadaChat.get("Chat Geral"));
                            leitorDeMensagem = new LeitorMensagensRecebidas(bufferReader);
                            leitorDeMensagem.start();
                        }
                    } else if(resposta.equals("pedir_senha")){
                        String senha = new String(txtSenha.getPassword());
                        if(senha.trim().isEmpty()){
                            lblPlaceholderSenha.setForeground(Color.RED);
                            lblPlaceholderSenha.setText("Senha obrigatória!");
                            lblPlaceholderSenha.setVisible(true);
                            janela.requestFocusInWindow();
                            return;
                        }
                        bufferWriter.write(senha);
                        bufferWriter.newLine();
                        bufferWriter.flush();

                        String resultado = bufferReader.readLine();

                        if(resultado.equals("autenticado")){
                            meuNome = nome;
                            cardLayout.show(painelGerenciador, "Chat Geral");
                            janela.getRootPane().setDefaultButton(botaoEnviarDeCadaChat.get("Chat Geral"));
                            leitorDeMensagem = new LeitorMensagensRecebidas(bufferReader);
                            leitorDeMensagem.start();
                        } else if(resultado.equals("senha_errada")){
                            // NÃO fecha o socket, servidor já está esperando outra senha na mesma conexão
                            nomeAtual = nome;
                            aguardandoSenha = true;
                            txtNome.setEditable(false); // trava o campo nome pra não digitar de novo
                            txtSenha.setText("");
                            lblPlaceholderSenha.setForeground(Color.RED);
                            lblPlaceholderSenha.setText("Senha incorreta!");
                            lblPlaceholderSenha.setVisible(true);
                            janela.requestFocusInWindow();
                        }
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
        PainelComFundo telaChat = new PainelComFundo(caminho, tamanho);
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
        scrollChat.setBounds(novaPosicao(186), novaPosicao(65), novaPosicao(612),novaPosicao( 600));
        scrollChat.setOpaque(false);
        scrollChat.getViewport().setOpaque(false); 
        scrollChat.setBorder(null);
        scrollChat.getVerticalScrollBar().setUnitIncrement(16);
        scrollChat.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollChat.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        
        //Place holder indicando onde digitar a mensagem
        JLabel lblPlaceholderMsg = new JLabel("Digite aqui...");
        lblPlaceholderMsg.setBounds(novaPosicao(219), novaPosicao(735), 471, 49);
        lblPlaceholderMsg.setForeground(Color.decode("#88adb3"));
        lblPlaceholderMsg.setFont(new Font("SansSerif", Font.PLAIN, 31));

        //Campo para digitar uma mensagem
        JTextField txtMensagem = new JTextField();
        txtMensagem.setBounds(novaPosicao(219), novaPosicao(735), novaPosicao(471), novaPosicao(49));
        configurarCampoInvisivel(txtMensagem, lblPlaceholderMsg);
        txtMensagem.setFont(new Font("SansSerif", Font.PLAIN, 31)); // Reduzindo um pouco a fonte da msg
        txtMensagem.setDocument(new LimitadorTexto(100));

        //Botao enviar
        JButton btnEnviar = new JButton();
        btnEnviar.setBounds(novaPosicao(704), novaPosicao(729), 65, 56);
        configurarBotaoInvisivel(btnEnviar);

        //Titulo do chat
        JLabel lblTituloChat = new JLabel(titulo, SwingConstants.CENTER);
        lblTituloChat.setBounds(novaPosicao(334), novaPosicao(24), 257, 31);
        lblTituloChat.setFont(new Font("SansSerif", Font.PLAIN, 31));
        lblTituloChat.setForeground(Color.BLACK);

        //Botao do Chat Geral
        JButton btnChatLateral = new JButton("Chat Geral");
        btnChatLateral.setBounds(novaPosicao(14), novaPosicao(48), novaPosicao(141), novaPosicao(45)); 
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
            removerDivisor(chatAtivo);
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
            btnAntigo.setPreferredSize(new Dimension(novaPosicao(141), novaPosicao(45)));
            btnAntigo.setMaximumSize(new Dimension(novaPosicao(141),novaPosicao( 45))); 
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
                removerDivisor(chatAtivo);
                cardLayout.show(painelGerenciador, nomeAntigo);
                janela.getRootPane().setDefaultButton(botaoEnviarDeCadaChat.get(nomeAntigo)); // Arruma o Enter
                
                limparNotificacao(nomeAntigo);
            });

            painelLateral.add(btnAntigo);
            painelLateral.add(Box.createRigidArea(new Dimension(0, novaPosicao(10))));
        }
        
        todosPaineisLaterais.add(painelLateral);

        //adicionando botao da tela privada
        if(!titulo.equals("Chat Geral")){
            adicionarNovoChatNaLateral(titulo);
        }

        //Scroll Lateral
        
        JScrollPane scrollLateral = new JScrollPane(painelLateral);
        scrollLateral.setBounds(novaPosicao(14), novaPosicao(200), novaPosicao(160), novaPosicao( 600));
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
                        /*MensagemPanel novaMsg = new MensagemPanel(mensagem);
                        SwingUtilities.invokeLater(() -> {
                            painelMensagens.add(novaMsg);
                        });
                        scrollarPraBaixo(painelMensagens);*/
                        txtMensagem.setText("");
                        if(mensagem.equalsIgnoreCase("/listar")){
                            bufferWriter.write("LISTAR_USUARIOS");
                        }else if(mensagem.equalsIgnoreCase("/ajuda")){
                            bufferWriter.write("AJUDA");
                        }else if(mensagem.equalsIgnoreCase("/sair")){
                            bufferWriter.write("SAIR");
                            leitorDeMensagem.desligar();
                            resetarChats();
                            cardLayout.show(painelGerenciador, "TELA_CONEXAO");
                        }else if(mensagem.charAt(0)=='@'){
                            bufferWriter.write("PRIVADA|"+meuNome+"|"+ mensagem.substring(1) +"|"+mensagem); //tripla
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
            btnNovo.setPreferredSize(new Dimension(novaPosicao(141), novaPosicao(45)));
            btnNovo.setMaximumSize(new Dimension(novaPosicao(141), novaPosicao(45))); 
            
            btnNovo.setBackground(Color.decode("#458c98"));
            btnNovo.setOpaque(true);
            btnNovo.setFocusPainted(false);
            btnNovo.setFont(new Font("SansSerif", Font.PLAIN, 17));
            btnNovo.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            botoesDaBarraLateral.putIfAbsent(tituloChat, new ArrayList<>());
            botoesDaBarraLateral.get(tituloChat).add(btnNovo);

            btnNovo.addActionListener(e -> { 
                removerDivisor(chatAtivo);
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

    private static int novaPosicao (int posicaoOriginal){
        return tamanho*(posicaoOriginal)/810;
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

    private static void injetarDivisorSeNecessario(String nomeChat, JPanel panel) {
        if (!chatAtivo.equals(nomeChat)) {
            int unread = mensagensNaoLidas.getOrDefault(nomeChat, 0);
            if (unread == 0) {
                // Se é a primeira mensagem, cria o balão de sistema
                MensagemPanel divisor = new MensagemPanel("1 mensagem não lida", true);
                divisoresChat.put(nomeChat, divisor);
                panel.add(divisor);
            } else {
                // Se já existe, pega o balão e só atualiza o texto (sem criar outro)
                MensagemPanel divisor = divisoresChat.get(nomeChat);
                if (divisor != null && divisor.getComponentCount() > 0) {
                    try {
                        // O JTextPane é o primeiro componente do painel de sistema
                        JTextPane txt = (JTextPane) divisor.getComponent(0);
                        txt.setText((unread + 1) + " mensagens não lidas");
                    } catch (Exception e) {}
                }
            }
        }
    }

    private static void removerDivisor(String nomeChat) {
        MensagemPanel divisor = divisoresChat.remove(nomeChat);
        if (divisor != null) {
            JPanel panel = PanelsMensagemTelas.get(nomeChat);
            if (panel != null) {
                panel.remove(divisor);
                panel.revalidate();
                panel.repaint();
            }
        }
    }

    private static void resetarChats() {
        // 1. Esvazia a tela de mensagens do Chat Geral
        JPanel painelGeral = PanelsMensagemTelas.get("Chat Geral");
        if (painelGeral != null) {
            painelGeral.removeAll();
            painelGeral.revalidate();
            painelGeral.repaint();
        }

        // 2. Destrói os botões de conversas privadas de todas as barras laterais
        for (JPanel painelLateral : todosPaineisLaterais) {
            painelLateral.removeAll();
            painelLateral.revalidate();
            painelLateral.repaint();
        }

        // 3. Limpa todas as referências da memória (preservando apenas o Chat Geral)
        botoesLaterais.clear();
        PanelsMensagemTelas.keySet().removeIf(k -> !k.equals("Chat Geral"));
        botaoEnviarDeCadaChat.keySet().removeIf(k -> !k.equals("Chat Geral"));
        botoesDaBarraLateral.keySet().removeIf(k -> !k.equals("Chat Geral"));
        mensagensNaoLidas.keySet().removeIf(k -> !k.equals("Chat Geral"));
        
        // Se você criou o mapa de divisores do WhatsApp, limpe ele também:
        if (divisoresChat != null) {
            divisoresChat.keySet().removeIf(k -> !k.equals("Chat Geral"));
        }
        
        // Reseta o estado de login pra próxima conexão pedir nome e senha do zero
        aguardandoSenha = false;
        nomeAtual = null;
        if(txtNome != null) txtNome.setEditable(true);
        if(txtSenha != null) txtSenha.setText("");

        chatAtivo = "Chat Geral";
    }
    

    //Thread pra ficar recebendo as mensagens
    private static class LeitorMensagensRecebidas extends Thread{
        private BufferedReader bufferReader = null;
        private boolean ativo = true;
        private boolean carregandoHistorico = true;

        public LeitorMensagensRecebidas (BufferedReader bf){
            this.setDaemon(true);
            bufferReader = bf;
        }
        public void run(){
            while(ativo){
                try{
                    String msg = bufferReader.readLine();

                    if (msg == null) {
                        throw new IOException("Servidor desconectado");
                    }

                    if(msg != null){
                        if (msg.equals("Servidor|FIM_HISTORICO")) {
                            carregandoHistorico = false; // Destrava as notificações!
                            continue; // Pula essa volta do laço para não imprimir isso na tela
                        }

                        final Mensagem novaMensagem = new Mensagem(msg);
                                                
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
                                            janela.getRootPane().setDefaultButton(botaoEnviarDeCadaChat.get(novaMensagem.getDestino()));
                                            semaforoMapPanels.acquire();
                                            panel = PanelsMensagemTelas.get(novaMensagem.getDestino());
                                            semaforoMapPanels.release();
                                        }

                                        MensagemPanel novaMensagemPanel = new MensagemPanel(novaMensagem.getConteudo());
                                        //injetarDivisorSeNecessario(novaMensagem.getRemetente(), panel);
                                        panel.add(novaMensagemPanel);
                                        scrollarPraBaixo(panel);
                                         if (!carregandoHistorico) atualizarNotificacao(novaMensagem.getRemetente());
                                    }else{
                                        MensagemPanel novaMensagemPanel = new MensagemPanel(novaMensagem.getConteudo(), true);
                                        semaforoMapPanels.acquire();
                                        JPanel panel = PanelsMensagemTelas.get("Chat Geral");
                                        semaforoMapPanels.release();
                                        panel.add(novaMensagemPanel);
                                        scrollarPraBaixo(panel);
                                         if (!carregandoHistorico)  atualizarNotificacao("Chat Geral");
                                    }
                                }
                                else if(novaMensagem.getTipo().equals("CHAT GERAL")){
                                    MensagemPanel novaMensagemPanel=null;
                                    if(novaMensagem.getRemetente().equals(meuNome)){
                                        novaMensagemPanel = new MensagemPanel(novaMensagem.getConteudo());
                                    }else{
                                        novaMensagemPanel = new MensagemPanel( novaMensagem.getRemetente(), novaMensagem.getConteudo());
                                    }
                                    semaforoMapPanels.acquire();
                                    JPanel panel = PanelsMensagemTelas.get("Chat Geral");
                                    semaforoMapPanels.release();
                                    if (!carregandoHistorico) injetarDivisorSeNecessario("Chat Geral", panel);
                                    panel.add(novaMensagemPanel);
                                    scrollarPraBaixo(panel);
                                    if (!carregandoHistorico) atualizarNotificacao("Chat Geral");
                                    
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
                                    if (!carregandoHistorico) injetarDivisorSeNecessario(novaMensagem.getRemetente(), panel);
                                    panel.add(novaMensagemPanel);
                                    scrollarPraBaixo(panel);
                                    if (!carregandoHistorico) atualizarNotificacao(novaMensagem.getRemetente());
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                        if(!novaMensagem.getRemetente().equals(meuNome)&&!carregandoHistorico){
                            sleep(100);    //pra nao ficar lendo toda hora e ocupando a cpu
                        }
                        
                    }
                    if(!carregandoHistorico)
                        sleep(100);
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
    private int tamanho;

    public PainelComFundo(String caminhoDaImagem, int tamanho) {
        try {
            // O pulo do gato: usar getResource para buscar na raiz do Classpath (pasta src)
            // A barra "/" no início garante que ele procure a partir da pasta principal do código
            java.net.URL imgUrl = getClass().getResource("/" + caminhoDaImagem);
            
            if (imgUrl != null) {
                this.imagemFundo = new ImageIcon(imgUrl).getImage();
            } else {
                System.err.println("Imagem não encontrada (verifique a pasta): /" + caminhoDaImagem);
            }
        } catch (Exception e) {
            System.err.println("Erro ao tentar ler a imagem: " + caminhoDaImagem);
            e.printStackTrace();
        }
        this.tamanho = tamanho;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagemFundo != null) {
            g.drawImage(imagemFundo, 0, 0, tamanho, tamanho, this);
        }
    }
}

class LimitadorTexto extends javax.swing.text.PlainDocument {
    private int limite;
    public LimitadorTexto(int limite) {
        this.limite = limite;
    }
    @Override
    public void insertString(int offset, String str, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
        if (str == null) return;
        if ((getLength() + str.length()) <= limite) {
        super.insertString(offset, str, attr);
    }
}
}