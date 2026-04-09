import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * Simulation of the Bully election algorithm for coordinator selection.
 * The coordinator is the active process with the highest ID.
 * When the coordinator fails, any process can start an election.
 */
public class BullyElection {

    static class Process {
        final int id;
        boolean alive;

        Process(int id) {
            this.id = id;
            this.alive = true;
        }

        @Override
        public String toString() {
            return "P" + id + (alive ? "" : " (dead)");
        }
    }

    private final List<Process> processes;
    private int coordinatorId;

    public BullyElection(List<Process> processes) {
        this.processes = new ArrayList<>(processes);
        this.coordinatorId = findHighestAliveId();
    }

    private int findHighestAliveId() {
        return processes.stream()
                .filter(p -> p.alive)
                .mapToInt(p -> p.id)
                .max()
                .orElse(-1);
    }

    /** Simulate coordinator failure and run bully election from starter process. */
    public void failCoordinator() {
        Process coord = processes.stream()
                .filter(p -> p.id == coordinatorId)
                .findFirst()
                .orElse(null);
        if (coord != null) {
            System.out.println("\n>>> Coordinator P" + coordinatorId + " FAILED.");
            coord.alive = false;
        }
        coordinatorId = -1;
    }

    /**
     * Bully election: process {@code starter} sends ELECTION to all higher IDs.
     * If any higher alive process exists, it will take over (recursive simulation).
     * Otherwise starter becomes coordinator and sends OK to lower processes.
     */
    public void runElectionFrom(int starterId) {
        Process starter = processes.stream()
                .filter(p -> p.id == starterId)
                .findFirst()
                .orElse(null);
        if (starter == null || !starter.alive) {
            System.out.println("Starter P" + starterId + " is not active; cannot start election.");
            return;
        }

        System.out.println("\n--- Election started by P" + starterId + " ---");

        List<Process> higher = new ArrayList<>();
        for (Process p : processes) {
            if (p.alive && p.id > starterId) {
                higher.add(p);
            }
        }
        higher.sort(Comparator.comparingInt(p -> p.id));

        if (higher.isEmpty()) {
            coordinatorId = starterId;
            System.out.println("No higher-ID alive processes. P" + starterId + " becomes COORDINATOR.");
            announceCoordinator(starterId);
            return;
        }

        System.out.println("P" + starterId + " sends ELECTION to: " + formatIds(higher));
        // Highest alive respondent wins bully election
        Process winner = higher.get(higher.size() - 1);
        System.out.println("Highest respondent P" + winner.id + " takes over election.");
        runElectionFrom(winner.id);
    }

    private void announceCoordinator(int id) {
        List<Integer> lower = new ArrayList<>();
        for (Process p : processes) {
            if (p.alive && p.id < id) {
                lower.add(p.id);
            }
        }
        if (!lower.isEmpty()) {
            System.out.println("P" + id + " sends COORDINATOR message to: " + lower);
        }
    }

    private static String formatIds(List<Process> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("P").append(list.get(i).id);
        }
        return sb.toString();
    }

    public void printState() {
        System.out.println("\n=== Cluster state ===");
        List<Process> sorted = new ArrayList<>(processes);
        sorted.sort(Comparator.comparingInt(p -> p.id));
        for (Process p : sorted) {
            System.out.println("  " + p);
        }
        if (coordinatorId >= 0) {
            Process c = processes.stream().filter(x -> x.id == coordinatorId).findFirst().orElse(null);
            if (c != null && c.alive) {
                System.out.println("Current coordinator: P" + coordinatorId);
            } else {
                System.out.println("No coordinator (election needed).");
            }
        } else {
            System.out.println("No coordinator (election needed).");
        }
    }

    public int getCoordinatorId() {
        return coordinatorId;
    }

    public static void main(String[] args) {
        List<Process> procs = new ArrayList<>();
        procs.add(new Process(1));
        procs.add(new Process(2));
        procs.add(new Process(3));
        procs.add(new Process(4));
        procs.add(new Process(5));

        BullyElection sim = new BullyElection(procs);
        System.out.println("Bully Election Algorithm — simulation");
        sim.printState();

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\nCommands: 1=fail coordinator & elect from P1, 2=elect from custom ID, 3=state, q=quit");
            System.out.print("> ");
            if (!sc.hasNextLine()) {
                break;
            }
            String line = sc.nextLine().trim();
            if (line.equalsIgnoreCase("q")) {
                break;
            }
            if (line.equals("1")) {
                sim.failCoordinator();
                sim.printState();
                int starter = 1;
                Process p1 = procs.stream().filter(p -> p.id == starter).findFirst().orElse(null);
                if (p1 != null && p1.alive) {
                    sim.runElectionFrom(starter);
                    sim.printState();
                } else {
                    // pick lowest alive
                    int low = procs.stream().filter(p -> p.alive).mapToInt(p -> p.id).min().orElse(-1);
                    if (low >= 0) {
                        sim.runElectionFrom(low);
                        sim.printState();
                    }
                }
            } else if (line.equals("2")) {
                System.out.print("Enter starter process ID: ");
                if (sc.hasNextInt()) {
                    int sid = sc.nextInt();
                    sc.nextLine();
                    sim.runElectionFrom(sid);
                    sim.printState();
                } else {
                    sc.nextLine();
                    System.out.println("Invalid ID.");
                }
            } else if (line.equals("3")) {
                sim.printState();
            } else if (line.equalsIgnoreCase("demo")) {
                sim.printState();
                sim.failCoordinator();
                sim.printState();
                sim.runElectionFrom(1);
                sim.printState();
            } else {
                System.out.println("Unknown command. Try: demo (non-interactive full demo)");
            }
        }
        sc.close();
        System.out.println("Bye.");
    }
}
