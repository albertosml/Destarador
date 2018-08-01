/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Destarador;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.printing.PDFPageable;

/**
 *
 * @author albertosml
 */
public class Resultado {
    
    private String ruta_pdf;
    
    public void capturarPantalla(String r, int x, int y, int width, int height) {
            // Hago captura
            BufferedImage captura = null;
            try {
                captura = new Robot().createScreenCapture(
                        new Rectangle(x,y,width,height));
            } catch (AWTException ex) {
                Logger.getLogger(Inicio.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Creo el nombre del archivo
            Date d = Date.from(Instant.now());

            int mes = d.getMonth() + 1; // Convierto mes
            String month;
            if(mes < 10) month = "0" + Integer.toString(mes);
            else month = Integer.toString(mes);

            int dia = d.getDate(); // Convierto día del mes
            String day;
            if(dia < 10) day = "0" + Integer.toString(dia);
            else day = Integer.toString(dia);

            int hora = d.getHours(); // Convierto hora
            String hour;
            if(hora < 10) hour = "0" + Integer.toString(hora);
            else hour = Integer.toString(hora);

            int minutos = d.getMinutes(); // Convierto minutos
            String minutes;
            if(minutos < 10) minutes = "0" + Integer.toString(minutos);
            else minutes = Integer.toString(minutos);
            
            int segundos = d.getSeconds(); // Convierto segundos
            String seconds;
            if(segundos < 10) seconds = "0" + Integer.toString(segundos);
            else seconds = Integer.toString(segundos);

            String ruta_defecto = r + "/captura_" + Integer.toString(d.getYear() + 1900) 
                    + month + day + "_" + hour + minutes + seconds;
            
            // Genero pdf con imagen
            PDDocument documento = new PDDocument();
            try {
                PDPage page = new PDPage(PDRectangle.A4);
                documento.addPage(page);
                PDImageXObject pdImage = LosslessFactory.createFromImage(documento, captura);
                PDPageContentStream contenido = new PDPageContentStream(documento,page);
                contenido.drawImage(pdImage,0,170);
                contenido.close();
                ruta_pdf = ruta_defecto + ".pdf";
                documento.save(ruta_pdf);
                documento.close();
            } catch (IOException ex) {
                Logger.getLogger(Inicio.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public void imprimirPantalla(String r, int x, int y, int width, int height) throws IOException, PrinterException {
        this.capturarPantalla(r, x, y, width, height);
        
        PDDocument documento = PDDocument.load(new File(ruta_pdf));
        
        PrinterJob impresora = PrinterJob.getPrinterJob();
        
        Logger logger = Logger.getLogger("mx.hash.impresionpdf.Impresor");
        logger.log(Level.INFO,"Mostrando diálogo de impresión");
        if(impresora.printDialog() == true) {
            impresora.setPageable(new PDFPageable(documento));
            logger.log(Level.INFO,"Imprimiendo documento");
            impresora.print();
        }
    }
}
