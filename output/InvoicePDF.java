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
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import customers.Client;
import invoices.InvoiceItemizationTable.InvoiceItems;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

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

    public String invoiceID, projectName, clientName, projectType, invoiceType;
    public TableView invoiceTableView;
    private Client client = new Client();
    private final String LOGO = "src/resources/letterhead.png";
    private final DateFormat DATE_FORMAT = new SimpleDateFormat("YYYY-MM-dd HH:mm");

    public void createPDF() throws IOException {
        String DEST = System.getProperty("user.home") + "/Desktop/Invoices" + this.projectName + ".pdf";
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        generatePDF(DEST);
    }

    /**
     *
     * @param dest
     * @throws IOException
     */
    public void generatePDF(String dest) throws FileNotFoundException, MalformedURLException, IOException {

        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        Image image = new Image(ImageDataFactory.create(LOGO));
        image.setWidthPercent(50);
        image.setHorizontalAlignment(HorizontalAlignment.CENTER);
        document.add(image);
        document.add(clientInformation());
        document.add(quoteSection());
        document.add(new Paragraph(terms()));
        document.close();

    }

    private Table clientInformation() throws IOException {
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
        cell.add("Website:");
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);

        cell = new Cell();
        cell.add(client.getClientURL(client.clientID(clientName)));
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);

        cell = new Cell();
        cell.add("Webhost:");
        cell.setBorder(Border.NO_BORDER);
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        cell.setPaddingLeft(50f);
        table.addCell(cell);

        cell = new Cell();
        cell.add(client.getClientHost(client.clientID(clientName)));
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);

        cell = new Cell();
        cell.add("Type:");
        cell.setBorder(Border.NO_BORDER);
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        table.addCell(cell);

        cell = new Cell();
        cell.add(client.getClientType(client.clientID(clientName)));
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);

        cell = new Cell();
        cell.add("Date:");
        cell.setBorder(Border.NO_BORDER);
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        cell.setPaddingLeft(50f);
        table.addCell(cell);

        cell = new Cell();
        cell.add(DATE_FORMAT.format(new Date()));
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);

        return table;
    }

    private Table quoteSection() throws IOException {
        Table table = new Table(new float[]{2,1,1,1,5});
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
        cell.add("Rate Unit");
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
            table.addCell(new Cell().add(new Paragraph(item.getCost())));
            table.addCell(new Cell().add(new Paragraph(item.getCostUnit())));
            table.addCell(new Cell().add(new Paragraph(item.getCostUnitTotal())));
            table.addCell(new Cell().add(new Paragraph(item.getShortDescription())));
            totals.add(Double.parseDouble(item.getCost()) * Double.parseDouble(item.getCostUnitTotal()));
        });
        
        Double total = 0.0;
        total = totals.stream().map((d) -> d).reduce(total, (accumulator, _item) -> accumulator + _item);
        
        cell = new Cell(1,1);
        cell.add("Total");
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        table.addCell(cell);
        
        cell = new Cell(1, 4);
        cell.add(String.valueOf(total));
        cell.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
        cell.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        table.addCell(cell);
        
        return table;
    }

    private String terms() {
        String terms = "All rates are subject to change without notice.\nAll quotes given are only estimates and not intended to reflect an exact final price. If a final price is lower than the quote given then the lower price will be billed.";

        return terms;
    }

    private String documentHeader() {
        String header = "CBM Web Development";
        return header;
    }

}
