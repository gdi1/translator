package com.example.translatorweb;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "translateFileFormServlet", value = "/translate-file-form-servlet")
public class TranslateFileFormServlet extends HttpServlet {

    public void init() {}

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Cookie[] cookies = request.getCookies();
        String translateFrom = null;
        String translateTo = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("translate_from_file")) {
                    translateFrom = cookie.getValue();

                } else if (cookie.getName().equals("translate_to_file")) {
                    translateTo = cookie.getValue();
                }
            }
        }
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>" + "<head>" + "<meta charset=\"UTF-8\">"
                + "<title>Title</title>" + "</head>" + "<body>"
                + "<form action=\"translate-file-servlet\" method=\"POST\" enctype=\"multipart/form-data\">" +
                " <label for=\"translateFrom\">Translate from:</label>");

        if (translateFrom != null) {
            out.println("<input type=\"text\" id=\"translateFrom\" name=\"translateFrom\" value=" + translateFrom + " required>");

        } else {
            out.println("<input type=\"text\" id=\"translateFrom\" name=\"translateFrom\" required>");
        }

        out.println("<br/>" + "<label for=\"translateTo\">Translate to:</label>");
        if (translateTo != null) {
            out.println("<input type=\"text\" id=\"translateTo\" name=\"translateTo\" value=" + translateTo + " required>");

        } else {
            out.println("<input type=\"text\" id=\"translateTo\" name=\"translateTo\" required>");
        }

        out.println("<br/>" + "<label for=\"file\">File:</label>");
        out.println("<input type=\"file\" id=\"file\" name=\"file\" required>");
        out.println(" <input type=\"submit\" value=\"Submit\"/>" + "</form>");
        out.println("<a href=\"index.jsp\">Go Back to Home Page</a>");
        out.println("</body>" + "</html>");
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        doGet(request, response);
    }

    public void destroy() {}
}
