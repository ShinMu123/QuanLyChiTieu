/* =====================================================================
   QuanLyChiTieu - Rebuild script
   Usage: copy, paste, execute once in SQL Server Management Studio.
   ===================================================================== */

-- Target database
IF DB_ID(N'QuanLyChiTieuDB') IS NULL
BEGIN
    CREATE DATABASE QuanLyChiTieuDB;
END;
GO

USE QuanLyChiTieuDB;
GO

/* 1. Drop tables in FK order */
IF OBJECT_ID(N'dbo.Transactions', N'U') IS NOT NULL DROP TABLE dbo.Transactions;
IF OBJECT_ID(N'dbo.Category', N'U')     IS NOT NULL DROP TABLE dbo.Category;
IF OBJECT_ID(N'dbo.Users', N'U')        IS NOT NULL DROP TABLE dbo.Users;
GO

/* 2. Core tables */
CREATE TABLE dbo.Users (
    user_id     INT IDENTITY(1,1) PRIMARY KEY,
    username    NVARCHAR(50)  NOT NULL UNIQUE,
    password    NVARCHAR(255) NOT NULL,
    created_at  DATETIME2(0)  NOT NULL CONSTRAINT DF_Users_CreatedAt DEFAULT SYSUTCDATETIME()
);

CREATE TABLE dbo.Category (
    category_id INT IDENTITY(1,1) PRIMARY KEY,
    name        NVARCHAR(100) NOT NULL,
    type        NVARCHAR(10)  NOT NULL,
    CONSTRAINT CK_Category_Type CHECK (type IN (N'income', N'expense')),
    CONSTRAINT UQ_Category UNIQUE (name, type)
);

CREATE TABLE dbo.Transactions (
    trans_id    INT IDENTITY(1,1) PRIMARY KEY,
    user_id     INT           NOT NULL,
    category_id INT           NOT NULL,
    amount      DECIMAL(18,2) NOT NULL CHECK (amount > 0),
    trans_date  DATE          NOT NULL,
    note        NVARCHAR(255) NULL,
    deleted     BIT           NOT NULL CONSTRAINT DF_Transactions_Deleted DEFAULT (0),
    created_at  DATETIME2(0)  NOT NULL CONSTRAINT DF_Transactions_CreatedAt DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_Transactions_Users FOREIGN KEY (user_id) REFERENCES dbo.Users(user_id),
    CONSTRAINT FK_Transactions_Category FOREIGN KEY (category_id) REFERENCES dbo.Category(category_id)
);
GO

/* 3. Seed shared categories */
INSERT INTO dbo.Category (name, type) VALUES
    (N'Lương', N'income'),
    (N'Thu nhập khác', N'income'),
    (N'Ăn uống', N'expense'),
    (N'Đi lại', N'expense'),
    (N'Nhà cửa', N'expense'),
    (N'Mua sắm', N'expense'),
    (N'Sức khỏe', N'expense');
GO

/* 4. Helpful indexes */
CREATE INDEX IX_Transactions_User_Date ON dbo.Transactions(user_id, trans_date DESC);
CREATE INDEX IX_Transactions_Category ON dbo.Transactions(category_id);
GO

/* 5. Sample usage */
-- Đăng ký user
INSERT INTO dbo.Users(username, password) VALUES (N'admin', N'123');

-- Đăng nhập user
SELECT user_id, username, password
FROM dbo.Users
WHERE username = N'admin';

-- Thêm giao dịch thu
INSERT INTO dbo.Transactions(user_id, category_id, amount, trans_date, note)
VALUES (1, (SELECT category_id FROM dbo.Category WHERE name = N'Lương' AND type = N'income'), 15000000, '2026-03-01', N'Lương tháng 3');

-- Thêm giao dịch chi
INSERT INTO dbo.Transactions(user_id, category_id, amount, trans_date, note)
VALUES (1, (SELECT category_id FROM dbo.Category WHERE name = N'Ăn uống' AND type = N'expense'), 45000, '2026-03-02', N'Ăn sáng');

DECLARE @UserID INT = 1, @Month INT = 3, @Year INT = 2026;

-- Tổng thu
SELECT COALESCE(SUM(t.amount), 0) AS TotalIncome
FROM dbo.Transactions t
JOIN dbo.Category c ON c.category_id = t.category_id
WHERE t.user_id = @UserID AND t.deleted = 0
  AND c.type = N'income'
  AND YEAR(t.trans_date) = @Year AND MONTH(t.trans_date) = @Month;

-- Tổng chi
SELECT COALESCE(SUM(t.amount), 0) AS TotalExpense
FROM dbo.Transactions t
JOIN dbo.Category c ON c.category_id = t.category_id
WHERE t.user_id = @UserID AND t.deleted = 0
  AND c.type = N'expense'
  AND YEAR(t.trans_date) = @Year AND MONTH(t.trans_date) = @Month;

-- Số dư
SELECT COALESCE(SUM(CASE WHEN c.type = N'income' THEN t.amount ELSE -t.amount END), 0) AS Balance
FROM dbo.Transactions t
JOIN dbo.Category c ON c.category_id = t.category_id
WHERE t.user_id = @UserID AND t.deleted = 0
  AND YEAR(t.trans_date) = @Year AND MONTH(t.trans_date) = @Month;

-- Lọc giao dịch theo tháng / năm
SELECT t.trans_id, t.trans_date, c.name, c.type, t.amount, t.note
FROM dbo.Transactions t
JOIN dbo.Category c ON c.category_id = t.category_id
WHERE t.user_id = @UserID AND t.deleted = 0
  AND YEAR(t.trans_date) = @Year AND MONTH(t.trans_date) = @Month
ORDER BY t.trans_date DESC;
GO
