package Cliente;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Servidor {
    private static int idUnico;
    private ArrayList<ClientThread> clienteLista;
    private ServidorGUI ServGui;
    private SimpleDateFormat formatoData;
    private int porta;
    private boolean continuar;

    public Servidor(int port) {
	this(port, null);
    }
	
    public Servidor(int port, ServidorGUI sg) {
	this.ServGui = sg;
	this.porta = port;
	formatoData = new SimpleDateFormat("HH:mm:ss");
	clienteLista = new ArrayList<ClientThread>();
    }
	
    public void start() {
	continuar = true;
	try {
            ServerSocket serverSocket = new ServerSocket(porta);
            while(continuar) {
                display("Servidor esperando por usuarios na porta: " + porta + ".");	
		Socket socket = serverSocket.accept();  
		if(!continuar)
                    break;
		ClientThread t = new ClientThread(socket);  
		clienteLista.add(t);									
                t.start();
            }
		
            try {
		serverSocket.close();
		for(int i = 0; i < clienteLista.size(); ++i) {
                    ClientThread tc = clienteLista.get(i);
                    try {
			tc.sInput.close();
			tc.sOutput.close();
			tc.socket.close();
                    }catch(IOException ioE) {
                    }
		}
            }catch(Exception e) {
		display("Exeção gerada ao fechar os usuarios: " + e);
            }
        }catch (IOException e) {
            String msg = formatoData.format(new Date()) + " Exeção ao criar novo socket: " + e + "\n";
            display(msg);
	}
    }		

    protected void stop() {
	continuar = false;
        try {
            new Socket("HostLocal", porta);
	}catch(Exception e) {
	}
    }
    
    private void display(String msg) {
	String data = formatoData.format(new Date()) + " " + msg;
	if(ServGui == null)
            System.out.println(data);
	else
            ServGui.appendEvent(data + "\n");
    }
    
    private synchronized void broadcast(String message) {
	String data = formatoData.format(new Date());
	String mensagemLf = data + " " + message + "\n";
	if(ServGui == null)
            System.out.print(mensagemLf);
	else
            ServGui.appendRoom(mensagemLf);    
        for(int i = clienteLista.size(); --i >= 0;) {
            ClientThread ct = clienteLista.get(i);
            if(!ct.writeMsg(mensagemLf)) {
		clienteLista.remove(i);
                display("Cliente desconectado " + ct.nomeUsr + " removido da lista.");
            }
        }
    }

    synchronized void remove(int id) {
        for(int i = 0; i < clienteLista.size(); ++i) {
            ClientThread ct = clienteLista.get(i);
            if(ct.id == id) {
		clienteLista.remove(i);
                return;
            }
	}
    }
	
    public static void main(String[] args) {
	int numPorta = 1500;
	switch(args.length) {
            case 1:
                try {
                    numPorta = Integer.parseInt(args[0]);
                }catch(Exception e) {
                    System.out.println("Numero de porta invalido.");
                    return;
		}
            case 0:
                break;
            default:
                System.out.println("Porta sendo usada: > java Server [portNumber]");
                return;			
	}
        
	Servidor server = new Servidor(numPorta);
	server.start();
    }

    class ClientThread extends Thread {
	Socket socket;
	ObjectInputStream sInput;
	ObjectOutputStream sOutput;
	int id;
	String nomeUsr;
	Mensagens chatM;
	String data;

        ClientThread(Socket socket) {
            id = ++idUnico;
            this.socket = socket;
            System.out.println("Thread gerada tentando criar Input/Output Streams");
            try{
		sOutput = new ObjectOutputStream(socket.getOutputStream());
		sInput  = new ObjectInputStream(socket.getInputStream());
		nomeUsr = (String) sInput.readObject();
                display(nomeUsr + " acabou de se conectar.");
            }catch (IOException e) {
		display("Exception gerada ao criar Input/output Streams: " + e);
                return;
            }catch (ClassNotFoundException e) {
            }
            
            data = new Date().toString() + "\n";
	}

	public void run() {
            boolean continua = true;
            while(continua) {
                try {
                    chatM = (Mensagens) sInput.readObject();
                }catch (IOException e) {
                    display(nomeUsr + " Exeção criando Streams: " + e);
                    break;				
                }catch(ClassNotFoundException e2) {
                    break;
		}
                
		String message = chatM.getMensagem();
		switch(chatM.getType()) {
                    
                    case Mensagens.MENSAGEM:
			broadcast(nomeUsr + ": " + message);
			break;
                    case Mensagens.LOGOUT:
			display(nomeUsr + " se desconectou.");
			continua = false;
			break;
			case Mensagens.USUARIOS:
			writeMsg("Lista de usuarios ativos " + formatoData.format(new Date()) + "\n");
			for(int i = 0; i < clienteLista.size(); ++i) {
                            ClientThread ct = clienteLista.get(i);
                            writeMsg((i+1) + ") " + ct.nomeUsr + " desde " + ct.data);
			}
			break;
                }
            }
            
            remove(id);
            close();
        }
		
	private void close() {
            try {
		if(sOutput != null) sOutput.close();
            }catch(Exception e) {}
            
            try {
                if(sInput != null) sInput.close();
            }catch(Exception e) {};
            
            try {
		if(socket != null) socket.close();
            }catch (Exception e) {}
            
	}

	private boolean writeMsg(String msg) {
            if(!socket.isConnected()) {
		close();
		return false;
            }
            
            try {
		sOutput.writeObject(msg);
            }catch(IOException e) {
		display("Erro ao enviar mensagem para " + nomeUsr);
		display(e.toString());
            }
            return true;
	}
    }
}