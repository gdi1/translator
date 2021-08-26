package com.example.translatorweb;

import com.example.Translator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "translateServlet", value = "/translate-servlet")
public class TranslateServlet extends HttpServlet {

    public void init(){}

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Cookie translateFrom = new Cookie("translate_from", request.getParameter("translateFrom"));
        Cookie translateTo = new Cookie("translate_to", request.getParameter("translateTo"));

        translateFrom.setMaxAge(60 * 60 * 24);
        translateTo.setMaxAge(60 * 60 * 24);

        response.addCookie(translateFrom);
        response.addCookie(translateTo);

        PrintWriter out = response.getWriter();

        String translation = null;
        if (request.getParameter("translateFrom") != null && request.getParameter("translateTo") != null && request.getParameter("text") != null) {
            translation = Translator.translateSentence(request.getParameter("translateFrom"), request.getParameter("translateTo"), request.getParameter("text"));
        }

        out.println("<!DOCTYPE html>" + "<head>" + "<meta charset=\"UTF-8\">"
                + "<title>Title</title>" + "</head>" + "<body>");
        if (translation != null) {

            out.println("<h1>" + request.getParameter("translateFrom") + ": " + request.getParameter("text") + "</h1>");
            out.println("<h1>" + request.getParameter("translateTo") + ": " + translation + "</h1>");
        } else {
            out.println("<h1>" + "No translation found for this sentence." + "</h1>");
        }
        out.println("<a href=\"translate-form-servlet\">Go Back to Translator</a>");
        out.println("</body>" + "</html>");
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        doGet(request, response);
    }

    public void destroy(){}
}
