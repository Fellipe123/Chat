package Cliente;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class UsuarioTanferencias {

    public final static int PORTA_SOCKET = 13267; 
    public final static String SERVIDOR = "127.0.0.1";  
    public final static int TAMANHO_ARQUIVO = 6022386; 

    public UsuarioTanferencias(String FILE_TO_RECEIVED) throws IOException {
        int bytesLidos;
        int atual = 0;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        Socket sock = null;
        try {
            sock = new Socket(SERVIDOR, PORTA_SOCKET);
            System.out.println("Connecting...");
            fos = new FileOutputStream(FILE_TO_RECEIVED);
            bos = new BufferedOutputStream(fos);
        }
        finally {
            if (fos != null) fos.close();
            if (bos != null) bos.close();
            if (sock != null) sock.close();
        }
    }

}