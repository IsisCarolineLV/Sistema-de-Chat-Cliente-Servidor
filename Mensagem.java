//para ficar mais clara a logica da separacao
public class Mensagem {

    private String conteudo;
    private String tipo;
    private String remetente;
    private String destinatario = null;

    public Mensagem (String mensagem){
        String[] termos = mensagem.split("\\|");
        if(termos.length==1){
            conteudo = mensagem;
            tipo = mensagem;
            remetente="";
            destinatario="";
            //System.out.println("COMANDO DE UMA LINHA:"+mensagem);
        }else if (termos.length==3){
            tipo = termos[0];
            remetente = termos[1];
            destinatario = "";
            conteudo = termos[2];
        }else{
            tipo = termos[0];
            remetente = termos[1];
            destinatario = termos[2];
            conteudo = termos[3];
        }
    }

    public String getConteudo(){
        return conteudo;
    }

    public String getTipo(){
        return tipo;
    }

    public String getDestino(){
        return destinatario;
    }

    public String getRemetente(){
        return remetente;
    }
    
}
