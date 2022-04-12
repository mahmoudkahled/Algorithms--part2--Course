import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FordFulkerson;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class BaseballElimination {
    private int[] w, l, r;
    private int[][] g;
    private String[] teams_names;
    private int number_of_teams = 0;
    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        try {
            String[] line_Data, formatted_line_Data = new String[0];
            File file = new File(filename);
            Scanner read = new Scanner(file);
            int counter = 0;
            boolean flag = true;
            while (read.hasNextLine()) {
                String Data = read.nextLine();
                if (flag) {
                    number_of_teams = Integer.parseInt(Data);
                    w = new int[number_of_teams];
                    l = new int[number_of_teams];
                    r = new int[number_of_teams];
                    g = new int[number_of_teams][number_of_teams];
                    teams_names = new String[number_of_teams];
                    formatted_line_Data = new String[(number_of_teams + 4)];
                    flag = false;
                } else {
                    line_Data = Data.split(" ");
                    for (int i = 0, j = 0; i < line_Data.length; i++) {
                        if (!line_Data[i].equals("")) {
                            formatted_line_Data[j] = line_Data[i];
                            ++j;
                        }
                    }
                    teams_names[counter] = formatted_line_Data[0];
                    w[counter] = Integer.parseInt(formatted_line_Data[1]);
                    l[counter] = Integer.parseInt(formatted_line_Data[2]);
                    r[counter] = Integer.parseInt(formatted_line_Data[3]);
                    for (int x = 0, y = 4; x < number_of_teams; x++, y++) {
                        g[counter][x] = Integer.parseInt(formatted_line_Data[y]);
                    }
                    ++counter;
                }
            }
            read.close();
        } catch (FileNotFoundException o) {
            System.out.print("File not Found");
        }
    }

    // number of teams
    public int numberOfTeams() {
        return number_of_teams;
    }

    // all teams
    public Iterable<String> teams() {
        Queue<String> queue = new LinkedList<>();
        for (int i = 0; i < teams_names.length; i++) {
            queue.add(teams_names[i]);
        }
        return queue;
    }

    // number of wins for given team
    public int wins(String team) {
        int team_index = validate_team(team);
        return w[team_index];
    }

    // number of losses for given team
    public int losses(String team) {
        int team_index = validate_team(team);
        return l[team_index];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        int team_index = validate_team(team);
        return r[team_index];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        int team1_index = -1, team2_index = -1;
        for (int i = 0; i < teams_names.length; i++) {
            if (teams_names[i].equals(team1)) {
                team1_index = i;
            }
            if (teams_names[i].equals(team2)) {
                team2_index = i;
            }
        }
        if (team1_index == -1 || team2_index == -1) {
            throw new IllegalArgumentException("Invalid team");
        }
        return g[team1_index][team2_index];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        int team_index = validate_team(team);
        // trivial elimination
        for (String other_team : teams()) {
            if ((wins(team) + remaining(team)) < wins(other_team)) {
                return true;
            }
        }
        // nontrivial elimination
        FlowNetwork network = create_flow_network(((numberOfTeams()) + 2 + game_verties(team_index)) , team_index);
        FordFulkerson ford = new FordFulkerson(network, 0, (network.V() - 1));
        for (FlowEdge edge : network.adj(0)) {
            if (edge.residualCapacityTo(edge.to()) != 0) {
                return true;
            }
        }
        return false;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        int team_index = validate_team(team);
        if (!isEliminated(team)) {
            return null;
        }
        Queue<String> queue = new LinkedList<>();
        // trivial case
        for (String other_team : teams()) {
            if ((wins(team) + remaining(team)) < wins(other_team)) {
                queue.add(other_team);
                return queue;
            }
        }
        // grade school arithmetic
        Queue<String> teams_queue = new LinkedList<>();
        FlowNetwork network = create_flow_network((2 + numberOfTeams() + game_verties(team_index)) , team_index);
        FordFulkerson ford = new FordFulkerson(network , 0 , (network.V() - 1));
        for(int i = 0 ; i < teams_names.length ; i++){
            if(!isEliminated(teams_names[i])){
                if(ford.inCut((1 + game_verties(team_index) + i))){
                    teams_queue.add(teams_names[i]);
                }
            }
        }
        int subset_R_wins_and_remainign = 0, team_wins_and_remaining = wins(team) + remaining(team);
        double count = 0;
        for(String t : teams_queue){
            subset_R_wins_and_remainign += wins(t) + remaining(t);
            ++count;
        }
        double average_subset_R = (subset_R_wins_and_remainign / count);
        average_subset_R = Math.ceil(average_subset_R);
        if (team_wins_and_remaining < average_subset_R) {
            for(String out_team : teams_queue){
                if(!queue.contains(out_team)){
                    queue.add(out_team);
                }
            }
        }
        return queue;
    }
    private FlowNetwork create_flow_network(int verices_number , int team_index){
        FlowNetwork internal_network = new FlowNetwork(verices_number);
        int edge_count = 1;
        for (int x = 0; x < g.length; x++) {
            if (x == team_index) {
                continue;
            }
            for (int j = 0; j < g[x].length; j++) {
                if (j == team_index || x == j || j < x) {
                    continue;
                }
                internal_network.addEdge(new FlowEdge(0, edge_count, g[x][j]));
                internal_network.addEdge(new FlowEdge(edge_count, (1 + game_verties(team_index) + x), Double.POSITIVE_INFINITY));
                internal_network.addEdge(new FlowEdge(edge_count, (1 + game_verties(team_index) + j), Double.POSITIVE_INFINITY));
                ++edge_count;
            }
        }
        for (int i = (1 + game_verties(team_index)), j = 0; i < (internal_network.V() - 1); i++, j++) {
            if(team_index == j){
                continue;
            }
            internal_network.addEdge(new FlowEdge(i, (internal_network.V() - 1), (w[team_index] + r[team_index] - w[j])));
        }
        return internal_network;
    }
    private int validate_team(String team){
        int team_index = -1;
        for (int i = 0; i < teams_names.length; i++) {
            if (teams_names[i].equals(team)) {
                team_index = i;
                break;
            }
        }
        if (team_index == -1) {
            throw new IllegalArgumentException("Invalid team");
        }
        return team_index;
    }
    private int game_verties(int team_index){
        int game_vertices_count = 0;
        for (int i = 0; i < g.length; i++) {
            if (i == team_index) {
                continue;
            }
            for (int j = 0; j < g[i].length; j++) {
                if (j == team_index || i == j || j < i) {
                    continue;
                }
                ++game_vertices_count;
            }
        }
        return game_vertices_count;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }

}
