package org.example.servlet;

import org.example.util.Dbutil;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            out.println("You are not logged in!first.");
            
        }

        int userId = (Integer) session.getAttribute("userId");

        try (Connection conn =  Dbutil.getConnection()) {
            String sql = "SELECT name, email, account_no, balance FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                out.println("Welcome, " + rs.getString("name") );
                out.println("Email: " + rs.getString("email") );
                out.println("Account No: " + rs.getString("account_no"));
                out.println("Balance: ₹" + rs.getDouble("balance") );
            } else {
                out.println("User not found!");
            }

        } catch (SQLException e) {
            e.printStackTrace(out);
        }
    }
}
