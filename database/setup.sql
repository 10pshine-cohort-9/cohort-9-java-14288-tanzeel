-- Contact Management System Database Setup Script
-- SQL Server

-- Create Database
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'ContactManagementDB')
BEGIN
    CREATE DATABASE ContactManagementDB;
END
GO

USE ContactManagementDB;
GO

-- Create Users Table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'users')
BEGIN
    CREATE TABLE users (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        first_name NVARCHAR(100) NOT NULL,
        last_name NVARCHAR(100) NOT NULL,
        email NVARCHAR(255) NOT NULL UNIQUE,
        phone_number NVARCHAR(20) UNIQUE,
        password NVARCHAR(255) NOT NULL,
        created_at DATETIME DEFAULT GETDATE() NOT NULL,
        updated_at DATETIME DEFAULT GETDATE() NOT NULL
    );
END
GO

-- Create Contacts Table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'contacts')
BEGIN
    CREATE TABLE contacts (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        user_id BIGINT NOT NULL,
        first_name NVARCHAR(100) NOT NULL,
        last_name NVARCHAR(100) NOT NULL,
        title NVARCHAR(100) NOT NULL,
        company NVARCHAR(200),
        notes NVARCHAR(MAX),
        created_at DATETIME DEFAULT GETDATE() NOT NULL,
        updated_at DATETIME DEFAULT GETDATE() NOT NULL,
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );
END
GO

-- Create Contact Emails Table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'contact_emails')
BEGIN
    CREATE TABLE contact_emails (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        contact_id BIGINT NOT NULL,
        email NVARCHAR(255) NOT NULL,
        label NVARCHAR(50) NOT NULL,
        FOREIGN KEY (contact_id) REFERENCES contacts(id) ON DELETE CASCADE
    );
END
GO

-- Create Contact Phones Table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'contact_phones')
BEGIN
    CREATE TABLE contact_phones (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        contact_id BIGINT NOT NULL,
        phone_number NVARCHAR(20) NOT NULL,
        label NVARCHAR(50) NOT NULL,
        FOREIGN KEY (contact_id) REFERENCES contacts(id) ON DELETE CASCADE
    );
END
GO

-- Create Indexes for better performance
CREATE NONCLUSTERED INDEX IX_users_email ON users(email);
CREATE NONCLUSTERED INDEX IX_users_phone ON users(phone_number);
CREATE NONCLUSTERED INDEX IX_contacts_user_id ON contacts(user_id);
CREATE NONCLUSTERED INDEX IX_contacts_first_name ON contacts(first_name);
CREATE NONCLUSTERED INDEX IX_contacts_last_name ON contacts(last_name);
CREATE NONCLUSTERED INDEX IX_contact_emails_contact_id ON contact_emails(contact_id);
CREATE NONCLUSTERED INDEX IX_contact_phones_contact_id ON contact_phones(contact_id);

GO

-- Print confirmation message
PRINT 'Database setup completed successfully!';
PRINT 'Database Name: ContactManagementDB';
PRINT 'Tables Created: users, contacts, contact_emails, contact_phones';
