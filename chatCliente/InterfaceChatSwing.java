//fiz a interiniciando a interfaceface no scene builder, mas como Javafx eh uma bomba nos pcs com java 22
//a gemini sugeriu fazer em swing, entao eu pedi pra ela adaptar o arquivo fxml que eu fiz
//para swing, assim roda, teoricamente em qualquer pc independente da versao do java

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;

public class InterfaceChatSwing {

    public static ArrayList<String> botoesLaterais = new ArrayList<>();
    public static java.util.List<JPanel> todosPaineisLaterais = new java.util.ArrayList<>();
    private static CardLayout cardLayout=null;
    private static JPanel painelGerenciador=null;

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
        lblPlaceholderIp.setBounds(151, 224, 351, 49);
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
        PainelComFundo telaIsis = criarTelaChat("chatCliente/imagens/TelaChat.png", "Isis");
        PainelComFundo telaOlavo = criarTelaChat("chatCliente/imagens/TelaChat.png", "Olavo");

        //Adiciona as telas no gerenciador:

        //A primeira tela adicionada é a que aparece por padrão
        painelGerenciador.add(telaConexao, "TELA_CONEXAO");
        painelGerenciador.add(telaMenu, "TELA_MENU");
        painelGerenciador.add(telaChat, "TELA_CHAT");
        painelGerenciador.add(telaIsis, "Isis");
        painelGerenciador.add(telaOlavo, "Olavo");

        //Mudanca de tela:
        // Conexao -> Nome
        btnConectar.addActionListener(e -> {
            String ip = txtIp.getText();
            String porta = txtPorta.getText();
            
            // Aqui você futuramente adiciona a lógica do "new Socket(ip, porta)"
            if (!ip.trim().isEmpty() && !porta.trim().isEmpty()) {
                cardLayout.show(painelGerenciador, "TELA_MENU");
            }
        });

        // Nome -> Chat Geral
        btnEntrar.addActionListener(e -> {
            String nome = txtNome.getText();
            if (!nome.trim().isEmpty()) {
                // Aqui você enviará o nome do usuário para o servidor via Socket
                cardLayout.show(painelGerenciador, "TELA_CHAT");
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
        campo.setForeground(Color.WHITE); 
        campo.setCaretColor(Color.BLACK); 
        
        campo.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { placeholder.setVisible(false); }
            public void focusLost(FocusEvent e) { if(campo.getText().isEmpty()) placeholder.setVisible(true); }
        });
    }

    //Cria Tela de Chat
    private static PainelComFundo criarTelaChat(String caminho, String titulo){
        PainelComFundo telaChat = new PainelComFundo(caminho);
        telaChat.setLayout(null);

        //acho que isso vai sair...
        JTextArea areaMensagens = new JTextArea();
        areaMensagens.setEditable(false);
        areaMensagens.setOpaque(false);
        areaMensagens.setFont(new Font("SansSerif", Font.PLAIN, 18));
        areaMensagens.setForeground(Color.WHITE);
        areaMensagens.setLineWrap(true);

        //Scroll para as mensagens da conversa
        JScrollPane scrollChat = new JScrollPane(areaMensagens);
        scrollChat.setBounds(186, 28, 612, 680);
        scrollChat.setOpaque(false);
        scrollChat.getViewport().setOpaque(false); 
        scrollChat.setBorder(null);
        
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