package codificatori_tmp;

import java.util.HashMap;

public class GeneratorePolinomi {

    private HashMap<Integer, boolean[][]> g_1_2;
    private HashMap<Integer, boolean[][]> g_1_3;

    public GeneratorePolinomi() {
        g_1_2 = new HashMap<>();
        g_1_3 = new HashMap<>();
        creaPolimomi();
    }

    public void creaPolimomi() {
        g_1_2.put(2, new boolean[][]{{true, true}, {true, true}});
        g_1_2.put(3, new boolean[][]{{true, true, false}, {true, true, true}});
        g_1_2.put(4, new boolean[][]{{true, true, false, true}, {true, true, true, false}});
        g_1_2.put(5, new boolean[][]{{true, true, false, true, false}, {true, true, true, false, true}});
        g_1_2.put(6, new boolean[][]{{true, true, false, true, false, true}, {true, true, true, false, true, true}});
        g_1_2.put(7, new boolean[][]{{true, true, false, true, false, true, false}, {true, true, true, false, true, true, false}});

        g_1_3.put(2, new boolean[][]{{true, false}, {true, true}});
        g_1_3.put(3, new boolean[][]{{true, false, true}, {true, false, true}, {true, true, false}});
        g_1_3.put(4, new boolean[][]{{true, false, true, true}, {true, false, true, true}, {true, true, false, true}});
        g_1_3.put(5, new boolean[][]{{true, false, true, true, false}, {true, false, true, true, true}, {true, true, false, true, true}});
        g_1_3.put(6, new boolean[][]{{true, false, true, true, false, true}, {true, false, true, true, true, true}, {true, true, false, true, true, false}});
        g_1_3.put(7, new boolean[][]{{true, false, true, true, false, true, false}, {true, false, true, true, true, true, false}, {true, true, false, true, true, false, false}});

    }

    public boolean[][] getPolinomio(int n, int N) {
        if (n == 2) {
            return g_1_2.get(N);
        }
        if (n == 3) {
            return g_1_3.get(N);
        } else {
            return null;
        }
    }

}
