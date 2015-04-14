package TwitterAdapter;

/**
 * Created by abc on 4/14/15.
 */
import java.io.FileOutputStream;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
public class HelloWorld {
    public static void main(String args[]) {
        try {
            // step 1: create a Document object
            Document document = new Document();
            // step 2: connect the Document with an OutputStream using a PdfWriter
            PdfWriter.getInstance(document, new FileOutputStream("hello.pdf"));
            // step 3: open the document

            document.open();
            // step 4: add content
            document.add(new Paragraph("Hello World"));
            // step 5: close the document
            document.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
