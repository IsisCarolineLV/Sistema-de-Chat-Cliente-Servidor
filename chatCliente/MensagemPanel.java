import javax.swing.*;
import java.awt.*;

public class MensagemPanel extends JPanel {

    public MensagemPanel(String nome, String mensagem) {
        // Fundo transparente para combinar com o fundo do seu chat
        setOpaque(false);
        
        // Empilha o Nome e a Mensagem um embaixo do outro
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // Espaçamento entre uma mensagem e outra (Cima, Esquerda, Baixo, Direita)
        setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15)); 

        JLabel lblNome = new JLabel(nome + ":");
        // Uma fonte em negrito e um pouco maior para destacar o remetente
        lblNome.setFont(new Font("SansSerif", Font.PLAIN, 23)); 
        lblNome.setForeground(Color.BLACK);
        lblNome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea txtMensagem = new JTextArea(mensagem);
        txtMensagem.setEditable(false);
        txtMensagem.setOpaque(false);
        txtMensagem.setLineWrap(true);
        txtMensagem.setWrapStyleWord(true);
        // Fonte regular e limpa para facilitar a leitura
        txtMensagem.setFont(new Font("SansSerif", Font.PLAIN, 20)); 
        txtMensagem.setForeground(Color.WHITE); // Texto em branco para contrastar com o fundo

        txtMensagem.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Pequena margem superior de 2 pixels para afastar a mensagem do nome
        txtMensagem.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0)); 


        // Adiciona os itens ao painel
        add(lblNome);
        add(txtMensagem);

        // Limita a largura para a mensagem quebrar de linha se for muito longa
        setMaximumSize(new Dimension(580, Integer.MAX_VALUE));
        
        setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    public MensagemPanel(String mensagem) {
        // Fundo transparente para combinar com o fundo do seu chat
        setOpaque(false);
        
        // Empilha o Nome e a Mensagem um embaixo do outro
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // Espaçamento entre uma mensagem e outra (Cima, Esquerda, Baixo, Direita)
        setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15)); 

        JLabel lblNome = new JLabel("Você:   ");
        // Uma fonte em negrito e um pouco maior para destacar o remetente
        lblNome.setFont(new Font("SansSerif", Font.BOLD, 23)); 
        lblNome.setForeground(Color.BLACK);
        lblNome.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JTextPane txtMensagem = new JTextPane();
        txtMensagem.setText(mensagem+"   ");
        txtMensagem.setEditable(false);
        txtMensagem.setOpaque(false);
        txtMensagem.setFont(new Font("SansSerif", Font.PLAIN, 20)); 
        txtMensagem.setForeground(Color.WHITE); 

        // Forçando o parágrafo a alinhar à direita dentro da caixa de texto
        javax.swing.text.StyledDocument doc = txtMensagem.getStyledDocument();
        javax.swing.text.SimpleAttributeSet alinhamentoDireita = new javax.swing.text.SimpleAttributeSet();
        javax.swing.text.StyleConstants.setAlignment(alinhamentoDireita, javax.swing.text.StyleConstants.ALIGN_RIGHT);
        doc.setParagraphAttributes(0, doc.getLength(), alinhamentoDireita, false);

        txtMensagem.setAlignmentX(Component.RIGHT_ALIGNMENT);
        txtMensagem.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));


        // Adiciona os itens ao painel
        add(lblNome);
        add(txtMensagem);

        // Limita a largura para a mensagem quebrar de linha se for muito longa
        setMaximumSize(new Dimension(580, Integer.MAX_VALUE));
        
        setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    public MensagemPanel(String mensagemSistema, boolean isSistema) {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // Espaçamento um pouco maior para isolar a mensagem no meio da tela
        //if(mensagemSistema.length()<30)
        //    setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); 

        JTextPane txtMensagem = new JTextPane();
        txtMensagem.setText(mensagemSistema);
        txtMensagem.setEditable(false);
        txtMensagem.setOpaque(false);
        
        // Fonte em Itálico, ligeiramente menor, com cor de destaque (ex: Amarelo Ouro)
        txtMensagem.setFont(new Font("SansSerif", Font.ITALIC, 19)); 
        txtMensagem.setForeground(Color.decode("#053028")); 

        // Forçando o parágrafo a alinhar ao CENTRO dentro da caixa
        javax.swing.text.StyledDocument doc = txtMensagem.getStyledDocument();
        javax.swing.text.SimpleAttributeSet alinhamentoCentro = new javax.swing.text.SimpleAttributeSet();
        javax.swing.text.StyleConstants.setAlignment(alinhamentoCentro, javax.swing.text.StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), alinhamentoCentro, false);

        txtMensagem.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(txtMensagem);

        setMaximumSize(new Dimension(780, Integer.MAX_VALUE));
        
        // Garante que o painel flutue no centro do ScrollPane
        setAlignmentX(Component.CENTER_ALIGNMENT); 
    }
}