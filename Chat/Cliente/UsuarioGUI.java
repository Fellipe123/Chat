package Cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsuarioGUI extends JFrame implements ActionListener {

    public String nomeUsr = "";
    public String recebe = "";
    public String manda = "";
                
    private JLabel nomeLabel;
    private JLabel transEnLabel;
    private JLabel transSaLabel;
    private JTextField usrTxt;
    private JTextField transEnTxt;
    private JTextField transSaTxt;
    private JTextField servidorTxt, portaTxt;
    private JButton login, transArq, logout, listUsrs;
    private JTextArea areaChat;
    private boolean conectado;
    private Usuario cliente;
    private int portaPadrao;
    private String hostPadrao;

    UsuarioGUI(String host, int porta) {

	super("Chat");
	portaPadrao = porta;
	hostPadrao = host;

	JPanel painelNorte = new JPanel(new GridLayout(3,1));
	JPanel servidorPorta = new JPanel(new GridLayout(1,5, 1, 3));
	servidorTxt = new JTextField(host);
	portaTxt = new JTextField("" + porta);
	portaTxt.setHorizontalAlignment(SwingConstants.RIGHT);

	servidorPorta.add(new JLabel("Endereço do servidor:  "));
	servidorPorta.add(servidorTxt);
	servidorPorta.add(new JLabel("Numero da porta:  "));
	servidorPorta.add(portaTxt);
	servidorPorta.add(new JLabel(""));
	painelNorte.add(servidorPorta);

	nomeLabel = new JLabel("Digite o seu usuario: ", SwingConstants.CENTER);
	painelNorte.add(nomeLabel);
	usrTxt = new JTextField("Anonimo");
	usrTxt.setBackground(Color.WHITE);
	painelNorte.add(usrTxt);
	add(painelNorte, BorderLayout.NORTH);

	areaChat = new JTextArea("Seja bem vindo ao chat\n", 70, 70);
	JPanel painelCentro = new JPanel(new GridLayout(1,1));
	painelCentro.add(new JScrollPane(areaChat));
	areaChat.setEditable(true);
	add(painelCentro, BorderLayout.CENTER);
                
        transEnLabel = new JLabel("Arquivo entrada:");
	transEnTxt = new JTextField("Endereco entrada");
	transEnTxt.setBackground(Color.WHITE);
                
        transSaLabel = new JLabel("Local Download:", SwingConstants.CENTER);
        transSaTxt = new JTextField("Endereco saida");
	transSaTxt.setBackground(Color.WHITE);

	login = new JButton("Login");
	login.addActionListener(this);
        transArq = new JButton("Transferencias");
	transArq.setEnabled(false);
        transArq.addActionListener(this);
	logout = new JButton("Logout");
	logout.addActionListener(this);
	logout.setEnabled(false);		
	listUsrs = new JButton("Usuarios online");
	listUsrs.addActionListener(this);
	listUsrs.setEnabled(false);		

	JPanel southPanel = new JPanel(new GridLayout(2,1));

        southPanel.add(transEnLabel);
        southPanel.add(transEnTxt);
        southPanel.add(transSaLabel);
        southPanel.add(transSaTxt);
	southPanel.add(login);
        southPanel.add(transArq);
	southPanel.add(logout);
	southPanel.add(listUsrs);
	add(southPanel, BorderLayout.SOUTH);

	setDefaultCloseOperation(EXIT_ON_CLOSE);
	setSize(600, 600);
	setVisible(true);
	usrTxt.requestFocus();

    }

    void append(String str) {
    areaChat.append(str);
    areaChat.setCaretPosition(areaChat.getText().length() - 1);
    }
    
    void connectionFailed() {
	login.setEnabled(true);
        transArq.setEnabled(false);
	logout.setEnabled(false);
	listUsrs.setEnabled(false);
	nomeLabel.setText("Digite seu usuário");
	usrTxt.setText("Anonimo");
	portaTxt.setText("" + portaPadrao);
	servidorTxt.setText(hostPadrao);
	servidorTxt.setEditable(false);
	portaTxt.setEditable(false);
	usrTxt.removeActionListener(this);
	conectado = false;
    }
        
    public void fid() throws IOException{
        ServidorTransferencias hj = new ServidorTransferencias(recebe, manda);
    }
		
    public void actionPerformed(ActionEvent e) {
	Object o = e.getSource();
                
        if(o == transArq){
            System.out.println(manda);
            recebe = transEnTxt.getText();
            manda = transSaTxt.getText();
            System.out.println(manda);
            try {
                fid();
            } catch (IOException ex) {
                Logger.getLogger(UsuarioGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
	if(o == logout) {
            cliente.sendMessage(new Mensagens(Mensagens.LOGOUT, ""));
            return;
	}
        
	if(o == listUsrs) {
            cliente.sendMessage(new Mensagens(Mensagens.USUARIOS, ""));				
            return;
	}

        if(conectado) {
            cliente.sendMessage(new Mensagens(Mensagens.MENSAGEM, usrTxt.getText()));	
            append(nomeUsr + ": " + usrTxt.getText() + "\n");
            usrTxt.setText("");
            return;
        }
		
	if(o == login) {
            nomeUsr = usrTxt.getText().trim();
            if(nomeUsr.length() == 0)
                return;
            String server = servidorTxt.getText().trim();
            if(server.length() == 0)
		return;
            String portNumber = portaTxt.getText().trim();
            if(portNumber.length() == 0)
		return;
            int port = 0;
            try {
		port = Integer.parseInt(portNumber);
            }catch(Exception en) {
		return;   
            }
            
            cliente = new Usuario(server, port, nomeUsr);
            if(!cliente.start()) 
		return;
            usrTxt.setText("");
            nomeLabel.setText("Digite a sua mensagem abaixo: ");
            conectado = true;
            login.setEnabled(false);
            logout.setEnabled(true);
            listUsrs.setEnabled(true);
            transArq.setEnabled(true);
            servidorTxt.setEditable(false);
            portaTxt.setEditable(false);
            usrTxt.addActionListener(this);
	}        
    }

    public static void main(String[] args) {
	new UsuarioGUI("localhost", 1500);
    }
}