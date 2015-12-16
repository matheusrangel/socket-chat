package br.edu.ifpb.pod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

public class ServerTCP extends Thread {
    private static Map<String, PrintStream> map_clientes;
    private Socket conexao;
    private String nomeCliente;
    private static List<String> lista_de_usuarios = new ArrayList<String>();

    public ServerTCP(Socket socket) {
        this.conexao = socket;
    }

    public boolean armazena(String newName) {
        for (int i = 0; i < lista_de_usuarios.size(); i++) {
            if (lista_de_usuarios.get(i).equals(newName))
                return true;
        }
        lista_de_usuarios.add(newName);
        return false;
    }

    public void remove(String oldName) {
        for (int i = 0; i < lista_de_usuarios.size(); i++) {
            if (lista_de_usuarios.get(i).equals(oldName))
                lista_de_usuarios.remove(oldName);
        }
    }

    public static void main(String args[]) {
        map_clientes = new HashMap<String, PrintStream>();
        try {
            ServerSocket server = new ServerSocket(5555);
            System.out.println("SocketChat IFPB rodando na porta 5555!");
            while (true) {
                Socket conexao = server.accept();
                Thread t = new ServerTCP(conexao);
                t.start();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
    }

    public void run() {
        try {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(this.conexao.getInputStream()));
            PrintStream saida = new PrintStream(this.conexao.getOutputStream());
            this.nomeCliente = entrada.readLine();
            if (armazena(this.nomeCliente)) {
                saida.println("Este nome ja existe! Conecte novamente com outro nome.");
                this.conexao.close();
                return;
            } else {
                //mostra o nome do cliente conectado ao servidor
                System.out.println(this.conexao.getInetAddress() + ":" + this.conexao.getPort() + "/~"+ 
				this.nomeCliente +": conectado ao bate-papo!");
            }

            if (this.nomeCliente == null) {
                return;
            }
            
            //adiciona os dados de saida do cliente no objeto map_clientes
            //A chave sera o nome e valor o printstream
            map_clientes.put(this.nomeCliente, saida);

            String msg = entrada.readLine();
            
            while (msg != null && !(msg.trim().equals(""))) {
            	String[] cmd = msg.split(" ");
            	
            	if(msg.equals("bye")){
            		System.out.println(this.conexao.getInetAddress() + ":" + this.conexao.getPort() + "/~"+ 
                            this.nomeCliente +" saiu do bate-papo!");
                    remove(this.nomeCliente);
                    map_clientes.remove(this.nomeCliente);
                    this.conexao.close();
            	}else if(msg.equals("list")){
                	saida.println("Conectados: " + lista_de_usuarios.toString());
                }
            	else{
                	switch(cmd[0].trim()){
                	case "send":
                		send(saida, cmd);
                		break;
                	case "rename":
//                		rename(saida, cmd);
                		break;
                	default:
                		saida.println("Comando invalido!");
                	}
                }
            	
            	msg = entrada.readLine();
            }
	
        } catch (IOException e) {
            System.out.println("Falha na Conexao... .. ." + " IOException: " + e);
        }
    }

    public void send(PrintStream saida, String[] msg) {
    	SimpleDateFormat formatador =
    			new SimpleDateFormat("HH:mm dd/MM/yyyy");
    	
    	GregorianCalendar d = new GregorianCalendar();
    	String datahora = formatador.format(d.getTime());
    		
    	for (Map.Entry<String, PrintStream> cliente : map_clientes.entrySet()) {
    		PrintStream chat = cliente.getValue();
    		if(chat != saida){
    			if(msg.length == 3){
    				chat.println(this.conexao.getInetAddress() + ":" + this.conexao.getPort() + "/~"+ 
    						this.nomeCliente + ": " +msg[2]+ " "+ datahora);
    			}else{
    				if(msg[2].equalsIgnoreCase(cliente.getKey())){
    					chat.println(this.conexao.getInetAddress() + ":" + this.conexao.getPort() + "/~"+ 
        						this.nomeCliente + ": " +msg[3]+ " "+ datahora);
        				break;
    				}
    			}
    		}
    		
    	}	
    }
    
}