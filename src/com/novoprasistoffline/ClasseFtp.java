package com.novoprasistoffline;

import android.os.Environment;
import java.io.FileInputStream;
 
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
 
import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;
 
public class ClasseFtp {

    public DataHelper dh;
    
    public boolean Testar() throws UnknownHostException
    {
    	 FTPClient ftp = new FTPClient();
         try {
             ftp.connect("ftp.prasist.com.br");
             ftp.login("prasistc", "conquistado");             
             ftp.logout();
             ftp.disconnect();             
             
         } catch (SocketException e) {             
             e.printStackTrace();
             
         } catch (IOException e) {              
             e.printStackTrace();
             
         }catch (Exception e) {
             e.printStackTrace();             
         }	
         return true;
    }
        
    public boolean Enviar(String sArquivoOrigem, String sPastaDestino, String ftpServerAddress,String userName,String password) {
    
        FTPClient ftpclient = new FTPClient();
        FileInputStream fis;
        boolean result;
                
        try {
                ftpclient.connect(ftpServerAddress);

                // verify success

                int reply = ftpclient.getReplyCode();

                if (!FTPReply.isPositiveCompletion(reply)) {
                	System.out.println("STATUS_CONNECTION_REFUSED");                	                	
                }
                              
                result = ftpclient.login(userName, password);

                if (result == true) {
                        System.out.println("Logged in Successfully !");
                } else {
                        System.out.println("Login Fail!");
                        return false;
                }
                ftpclient.setFileType(FTP.BINARY_FILE_TYPE);

                //ftpclient.changeWorkingDirectory("/" + sPastaDestino);
                ftpclient.changeWorkingDirectory(sPastaDestino);

                File file = new File(Environment.getExternalStorageDirectory() + "/enviar/" + sArquivoOrigem);
                String testName = file.getName();
                fis = new FileInputStream(file);

                ftpclient.enterLocalPassiveMode();
                // Upload file to the ftp server
                result = ftpclient.storeFile(testName, fis);

                if (result == true) {
                    System.out.println("Arquivo enviado com sucesso");
                } else {
                    System.out.println("Falha no envio");
                    return false;
                }
                ftpclient.logout();

        } catch (SocketException e) {
            e.printStackTrace();            
        } catch (FTPConnectionClosedException e) {
        	e.printStackTrace();
        } catch (IOException ex) {
        	ex.printStackTrace();                    
        } finally {
                try {
                        ftpclient.disconnect();                        
                } catch (FTPConnectionClosedException e) {
                        System.out.println(e);
                } catch (IOException ex) {
                    Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        
        return true;        
        
    }       
}