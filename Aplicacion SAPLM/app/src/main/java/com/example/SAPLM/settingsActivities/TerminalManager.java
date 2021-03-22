package com.example.SAPLM.settingsActivities;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;

import com.example.SAPLM.bluetoothActivities.UnifiedTransmissionProtocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TerminalManager {

    public static void handleTerminalInput(String newTerminalInput){

    }

    public static List<String> currentParameters = new ArrayList<>();

    public static List<TerminalCommand> commandsList = new ArrayList<>(Arrays.asList(
            new TerminalCommand("COMPOSE", new Runnable() {
                @Override
                public void run() {
                    UnifiedTransmissionProtocol.CommandType command = UnifiedTransmissionProtocol.CommandType.definedCommandList.stream().filter(p-> p.getCommandTag().equals((currentParameters.get(0)))).findFirst().orElse(null);
                    UnifiedTransmissionProtocol.Module target = UnifiedTransmissionProtocol.modulesList.stream().filter(p-> p.getModuleAlias().equals((currentParameters.get(1)))).findFirst().orElse(null);

                    if (command==null) {
                        SpannableStringBuilder builder= new SpannableStringBuilder();
                        builder.append("Tipo de comando \"")
                                .append(currentParameters.get(0), new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                                .append("\" no reconocido");
                        DeveloperConsoleActivity.sendTextToTerminal(builder);
                        return;
                    }

                    if (target==null) {
                        SpannableStringBuilder builder= new SpannableStringBuilder();
                        builder.append("Alias de modulo \"")
                                .append(currentParameters.get(1), new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                                .append("\" no encontrado");
                        DeveloperConsoleActivity.sendTextToTerminal(builder);
                        return;
                    }


                    System.out.println( insertSpaces(UnifiedTransmissionProtocol.bytesToHex( UnifiedTransmissionProtocol.getCommand_buffer() ), " ", 2) );
                }
            })
    ));

    public static class TerminalCommand{
        private String commandBaseTag;
        private Runnable commandHandler;

        TerminalCommand(String commandBaseTag, Runnable commandHandler){
            this.commandBaseTag = commandBaseTag;
            this.commandHandler = commandHandler;
        }

        public void handleCommand(){

        }
    }

    public static String insertSpaces(String text, String insert, int period) {
        Pattern p = Pattern.compile("(.{" + period + "})", Pattern.DOTALL);
        Matcher m = p.matcher(text);
        return m.replaceAll("$1" + insert);
    }
}
