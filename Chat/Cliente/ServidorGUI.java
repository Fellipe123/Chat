package Cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ServidorGUI extends JFrame implements ActionListener, WindowListener {
	
    private JButton iniciarParar;
    private JTextArea chat, evento;
    private JTextField portaTxt;
    private Servidor servidor;

    ServidorGUI(int port) {
	super("Servidor Chat");
	servidor = null;
	JPanel norte = new JPanel();
	norte.add(new JLabel("Numero porta: "));
	portaTxt = new JTextField("  " + port);
	norte.add(portaTxt);
	iniciarParar = new JButton("Iniciar");
	iniciarParar.addActionListener(this);
	norte.add(iniciarParar);
	add(norte, BorderLayout.NORTH);

	JPanel centro = new JPanel(new GridLayout(2,1));
	chat = new JTextArea(80,80);
	chat.setEditable(false);
	appendRoom("Chat.\n");
	centro.add(new JScrollPane(chat));
	evento = new JTextArea(80,80);
	evento.setEditable(false);
	appendEvent("Eventos ocorridos.\n");
	centro.add(new JScrollPane(evento));	
	add(centro);

	addWindowListener(this);
	setSize(400, 600);
	setVisible(true);
    }		

    void appendRoom(String str) {
	chat.append(str);
	chat.setCaretPosition(chat.getText().length() - 1);
    }
    
    void appendEvent(String str) {
	evento.append(str);
	evento.setCaretPosition(chat.getText().length() - 1);
    }

    public void actionPerformed(ActionEvent e) {
	if(servidor != null) {
            servidor.stop();
            servidor = null;
            portaTxt.setEditable(true);
            iniciarParar.setText("Iniciar");
            return;
        }
        
	int porta;
	try {
	porta = Integer.parseInt(portaTxt.getText().trim());
        }catch(Exception er) {
            appendEvent("Numero de porta invalida");
            return;
        }
        
        servidor = new Servidor(porta, this);
        new ServerRunning().start();
        iniciarParar.setText("Parar");
        portaTxt.setEditable(false);
    }

    public static void main(String[] arg) {
	new ServidorGUI(1500);
    }

    public void windowClosing(WindowEvent e) {
        if(servidor != null) {
            try {
                servidor.stop();			
            }catch(Exception eClose) {
            }
            
            servidor = null;
	}
	dispose();
	System.exit(0);
    }
    
    public void windowClosed(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}

    class ServerRunning extends Thread {
	public void run() {
            servidor.start();      
            iniciarParar.setText("Iniciar");
            portaTxt.setEditable(true);
            appendEvent("O servidor caiu\n");
            servidor = null;
	}
    }
}