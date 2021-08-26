package com.example.translatorweb;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "translateFormServlet", value = "/translate-form-servlet")
public class TranslateFormServlet extends HttpServlet {

    @Override
    public void init() {}

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Cookie[] cookies = request.getCookies();
        String translateFrom = null;
        String translateTo = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("translate_from")) {
                    translateFrom = cookie.getValue();

                } else if (cookie.getName().equals("translate_to")) {
                    translateTo = cookie.getValue();
                }
            }
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>" + "<head>" + "<meta charset=\"UTF-8\">"
                + "<title>Title</title>" + "</head>" + "<body>"
                + "<form action=\"translate-servlet\" method=\"POST\" >" +
                " <label for=\"translateFrom\">Translate from:</label>");

        if (translateFrom != null) {
            System.out.println("translate_from is not null " + translateFrom);
            out.println("<input type=\"text\" id=\"translateFrom\" name=\"translateFrom\" value=" + translateFrom + " required>");
        } else {
            out.println("<input type=\"text\" id=\"translateFrom\" name=\"translateFrom\" required>");
        }

        out.println("<br/>" + "<label for=\"translateTo\">Translate to:</label>");
        if (translateTo != null) {
            System.out.println("translate_to is not null " + translateTo);
            out.println("<input type=\"text\" id=\"translateTo\" name=\"translateTo\" value=" + translateTo + " required>");
        } else {
            out.println("<input type=\"text\" id=\"translateTo\" name=\"translateTo\" required>");
        }

        out.println("<br/>" + "<label for=\"text\">Text:</label>");
        out.println("<input type=\"text\" id=\"text\" name=\"text\" required>");
        out.println(" <input type=\"submit\" value=\"Submit\"/>" + "</form>");
        out.println("<a href=\"index.jsp\">Go Back to Home Page</a>");
        out.println("</body>" + "</html>");
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        doGet(request, response);
    }

    public void destroy() {}
}