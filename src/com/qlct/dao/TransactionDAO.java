package com.qlct.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.qlct.db.DBConnection;
import com.qlct.model.Category;
import com.qlct.model.Summary;
import com.qlct.model.Transaction;
import com.qlct.util.DateUtils;

public class TransactionDAO {
        private static final String BASE_SELECT = """
            SELECT t.TransactionID,
               t.UserID,
               t.CategoryID,
               t.Amount,
               t.TransactionDate,
               t.Note,
               ISNULL(t.IsDeleted, 0) AS IsDeleted,
               c.CategoryName,
               c.Type
            FROM TRANSACTIONS t
            INNER JOIN CATEGORIES c ON c.CategoryID = t.CategoryID
            WHERE t.UserID = ?
              AND ISNULL(t.IsDeleted, 0) = 0
            """;

        private static final String SELECT_BY_USER_SQL = BASE_SELECT + " ORDER BY t.TransactionDate DESC, t.TransactionID DESC";

        private static final String SELECT_BY_MONTH_SQL = BASE_SELECT
            + " AND MONTH(t.TransactionDate) = ? AND YEAR(t.TransactionDate) = ?"
            + " ORDER BY t.TransactionDate DESC, t.TransactionID DESC";

    private static final String INSERT_SQL = """
            INSERT INTO TRANSACTIONS (UserID, CategoryID, Amount, TransactionDate, Note, IsDeleted, CreatedAt)
            VALUES (?, ?, ?, ?, ?, 0, SYSDATETIME())
            """;

    private static final String SOFT_DELETE_SQL = "UPDATE TRANSACTIONS SET IsDeleted = 1 WHERE TransactionID = ?";

    private static final String SUMMARY_SQL = """
            SELECT
                COALESCE(SUM(CASE WHEN c.Type = N'Thu' THEN t.Amount END), 0) AS TotalIncome,
                COALESCE(SUM(CASE WHEN c.Type = N'Chi' THEN t.Amount END), 0) AS TotalExpense
            FROM TRANSACTIONS t
            INNER JOIN CATEGORIES c ON c.CategoryID = t.CategoryID
            WHERE t.UserID = ?
              AND ISNULL(t.IsDeleted, 0) = 0
              AND MONTH(t.TransactionDate) = ?
              AND YEAR(t.TransactionDate) = ?
            """;

    public List<Transaction> getByUser(int userId) {
        return executeTransactionQuery(SELECT_BY_USER_SQL, ps -> ps.setInt(1, userId));
    }

    public List<Transaction> getByMonth(int userId, int month, int year) {
        return executeTransactionQuery(SELECT_BY_MONTH_SQL, ps -> {
            ps.setInt(1, userId);
            ps.setInt(2, month);
            ps.setInt(3, year);
        });
    }

    public Transaction insert(Transaction transaction) {
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, transaction.getUserId());
            statement.setInt(2, transaction.getCategory().getCategoryId());
            statement.setBigDecimal(3, transaction.getAmount());
            statement.setDate(4, DateUtils.toSqlDate(transaction.getTransactionDate()));
            statement.setNString(5, transaction.getNote());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    transaction.setTransId(keys.getInt(1));
                }
            }
            transaction.setDeleted(false);
            return transaction;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to insert transaction", e);
        }
    }

    public void softDelete(int transactionId) {
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(SOFT_DELETE_SQL)) {
            statement.setInt(1, transactionId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to delete transaction", e);
        }
    }

    public Summary calculateMonthlySummary(int userId, int month, int year) {
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(SUMMARY_SQL)) {
            statement.setInt(1, userId);
            statement.setInt(2, month);
            statement.setInt(3, year);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    BigDecimal income = rs.getBigDecimal("TotalIncome");
                    BigDecimal expense = rs.getBigDecimal("TotalExpense");
                    return new Summary(income, expense);
                }
            }
            return new Summary();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to summarize transactions", e);
        }
    }

    private List<Transaction> executeTransactionQuery(String sql, SqlConfigurer configurer) {
        List<Transaction> transactions = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            configurer.accept(statement);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to load transactions", e);
        }
        return transactions;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransId(rs.getInt("TransactionID"));
        transaction.setUserId(rs.getInt("UserID"));
        transaction.setAmount(rs.getBigDecimal("Amount"));
        transaction.setTransactionDate(DateUtils.toLocalDate(rs.getDate("TransactionDate")));
        transaction.setNote(rs.getNString("Note"));
        boolean deleted = rs.getBoolean("IsDeleted");
        if (rs.wasNull()) {
            deleted = false;
        }
        transaction.setDeleted(deleted);

        Category category = new Category();
        category.setCategoryId(rs.getInt("CategoryID"));
        category.setName(rs.getString("CategoryName"));
        category.setType(rs.getString("Type"));
        transaction.setCategory(category);
        return transaction;
    }

    @FunctionalInterface
    private interface SqlConfigurer {
        void accept(PreparedStatement statement) throws SQLException;
    }
}








