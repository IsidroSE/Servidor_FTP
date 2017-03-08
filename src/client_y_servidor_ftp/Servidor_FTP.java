package client_y_servidor_ftp;

import Utils.IO;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/*@author Isidro*/

public class Servidor_FTP extends Thread {
    
    private Socket elSocket;
    private String ruta;
    private String rutaActual;
    private boolean fin;
    
    public Servidor_FTP (Socket elSocket) {
        this.elSocket = elSocket;
        ruta = "C:\\tmp";
        rutaActual = ruta;
        fin = false;
    }
    
//    static final int PORT = 5555;

    @Override
    public void run() {
        
        try {
            
            InputStream is = elSocket.getInputStream();
            OutputStream os = elSocket.getOutputStream();
            
            while (!fin) {

                String peticio = IO.leeLinea(is);
                String [] lesParaules = peticio.split("[ ]+");

                if (lesParaules.length > 2) {
                    IO.escribeLinea("HTTP/1.1 Error 400 Bad Request: la solicitud no pot tindre més de dos parámetres", os);
                    IO.escribeLinea("", os);
                }
                else if (lesParaules[0].equalsIgnoreCase("GET")) {

                    if (lesParaules.length != 2) {
                        IO.escribeLinea("HTTP/1.1 Error 400 Bad Request: la solicitud no te 2 paraules", os);
                        IO.escribeLinea("", os);
                    }
                    else {
                        File elFitxer = new File(rutaActual+"\\"+lesParaules[1]);
                        getFitxer(is, os, elFitxer);
                    }
                }
                else if (lesParaules[0].equalsIgnoreCase("PUT")) {

                    if (lesParaules.length != 2) {
                        IO.escribeLinea("HTTP/1.1 Error 400 Bad Request: la solicitud no te 2 paraules", os);
                        IO.escribeLinea("", os);
                    }
                    else {
                        File elFitxer = new File(rutaActual+"\\"+lesParaules[1]);
                        putFitxer(is, os, elFitxer);
                    }
                }
                else if (lesParaules[0].equalsIgnoreCase("PWD")) {

                    if (lesParaules.length != 1) {
                        IO.escribeLinea("HTTP/1.1 Error 400 Bad Request: la solicitud no te 1 paraula", os);
                        IO.escribeLinea("", os);
                    }
                    else {
                        IO.escribeLinea("HTTP/1.1 OK PWD", os);
                        IO.escribeLinea("", os);
                        IO.escribeLinea(ruta, os);
                    }

                }
                else if (lesParaules[0].equalsIgnoreCase("DIR")) {

                    if (lesParaules.length > 2) {
                        IO.escribeLinea("HTTP/1.1 Error 400 Bad Request: máxim de paraules: 2", os);
                        IO.escribeLinea("", os);
                    }
                    else {
                        dirFitxer(is, os, lesParaules);
                    }

                }
                else if (lesParaules[0].equalsIgnoreCase("CD")) {

                    if (lesParaules.length != 2) {
                        IO.escribeLinea("HTTP/1.1 Error 400 Bad Request: la solicitud no te 2 paraules", os);
                        IO.escribeLinea("", os);
                    }
                    else {
                        cdFitxer(is, os, lesParaules);
                    }

                }
                else if (lesParaules[0].equalsIgnoreCase("EXIT")) {
                    IO.escribeLinea("HTTP/1.1 OK EXIT", os);
                    IO.escribeLinea("", os);
                    fin = true;
                }
                else {
                    IO.escribeLinea("HTTP/1.1 Error 410 Incorrect order: órden incorrecta.", os);
                    IO.escribeLinea("", os);
                }
            }
            is.close();
            os.close();
            elSocket.close();
            Llansador_Servidor.restarConnexio();
            System.out.println("Un fil s'ha tancat.");
        } //end try
        catch (IOException | InterruptedException ex) {
            Logger.getLogger(Servidor_FTP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Servidor_FTP.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void getFitxer (InputStream is, OutputStream os, File elFitxer) throws Exception {
        
        if (!elFitxer.exists()) {
            IO.escribeLinea("HTTP/1.1 Error 404 Not Found : no he trobat el recurs", os);
            IO.escribeLinea("", os);
        }
        else {
            IO.escribeLinea("HTTP/1.1 OK GET", os);
            IO.escribeLinea("", os);

            FileInputStream fis = new FileInputStream(elFitxer);
            int bytesCopiats = IO.copia(fis, os);
            System.out.println("Es trasfereixen "+ bytesCopiats);
            fis.close();
        }
    }
    
    public void putFitxer (InputStream is, OutputStream os, File elFitxer) throws Exception {
        
        if (elFitxer.exists()) {
            IO.escribeLinea("HTTP/1.1 Error 404 Not Found : ya existeix el recurs", os);
            IO.escribeLinea("", os);
        }
        else {
            IO.escribeLinea("HTTP/1.1 OK PUT", os);
            IO.escribeLinea("", os);
            
            FileOutputStream fos = new FileOutputStream(elFitxer);
            int bytesCopiats = IO.copia(is, fos);
            System.out.println("Es trasfereixen "+ bytesCopiats);
            fos.close();
        }
    }
    
    public void dirFitxer (InputStream is, OutputStream os, String [] lesParaules) throws Exception {
        
        if (lesParaules.length == 1) {
            
            File f = new File(rutaActual);
            String [] items = f.list();
            String directoris = "";
            for (String nom:items) {
                File ff = new File(rutaActual+"\\"+nom);

                if (ff.isDirectory()) {
                    directoris += "D "+nom+"\n";
                }
                else {
                    directoris += "F "+nom+"\n";
                }
            }
            
            IO.escribeLinea("HTTP/1.1 OK DIR", os);
            IO.escribeLinea("", os);
            
            IO.escribeLinea(directoris, os);

        }
        else {
            File f = new File(rutaActual+"\\"+lesParaules[1]);
            if (f.exists()) {
                if (f.isDirectory()){
                    String [] items = f.list();
                    String directoris = "";
                    for (String nom:items) {
                        File ff = new File(rutaActual+"\\"+nom);
                        
                        if (ff.isDirectory()) {
                            directoris += "D "+nom+"\n";
                        }
                        else {
                            directoris += "F "+nom+"\n";
                        }
                    }
                    
                    IO.escribeLinea("HTTP/1.1 OK DIR", os);
                    IO.escribeLinea("", os);
                    
                    IO.escribeLinea(directoris, os);

                }
                else {
                    IO.escribeLinea("HTTP/1.1 OK DIR", os);
                    IO.escribeLinea("", os);
                    
                    IO.escribeLinea("F "+lesParaules[1], os);
                }
            }
            else {
                IO.escribeLinea("HTTP/1.1 Error 405 Not Exist: El directorio no existe", os);
                IO.escribeLinea("", os);
            }
        }
    }
    
    public void cdFitxer (InputStream is, OutputStream os, String [] lesParaules) throws Exception {
        if (lesParaules[1].equalsIgnoreCase("..")) {
            
            if (rutaActual.equalsIgnoreCase(ruta)) {
                IO.escribeLinea("HTTP/1.1 Error 408 Not permited: Opción no permitida, ya estás en la raiz", os);
                IO.escribeLinea("", os);
            }
            else {
                IO.escribeLinea("HTTP/1.1 OK CD", os);
                IO.escribeLinea("", os);
                
                String [] d = rutaActual.split("[\\\\]+");
                rutaActual = d[0]+"\\";
                
                for (int i=1 ; i<d.length-1 ; i++) {
                    rutaActual += d[i];
                }
                
                IO.escribeLinea(rutaActual, os);
            }
        }
        else if (lesParaules[1].equalsIgnoreCase("/")) {
            
            IO.escribeLinea("HTTP/1.1 OK CD", os);
            IO.escribeLinea("", os);
            
            rutaActual = ruta;
            IO.escribeLinea(rutaActual, os);
        }
        else {
            
            int count = lesParaules[1].length() - lesParaules[1].replace("\\", "").length();
            
            if (count != 0) {
                rutaActual = ruta;
            }
            
            File f = new File(rutaActual+"\\"+lesParaules[1]);
            
            
            if (f.exists()) {
                if (f.isDirectory()){
                    
                    IO.escribeLinea("HTTP/1.1 OK CD", os);
                    IO.escribeLinea("", os);
                    
                    rutaActual += "\\"+lesParaules[1];
                    IO.escribeLinea(rutaActual, os);
                }
                else {
                    IO.escribeLinea("HTTP/1.1 Error 406 Not Directory: No es un directorio", os);
                    IO.escribeLinea("", os);
                }
            }
            else {
                IO.escribeLinea("HTTP/1.1 Error 405 Not Exist: El directorio no existe", os);
                IO.escribeLinea("", os);
            }
        }
    }
    
    /*Coses per fer:
    
        1. El get i el put copien el fitxer correctament, pero el que recibeix el fitxer es queda atascat en el métode
        de copiar, arreglar-ho. Lo del bufferedreader no funciona amb images i pdf, només en txt*/

}
