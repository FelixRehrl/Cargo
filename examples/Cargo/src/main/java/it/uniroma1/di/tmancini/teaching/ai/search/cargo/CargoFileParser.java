package it.uniroma1.di.tmancini.teaching.ai.search.cargo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.PortUnreachableException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import it.uniroma1.di.tmancini.teaching.ai.search.cargo.Cargo.Heuristics;

public class CargoFileParser {
        private String[] planes;
        private String[] airports;
        private String[] cargoes;
        private List<String> initial_state;
        private List<String> goal_state;

        public CargoFileParser(String file_path) {
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

                } catch (FileNotFoundException e) {
                        e.printStackTrace();
                }
        }

        private String[] parseObjects(String line, String prefix) {
                return line.replace(prefix, "").trim().split("\\s+");
        }

        private List<String> parseState(String line, String prefix) {
                List<String> states = new ArrayList<>();
                String[] stateParts = line.replace(prefix, "").trim().split("\\)\\s*");

                for (String part : stateParts) {
                        if (!part.isEmpty()) {
                                String formatted = part.replaceAll("AT\\(", "").replaceAll("[(),\\s]", "");
                                states.add(formatted);
                        }
                }
                return states;

        }

        public static void write_stats_to_output_file(String algo, Heuristics heuristics, long millies,
                        String problem_instance, long seed) {

                try {
                        write_to_file(algo, heuristics, millies, problem_instance, seed);
                } catch (Exception e) {
                        System.out.println("Error writing to output");
                }
        }

        private static void write_to_file(String algo, Heuristics heuristics, long millies, String problem_instance,
                        long seed) throws IOException {
                String h = heuristics != null ? ":" + heuristics.toString() : "";

                FileWriter fw = new FileWriter("data/output_stats.csv", true);
                PrintWriter printWriter = new PrintWriter(fw, true);
                printWriter.printf("%d,%s%s,%s, %d\n", seed, algo, h, problem_instance, millies);
                printWriter.close();
        }

        private void printParsedData() {
                for (String plane : planes) {
                        System.out.println(plane);
                }

                for (String airport : airports) {
                        System.out.println(airport);
                }

                for (String cargo : cargoes) {
                        System.out.println(cargo);
                }

                for (String state : initial_state) {
                        System.out.println(state);
                }

                for (String state : goal_state) {
                        System.out.println(state);
                }
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
