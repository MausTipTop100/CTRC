package net.maustiptop100.ctrc;

import net.maustiptop100.ctrc.commands.CREATE;
import net.maustiptop100.ctrc.commands.CommandHandler;
import net.maustiptop100.ctrc.commands.HELP;
import net.maustiptop100.ctrc.commands.OPEN;

import javax.swing.*;

public class Main {

    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        System.out.println();
        System.out.println("   =====  =====  =====   =====");
        System.out.println("  |         |   |     | |");
        System.out.println("  |         |   |=====  |");
        System.out.println("  |         |   |  \\    |");
        System.out.println("   =====    |   |   \\    =====  v1.0 by MausTipTop100");
        System.out.println();
        System.out.println("Type help to get a list of commands.");

        CommandHandler.setup();

        CommandHandler.register("help", new HELP());
        CommandHandler.register("exit", args_ -> System.exit(0));
        CommandHandler.register("create", new CREATE());
        CommandHandler.register("open", new OPEN());

        CommandHandler.begin();
    }

}
