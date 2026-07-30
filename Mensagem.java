//para ficar mais clara a logica da separacao
public class Mensagem {

    private String conteudo;
    private String tipo;
    private String remetente;
    private String destinatario = null;

    public Mensagem (String mensagem, String remetente){
        String[] termos = mensagem.split("\\|");
        if(termos.length==2){
            tipo = termos[0];
            conteudo = termos[1];
        }else if (termos.length==3){
            tipo = termos[0];
            destinatario = termos[1];
            conteudo = termos[2];
        }

        this.remetente = remetente;
    }

    public String getConteudo(){
        return conteudo;
    }

    public int getTipo(){
        if(tipo.equals("CHAT GERAL")) return 1;
        else return 2;
    }

    public String getDestino(){
        return destinatario;
    }

    public String getRemetente(){
        return remetente;
    }
    
}
