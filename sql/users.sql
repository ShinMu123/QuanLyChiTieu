CREATE TABLE Users (
    user_id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(100) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);
GO

CREATE OR ALTER PROCEDURE sp_login
    @Username NVARCHAR(100),
    @Password NVARCHAR(255)
AS
BEGIN
    SET NOCOUNT ON;

    IF NOT EXISTS (SELECT 1 FROM Users WHERE username = @Username)
    BEGIN
        SELECT 1 AS status_code,
               N'Tên đăng nhập không tồn tại' AS status_message,
               NULL AS user_id,
               NULL AS username;
        RETURN;
    END;

    DECLARE @UserId INT;
    DECLARE @CurrentPassword NVARCHAR(255);

    SELECT @UserId = user_id,
           @CurrentPassword = password
    FROM Users
    WHERE username = @Username;

    IF (@CurrentPassword <> @Password)
    BEGIN
        SELECT 2 AS status_code,
             N'Mật khẩu không chính xác' AS status_message,
               NULL AS user_id,
               NULL AS username;
        RETURN;
    END;

    SELECT 0 AS status_code,
            N'Đăng nhập thành công' AS status_message,
           @UserId AS user_id,
           @Username AS username;
END;
GO

CREATE OR ALTER PROCEDURE sp_reset_password
    @Username NVARCHAR(100),
    @NewPassword NVARCHAR(255)
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE Users
    SET password = @NewPassword
    WHERE username = @Username;
END;
GO
