package com.danielacedo.udpfragmentado;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ContenedorMensajes implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int mensajeActual;
	private int mensajesEsperados;
	private StringBuilder mensaje;
	private String ultimoSegmento;
	
	public ContenedorMensajes(int mensajesEsperados){
		this.mensajesEsperados = mensajesEsperados;
		this.mensajeActual = 0;
		this.mensaje = new StringBuilder();
	}

	public int getMensajeActual() {
		return mensajeActual;
	}

	public void setMensajeActual(int mensajeActual) {
		this.mensajeActual = mensajeActual;
	}

	public int getMensajesEsperados() {
		return mensajesEsperados;
	}

	public void setMensajesEsperados(int mensajesEsperados) {
		this.mensajesEsperados = mensajesEsperados;
	}

	public String getMensaje() {
		return mensaje.toString();
	}
	
	
	
	public void setUltimoSegmento(String ultimoSegmento) {
		this.ultimoSegmento = ultimoSegmento;
	}

	public String getUltimoSegmento() {
		return ultimoSegmento;
	}

	public void anadirMensaje(String msg){
		
		if(mensajeActual!= mensajesEsperados){
			mensajeActual++;
			mensaje.append(msg);
		}
	}
	
	public boolean estaLleno(){
		if(mensajeActual == mensajesEsperados){
			return true;
		}
		
		return false;
	}
	
	public static byte[] toByteArray(ContenedorMensajes obj) throws IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		ObjectOutputStream out = null;
	
		  out = new ObjectOutputStream(stream);   
		  out.writeObject(obj);
		  out.flush();
		  byte[] yourBytes = stream.toByteArray();		  
		  	
		  stream.close();
		  out.close();
		  
		return yourBytes;
	}
	
	public static ContenedorMensajes fromByteArray(byte[] array) throws ClassNotFoundException, IOException{
		ByteArrayInputStream stream = new ByteArrayInputStream(array);
		ObjectInputStream in = null;
		
		in = new ObjectInputStream(stream);
		
		ContenedorMensajes obj = (ContenedorMensajes)in.readObject();
		
		stream.close();
		in.close();
		
		return obj;
		
	}
	
}
