package Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Joan Gerard
 */
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
public class Utilitats {

    private final static BufferedReader entradaConsola = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));

    //.........................................................................
    //.........................................................................
    public static void muestraMensajeC(String mensaje) {
        System.out.println(mensaje);
    }

    //.........................................................................
    //.........................................................................
    public static int leerEnteroC(String mensaje) {
        return Integer.parseInt(leerTextoC(mensaje));
    }

    //.........................................................................
    //.........................................................................
    public static double leerRealC(String mensaje) {
        return Double.parseDouble(leerTextoC(mensaje));
    }

    //.........................................................................
    //.........................................................................
    public static String leerTextoC(String mensaje) {
        try {
            System.out.print(mensaje);
            return entradaConsola.readLine();
        } // ()
        catch (IOException ex) {
            return "";
        }
    } // ()

    //.........................................................................
    //.........................................................................
    public static String leerTextoG(String mensaje) {
        String leido = JOptionPane.showInputDialog(mensaje);
        if (leido == null) {
            return "";
        }
        return leido;
    } // ()

    //.........................................................................
    //.........................................................................
    public static int leerEnteroG(String mensaje) {
        int v = Integer.parseInt(leerTextoG(mensaje));
        return v;
    } // ()

    //.........................................................................
    //.........................................................................
    public static double leerRealG(String mensaje) {
        return Double.parseDouble(leerTextoG(mensaje));
    } // ()

    //.........................................................................
    //.........................................................................
    public static void muestraMensajeG(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje);
    } // ()

    //.........................................................................
    //.........................................................................
    public static void copiarBytes(byte[] destino, int inicioDestino, byte[] origen, int inicioOrigen, int longitud) {
        for (int i = 0; i <= longitud - 1; i++) {
            destino[inicioDestino + i] = origen[inicioOrigen + i];
        }
    } // ()

    //.........................................................................
    //.........................................................................
    public static byte[] juntarBytes(byte[]  
        ... lista) {
		int totalBytes = 0;
        for (byte[] arr : lista) {
            totalBytes += arr.length;
        }

        byte[] result = new byte[totalBytes];
        int inicioDestino = 0;

        for (byte[] arr : lista) {
            copiarBytes(result, inicioDestino, arr, 0, arr.length);
            inicioDestino += arr.length;
        }

        return result;
    } // ()

    //Mostra les adreces IP de la maquina on s'executa
    public static String mostrarAdreces() throws UnknownHostException, SocketException {
        String res = "Direcciones IP y Físicas de la máquina:\n";
        List<InetAddress> addrList = new ArrayList<InetAddress>();
        Enumeration<NetworkInterface> enumNI = NetworkInterface.getNetworkInterfaces();
        while (enumNI.hasMoreElements()) {
            NetworkInterface ifc = enumNI.nextElement();
            if (ifc.isUp()) {
                Enumeration<InetAddress> enumAdds = ifc.getInetAddresses();
                while (enumAdds.hasMoreElements()) {
                    InetAddress addr = enumAdds.nextElement();
                    addrList.add(addr);
                    res += addr.getHostAddress()+"\n";
                }
            }

        }
        return res;
    }
} // class
