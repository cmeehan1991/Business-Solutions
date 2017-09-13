package com.cbmwebdevelopment.output;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.cbmwebdevelopment.customers.Client;
import com.cbmwebdevelopment.invoices.InvoiceItemizationTable.InvoiceItems;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author cmeehan
 */
public class InvoicePDF {

    public String invoiceID, clientName, project, invoiceType, invoiceTitle, notes, billTo, paymentDueDate, invoiceStatus, totalDue;
    public TableView invoiceTableView;
    private final Client client = new Client();
    
    private final Clipboard clipboard = Clipboard.getSystemClipboard();
    private final ClipboardContent content = new ClipboardContent();
    
    private final String LOGO = getClass().getClassLoader().getResource("resources/letterhead.png").toString();
    private final DateFormat DATE_FORMAT = new SimpleDateFormat("YYYY-MM-dd HH:mm");
    private final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.US);
    private final String USER_HOME = System.getProperty("user.home");

    /**
     * Sends an email with a PDF attachment
     * @param mailTo
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     */
    public void emailPDF(String mailTo) throws URISyntaxException, IOException{
        String smtpHost = "mail.cbmwebdevelopment.com";
        int smtpPort = 465;
        
        String sender = "connor.meehan@cbmwebdevelopment.com";
        String recipient = mailTo;
        String messageContent = "Please find the attached invoice for your records. Please note that all invoices are to be paid in full within 30 days of the first invoice, unless otherwise noted on the invoice.\n\nThis message was automatically generated.";
        String subject = "CBM Web Development Invoice";
        
        Properties properties = new Properties();
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        Session session = Session.getDefaultInstance(properties, null);
        
        ByteArrayOutputStream outputStream = null;
        
        try{
            // Construct the text body part
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setText(messageContent);
            
            // now write the PDF content to the output stream
            outputStream = new ByteArrayOutputStream();
            generateMemoryPDF(outputStream);
            byte[] bytes = outputStream.toByteArray();
            
            // Construct the pdf body part
            DataSource dataSource = new ByteArrayDataSource(bytes, "application/pdf");
            MimeBodyPart pdfBodyPart = new MimeBodyPart();
            pdfBodyPart.setDataHandler(new DataHandler(dataSource));
            pdfBodyPart.setFileName("Invoice.pdf");
            
            // Construct the mime multi part
            MimeMultipart mimeMultiPart = new MimeMultipart();
            mimeMultiPart.addBodyPart(textBodyPart);
            mimeMultiPart.addBodyPart(pdfBodyPart);
            
            // create the sender/recipient addresses
            InternetAddress iaSender = new InternetAddress(sender);
            InternetAddress iaRecipient = new InternetAddress(recipient);
            
            // Construct the mime message
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setSender(iaSender);
            mimeMessage.setFrom(iaSender);
            mimeMessage.setSubject(subject);
            mimeMessage.setRecipient(Message.RecipientType.TO, iaRecipient);
            mimeMessage.setContent(mimeMultiPart);
            
            Transport.send(mimeMessage);
            
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Message Sent");
            alert.setHeaderText("Message Successfully Sent");
            alert.showAndWait();
            
        }catch(IOException | SQLException | MessagingException ex){
            System.out.println(ex.getMessage());
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }finally{
            // clean off
            if(null != outputStream){
                try{
                    outputStream.close();
                    outputStream = null;
                }catch(IOException ex){
                    System.out.println(ex.getMessage());
                }
            }
        }
        
    }
    
    public void printPDF() throws IOException {
        PrinterJob job = PrinterJob.getPrinterJob();
        boolean doPrint = job.printDialog();
        if (doPrint == true) {
            try {
                job.print();
            } catch (PrinterException ex) {
                System.out.println(ex);
            }
        }
    }

    /**
     * Create a PDF that is saved to the user's machine. 
     * 
     * @throws IOException
     * @throws FileNotFoundException
     * @throws MalformedURLException
     * @throws SQLException 
     */
    public void createPDF() throws IOException, FileNotFoundException, MalformedURLException, SQLException {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(USER_HOME + "/Documents"));
        chooser.setTitle("Choose where to save the document");
        chooser.setInitialFileName(this.invoiceTitle + ".pdf");
        File file = chooser.showSaveDialog(new Stage());
        if (file != null) {
            String dest = file.getAbsolutePath();
            file.getParentFile().mkdirs();
            generatePDF(dest);
        } else {
            System.out.println("Not saved");
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error Saving Invoice");
            alert.setContentText("There was an error saving the invoice. Please try again or contact your systems administrator for assistance.");
            alert.showAndWait();
        }
    }

    private void generateMemoryPDF(ByteArrayOutputStream outputStream) throws MalformedURLException, IOException, SQLException{
    
        // Create the document and open for editing
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Get the logo and align it
        Image image = new Image(ImageDataFactory.create(LOGO));
        image.setWidthPercent(50);
        image.setHorizontalAlignment(HorizontalAlignment.CENTER);

        // Add all of the elements to the document. 
        document.add(image);
        document.add(clientInformation());
        document.add(quoteSection());
        document.add(notesArea());
        document.add(terms());

        // Close the document for editing.
        document.close();

    }
    
    /**
     * Generate the PDF
     * 
     * @param dest
     * @throws java.io.FileNotFoundException
     * @throws java.net.MalformedURLException
     * @throws IOException
     * @throws java.sql.SQLException
     */
    public void generatePDF(String dest) throws FileNotFoundException, MalformedURLException, IOException, SQLException {

        // Create the document and open for editing
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Get the logo and align it
        Image image = new Image(ImageDataFactory.create(LOGO));
        image.setWidthPercent(50);
        image.setHorizontalAlignment(HorizontalAlignment.CENTER);

        // Add all of the elements to the document. 
        document.add(image);
        document.add(clientInformation());
        document.add(quoteSection());
        document.add(notesArea());
        document.add(terms());

        // Close the document for editing.
        document.close();

    }

    private Table clientInformation() throws IOException, SQLException {
        float[] widths = {1, 5, 1, 5};
        Table table = new Table(widths);
        table.setMarginTop(50f);
        table.setMarginBottom(25f);
        Cell cell;

        cell = new Cell();
        cell.add("Invoice ID:");
        cell.setBorder(Border.NO_BORDER);
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        table.addCell(cell);

        cell = new Cell();
        cell.add(this.invoiceID);
        cell.setBorder(Border.NO_BORDER);
        cell.setPaddingRight(50f);
        cell.setMarginRight(50f);
        table.addCell(cell);
        
        cell = new Cell();
        cell.add("Date:");
        cell.setBorder(Border.NO_BORDER);
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        table.addCell(cell);

        cell = new Cell();
        cell.add(DATE_FORMAT.format(new Date()));
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);

        cell = new Cell();
        cell.add("Client:");
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);

        cell = new Cell();
        cell.add(this.clientName);
        cell.setBorder(Border.NO_BORDER);
        cell.setMarginRight(50f);
        cell.setPaddingRight(50f);
        table.addCell(cell);

        cell = new Cell(1, 2);
        cell.add("Bill To:");
        cell.setTextAlignment(TextAlignment.LEFT);
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        table.addCell(cell);

        // Blank cell;
        cell = new Cell(0, 2);
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);
        
        cell = new Cell(2, 2);
        cell.add(this.billTo);
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA));
        cell.setMinHeight(50);
        table.addCell(cell);

        cell = new Cell();
        cell.add("Payment Due:");
        cell.setBorder(Border.NO_BORDER);
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        table.addCell(cell);

        cell = new Cell();
        cell.add(this.paymentDueDate);
        cell.setBorder(Border.NO_BORDER);
        cell.setPaddingRight(50f);
        cell.setMarginRight(50f);
        table.addCell(cell);

        return table;
    }

    private Table quoteSection() throws IOException {
        Table table = new Table(new float[]{2, 1, 1, 1, 5});
        table.setWidthPercent(100f);
        Cell cell;

        cell = new Cell();
        cell.add("Category");
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        cell.setTextAlignment(TextAlignment.CENTER);
        table.addCell(cell);

        cell = new Cell();
        cell.add("Rate");
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        cell.setTextAlignment(TextAlignment.CENTER);
        table.addCell(cell);

        cell = new Cell();
        cell.add("per");
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        cell.setTextAlignment(TextAlignment.CENTER);
        table.addCell(cell);

        cell = new Cell();
        cell.add("Unit(s)");
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        cell.setTextAlignment(TextAlignment.CENTER);
        table.addCell(cell);

        cell = new Cell();
        cell.add("Short Description");
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        cell.setTextAlignment(TextAlignment.CENTER);
        table.addCell(cell);

        List<Double> totals = new ArrayList<>();

        // Loop through table here
        ObservableList<InvoiceItems> invoiceItems = this.invoiceTableView.getItems();
        invoiceItems.forEach((item) -> {
            table.addCell(new Cell().add(new Paragraph(item.getCategory())));
            table.addCell(new Cell().add(new Paragraph(CURRENCY_FORMAT.format(Double.parseDouble(item.getCost())))));
            table.addCell(new Cell().add(new Paragraph(item.getCostUnit())));
            table.addCell(new Cell().add(new Paragraph(item.getCostUnitTotal())));
            table.addCell(new Cell().add(new Paragraph(item.getShortDescription())));
            totals.add(Double.parseDouble(item.getCost()) * Double.parseDouble(item.getCostUnitTotal()));
        });

        Double total = 0.0;
        total = totals.stream().map((d) -> d).reduce(total, (accumulator, _item) -> accumulator + _item);

        cell = new Cell(1, 1);
        cell.add("Subtotal");
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA));
        table.addCell(cell);

        cell = new Cell(1, 4);
        cell.add(CURRENCY_FORMAT.format(total));
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA));
        cell.setTextAlignment(TextAlignment.RIGHT);
        table.addCell(cell);

        cell = new Cell(1, 1);
        cell.add("Total Due:");
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        table.addCell(cell);
        
        Double invoiceTotal = (Double.parseDouble(totalDue) <= total) ? Double.parseDouble(totalDue) : total;
        cell = new Cell(1, 4);
        cell.add(CURRENCY_FORMAT.format(invoiceTotal));
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        cell.setTextAlignment(TextAlignment.RIGHT);
        table.addCell(cell);

        return table;
    }

    private Table notesArea() throws IOException {
        Table table = new Table(1);
        table.setMarginTop(10f);
        table.setWidthPercent(100f);
        Cell cell;

        cell = new Cell();
        cell.add("Notes:");
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);

        cell = new Cell();
        String projectNotes = (this.notes != null) ? this.notes : "N/A";
        cell.add(projectNotes);
        cell.setTextAlignment(TextAlignment.JUSTIFIED);
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);

        return table;
    }

    private Paragraph terms() throws IOException {
        Paragraph terms = new Paragraph();
        terms.add(new Text("Terms & Conditions").setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD)).setTextAlignment(TextAlignment.CENTER).setFontSize(18f).setHorizontalAlignment(HorizontalAlignment.CENTER));
        terms.add(new Text("\nAll rates are subject to change without notice.\nAll quotes given are only estimates and not intended to reflect an exact final price. If a final price is lower than the quote given then the lower price will be billed.").setFont(PdfFontFactory.createFont(FontConstants.HELVETICA)).setTextAlignment(TextAlignment.CENTER));
        terms.add(new Text("\nAccepted forms of payment include cash, check, PayPal, and most major credit cards."));
        terms.add(new Text("\nMinimum charge of $350 or 50% down for all new development required prior to starting the project."));
        terms.add(new Text("\nAll other charges not listed here are to be assessed directly to the customer."));

        return terms;
    }

    private String documentHeader() {
        String header = "CBM Web Development";
        return header;
    }

}
