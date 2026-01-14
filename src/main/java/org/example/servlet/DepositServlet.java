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

public class DepositServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            out.println("Login first!");
            out.close();
            return;
        }

        int userId = (Integer) session.getAttribute("userId");

       
        double depositAmount;
        try {
            depositAmount = Double.parseDouble(req.getParameter("amount"));
            if (depositAmount <= 0) {
                out.println("Invalid deposit amount!");
                out.close();
                return;
            }
        } catch (NumberFormatException e) {
            out.println("Enter a valid number!");
            out.close();
            return;
        }

      
        try (Connection conn = Dbutil.getConnection()) {

            // 3a. Check if user exists
            try (PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT id FROM users WHERE id = ?")) {
                checkStmt.setInt(1, userId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next()) {
                        out.println("User not found!");
                        out.close();
                        return;
                    }
                }
            }

            
            conn.setAutoCommit(false); // start transaction
            try {
                // Update balance
                try (PreparedStatement updateStmt = conn.prepareStatement(
                        "UPDATE users SET balance = balance + ? WHERE id = ?")) {
                    updateStmt.setDouble(1, depositAmount);
                    updateStmt.setInt(2, userId);
                    updateStmt.executeUpdate();
                }

                
                try (PreparedStatement insertStmt = conn.prepareStatement(
                        "INSERT INTO transactions (user_id, type, amount) VALUES (?, 'DEPOSIT', ?)")) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setDouble(2, depositAmount);
                    insertStmt.executeUpdate();
                }

                conn.commit(); // commit if both succeed
                out.println("Deposit successful!");
            } catch (SQLException e) {
                conn.rollback(); // rollback if anything fails
                e.printStackTrace();
                out.println("Deposit failed!");
            } 

        } catch (SQLException e) {
            e.printStackTrace();
            out.println("Database connection error!");
        }

        out.close();
    }
}
