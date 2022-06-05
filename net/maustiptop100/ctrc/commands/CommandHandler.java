package net.maustiptop100.ctrc.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class CommandHandler {

    static HashMap<String, CommandExecutor> executorMap;

    public static void setup() {
        executorMap = new HashMap<>();
    }

    public static void handle(String command, String[] args) {
        if(executorMap.containsKey(command))
            executorMap.get(command).runCommand(args);
        else System.out.println("Command not found. Type \"help\" to get a list of commands");
    }

    public static void register(String command, CommandExecutor executor) {
        executorMap.put(command, executor);
    }

    public static void begin() {
        new Thread(() -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while(true) {
                System.out.print("\b~$ ");
                try {
                    String commandWithArgs = reader.readLine();
                    String command = commandWithArgs.split(" ")[0];

                    handle(command, commandWithArgs.split(" "));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
