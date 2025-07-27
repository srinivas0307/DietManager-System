#!/bin/bash

# Compile all Java files
echo "Compiling..."
javac -d build/ -sourcepath src/ src/com/dietmanager/**/*.java

if [ $? -eq 0 ]; then
    echo "Compilation successful."

    # Run the main application
    echo "Running DietManager..."
    java -cp build/ com.dietmanager.manager.DietManager
else
    echo "Compilation failed."
fi
