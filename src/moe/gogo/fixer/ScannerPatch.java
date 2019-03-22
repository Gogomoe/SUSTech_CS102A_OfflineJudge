package moe.gogo.fixer;

import java.util.Scanner;

public class ScannerPatch {

    private static Scanner scanner;

    public static Scanner getScanner() {
        return scanner;
    }

    public static void setScanner(Scanner scanner) {
        ScannerPatch.scanner = scanner;
    }
}
