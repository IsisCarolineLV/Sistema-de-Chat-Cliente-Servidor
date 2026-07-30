//fiz a interface no scene builder, mas como Javafx eh uma bomba nos pcs com java 22
//o gemini sugeriu fazer em swing, entao eu pedi pra ela adaptar o arquivo fxml que eu fiz
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
        
        // Define o tamanho exato tirando as bordas do sistema operacional
        janela.getContentPane().setPreferredSize(new Dimension(810, 810));
        janela.pack(); 
        janela.setResizable(false);

        // O CardLayout é o responsável por alternar as telas
        CardLayout cardLayout = new CardLayout();
        JPanel painelGerenciador = new JPanel(cardLayout);

        // ==========================================
        // TELA 1: MENU PRINCIPAL (paneMenu)
        // ==========================================
        PainelComFundo telaMenu = new PainelComFundo("chatCliente/imagens/Menu.png");
        telaMenu.setLayout(null); // Permite usar coordenadas absolutas como no AnchorPane

        // Label do Placeholder "Digite seu nome aqui..."
        JLabel lblPlaceholderNome = new JLabel("Digite seu nome aqui...");
        lblPlaceholderNome.setBounds(225, 562, 351, 49);
        lblPlaceholderNome.setForeground(Color.decode("#88adb3"));
        lblPlaceholderNome.setFont(new Font("SansSerif", Font.PLAIN, 31));

        // Campo de Texto (Nome)
        JTextField txtNome = new JTextField();
        txtNome.setBounds(156, 543, 481, 85);
        txtNome.setOpaque(false); // opacity = 0
        txtNome.setBorder(null);  // Remove borda nativa
        txtNome.setFont(new Font("SansSerif", Font.PLAIN, 31));
        txtNome.setForeground(Color.WHITE); // Cor do texto digitado
        txtNome.setCaretColor(Color.WHITE); // Cor do cursor piscando

        // Lógica para esconder o placeholder quando o usuário clica no campo
        txtNome.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { lblPlaceholderNome.setVisible(false); }
            public void focusLost(FocusEvent e) { if(txtNome.getText().isEmpty()) lblPlaceholderNome.setVisible(true); }
        });

        // Botão Aleatório
        JButton btnAleatorio = new JButton();
        btnAleatorio.setBounds(179, 521, 45, 43);
        configurarBotaoInvisivel(btnAleatorio);

        // Botão Entrar
        JButton btnEntrar = new JButton();
        btnEntrar.setBounds(246, 653, 312, 64);
        configurarBotaoInvisivel(btnEntrar);

        // Adiciona os componentes na tela Menu
        telaMenu.add(txtNome);
        telaMenu.add(lblPlaceholderNome); // Adiciona DEPOIS do texto para ficar no fundo
        telaMenu.add(btnAleatorio);
        telaMenu.add(btnEntrar);


        // ==========================================
        // TELA 2: CHAT (paneChat)
        // ==========================================
        PainelComFundo telaChat = new PainelComFundo("chatCliente/imagens/TelaChat.png");
        telaChat.setLayout(null);

        // Área de histórico (ScrollPane e TextArea)
        JTextArea areaMensagens = new JTextArea();
        areaMensagens.setEditable(false);
        areaMensagens.setOpaque(false);
        areaMensagens.setFont(new Font("SansSerif", Font.PLAIN, 18));
        areaMensagens.setForeground(Color.WHITE);
        areaMensagens.setLineWrap(true);

        JScrollPane scrollChat = new JScrollPane(areaMensagens);
        scrollChat.setBounds(186, 28, 612, 680);
        scrollChat.setOpaque(false);
        scrollChat.getViewport().setOpaque(false); // Transparência interna
        scrollChat.setBorder(null);
        
        // Label do Placeholder "Digite aqui..."
        JLabel lblPlaceholderMsg = new JLabel("Digite aqui...");
        lblPlaceholderMsg.setBounds(219, 735, 471, 49);
        lblPlaceholderMsg.setForeground(Color.decode("#88adb3"));
        lblPlaceholderMsg.setFont(new Font("SansSerif", Font.PLAIN, 31));

        // Campo de Texto (Mensagem)
        JTextField txtMensagem = new JTextField();
        txtMensagem.setBounds(200, 722, 496, 75);
        txtMensagem.setOpaque(false); // opacity = 0
        txtMensagem.setBorder(null);
        txtMensagem.setFont(new Font("SansSerif", Font.PLAIN, 24));
        txtMensagem.setForeground(Color.WHITE);
        txtMensagem.setCaretColor(Color.WHITE);

        txtMensagem.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { lblPlaceholderMsg.setVisible(false); }
            public void focusLost(FocusEvent e) { if(txtMensagem.getText().isEmpty()) lblPlaceholderMsg.setVisible(true); }
        });

        // Botão Enviar
        JButton btnEnviar = new JButton();
        btnEnviar.setBounds(704, 729, 65, 56);
        configurarBotaoInvisivel(btnEnviar);

        // Label do Topo (Chat Geral)
        JLabel lblTituloChat = new JLabel("Chat Geral", SwingConstants.CENTER);
        lblTituloChat.setBounds(384, 14, 157, 45);
        lblTituloChat.setFont(new Font("SansSerif", Font.PLAIN, 31));
        lblTituloChat.setForeground(Color.WHITE);

        // Botão do Menu Lateral (Este tinha cor no FXML, então não é invisível)
        JButton btnChatLateral = new JButton("Chat Geral");
        // Posição x=-4 + layoutX=18 e y=-2 + layoutY=50
        btnChatLateral.setBounds(14, 48, 141, 45); 
        btnChatLateral.setBackground(Color.decode("#b2bdff"));
        btnChatLateral.setFont(new Font("SansSerif", Font.PLAIN, 20));
        btnChatLateral.setFocusPainted(false); // Tira a bordinha de seleção

        // Adiciona tudo na tela de Chat
        telaChat.add(scrollChat);
        telaChat.add(txtMensagem);
        telaChat.add(lblPlaceholderMsg);
        telaChat.add(btnEnviar);
        telaChat.add(lblTituloChat);
        telaChat.add(btnChatLateral);

        // ==========================================
        // ADICIONA AS TELAS NO GERENCIADOR
        // ==========================================
        painelGerenciador.add(telaMenu, "TELA_MENU");
        painelGerenciador.add(telaChat, "TELA_CHAT");

        // AÇÃO PARA MUDAR DE TELA
        btnEntrar.addActionListener(e -> {
            String nome = txtNome.getText();
            if (!nome.trim().isEmpty()) {
                // Aqui você vai disparar a conexão do Socket!
                
                // Muda para a tela de chat
                cardLayout.show(painelGerenciador, "TELA_CHAT");
            }
        });

        // Configuração final
        janela.add(painelGerenciador);
        janela.setLocationRelativeTo(null); // Centraliza no monitor
        janela.setVisible(true);
    }

    // Método auxiliar para criar os "botões falsos" com opacity=0
    private static void configurarBotaoInvisivel(JButton botao) {
        botao.setOpaque(false);
        botao.setContentAreaFilled(false);
        botao.setBorderPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Cursor de mãozinha
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