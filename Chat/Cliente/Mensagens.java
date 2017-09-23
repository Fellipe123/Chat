package Cliente;

import java.io.*;

public class Mensagens implements Serializable {
 
    static final int USUARIOS = 0, MENSAGEM = 1, LOGOUT = 2, TRANS_ARQ = 3;
    private int tipo;
    private String mensagem;
    Mensagens(int tipo, String mensagem) {
        this.tipo = tipo;
        this.mensagem = mensagem;
    }

    int getType() {
        return tipo;
    }
    String getMensagem() {
        return mensagem;
    }
}
