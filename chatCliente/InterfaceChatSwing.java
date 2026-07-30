//fiz a interface no scene builder, mas como Javafx eh uma bomba nos pcs com java 22
//a geminina sugeriu fazer em swing, entao eu pedi pra ela adaptar o arquivo fxml que eu fiz
//para swing, assim roda, teoricamente em qualquer pc independente da versao do java

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class InterfaceChatSwing {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> criarTela());
    }

    private static void criarTela() {
        // Configuração da janela principal com base nas dimensões do AnchorPane (810x810)
        JFrame janela = new JFrame("Sistema de Chat");
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        janela.getContentPane().setPreferredSize(new Dimension(810, 810));
        janela.pack(); 
        janela.setResizable(false); // Trava o redimensionamento

        CardLayout cardLayout = new CardLayout();
        JPanel painelGerenciador = new JPanel(cardLayout);

        // ==========================================
        // TELA 1: CONEXÃO (paneMenu1)
        // ==========================================
        PainelComFundo telaConexao = new PainelComFundo("chatCliente/imagens/telaConexao.png");
        telaConexao.setLayout(null);

        // Placeholder e Campo do IP
        JLabel lblPlaceholderIp = new JLabel("Ex: 10.1.1.1");
        lblPlaceholderIp.setBounds(151, 224, 351, 49);
        lblPlaceholderIp.setForeground(Color.decode("#88adb3"));
        lblPlaceholderIp.setFont(new Font("SansSerif", Font.PLAIN, 31));

        JTextField txtIp = new JTextField();
        txtIp.setBounds(151, 206, 532, 85);
        configurarCampoInvisivel(txtIp, lblPlaceholderIp);

        // Placeholder e Campo da Porta
        JLabel lblPlaceholderPorta = new JLabel("Ex: 1234");
        lblPlaceholderPorta.setBounds(151, 391, 351, 49);
        lblPlaceholderPorta.setForeground(Color.decode("#88adb3"));
        lblPlaceholderPorta.setFont(new Font("SansSerif", Font.PLAIN, 31));

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

        // ==========================================
        // TELA 2: MENU PRINCIPAL (paneMenu)
        // ==========================================
        PainelComFundo telaMenu = new PainelComFundo("chatCliente/imagens/Menu.png");
        telaMenu.setLayout(null); 

        JLabel lblPlaceholderNome = new JLabel("Digite seu nome aqui...");
        lblPlaceholderNome.setBounds(225, 562, 351, 49);
        lblPlaceholderNome.setForeground(Color.decode("#88adb3"));
        lblPlaceholderNome.setFont(new Font("SansSerif", Font.PLAIN, 31));

        JTextField txtNome = new JTextField();
        txtNome.setBounds(156, 543, 481, 85);
        configurarCampoInvisivel(txtNome, lblPlaceholderNome);

        JButton btnAleatorio = new JButton();
        btnAleatorio.setBounds(179, 521, 45, 43);
        configurarBotaoInvisivel(btnAleatorio);

        JButton btnEntrar = new JButton();
        btnEntrar.setBounds(246, 653, 312, 64);
        configurarBotaoInvisivel(btnEntrar);

        telaMenu.add(txtNome);
        telaMenu.add(lblPlaceholderNome); 
        telaMenu.add(btnAleatorio);
        telaMenu.add(btnEntrar);

        // ==========================================
        // TELA 3: CHAT (paneChat)
        // ==========================================
        PainelComFundo telaChat = new PainelComFundo("chatCliente/imagens/TelaChat.png");
        telaChat.setLayout(null);

        JTextArea areaMensagens = new JTextArea();
        areaMensagens.setEditable(false);
        areaMensagens.setOpaque(false);
        areaMensagens.setFont(new Font("SansSerif", Font.PLAIN, 18));
        areaMensagens.setForeground(Color.WHITE);
        areaMensagens.setLineWrap(true);

        JScrollPane scrollChat = new JScrollPane(areaMensagens);
        scrollChat.setBounds(186, 28, 612, 680);
        scrollChat.setOpaque(false);
        scrollChat.getViewport().setOpaque(false); 
        scrollChat.setBorder(null);
        
        JLabel lblPlaceholderMsg = new JLabel("Digite aqui...");
        lblPlaceholderMsg.setBounds(219, 735, 471, 49);
        lblPlaceholderMsg.setForeground(Color.decode("#88adb3"));
        lblPlaceholderMsg.setFont(new Font("SansSerif", Font.PLAIN, 31));

        JTextField txtMensagem = new JTextField();
        txtMensagem.setBounds(219, 735, 471, 49);
        configurarCampoInvisivel(txtMensagem, lblPlaceholderMsg);
        txtMensagem.setFont(new Font("SansSerif", Font.PLAIN, 31)); // Reduzindo um pouco a fonte da msg

        JButton btnEnviar = new JButton();
        btnEnviar.setBounds(704, 729, 65, 56);
        configurarBotaoInvisivel(btnEnviar);

        JLabel lblTituloChat = new JLabel("Chat Geral", SwingConstants.CENTER);
        lblTituloChat.setBounds(384, 14, 157, 45);
        lblTituloChat.setFont(new Font("SansSerif", Font.PLAIN, 31));
        lblTituloChat.setForeground(Color.WHITE);

        JButton btnChatLateral = new JButton("Chat Geral");
        btnChatLateral.setBounds(14, 48, 141, 45); 
        btnChatLateral.setBackground(Color.decode("#b2bdff"));
        btnChatLateral.setFont(new Font("SansSerif", Font.PLAIN, 20));
        btnChatLateral.setFocusPainted(false);

        telaChat.add(scrollChat);
        telaChat.add(txtMensagem);
        telaChat.add(lblPlaceholderMsg);
        telaChat.add(btnEnviar);
        telaChat.add(lblTituloChat);
        telaChat.add(btnChatLateral);

        // ==========================================
        // ADICIONA AS TELAS NO GERENCIADOR
        // ==========================================
        // A primeira tela adicionada é a que aparece por padrão
        painelGerenciador.add(telaConexao, "TELA_CONEXAO");
        painelGerenciador.add(telaMenu, "TELA_MENU");
        painelGerenciador.add(telaChat, "TELA_CHAT");

        // ==========================================
        // AÇÕES DE MUDANÇA DE TELA
        // ==========================================
        
        // Ação da Tela 1 -> Tela 2
        btnConectar.addActionListener(e -> {
            String ip = txtIp.getText();
            String porta = txtPorta.getText();
            
            // Aqui você futuramente adiciona a lógica do "new Socket(ip, porta)"
            if (!ip.trim().isEmpty() && !porta.trim().isEmpty()) {
                cardLayout.show(painelGerenciador, "TELA_MENU");
            }
        });

        // Ação da Tela 2 -> Tela 3
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

    // Método auxiliar para criar os "botões falsos" com opacity=0
    private static void configurarBotaoInvisivel(JButton botao) {
        botao.setOpaque(false);
        botao.setContentAreaFilled(false);
        botao.setBorderPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
    }
    
    // Método auxiliar para configurar campos transparentes com comportamento de Placeholder
    private static void configurarCampoInvisivel(JTextField campo, JLabel placeholder) {
        campo.setOpaque(false); 
        campo.setBorder(null);  
        campo.setFont(new Font("SansSerif", Font.PLAIN, 31));
        campo.setForeground(Color.WHITE); 
        campo.setCaretColor(Color.WHITE); 
        
        campo.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { placeholder.setVisible(false); }
            public void focusLost(FocusEvent e) { if(campo.getText().isEmpty()) placeholder.setVisible(true); }
        });
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