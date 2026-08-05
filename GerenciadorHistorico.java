import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorHistorico {
    private static final String ARQUIVO_HISTORICO = "historico_mensagens.txt";

    // Salva uma mensagem crua (exatamente como trafega no socket) no arquivo
    public static synchronized void salvarMensagem(String mensagem) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQUIVO_HISTORICO, true))) {
            bw.write(mensagem);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Erro ao salvar mensagem no histórico: " + e.getMessage());
        }
    }

    // Retorna todas as mensagens salvas
    public static synchronized List<String> carregarHistorico() {
        List<String> historico = new ArrayList<>();
        File arquivo = new File(ARQUIVO_HISTORICO);
        
        if (!arquivo.exists()) {
            return historico; // Retorna lista vazia se for a primeira vez rodando
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                historico.add(linha);
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar histórico: " + e.getMessage());
        }
        return historico;
    }
}