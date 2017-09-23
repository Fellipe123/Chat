package Cliente;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorTransferencias {

    public final static int PORTA_SOCKET = 13267;

    public ServidorTransferencias(String ARQUIVO_ENVIADO, String LOCAL_SAIDA) throws IOException {
        FileInputStream arquivo = null;
        BufferedInputStream listaArq = null;
        OutputStream Sarq = null;
        ServerSocket sockServ = null;
        Socket sock = null;
        try {
            sockServ = new ServerSocket(PORTA_SOCKET);
            try {
                UsuarioTanferencias recebe = new UsuarioTanferencias(LOCAL_SAIDA); 
                sock = sockServ.accept();
                System.out.println("Coneão aceitada: " + sock);
                File meuArquivo = new File (ARQUIVO_ENVIADO);
                byte [] mybytearray  = new byte [(int)meuArquivo.length()];
                arquivo = new FileInputStream(meuArquivo);
                listaArq = new BufferedInputStream(arquivo);
                listaArq.read(mybytearray,0,mybytearray.length);
                Sarq = sock.getOutputStream();
                System.out.println("Enviando " + ARQUIVO_ENVIADO + "(" + mybytearray.length + " bytes)");
                Sarq.write(mybytearray,0,mybytearray.length);
                Sarq.flush();
                System.out.println("Terminado.");
            }
            finally {
                if (listaArq != null) listaArq.close();
                if (Sarq != null) Sarq.close();
                if (sock!=null) sock.close();
            }
        }
        finally {
            if (sockServ != null) sockServ.close();
        }
    }
}