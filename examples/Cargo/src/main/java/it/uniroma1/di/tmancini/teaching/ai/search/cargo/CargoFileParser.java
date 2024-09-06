package it.uniroma1.di.tmancini.teaching.ai.search.cargo;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CargoFileParser {
        private String[] planes;
        private String[] airports;
        private String[] cargoes;
        private List<String> initial_state;
        private List<String> goal_state;

        public CargoFileParser(String file_path) {
                System.out.println("Current working directory: " + new File(".").getAbsolutePath()); // Debugging
                process_input(file_path);
        }

        public void process_input(String file_path) {
                try {
                        File input_file = new File(file_path);
                        Scanner scanner = new Scanner(input_file);

                        while (scanner.hasNextLine()) {
                                String line = scanner.nextLine();

                                if (line.startsWith("PLANES:")) {
                                        planes = parseObjects(line, "PLANES:");
                                } else if (line.startsWith("AIRPORTS:")) {
                                        airports = parseObjects(line, "AIRPORTS:");
                                } else if (line.startsWith("CARGOES:")) {
                                        cargoes = parseObjects(line, "CARGOES:");
                                } else if (line.startsWith("INITIAL_STATE:")) {
                                        initial_state = parseState(line, "INITIAL_STATE:");
                                } else if (line.startsWith("GOAL_STATE:")) {
                                        goal_state = parseState(line, "GOAL_STATE:");
                                }
                        }

                        scanner.close();

                        // Print for debugging
                        printParsedData();

                } catch (FileNotFoundException e) {
                        e.printStackTrace();
                }
        }

        // Helper function to parse objects (planes, airports, cargoes)
        private String[] parseObjects(String line, String prefix) {
                return line.replace(prefix, "").trim().split("\\s+");
        }

        // Helper function to parse state (INITIAL_STATE and GOAL_STATE)
        private List<String> parseState(String line, String prefix) {
                List<String> states = new ArrayList<>();
                String[] stateParts = line.replace(prefix, "").trim().split("\\)\\s*");

                for (String part : stateParts) {
                        if (!part.isEmpty()) {
                                String formatted = part.replaceAll("AT\\(", "").replaceAll("[(),\\s]", ""); // Remove
                                                                                                            // 'AT',
                                                                                                            // '(', ')',
                                                                                                            // and
                                                                                                            // commas
                                states.add(formatted);
                        }
                }
                return states;
        }

        // Helper function to print the parsed data (for debugging)
        private void printParsedData() {
                System.out.println("Planes: ");
                for (String plane : planes) {
                        System.out.println(plane);
                }

                System.out.println("Airports: ");
                for (String airport : airports) {
                        System.out.println(airport);
                }

                System.out.println("Cargoes: ");
                for (String cargo : cargoes) {
                        System.out.println(cargo);
                }

                System.out.println("Initial State: ");
                for (String state : initial_state) {
                        System.out.println(state);
                }

                System.out.println("Goal State: ");
                for (String state : goal_state) {
                        System.out.println(state);
                }
        }

        // Getters and setters (omitted for brevity)
        // ...

        public static void main(String[] args) {
                CargoFileParser parser = new CargoFileParser("first_instance.txt");
        }

        public String[] getPlanes() {
                return planes;
        }

        public void setPlanes(String[] planes) {
                this.planes = planes;
        }

        public String[] getAirports() {
                return airports;
        }

        public void setAirports(String[] airports) {
                this.airports = airports;
        }

        public String[] getCargoes() {
                return cargoes;
        }

        public void setCargoes(String[] cargoes) {
                this.cargoes = cargoes;
        }

        public ArrayList<String> getInitial_state() {
                return new ArrayList<String>(initial_state);
        }

        public void setInitial_state(List<String> initial_state) {
                this.initial_state = initial_state;
        }

        public ArrayList<String> getGoal_state() {
                return new ArrayList<String>(goal_state);
        }

        public void setGoal_state(List<String> goal_state) {
                this.goal_state = goal_state;
        }
}
