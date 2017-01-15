package com.danielacedo.udpfragmentado;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ServidorUDP {
	//Cambiar los valores de las constantes por tus IP
	public static final String IP_SERV = "192.168.1.130";
	public static final int PORT_SERV = 5000;
	
	private Map<InetAddress, ContenedorMensajes> mapaMensajes;
	private boolean salir;
	
	public ServidorUDP(){
		mapaMensajes = new HashMap<InetAddress, ContenedorMensajes>();
		salir = false;
	}
	
	
	public void run(){
		try {
			byte[] buffer = new byte[512];
			DatagramSocket socket = new DatagramSocket(PORT_SERV, InetAddress.getByName(IP_SERV));
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

			while(!salir){
				socket.receive(packet);
				
				//Si ya tenemos esta IP en el mapa
				if(mapaMensajes.containsKey(packet.getAddress())){
				
					ContenedorMensajes contenedorMensaje = mapaMensajes.get(packet.getAddress());
					
					//Añadimos al contenedor
					String msg = new String(packet.getData(), packet.getOffset(), packet.getLength());
					contenedorMensaje.anadirMensaje(msg);
						
					System.out.println("Mensaje recibido de "+packet.getAddress()
					+ "("+contenedorMensaje.getMensajeActual()+"/"+contenedorMensaje.getMensajesEsperados()+"): "+msg);
					
					//Si esta lleno lo quitamos
					if(contenedorMensaje.estaLleno()){
						System.out.println("Mensaje completado de "+packet.getAddress()+""
							+ "("+contenedorMensaje.getMensajeActual()+"/"+contenedorMensaje.getMensajesEsperados()+"): "+contenedorMensaje.getMensaje());
							mapaMensajes.remove(packet.getAddress());
					}else{
						//Si no esta lleno lo actualizamos
						mapaMensajes.put(packet.getAddress(), contenedorMensaje);
					}
				}
				else{
					//Si no tenemos la IP le hacemos una entrada
					//El primer mensaje que recibimos son los trozos que va a tener el mensaje
					int mensajesEsperados = ByteBuffer.wrap(packet.getData()).getInt();
					ContenedorMensajes contenedorMensaje = new ContenedorMensajes(mensajesEsperados);
					mapaMensajes.put(packet.getAddress(), contenedorMensaje);
				}
			
			}
			
			socket.close();
			
		} catch (SocketException | UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new ServidorUDP().run();
	}

}
