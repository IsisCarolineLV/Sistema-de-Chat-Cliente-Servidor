import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class GerenciadorUsuarios {
    private static final String ARQUIVO_USUARIO = "usuarios.txt";
    private static final Map <String, String> usuarios = new HashMap<>();
    private static final Semaphore semaforoArquivo = new Semaphore(1);

    static {
        try {
            carregarUsuarios();
        } catch (IOException e) {
            System.err.println("Erro ao carregar usuários: " + e.getMessage());
        }
    }

    private static void carregarUsuarios() throws IOException {
        File arquivo = new File(ARQUIVO_USUARIO);
        if(!arquivo.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(";", 2);
                if (partes.length == 2) {
                    usuarios.put(partes[0], partes[1]);
                }
            }
        } catch (IOException e) {
            throw new IOException("Erro ao ler o arquivo de usuários.", e);
        }
    }

    public static boolean usuarioExiste(String nome) throws InterruptedException {
        semaforoArquivo.acquire();
        try {
            return usuarios.containsKey(nome);
        } finally {
            semaforoArquivo.release();
        }
    }

    public static boolean autenticar(String nome, String senha) throws InterruptedException {
        semaforoArquivo.acquire();
        try {
            String senhaArmazenada = usuarios.get(nome);
            return senhaArmazenada != null && senhaArmazenada.equals(hash(senha));
        } finally {
            semaforoArquivo.release();
        }
    }

  //sem hash na senha
  //  public static boolean registrarUsuario(String nome, String senha) throws IOException, InterruptedException {
  //      if (usuarioExiste(nome)) {
  //          return false; // Usuário já existe
  //      }
  //      semaforoArquivo.acquire();
  //      try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQUIVO_USUARIO, true))) {
  //          bw.write(nome + ":" + senha);
  //          bw.newLine();
  //          usuarios.put(nome, senha);
  //          return true;
  //      } catch (IOException e) {
  //          throw new IOException("Erro ao escrever no arquivo de usuários.", e);
  //      } finally {
  //          semaforoArquivo.release();
  //      }
  //  }

    public static String hash(String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(senha.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash da senha ", e);
        }
    }


    //com hash na senha
    public static boolean registrarUsuario(String nome, String senha) throws IOException, InterruptedException {
        if (usuarioExiste(nome)) {
            return false; // Usuário já existe
        }
        semaforoArquivo.acquire();
        String senhaHash = hash(senha);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQUIVO_USUARIO, true))) {
            bw.write(nome + ";" + senhaHash);
            bw.newLine();
            usuarios.put(nome, senhaHash);
            return true;
        } catch (IOException e) {
            throw new IOException("Erro ao escrever no arquivo de usuários.", e);
        } finally {
            semaforoArquivo.release();
        }
    }
    
}
