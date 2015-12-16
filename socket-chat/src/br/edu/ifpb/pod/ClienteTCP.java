package br.edu.ifpb.pod;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;

public class ClienteTCP {

	public static void main(String[] args) throws IOException {
		
		Socket socket = new Socket("127.0.0.1", 1593);
		DataInputStream entrada = new DataInputStream(socket.getInputStream());
		DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
		String msg = new String();
		
		do {
			String msgEnvio = JOptionPane.showInputDialog("Cliente, digite a msg: ");
			System.out.println("Cliente: " + msgEnvio);
			saida.writeUTF(msgEnvio);
			msg = entrada.readUTF();
			System.err.println("Servidor: " + msg);
		} while (msg != "FECHAR");
		socket.close();

	}

}
