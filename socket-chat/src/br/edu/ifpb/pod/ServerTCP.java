package br.edu.ifpb.pod;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

public class ServerTCP {

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(1593, 10);
		Socket socket = serverSocket.accept();
		DataInputStream entrada = new DataInputStream(socket.getInputStream());
		DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
		String msg = new String();
		
		do {
			msg = entrada.readUTF();
			System.err.println("Cliente: " + msg);
			String msgEnvio = JOptionPane.showInputDialog("Servidor, digite a msg: ");
			System.out.println("Servidor: " + msgEnvio);
			saida.writeUTF(msgEnvio);
		} while (msg != "SAIR");
		
		socket.close();
		serverSocket.close();

	}

}
