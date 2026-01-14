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

public class TransactionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            out.println("You are not logged in! Please <a href='login.html'>login</a> first.");
            
        }

        int userId = (Integer) session.getAttribute("userId");

        try (Connection conn = Dbutil.getConnection()) {
            String sql = "SELECT type, amount, timestamp FROM transactions WHERE user_id = ? ORDER BY timestamp DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            out.println("<h2>Transaction History</h2>");
            out.println("<table border='1'>");
            out.println("<tr><th>Type</th><th>Amount</th><th>Date & Time</th></tr>");

            boolean hasTransactions = false;
            while (rs.next()) {
                hasTransactions = true;
                out.println("<tr>");
                out.println("<td>" + rs.getString("type") + "</td>");
                out.println("<td>₹" + rs.getDouble("amount") + "</td>");
                out.println("<td>" + rs.getTimestamp("timestamp") + "</td>");
                out.println("</tr>");
            }

            if (!hasTransactions) {
                out.println("<tr><td colspan='3'>No transactions found!</td></tr>");
            }

            out.println("</table>");

        } catch (SQLException e) {
            e.printStackTrace(out);
        }
    }
}
