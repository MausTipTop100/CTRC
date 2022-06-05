package net.maustiptop100.ctrc.commands;

public class HELP implements CommandExecutor {

    @Override
    public void runCommand(String[] args) {
        System.out.println("\n"
                + "help                                            shows this list of commands\n"
                + "exit                                            closes the program\n"
                + "\n= OPEN A PROJECT =\n"
                + "create                                          create a new project\n"
                + "open                                            open an existing project\n"
        );
    }
}
