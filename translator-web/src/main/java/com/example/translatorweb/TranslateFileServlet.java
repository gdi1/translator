package com.example.translatorweb;

import com.example.Translator;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

@WebServlet(name = "translateFileServlet", value = "/translate-file-servlet")
public class TranslateFileServlet extends HttpServlet {

    public void init() {
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {


        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        if (!isMultipart) {
            out.println("<!DOCTYPE html>");
            out.println("<head>");
            out.println("<meta charset=\"UTF-8\">");
            out.println("<title>Servlet upload</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<p>No file uploaded</p>");
            out.println("</body>");
            out.println("</html>");
            return;
        }
        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();

// Configure a repository (to ensure a secure temp location is used)
        ServletContext servletContext = this.getServletConfig().getServletContext();
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        factory.setRepository(repository);

// Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);

        File file = new File("fileToTranslate.txt");

        String from = null;
        String to = null;
        String fileName = null;

// Parse the request
        try {
            // Parse the request to get file items.
            List<FileItem> fileItems = upload.parseRequest(request);
            System.out.println(fileItems.size());

            // Process the uploaded file items
            Iterator<FileItem> i = fileItems.iterator();

            while (i.hasNext()) {

                FileItem fi = i.next();
                if (fi.isFormField()) {
                    if (fi.getFieldName().equals("translateFrom")) {
                        from = fi.getString();
                        System.out.println(from);

                    } else if (fi.getFieldName().equals("translateTo")) {
                        to = fi.getString();
                        System.out.println(to);
                    }
                } else {
                    fileName = fi.getName();
                    fi.write(file);
                }
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        Cookie translateFrom = new Cookie("translate_from_file", from);
        Cookie translateTo = new Cookie("translate_to_file", to);

        translateFrom.setMaxAge(60 * 60 * 24);
        translateTo.setMaxAge(60 * 60 * 24);

        response.addCookie(translateFrom);
        response.addCookie(translateTo);

        String translation = null;
        if (from != null && to != null) {

            translation = Translator.translateFile(from, to, file.getAbsolutePath());
        }

        out.println("<!DOCTYPE html>" + "<head>" + "<meta charset=\"UTF-8\">"
                + "<title>Title</title>" + "</head>" + "<body>");
        if (translation != null) {

            out.println("<h1>" + from + ": " + fileName + "</h1>");
            out.println("<h1>" + to + ": " + translation + "</h1>");
        } else {
            out.println("<h1>" + "No translation found for this sentence." + "</h1>");
        }
        out.println("<a href=\"translate-file-form-servlet\">Go Back to Translator</a>");
        out.println("</body>" + "</html>");
    }


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        doGet(request, response);
    }

    public void destroy() {
    }
}
