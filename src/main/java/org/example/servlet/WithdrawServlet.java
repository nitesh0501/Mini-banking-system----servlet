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

public class WithdrawServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

       
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            out.println("Login first!");
        }

        int userId = (Integer) session.getAttribute("userId");


        double withdrawAmount;
        try {
            withdrawAmount = Double.parseDouble(req.getParameter("amount"));
            if (withdrawAmount <= 0) {
                out.println("Invalid withdraw amount!");
            }
        } catch (NumberFormatException e) {
            out.println("Enter a valid number!");
        }

        
        try (Connection conn = Dbutil.getConnection()) {

        
            double currentBalance = 0;
            try (PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT balance FROM users WHERE id = ?")) {
                checkStmt.setInt(1, userId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        currentBalance = rs.getDouble("balance");
                    } else {
                        out.println("User not found!");
                    }
                }
            }

            if (currentBalance < withdrawAmount) {
                out.println("Insufficient balance!");
            }

            
            conn.setAutoCommit(false);
            try {
                
                try (PreparedStatement updateStmt = conn.prepareStatement(
                        "UPDATE users SET balance = balance - ? WHERE id = ?")) {
                    updateStmt.setDouble(1, withdrawAmount);
                    updateStmt.setInt(2, userId);
                    updateStmt.executeUpdate();
                }

               
                try (PreparedStatement insertStmt = conn.prepareStatement(
                        "INSERT INTO transactions (user_id, type, amount) VALUES (?, 'WITHDRAW', ?)")) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setDouble(2, withdrawAmount);
                    insertStmt.executeUpdate();
                }

                conn.commit();
                out.println("Withdraw successful!");
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                out.println("Withdraw failed!");
            } finally {
                conn.setAutoCommit(true); 
            }

        } catch (SQLException e) {
            e.printStackTrace();
            out.println("Database connection error!");
        }

        out.close();
    }
}
