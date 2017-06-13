package output;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
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
import customers.Client;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
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
import quotes.QuoteItemizationTable.QuoteItems;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author cmeehan
 */
public class QuotePDF {

    public String quoteID, clientName, projectType, startDate, completionDate, siteType, siteDescription, host, url, owned, budget, notes;
    public boolean deadLine, flexible;
    public TableView quoteTableView;
    private Client client = new Client();
    private final String LOGO = getClass().getClassLoader().getResource("resources/letterhead.png").toString();
    private final DateFormat DATE_FORMAT = new SimpleDateFormat("YYYY-MM-dd HH:mm");
    private final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.US);
    private final String USER_HOME = System.getProperty("user.home");

    public void emailPDF(String mailTo) {
        String smtpHost = "mail.cbmwebdevelopment.com";
        int smtpPort = 465;

        String sender = "connor.meehan@cbmwebdevelopment.com";
        String recipient = mailTo;
        String messageContent = "Please find the attached quote as requested. This quote is only for the services listed in the quote. If you feel this quote does not satisfy all of your needs please contact us immediately so that we may ammend the quote. All quotes are to be considered estimates and not final prices. Please note that prior to starting any project either $350 or 50% must be paid in full.\n\nThis email message was automatically generated.\n\nPlease direct any questions or to Connor.Meehan@cbmwebdevelopment.com";
        String subject = "CBM Web Development Invoice";

        Properties properties = new Properties();
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        Session session = Session.getDefaultInstance(properties, null);

        ByteArrayOutputStream outputStream = null;

        try {
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

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Message Sent");
            alert.setHeaderText("Message Successfully Sent");
            alert.showAndWait();

        } catch (IOException | SQLException | MessagingException ex) {
            System.out.println(ex.getMessage());
            System.out.println(Arrays.toString(ex.getStackTrace()));
        } finally {
            // clean off
            if (null != outputStream) {
                try {
                    outputStream.close();
                    outputStream = null;
                } catch (IOException ex) {
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
     * Creates a PDF and saves it to memory for temporary use. 
     * 
     * @param outputStream
     * @throws MalformedURLException
     * @throws IOException
     * @throws SQLException 
     */
    private void generateMemoryPDF(ByteArrayOutputStream outputStream) throws MalformedURLException, IOException, SQLException {
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        Image image = new Image(ImageDataFactory.create(LOGO));
        image.setWidthPercent(50);
        image.setHorizontalAlignment(HorizontalAlignment.CENTER);
        document.add(image);
        document.add(clientInformation());
        document.add(quoteSection());
        document.add(notesArea());
        document.add(terms());
        document.close();
    }

    public void createPDF() throws IOException, FileNotFoundException, MalformedURLException, MalformedURLException, SQLException {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(USER_HOME + "/Documents"));
        chooser.setTitle("Choose where to save the document");
        chooser.setInitialFileName(clientName + " - " + this.quoteID + ".pdf");
        File file = chooser.showSaveDialog(new Stage());
        if (file != null) {
            String dest = file.getAbsolutePath();
            file.getParentFile().mkdirs();
            generatePDF(dest);
        } else {
            System.out.println("Not saved");
        }
    }

    /**
     *
     * @param dest
     * @throws java.io.FileNotFoundException
     * @throws java.net.MalformedURLException
     * @throws IOException
     */
    public void generatePDF(String dest) throws FileNotFoundException, MalformedURLException, IOException, SQLException {

        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        Image image = new Image(ImageDataFactory.create(LOGO));
        image.setWidthPercent(50);
        image.setHorizontalAlignment(HorizontalAlignment.CENTER);
        document.add(image);
        document.add(clientInformation());
        document.add(quoteSection());
        document.add(notesArea());
        document.add(terms());
        document.close();

    }

    private Table clientInformation() throws IOException, SQLException {
        float[] widths = {1, 5, 1, 5};
        Table table = new Table(widths);

        Cell cell;

        cell = new Cell();
        cell.add("Client:");
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);

        cell = new Cell();
        cell.add(this.clientName);
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);

        cell = new Cell();
        cell.add("Email:");
        cell.setBorder(Border.NO_BORDER);
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        cell.setPaddingLeft(50f);
        table.addCell(cell);

        cell = new Cell();
        cell.add(client.getClientEmail(client.clientID(clientName)));
        cell.setFontColor(Color.BLUE);
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);

        cell = new Cell();
        cell.add("Name:");
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);

        cell = new Cell();
        cell.add(client.getClientName(client.clientID(clientName)));
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);

        cell = new Cell();
        cell.add("Tel:");
        cell.setBorder(Border.NO_BORDER);
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        cell.setPaddingLeft(50f);
        table.addCell(cell);

        cell = new Cell();
        cell.add(client.getClientTel(client.clientID(clientName)));
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);

        cell = new Cell();
        cell.add("Project Type:");
        cell.setBorder(Border.NO_BORDER);
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        table.addCell(cell);

        cell = new Cell();
        cell.add(this.projectType);
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);

        cell = new Cell();
        cell.add("Website:");
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        cell.setBorder(Border.NO_BORDER);
        cell.setPaddingLeft(50f);
        table.addCell(cell);

        cell = new Cell();
        cell.add(client.getClientURL(client.clientID(clientName)));
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);

        cell = new Cell();
        cell.add("Webhost:");
        cell.setBorder(Border.NO_BORDER);
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        table.addCell(cell);

        cell = new Cell();
        cell.add(this.host);
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);

        cell = new Cell();
        cell.add("Type:");
        cell.setBorder(Border.NO_BORDER);
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        cell.setPaddingLeft(50f);
        table.addCell(cell);

        cell = new Cell();
        cell.add(client.getClientType(client.clientID(clientName)));
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);

        cell = new Cell();
        cell.add("Start By:");
        cell.setBorder(Border.NO_BORDER);
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        table.addCell(cell);

        cell = new Cell();
        cell.add(this.startDate);
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);

        cell = new Cell();
        String completion = (deadLine) ? "Completion Date:" : "Estimated Completion Date";
        cell.add(completion);
        cell.setBorder(Border.NO_BORDER);
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        cell.setPaddingLeft(50f);
        table.addCell(cell);

        cell = new Cell();
        cell.add(this.completionDate);
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);

        cell = new Cell();
        cell.add("Site Type:");
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);

        cell = new Cell(1, 3);
        cell.add(this.siteType);
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);

        cell = new Cell();
        cell.add("Site Description:");
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);

        cell = new Cell(1, 3);
        cell.add(this.siteDescription);
        cell.setBorder(Border.NO_BORDER);
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
        cell.add("Charge");
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
        ObservableList<QuoteItems> quoteItems = this.quoteTableView.getItems();
        quoteItems.forEach((item) -> {
            table.addCell(new Cell().add(new Paragraph(item.getCategory())));
            table.addCell(new Cell().add(new Paragraph(item.getCost())));
            table.addCell(new Cell().add(new Paragraph(item.getCostUnit())));
            table.addCell(new Cell().add(new Paragraph(item.getCostUnitTotal())));
            table.addCell(new Cell().add(new Paragraph(item.getShortDescription())));
            totals.add(Double.parseDouble(item.getCost()) * Double.parseDouble(item.getCostUnitTotal()));
        });

        Double total = 0.0;
        total = totals.stream().map((d) -> d).reduce(total, (accumulator, _item) -> accumulator + _item);

        cell = new Cell(1, 1);
        cell.add("Total");
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        table.addCell(cell);

        cell = new Cell(1, 4);
        cell.add(CURRENCY_FORMAT.format(total));
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
        terms.add(new Text("\nAll quotes are subject to change without notice.\nAll quotes given are only estimates and not intended to reflect an exact final price. If a final price is lower than the quote given then the lower price will be billed."));
        terms.add(new Text("\nAccepted forms of payment include cash, check, PayPal, and most major credit cards."));
        terms.add(new Text("\nMinimum charge of $350 or 50% down for all new development required prior to starting the project."));
        terms.add(new Text("\nAll other charges not listed here are to be assessed directly to the customer."));

        return terms;
    }
}
