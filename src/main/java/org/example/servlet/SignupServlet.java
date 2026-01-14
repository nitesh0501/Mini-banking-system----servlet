package org.example.servlet;

import org.example.util.Dbutil;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class SignupServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        res.setContentType("text/plain");
        PrintWriter out = res.getWriter();

        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String accountNo = req.getParameter("account_no");

        if (name == null || email == null || password == null || accountNo == null ||
                name.isEmpty() || email.isEmpty() || password.isEmpty() || accountNo.isEmpty()) {
            out.println("All fields are required");
            return;
        }


        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        try (Connection conn = Dbutil.getConnection()) {

            String sql = "INSERT INTO users(name, email, password, account_no) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, hashedPassword);
            ps.setString(4, accountNo);

            ps.executeUpdate();
            out.println("Signup successful");

        } catch (Exception e) {
            e.printStackTrace();
            out.println("Error: " + e.getMessage());
        }
    }
}
