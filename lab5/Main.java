package lab5;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

public class Main {
    ArrayList<Beer> lista = new ArrayList<>();

    protected void list(String[] cmd) {
        if (lista.isEmpty()) {
            System.out.println("Üres a lista");
        }
        java.util.Comparator<Beer> comp = null;
        if (cmd.length >= 2) {
            switch (cmd[1]) {
                case "style":
                    comp = new StyleComparator();
                    break;
                case "strength":
                    comp = new StrengthComparator();
                    break;
                default:
                    comp = new NameComparator();
                    return;
            }
        }

        if (comp != null) {
            java.util.Collections.sort(lista, comp);
        }

        for (Beer b : lista) {
            System.out.println(b);
        }
    }

    protected void add(String[] cmd) {
        try {
            lista.add(new Beer(cmd[1], cmd[2], Double.valueOf(cmd[3])));
        } catch (Exception e) {
            System.out.println("useage: add name type strength");
        }
    }

    protected void load(String[] cmd) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(cmd[1]));
            String line;
            while ((line = br.readLine()) != null) {
                String args[] = line.split(" ");
                lista.add(new Beer(args[0], args[1], Double.parseDouble(args[2])));

            }
            br.close();
        } catch (Exception e) {
            System.out.println("Usage: load filename");
        }

    }

    protected void save(String[] cmd) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(cmd[1]));
            Iterator<Beer> it = lista.iterator();
            while (it.hasNext()) {
                bw.append(it.next().toString() + "\n");
            }
            bw.close();
        } catch (Exception e) {
            System.out.println("Usage: save filename");
        }
    }

    protected void search(String[] cmd) {
        try {
            for (Beer b : lista) {
                if (b.getName().equals(cmd[1])) {
                    System.out.println(b);

                }
            }
        } catch (Exception e) {
            System.out.println("usage search name");
        }
    }

    protected void find(String[] cmd) {
        try {
            for (Beer b : lista) {
                if (b.getName().contains(cmd[1])) {
                    System.out.println(b);
                }
            }
        } catch (Exception e) {
            System.out.println("usage find string");
        }
    }

    protected void delete(String[] cmd) {
        Iterator<Beer> it = lista.iterator();
        try {
            while (it.hasNext()) {
                Beer b = it.next();
                if (b.getName().equals(cmd[1])) {
                    it.remove();
                }
            }
        } catch (Exception e) {
            System.out.println("usage: delete name");
        }
    }

    public static void main(String[] args) {
        /*
         * Beer b1 = new Beer("Soproni", "IPA");
         * Beer b2 = new Beer("Dreher", "Barna");
         * System.out.println(b1.toString() + " " + b2.toString());
         */
        Main program = new Main();
        InputStreamReader is = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(is);
        while (true) {
            System.out.print("> ");
            String line = new String();
            try {
                line = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (line == null) {
                break;
            }
            String[] cmd = line.split(" ");
            switch (cmd[0]) {
                case "add":
                    program.add(cmd);
                    break;
                case "list":
                    program.list(cmd);
                    break;
                case "save":
                    program.save(cmd);
                    break;
                case "load":
                    program.load(cmd);
                    break;
                case "search":
                    program.search(cmd);
                    break;
                case "find":
                    program.find(cmd);
                    break;
                case "delete":
                    program.delete(cmd);
                    break;
                case "exit":
                    return;
                default:
                    System.out.println(cmd[0] + " " + cmd.length);
                    break;
            }

        }
    }
}
