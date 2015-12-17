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
    private static Map<String, PrintStream> clientesMap;
    private Socket conexao;
    private String nomeCliente;
    private static List<String> usuariosLista = new ArrayList<String>();
 
    public ServerTCP(Socket socket) {
        this.conexao = socket;
    }
 
    public boolean armazena(String newName) {
        for (int i = 0; i < usuariosLista.size(); i++) {
            if (usuariosLista.get(i).equals(newName))
                return true;
        }
        usuariosLista.add(newName);
        return false;
    }
 
    public void remove(String oldName) {
        for (int i = 0; i < usuariosLista.size(); i++) {
            if (usuariosLista.get(i).equals(oldName))
                usuariosLista.remove(oldName);
        }
    }
 
    public static void main(String args[]) {
        clientesMap = new HashMap<String, PrintStream>();
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
                // mostra o nome do cliente conectado ao servidor e ao cliente
                System.out.println(this.conexao.getInetAddress() + ":" + this.conexao.getPort() + "/~" + this.nomeCliente + ": conectado ao bate-papo!");
                // adiciona os dados de saida do cliente no objeto map_clientes
                // A chave sera o nome e valor o printstream
                clientesMap.put(this.nomeCliente, saida);
                if (clientesMap != null && !clientesMap.isEmpty()) {
                	saida.println(nomeCliente.toUpperCase()+ " seja bem-vindo!");
                    for (Map.Entry<String, PrintStream> cliente : clientesMap.entrySet()) {
                        PrintStream chat = cliente.getValue();
                        if (chat != saida) {
                        	chat.println(nomeCliente.toUpperCase() + " conectou ao chat!");
						}
                    }
                }
            }
 
            if (this.nomeCliente == null) {
                return;
            }
 
            String msg = entrada.readLine();
 
            while (msg != null && !(msg.trim().equals(""))) {
                String[] cmd = msg.split(" ");
 
                if(msg.equals("bye")){
                    System.out.println(this.conexao.getInetAddress() + ":" + this.conexao.getPort() + "/~"+ this.nomeCliente +" saiu do bate-papo!");
                    remove(this.nomeCliente);
                    clientesMap.remove(this.nomeCliente);
                    this.conexao.close();
                }else if(msg.equals("list")){
                    saida.println("Conectados: " + usuariosLista.toString());
                }
                else{
                    switch(cmd[0].trim()){
                    case "send":
                        send(saida, cmd);
                        break;
                    case "rename":
                    	if (cmd.length == 2) {
                    		rename(saida, cmd);
						} else {
							saida.println("Comando invalido");
						}
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
        int count = 1;
        SimpleDateFormat formatador = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        GregorianCalendar d = new GregorianCalendar();
        String datahora = formatador.format(d.getTime());
 
        for (Map.Entry<String, PrintStream> cliente : clientesMap.entrySet()) {
            PrintStream chat = cliente.getValue();
            if(chat != saida){
                if(msg.length >= 3 && msg[1].equals("-all")){
                    String mensagem = "";
                    for (int i = 2; i < msg.length; i++) {
                        mensagem += (msg[i]+" ");
                    }
                    chat.println(this.conexao.getInetAddress() + ":" + this.conexao.getPort() + "/~"+ this.nomeCliente + ": " +mensagem+ " "+ datahora);
                } else if(msg.length >= 4 && msg[1].equals("-user")) {
                    if (msg[2].equalsIgnoreCase(cliente.getKey())) {
                        String mensagem = "";
                        for (int i = 3; i < msg.length; i++) {
                            mensagem += (msg[i]+" ");
                        }
                        chat.println(this.conexao.getInetAddress() + ":" + this.conexao.getPort() + "/~"+ this.nomeCliente + ": " +mensagem+ " "+ datahora);
                        break;
                    } else {
                        count++;
                    }
                } else {
                    saida.println("Comando inválido!");
                }
            }
        }  
        if(count == clientesMap.size()) {
            saida.println("O usuário "+msg[2]+" não existe!");
        }
    }
   
    public void rename(PrintStream saida, String[] msg) {
    	
        String novoNome = msg[1];
        String nomeAntigo = nomeCliente;
        if (armazena(novoNome)) {
            saida.println("Usuário já cadastrado, tente novamente!!");
        } else {
            clientesMap.remove(this.nomeCliente);
            remove(this.nomeCliente);
            nomeCliente = novoNome;
            clientesMap.put(novoNome, saida);
            if (clientesMap != null && !clientesMap.isEmpty()) {
                for (Map.Entry<String, PrintStream> cliente : clientesMap.entrySet()) {
                    PrintStream chat = cliente.getValue();
                    if (saida != chat) {
                        chat.println(nomeAntigo.toUpperCase() + " alterou seu nome para: " + novoNome.toUpperCase());
                    }
                }
            }
            saida.println("Usuário Alterado com sucesso!!");
 
        }
    }
 
}