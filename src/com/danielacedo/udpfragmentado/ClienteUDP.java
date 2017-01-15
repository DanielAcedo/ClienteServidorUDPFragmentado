package com.danielacedo.udpfragmentado;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ClienteUDP extends Thread {
	//Cambiar los valores de las constantes por tus IP
	public static final String IP_SERVER = "192.168.1.130";
	public static final String IP_CLIENT = "192.168.1.130";
	
	public static final int PORT_SERVER = 5000;
	public static final int PORT_CLIENT = 5001;
	
	private int numCorte = 5;
	
	private String mensaje;
	
	public ClienteUDP(String msg, int numCorte) {
		mensaje = msg;
		this.numCorte = numCorte;
	}
	
	@Override
	public void run(){
		
		if(mensaje.length() < numCorte){
			System.err.println("El mensaje debe de ser mas largo que el tamaño de cada corte");
			return;
		}
		
		int corte = 0;
		
		//Decidir el numero de paquetes en los que se cortará el mensaje
		if(mensaje.length()%numCorte != 0 ){
			corte = (mensaje.length() / numCorte) + 1;
		}else{
			corte = (mensaje.length() / numCorte);
		}

		ContenedorMensajes contenedorMensajes = new ContenedorMensajes(corte);
		int cont = 0;
		int next = 0;
		
		try {
			DatagramSocket socket = new DatagramSocket(PORT_CLIENT, InetAddress.getByName(IP_CLIENT));
			
			while(!contenedorMensajes.estaLleno()){
				
				//Cortar el mensaje y actualizar el puntero
				String segmento;
				
				if(cont + numCorte >= mensaje.length()){
					segmento = mensaje.substring(cont, cont+(mensaje.length()%numCorte));
					contenedorMensajes.setUltimoSegmento(segmento);
					contenedorMensajes.anadirMensaje(segmento);
					cont+= (mensaje.length()%numCorte);
				}else{
					segmento = mensaje.substring(cont, cont+(numCorte));
					contenedorMensajes.setUltimoSegmento(segmento);
					contenedorMensajes.anadirMensaje(segmento);
					cont+=numCorte;
				}
				
			
				//Enviar el mensaje y mostrar los datos enviados
				byte[] array = ContenedorMensajes.toByteArray(contenedorMensajes);
				
				DatagramPacket packet = new DatagramPacket(array, array.length, InetAddress.getByName(IP_SERVER), PORT_SERVER);
				socket.send(packet);
				
				ContenedorMensajes contenedorMensajes2 = ContenedorMensajes.fromByteArray(array);
				
				//Mostrar información
				System.out.println("Enviado "+contenedorMensajes2.getUltimoSegmento()+"("
						+contenedorMensajes2.getMensajeActual()+"/"+contenedorMensajes2.getMensajesEsperados()+")");
				
				if(contenedorMensajes2.estaLleno()){
					System.out.println("Mensaje completado " + 
							"("+contenedorMensajes2.getMensajeActual()+"/"+contenedorMensajes2.getMensajesEsperados()+"): "+
							contenedorMensajes2.getMensaje());
				
				}
			}
			
			socket.close();
			
		} catch (SocketException | UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public static void main(String[] args) {
		//Selecciona el mensaje a enviar y el numero de caracteres de cada trozo
		new ClienteUDP("Era una preciosa tarde de verano cuando aun no habia amainado", 10).start();
	}

}
