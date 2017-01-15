package com.danielacedo.udpfragmentado;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ServidorUDP extends Thread {
	//Cambiar los valores de las constantes por tus IP
	public static final String IP_SERV = "192.168.1.130";
	public static final int PORT_SERV = 5000;
	
	private Map<InetAddress, ContenedorMensajes> mapaMensajes;
	private boolean salir;
	
	public ServidorUDP(){
		mapaMensajes = new HashMap<InetAddress, ContenedorMensajes>();
		salir = false;
	}
	
	@Override
	public void run(){
		try {
			byte[] buffer = new byte[100000];
			DatagramSocket socket = new DatagramSocket(PORT_SERV, InetAddress.getByName(IP_SERV));
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

			while(!salir){
				socket.receive(packet);
				ContenedorMensajes contenedorMensaje = ContenedorMensajes.fromByteArray(packet.getData());
				
				//Si ya tenemos esta IP en el mapa
				if(mapaMensajes.containsKey(packet.getAddress())){
				
					//Si esta lleno lo mostramos y sacamos
					if(contenedorMensaje.estaLleno()){
						System.out.println("Mensaje completado de "+packet.getAddress()+""
								+ "("+contenedorMensaje.getMensajeActual()+"/"+contenedorMensaje.getMensajesEsperados()+"): "+contenedorMensaje.getMensaje());
						mapaMensajes.remove(packet.getAddress());
					}else{
						//Si no esta lleno lo actualizamos
						mapaMensajes.put(packet.getAddress(), contenedorMensaje);
						System.out.println("Mensaje recibido de "+packet.getAddress()+""
								+ "("+contenedorMensaje.getMensajeActual()+"/"+contenedorMensaje.getMensajesEsperados()+"): "+contenedorMensaje.getMensaje());
					}
					
				}else{
					//Si no tenemos la IP le hacemos una entrada
					mapaMensajes.put(packet.getAddress(), contenedorMensaje);
					System.out.println("Mensaje recibido de "+packet.getAddress()+""
							+ "("+contenedorMensaje.getMensajeActual()+"/"+contenedorMensaje.getMensajesEsperados()+"): "+contenedorMensaje.getMensaje());
				}
				
				Arrays.fill(buffer, (byte)0);
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
		new ServidorUDP().start();
	}

}
