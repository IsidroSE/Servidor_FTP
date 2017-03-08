package client_y_servidor_ftp;

import Utils.IO;
import Utils.Utilitats;
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

public class Client {
    
    static final int PORT = 5555;

    public static void main(String[] args) {
        
        boolean fin = false;
        
        try {
            
            Socket elSocket = new Socket("localhost", PORT);
            
            
            
            InputStream is = elSocket.getInputStream();
            OutputStream os = elSocket.getOutputStream();
            
            while (!fin) {
            
                String comando = Utilitats.leerTextoC("Escriu el comando: ");
                IO.escribeLinea(comando, os);

                String resposta = IO.leeLinea(is);
                String blanc = IO.leeLinea(is);

                String trosResposta[]=resposta.split("[ ]+");

                //avaluem el codi d'error
                String codi = trosResposta[1];
                String orde = trosResposta[2];

                String error = "";

                for (int i=2 ; i<trosResposta.length ; i++) {
                    error += trosResposta[i]+" ";
                }

                if (codi.equalsIgnoreCase("Error")) {
                    Utilitats.muestraMensajeC(error);
                }
                else if (codi.equalsIgnoreCase("OK")) {
                    if (orde.equalsIgnoreCase("GET")) {

                        String [] lesParaules = comando.split("[ ]+");

                        Utilitats.muestraMensajeC("Transferint el fitxer");
                        String fitxerDesti = "copia_"+lesParaules[1];
                        /*Nota: Así pose esta ruta per no embrutar altres directoris, pero podría ser atra perfectament.*/
                        File f = new File ("C:\\tmp\\"+fitxerDesti);
                        FileOutputStream fos = new FileOutputStream(f);

                        int numBytes = IO.copia(is, fos);

                        Utilitats.muestraMensajeC(numBytes + " Bytes rebuts.");

                        fos.close();
                    }
                    else if (orde.equalsIgnoreCase("PUT")) {
                        
                        String [] lesParaules = comando.split("[ ]+");
                        
                        File elFitxer = new File("C:\\tmp\\cliente\\"+lesParaules[1]);
                        FileInputStream fis = new FileInputStream(elFitxer);
                        int bytesCopiats = IO.copia(fis, os);
                        System.out.println("Es trasfereixen "+ bytesCopiats);
                        fis.close();
                    }
                    else if (orde.equalsIgnoreCase("DIR")) {
                        resposta = IO.leeLinea(is);
                        Utilitats.muestraMensajeC(resposta);
                    }
                    else if (orde.equalsIgnoreCase("CD")) {
                        resposta = IO.leeLinea(is);
                        Utilitats.muestraMensajeC(resposta);
                    }
                    else if (orde.equalsIgnoreCase("PWD")) {
                        resposta = IO.leeLinea(is);
                        Utilitats.muestraMensajeC(resposta);
                    }
                    else if (orde.equalsIgnoreCase("EXIT")) {
                        fin = true;
                    }
                }
            }
            is.close();
            os.close();
            elSocket.close();
            System.out.println("El client s'ha tancat.");
            
        }
        catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
