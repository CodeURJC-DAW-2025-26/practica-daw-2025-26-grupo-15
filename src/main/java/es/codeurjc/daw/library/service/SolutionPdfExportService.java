package es.codeurjc.daw.library.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import es.codeurjc.daw.library.model.Exercise;
import es.codeurjc.daw.library.model.Solution;

@Service
public class SolutionPdfExportService {

    private static final DateTimeFormatter EXPORT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final Color BRAND_INK = new Color(70, 54, 95);       
    private static final Color BRAND_DARK = new Color(47, 38, 64);      
    private static final Color BRAND_PAPER = new Color(242, 242, 246);  
    private static final Color BRAND_SKY = new Color(142, 156, 224);    
    private static final Color BRAND_POWDER = new Color(184, 205, 242); 
    private static final Color BRAND_MINT = new Color(182, 242, 225);   

    public byte[] generateSolutionPdf(Solution solution) {
        if (solution == null) {
            throw new IllegalArgumentException("Solution is required to generate PDF");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 42f, 42f, 110f, 70f);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            writer.setPageEvent(new DsgramPageEvent());
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 19, Font.BOLD, BRAND_INK);
            Font sectionFont = new Font(Font.HELVETICA, 12, Font.BOLD, BRAND_DARK);
            Font bodyFont = new Font(Font.HELVETICA, 11, Font.NORMAL, BRAND_DARK);
            Font labelFont = new Font(Font.HELVETICA, 10, Font.BOLD, BRAND_DARK);

            Paragraph title = new Paragraph("DSGram Solution Dossier", titleFont);
            title.setSpacingAfter(14f);
            document.add(title);

            Exercise exercise = solution.getExercise();

            PdfPTable exerciseContext = new PdfPTable(1);
            exerciseContext.setWidthPercentage(100);
            exerciseContext.setSpacingAfter(12f);
            exerciseContext.addCell(titleCell("Exercise context", sectionFont, BRAND_POWDER, BRAND_SKY));
            PdfPCell exerciseBody = new PdfPCell();
            exerciseBody.setBackgroundColor(BRAND_PAPER);
            exerciseBody.setBorderColor(BRAND_POWDER);
            exerciseBody.setPadding(10f);
            exerciseBody.addElement(row("Exercise title: ", safe(exercise != null ? exercise.getTitle() : null), labelFont, bodyFont));
            exerciseBody.addElement(row("Exercise description: ", safe(exercise != null ? exercise.getDescription() : null), labelFont, bodyFont));
            exerciseContext.addCell(exerciseBody);
            document.add(exerciseContext);

            PdfPTable solutionDetail = new PdfPTable(1);
            solutionDetail.setWidthPercentage(100);
            solutionDetail.setSpacingAfter(12f);
            solutionDetail.addCell(titleCell("Solution detail", sectionFont, BRAND_MINT, BRAND_POWDER));
            PdfPCell solutionBody = new PdfPCell();
            solutionBody.setBackgroundColor(Color.WHITE);
            solutionBody.setBorderColor(BRAND_POWDER);
            solutionBody.setPadding(10f);
            solutionBody.addElement(row("Solution name: ", safe(solution.getName()), labelFont, bodyFont));
            solutionBody.addElement(row("Author: ", safe(solution.getOwner() != null ? solution.getOwner().getName() : null), labelFont, bodyFont));
            solutionBody.addElement(row("Exported at: ", EXPORT_DATE_FORMAT.format(java.time.LocalDateTime.now()), labelFont, bodyFont));
            solutionBody.addElement(new Paragraph(" "));
            solutionBody.addElement(row("Solution Description: ", safe(solution.getDescription()),labelFont, bodyFont));
            solutionDetail.addCell(solutionBody);
            document.add(solutionDetail);

            byte[] imageBytes = extractImageBytes(solution);

            PdfPTable imageBlock = new PdfPTable(1);
            imageBlock.setWidthPercentage(100);
            imageBlock.addCell(titleCell("Solution image", sectionFont, BRAND_POWDER, BRAND_SKY));
            PdfPCell imageCell = new PdfPCell();
            imageCell.setBackgroundColor(Color.WHITE);
            imageCell.setBorderColor(BRAND_POWDER);
            imageCell.setPadding(12f);

            try {
                Image image = Image.getInstance(imageBytes);
                float maxWidth = PageSize.A4.getWidth() - document.leftMargin() - document.rightMargin() - 24f;
                float maxHeight = PageSize.A4.getHeight() - document.topMargin() - document.bottomMargin() - 250f;
                image.scaleToFit(maxWidth, maxHeight);
                image.setAlignment(Element.ALIGN_CENTER);
                imageCell.addElement(image);
            } catch (Exception e) {
                throw new RuntimeException("Solution image is invalid and could not be rendered in PDF");
            }

            imageBlock.addCell(imageCell);
            document.add(imageBlock);

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating solution PDF", e);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    private PdfPCell titleCell(String title, Font font, Color backgroundColor, Color borderColor) {
        PdfPCell cell = new PdfPCell(new Paragraph(title, font));
        cell.setBackgroundColor(backgroundColor);
        cell.setBorderColor(borderColor);
        cell.setPadding(8f);
        return cell;
    }

    private Paragraph row(String label, String value, Font labelFont, Font bodyFont) {
        Paragraph p = new Paragraph();
        p.add(new Phrase(label, labelFont));
        p.add(new Phrase(value, bodyFont));
        p.setSpacingAfter(5f);
        return p;
    }

    private byte[] extractImageBytes(Solution solution) {
        if (solution.getSolImage() == null || solution.getSolImage().getImageFile() == null) {
            throw new RuntimeException("Solution image is required to generate PDF");
        }

        Blob imageBlob = solution.getSolImage().getImageFile();
        try {
            return imageBlob.getBytes(1, (int) imageBlob.length());
        } catch (SQLException e) {
            throw new RuntimeException("Could not read solution image from database", e);
        }
    }

    private String safe(Object value) {
        if (value == null) {
            return "N/A";
        }
        String text = value.toString().trim();
        return text.isEmpty() ? "N/A" : text;
    }

    private static class DsgramPageEvent extends PdfPageEventHelper {
        private final Font headerBrandFont = new Font(Font.HELVETICA, 14, Font.BOLD, Color.WHITE);
        private final Font headerSubtitleFont = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.WHITE);
        private final Font footerFont = new Font(Font.HELVETICA, 9, Font.NORMAL, BRAND_DARK);

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            Rectangle page = document.getPageSize();

            PdfContentByte under = writer.getDirectContentUnder();
            under.saveState();

            under.setColorFill(BRAND_PAPER);
            under.rectangle(page.getLeft(), page.getBottom(), page.getWidth(), page.getHeight());
            under.fill();

            under.setColorFill(BRAND_DARK);
            under.rectangle(page.getLeft(), page.getTop() - 66f, page.getWidth(), 66f);
            under.fill();

            under.setColorFill(BRAND_SKY);
            under.rectangle(page.getLeft(), page.getTop() - 74f, page.getWidth(), 8f);
            under.fill();

            under.setColorFill(BRAND_MINT);
            under.rectangle(page.getLeft(), page.getBottom(), page.getWidth(), 12f);
            under.fill();

            under.restoreState();

            PdfContentByte over = writer.getDirectContent();
            ColumnText.showTextAligned(over, Element.ALIGN_LEFT, new Phrase("DSGram", headerBrandFont),
                    document.leftMargin(), page.getTop() - 30f, 0f);
            ColumnText.showTextAligned(over, Element.ALIGN_LEFT, new Phrase("Corporate solution export", headerSubtitleFont),
                    document.leftMargin(), page.getTop() - 46f, 0f);

            String pageLabel = "Page " + writer.getPageNumber();
            ColumnText.showTextAligned(over, Element.ALIGN_RIGHT, new Phrase(pageLabel, footerFont),
                    page.getRight() - document.rightMargin(), page.getBottom() + 26f, 0f);
        }
    }
}
