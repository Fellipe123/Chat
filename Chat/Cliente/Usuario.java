package Cliente;

import java.net.*;
import java.io.*;
import java.util.*;

public class Usuario  {
    

    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;		
    private Socket socket;

    private UsuarioGUI cg;

    private String servidor, nomeUsr;
    private int porta;

    Usuario(String server, int port, String username) {
        this(server, port, username, null);
    }

    Usuario(String servidor, int porta, String nomeUsr, UsuarioGUI cg) {
        this.servidor = servidor;
        this.porta = porta;
        this.nomeUsr = nomeUsr;
        this.cg = cg;
    }

    public boolean start() {
        try {
            socket = new Socket(servidor, porta);
        } 
        catch(Exception ec) {
                display("Erro na conexão do servidor:" + ec);
                return false;
        }

        String msg = "Conexão estabelecida " + socket.getInetAddress() + ":" + socket.getPort();
        display(msg);

	try{
            sInput  = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
	}catch (IOException eIO) {
            display("Exeção ao tentar criar Streams de Input/output: " + eIO);
            return false;
	}

	new ListenFromServer().start();
               
	try{
            sOutput.writeObject(nomeUsr);
	}catch (IOException eIO) {
            display("Exeção no Login : " + eIO);
            disconnect();
            return false;
	}
        return true;
    }

    private void display(String msg) {
	if(cg == null)
            System.out.println(msg);      
	else
            cg.append(msg + "\n");		
	}
	
	void sendMessage(Mensagens msg) {
            try {
                sOutput.writeObject(msg);
            }
            catch(IOException e) {
		display("Exeção ao escrever no servidor: " + e);
            }
	}

	private void disconnect() {
            try { 
		if(sInput != null) sInput.close();
            }catch(Exception e) {}
            try {
                if(sOutput != null) sOutput.close();
            }catch(Exception e) {}
            try{
		if(socket != null) socket.close();
            }catch(Exception e) {}

            if(cg != null){
		cg.connectionFailed();	
            }
	}

	public static void main(String[] args) {
            int numPorta = 1500;
            String endServidor = "localhost";
            String nomeUsr = "Anonimo";
            switch(args.length) {
		case 3:
                    endServidor = args[2];
		case 2:
                    try {
			numPorta = Integer.parseInt(args[1]);
                    }catch(Exception e) {
			System.out.println("Numero da porta invalida.");
			return;
                    }
		case 1: 
                    nomeUsr = args[0];
		case 0:
                    break;
		default:
                    System.out.println("Usuarios: > Cliente Java [usuario] [NumeroPorta] [Endereço servidor]");
                    return;
            }
	Usuario client = new Usuario(endServidor, numPorta, nomeUsr);
	if(!client.start()){
            return;
        }
		
	Scanner scan = new Scanner(System.in);
	while(true) {
            System.out.print("> ");
            String msg = scan.nextLine();
            if(msg.equalsIgnoreCase("LOGOUT")) {
		client.sendMessage(new Mensagens(Mensagens.LOGOUT, ""));
		break;
            }else if(msg.equalsIgnoreCase("USUARIOS")) {
		client.sendMessage(new Mensagens(Mensagens.USUARIOS, ""));				
            }else if(msg.equalsIgnoreCase("TRANS_ARQ")) {
                client.sendMessage(new Mensagens(Mensagens.TRANS_ARQ, ""));
            }else{			
		client.sendMessage(new Mensagens(Mensagens.MENSAGEM, msg));
            }
	}
	client.disconnect();	
    }

    class ListenFromServer extends Thread {

	public void run() {
            while(true) {
		try {
                    String msg = (String) sInput.readObject();
                    if(cg == null) {
			System.out.println(msg);
			System.out.print("> ");
                    }else {
			cg.append(msg);
                    }
		}catch(IOException e) {
                    display("O servidor encerrou a conxáo: " + e);
                    if(cg != null) 
			cg.connectionFailed();
                    break;
                    } catch(ClassNotFoundException e2) {
		}
            }
	}
    }
}

