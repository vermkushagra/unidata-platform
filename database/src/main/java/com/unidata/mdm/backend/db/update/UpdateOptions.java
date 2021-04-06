package com.unidata.mdm.backend.db.update;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.io.CharStreams;

/**
 * @author Pavel Alexeev.
 * @created 2015-12-28 19:46.
 */
public class UpdateOptions {
    public static final String DEFAULT_PROPERTIES_FILE = "gradle.properties";

    @DynamicParameter(names = {"-f", "--fly", "--flyway"}, description = "Flyway options. See options in http://flywaydb.org/documentation/maven/\n" +
        "\tF.e.:\n" +
        "\t\t-f flyway.url=jdbc:postgresql://localhost:5432/egais --fly flyway.user=myUser --flyway flyway.password=superPass\n" +
        "\t\tYou may also just provide filename like: @" + DEFAULT_PROPERTIES_FILE + " then such options will be read from there (for backward compatibility also databaseUrl, databaseUser and databasePassword supported).")
    public Map<String, String> parameters = new HashMap<>();

    @Parameter(description = "Help", hidden = true)
    private List<String> props = new ArrayList<>();

    @Parameter(names = {"-h", "--help"}, description = "Help", help = true)
    public boolean help = false;

    /**
     * For backward compatibility accept and convert next keys also:
     * databaseUrl as flyway.url
     * databaseUser as flyway.user
     * databasePassword as flyway.password
     */
    protected void backwardCompatibilitySupport() {
        if (parameters.isEmpty() && !props.isEmpty()) { // Direct read from properties file
            props.forEach(it-> {
                    String[] s = it.split("\\s*=\\s*");
                    parameters.put(s[0], s[1]);
                }
            );
        }

        if (parameters.containsKey("databaseUrl") && !parameters.containsKey("flyway.url"))
            parameters.put("flyway.url", parameters.get("databaseUrl"));
        if (parameters.containsKey("databaseUser") && !parameters.containsKey("flyway.user"))
            parameters.put("flyway.user", parameters.get("databaseUser"));
        if (parameters.containsKey("databasePassword") && !parameters.containsKey("flyway.password"))
            parameters.put("flyway.password", parameters.get("databasePassword"));

        validate();
    }

    /**
     * Unfortunately there no interface to check properties set presence in DynamicParameters map
     */
    private void validate() {
        if (!parameters.containsKey("flyway.url") || !parameters.containsKey("flyway.user") || !parameters.containsKey("flyway.password"))
            throw new IllegalArgumentException("At least flyway.url (databaseUrl), flyway.user (databaseUser) and flyway.password (databasePassword) MUST be provided");
    }

    public static Map<String, String> parse(String[] args){
        if (0 == args.length){
            System.out.println("You have invoke me without arguments (see --help), trying loading settings from ./" + DEFAULT_PROPERTIES_FILE + " file for backward capabilities");
            args = new String[]{"@" + DEFAULT_PROPERTIES_FILE};
        }

        UpdateOptions options = new UpdateOptions();
        JCommander jCommander = new JCommander(options, args);

        if (options.help) {
            guessTerminalWidth(jCommander);
            jCommander.usage();
            System.exit(0);
        }
        options.backwardCompatibilitySupport();

        return options.parameters;
    }

    /**
     * Try gracefully determine console width. Linux only.
     *
     * @param jCommander object on what called setColumnSize() if determined successfully.
     */
    public static void guessTerminalWidth(JCommander jCommander) {
        try{
            jCommander.setColumnSize(
                Integer.valueOf(
                    CharStreams.toString(
                        new InputStreamReader(
                            Runtime.getRuntime().exec(new String[]{"bash", "-c", "tput cols 2> /dev/tty"}).getInputStream()
                            ,"UTF-8"
                        )
                    ).trim()
                )
            );
        }
        catch(IOException ignore){}
    }
}
