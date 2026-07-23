#!/bin/bash

# CalBank - Banking Application Launcher

echo "================================"
echo "CalBank - Smart Banking for Everyone"
echo "================================"
echo ""

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Java is not installed!"
    echo "Please install Java 11 or higher from https://www.java.com"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | grep "version" | cut -d'"' -f2 | cut -d'.' -f1)
if [ $JAVA_VERSION -lt 11 ]; then
    echo "Java 11 or higher is required!"
    echo "You have Java $JAVA_VERSION"
    exit 1
fi

echo "Java $JAVA_VERSION detected"
echo ""

# Check if MySQL is running
echo "Checking MySQL connection..."
if ! mysqladmin ping -h localhost -u root -pmynameis123MASTER --silent 2>/dev/null; then
    echo "Warning: MySQL might not be running or credentials may be incorrect."
    echo "Please ensure MySQL is started on localhost:3306"
    echo ""
fi

# Create build directory if it doesn't exist
if [ ! -d "build/classes" ]; then
    echo "Creating build directory..."
    mkdir -p build/classes
fi

# Compile if needed
echo "Compiling CalBank..."
javac -d build/classes -cp "lib/*" -sourcepath src/main src/main/com/calbank/main/CalBankApp.java 2>&1

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
else
    echo "Compilation failed!"
    exit 1
fi

echo ""
echo "Starting CalBank..."
echo ""

# Run the application
java -cp "build/classes:lib/*" com.calbank.main.CalBankApp
