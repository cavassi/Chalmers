import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * A simple command-line parser, inspired by Python's `argparse` module.
 * It has the additional feature that if the user doesn't specify 
 * any arguments at all, the module will ask for them interactively.
 */
public class CommandParser {
    private String progName;
    private String description;
    private LinkedList<Argument> arguments;
    private Argument helpArgument;

    public CommandParser(String progName, String description) {
        this.progName = progName;
        this.description = description;
        arguments = new LinkedList<>();
        helpArgument = addArgument("-h", "--help", "show this help message and exit")
            .makeTrueOption();
    }

    public Argument addArgument(String option, String help) {
        Argument arg = new Argument(option, help);
        arguments.add(arg);
        return arg;
    }

    public Argument addArgument(String longOption, String shortOption, String help) {
        return addArgument(longOption, help).addOption(shortOption);
    }


    public enum Type { STR, INT, BOOL, FLOAT }

    public class Argument {
        List<String> options;
        String dest;
        Type type;
        Object const_;
        Object default_;
        boolean islist;
        Collection<?> choices;
        boolean required;
        String help;

        Argument(String option, String help) {
            this.options = new LinkedList<>();
            this.options.add(option);
            this.dest = option.replaceFirst("^-+", "");
            this.type = Type.STR;
            this.const_ = null;
            this.default_ = null;
            this.islist = false;
            this.choices = null;
            this.required = false;
            this.help = help;
        }

        private void argumentError(String format, Object... args) {
            throw new IllegalArgumentException(String.format(format, args));
        }

        private boolean checkType(Type type, Object value) {
            return (
                type == Type.BOOL ? value instanceof Boolean :
                type == Type.INT ? value instanceof Integer :
                type == Type.FLOAT ? value instanceof Float :
                value instanceof String
            );
        }

        private Argument sanityCheck() {
            if (options == null || options.size() == 0) argumentError("empty options");
            if (help.isBlank()) argumentError("no help text for option: %s", options.get(0));
            for (String opt : options) {
                if (! opt.startsWith("-")) argumentError("option doesn't start with '-': %s", opt);
            }
            if (const_ != null) {
                if (default_ != null || choices != null || required || islist)
                    argumentError("constant flags cannot have default/choices/required/islist");
                if (!checkType(type, const_))
                    argumentError("Wrong type for constant value: %s is not of type %s", const_, type);
            } else {
                if (choices != null) {
                    if (choices.size() == 0) argumentError("empty choices list");
                    Object firstChoice = choices.iterator().next();
                    if (!checkType(type, firstChoice))
                        argumentError("Wrong type for choices: %s is not of type %s", choices, type);
                }
                if (default_ != null) {
                    if (!checkType(type, default_))
                        argumentError("Wrong type for default value: %s is not of type %s", default_, type);
                }
            }
            return this;
        }
    
        public Argument addOption(String option) {
            this.options.add(option); return sanityCheck();
        }

        public Argument setType(Type type) {
            this.type = type; return sanityCheck();
        }

        public Argument makeBoolean() {
            return setType(Type.BOOL);
        }

        public Argument makeInteger() {
            return setType(Type.INT);
        }

        public Argument makeFloat() {
            return setType(Type.FLOAT);
        }

        public Argument setConst(Integer const_) {
            makeInteger(); this.const_ = const_; return sanityCheck();
        }

        public Argument setConst(Float const_) {
            makeFloat(); this.const_ = const_; return sanityCheck();
        }

        public Argument setConst(Boolean const_) {
            makeBoolean(); this.const_ = const_; return sanityCheck();
        }

        public Argument makeTrueOption() {
            return setConst(true);
        }

        public Argument makeFalseOption() {
            return setConst(false);
        }

        public Argument setDefault(Object default_) {
            this.default_ = default_; return sanityCheck();
        }

        public Argument setChoices(Collection<?> choices) {
            this.choices = choices; return sanityCheck();
        }

        public Argument setChoices(Object[] choices) {
            return setChoices(Arrays.asList(choices));
        }

        public Argument makeRequired() {
            this.required = true; return sanityCheck();
        }

        public Argument makeList() {
            this.islist = true; return sanityCheck();
        }
    }


    public class Namespace {
        private Map<String, Object> options;

        Namespace(Map<String, Object> options) {
            this.options = options;
        }

        public boolean hasOption(String opt) {
            return options.containsKey(opt);
        }

        public String getString(String opt) {
            return (String) options.get(opt);
        }

        public int getInteger(String opt) {
            Integer value = (Integer) options.get(opt);
            return value == null ? 0 : value;
        }

        public float getFloat(String opt) {
            Float value = (Float) options.get(opt);
            return value == null ? 0 : value;
        }

        public boolean getBoolean(String opt) {
            Boolean value = (Boolean) options.get(opt);
            return value != null && value;
        }

        public List<Object> getList(String opt) {
            @SuppressWarnings("unchecked")
            List<Object> result = (List) options.get(opt);
            return result;
        }

        public List<String> getStringList(String opt) {
            @SuppressWarnings("unchecked")
            List<String> result = (List) options.get(opt);
            return result;
        }
    }

    public Namespace parseArgs(String[] args) {
        if (args.length == 0) {
            return parseInteractive();
        }
        Map<String, Object> results = new HashMap<>();
        for (int i=0; i < args.length; i++) {
            int argpos = i;
            String option = args[argpos];
            Argument arg = findArgument(option);
            if (arg == null) parsingError(argpos, "unrecognised option: %s", option);
            if (results.containsKey(arg.dest)) parsingError(argpos, "duplicate option: %s", option);

            Object value;
            if (arg.const_ != null) {
                value = arg.const_;
            } else if (!arg.islist) {
                i++;
                if (i >= args.length || args[i].startsWith("-")) 
                    parsingError(argpos, "missing value for option: %s", option);
                value = (
                    arg.type == Type.INT ? Integer.parseInt(args[i]) :
                    arg.type == Type.FLOAT ? Float.parseFloat(args[i]) :
                    args[i]
                );
            } else {
                List<Object> values = new LinkedList<>();
                while (i+1 < args.length && ! args[i+1].startsWith("-")) {
                    i++;
                    values.add(
                        arg.type == Type.INT ? Integer.parseInt(args[i]) :
                        arg.type == Type.FLOAT ? Float.parseFloat(args[i]) :
                        args[i]
                    );
                }
                value = values;
            }
            if (arg.choices != null) {
                if (!arg.choices.contains(value)) parsingError(i, "not a valid choice: %s", value);
            }
            results.put(arg.dest, value);
        }

        if (results.containsKey(helpArgument.dest)) {
            showHelp();
            System.exit(0);
        }

        for (Argument arg : arguments) {
            if (!results.containsKey(arg.dest)) {
                if (arg.required) {
                    parsingError(0, "missing required option: %s", arg.options.get(0));
                } else if (arg.default_ != null) {
                    results.put(arg.dest, arg.default_);
                }
            }
        }
        return new Namespace(results);
    }

    private void parsingError(int pos, String format, Object... args) {
        throw new IllegalArgumentException(
            String.format("(Error parsing argument %d): %s", pos, String.format(format, args))
        );
    }

    private Argument findArgument(String option) {
        for (Argument arg : arguments) {
            for (String opt : arg.options) {
                if (option.equals(opt)) {
                    return arg;
                }
            }
        }
        return null;
    }

    public Namespace parseInteractive() {
        System.err.println("Note: You can also pass command-line arguments.");
        System.out.println();
        showHelp();
        System.out.println();
        System.out.println("Enter values:");

        Scanner stdin = new Scanner(System.in);
        Map<String, Object> results = new HashMap<>();
        for (Argument arg : arguments) {
            if (arg == helpArgument) continue;
            if (arg.islist) continue;
            boolean optional = !arg.required || arg.islist;
            String prompt = " * " + arg.help;
            String choicesString = null;
            if (arg.const_ != null) {
                prompt += String.format("? (yes/+/%s for %s)", arg.const_.toString().toLowerCase(), arg.const_);
            }
            if (arg.choices != null) {
                choicesString = arg.choices.stream()
                    .map(a -> a.toString())
                    .collect(Collectors.joining(", "));
                prompt += String.format(" (one of: %s)", choicesString);
            }
            if (arg.default_ != null) {
                prompt += String.format(" (ENTER for %s)", arg.default_);
            } else if (arg.type == Type.BOOL && (Boolean) arg.const_) {
                prompt += String.format(" (no/-/ENTER for false)");
            } else if (optional) {
                prompt += String.format(" (ENTER for nothing)");
            }
            prompt += ": ";

            Object value = null;
            while (value == null) {
                System.out.print(prompt);
                String inStr = stdin.nextLine().trim();
                try {
                    if (inStr.isEmpty()) {
                        value = arg.default_;
                    } else if (arg.const_ != null) {
                        inStr = inStr.toLowerCase();
                        if (inStr.equals("y") || inStr.equals("yes") || inStr.equals("+")
                                || inStr.equals(arg.const_.toString().toLowerCase())) {
                            value = arg.const_;
                        } else if (inStr.equals("n") || inStr.equals("no") || inStr.equals("-")) {
                            value = arg.default_;
                        } else {
                            System.err.format("ERROR: please answer yes or no\n");
                            value = null;
                            continue;
                        }
                    } else if (arg.type == Type.BOOL) {
                        value = Boolean.parseBoolean(inStr);
                    } else if (arg.type == Type.INT) {
                        value = Integer.parseInt(inStr);
                    } else if (arg.type == Type.FLOAT) {
                        value = Float.parseFloat(inStr);
                    } else {
                        value = inStr;
                    }
                } catch (NumberFormatException e) {
                    System.err.format("ERROR: %s\n", e);
                    value = null;
                    continue;
                }

                if (value != null && arg.choices != null) {
                    if (!arg.choices.contains(value)) {
                        System.err.format("ERROR: value must be one of: %s\n", choicesString);
                        value = null;
                        continue;
                    }
                }

                if (optional) break;
            }

            results.put(arg.dest, value);
        }
        return new Namespace(results);
    }


    private static int HELP_COLUMN = 30;
    public void showHelp() {
        System.out.print("Usage: java " + progName);
        for (Argument arg : arguments) {
            String opt = arg.options.get(0);
            String optStr = opt;
            if (arg.const_ == null) {
                if (arg.choices == null) {
                    optStr += " " + arg.dest.toUpperCase();
                } else {
                    optStr += " " + arg.choices.stream()
                                        .map(a -> a.toString())
                                        .collect(Collectors.joining(",", "{", "}"));
                }
            }
            if (arg.required) {
                System.out.print(" " + optStr);
            } else {
                System.out.print(" [" + optStr + "]");
            }
        }
        System.out.println();
        System.out.println();

        if (description != null && !description.isBlank()) {
            System.out.println(description);
            System.out.println();
        }

        System.out.println("Options:");
        for (Argument arg : arguments) {
            List<String> optStrings = new LinkedList<>();
            for (String opt : arg.options) {
                String optStr = opt;
                if (arg.const_ == null) {
                    if (arg.choices == null) {
                        optStr += " " + arg.dest.toUpperCase();
                    } else {
                        optStr += " " + arg.choices.stream()
                                            .map(a -> a.toString())
                                            .collect(Collectors.joining(",", "{", "}"));
                    }
                }
                optStrings.add(optStr);
            }
            String help = "  " + String.join(", ", optStrings);
            if (help.length() > HELP_COLUMN - 2) {
                help += "\n" + " ".repeat(HELP_COLUMN);
            } else {
                help += " ".repeat(HELP_COLUMN - help.length());
            }
            help += arg.help;
            System.out.println(help);
        }
    }

}

