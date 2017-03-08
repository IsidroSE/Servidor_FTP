package client_y_servidor_ftp;

import static java.lang.Thread.sleep;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/*@author Isidro*/

public class Llansador_Servidor {
    
    static final int PORT = 5555;
    static int numConexions = 0;
    
    public static void main(String[] args) {
        
        int numConexions = 0;
        
        try {
            
            ServerSocket ss = new ServerSocket(PORT);
            System.out.println("Esperant connexions en "+PORT);
            
            while (true) {
                if (numConexions > 20) {
                    System.out.println("Número máxim de conexions permeses superat.");
                    sleep(1000);
                }
                else {
                    Socket elSocket = ss.accept();
                    System.out.println("S'ha connectat: "+elSocket.getRemoteSocketAddress().toString());
                    Servidor_FTP fs = new Servidor_FTP(elSocket);
                    fs.start();
                    numConexions++;
                }
            }
        }
        catch (Exception ex) {
            Logger.getLogger(Llansador_Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void restarConnexio() {
        numConexions--;
    }

}
