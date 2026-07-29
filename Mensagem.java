//para ficar mais clara a logica da separacao
public class Mensagem {

    private String conteudo;
    private String tipo;
    private String remetente;
    private String destinatario = null;

    public Mensagem (String mensagem, String clienteOrigem){
        String[] termos = mensagem.split("\\|");
        remetente = clienteOrigem;
        if(termos.length==2){
            tipo = termos[0];
            conteudo = termos[1];
        }else if (termos.length==3){
            tipo = termos[0];
            destinatario = termos[1];
            conteudo = termos[2];
        }
    }

    public String getMensagemPraEncaminhar(){
        if(destinatario.equals(null))
            return tipo+"|"+conteudo;
        return tipo+"|"+destinatario+"|"+conteudo;
    }

    public Mensagem prontaPraEncaminhar(){
        return new Mensagem("MSG RECEBIDA|"+remetente+"|"+conteudo, destinatario);
    }

    public String getConteudo(){
        return conteudo;
    }

    public int getTipo(){
        if(tipo.equals("CHAT GERAL")) return 1;
        else return 2;
    }
    
}
