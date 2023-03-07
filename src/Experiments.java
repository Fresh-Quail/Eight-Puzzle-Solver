import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;

public class Experiments {
    Class<?> puzzleStateClass;
    Constructor<?> constructor;
    EightPuzzle.PuzzleState puzzleState;
    Method legalMove;

    public Experiments() throws FileNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
            puzzleStateClass = EightPuzzle.class.getDeclaredClasses()[0];
            constructor = puzzleStateClass.getDeclaredConstructors()[0];
            constructor.setAccessible(true);
        }

    public double[] astarh1(int maxNodes) throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, FileNotFoundException {
        EightPuzzle puzzle = new EightPuzzle( System.getProperty("user.dir") + "\\test\\Empty.txt");
        Method randomizeState = puzzleStateClass.getDeclaredMethod("randomizeState", String.class);
        randomizeState.setAccessible(true);
        Field puzzle8 = EightPuzzle.class.getDeclaredField("puzzle8");
        puzzle8.setAccessible(true);

        puzzle.maxNodes(String.valueOf(maxNodes));

        // Represents the number of solved puzzles out of 500
        int solved = 0;
        // Represents the total length of all the solution paths
        int movesetLengths = 0;
        for (int i = 1; i <= 500; i++)
        {
            //Randomizes the state for the search algorithm
            randomizeState.invoke((puzzle8.get(puzzle)), String.valueOf(1000));

            //Gets the moveset for that Astar made on its path
            LinkedList<String> moveset = puzzle.solveAstar("h1");
            //Makes sure the puzzle reached the goal state
            if("b12345678".equals(puzzle8.get(puzzle).toString())) {
                solved++;
                movesetLengths += moveset.size();
            }
        }
        return new double[]{solved*1.0 / 500, movesetLengths*1.0 / 500};
    }

    public double[] astarh2(int maxNodes) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException, FileNotFoundException {
        EightPuzzle puzzle = new EightPuzzle( System.getProperty("user.dir") + "\\test\\Empty.txt");
        Method randomizeState = puzzleStateClass.getDeclaredMethod("randomizeState", String.class);
        randomizeState.setAccessible(true);
        Field puzzle8 = EightPuzzle.class.getDeclaredField("puzzle8");
        puzzle8.setAccessible(true);

        puzzle.maxNodes(String.valueOf(maxNodes));

        // Represents the number of solved puzzles out of 500
        int solved = 0;
        // Represents the total length of all the solution paths
        int movesetLengths = 0;
        for (int i = 1; i <= 500; i++)
        {
            //Randomizes the state for the search algorithm
            randomizeState.invoke((puzzle8.get(puzzle)), String.valueOf(500));

            //Gets the moveset for that Astar made on its path
            LinkedList<String> moveset = puzzle.solveAstar("h2");
            //Makes sure the puzzle reached the goal state
            if("b12345678".equals(puzzle8.get(puzzle).toString())){
                solved++;
                movesetLengths += moveset.size();
            }
        }
        return new double[]{solved*1.0 / 500, movesetLengths*1.0 / 500};
    }

    public double[] beam(int maxNodes, int k) throws FileNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException, InstantiationException {
        EightPuzzle puzzle = new EightPuzzle(System.getProperty("user.dir") + "\\test\\Empty.txt");
        Method randomizeState = puzzleStateClass.getDeclaredMethod("randomizeState", String.class);
        randomizeState.setAccessible(true);
        Field puzzle8 = EightPuzzle.class.getDeclaredField("puzzle8");
        puzzle8.setAccessible(true);

        puzzle.maxNodes(String.valueOf(maxNodes));

        // Represents the number of solved puzzles out of 500
        int solved = 0;
        // Represents the total length of all the solution paths
        int movesetLengths = 0;
        for (int i = 1; i <= 500; i++)
        {
            //Randomizes the state for the search algorithm
            randomizeState.invoke((puzzle8.get(puzzle)), String.valueOf(500));

            //Gets the moveset for that Astar made on its path
            LinkedList<String> moveset = puzzle.solvebeam(String.valueOf(k));
            //Makes sure the puzzle reached the goal state
            if("b12345678".equals(puzzle8.get(puzzle).toString())) {
                solved++;
                movesetLengths += moveset.size();
            }
        }
        return new double[]{solved*1.0 / 500, movesetLengths*1.0 / 500};
    }

    public static void main(String[] jigglers) throws FileNotFoundException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    Experiments puzzle = new Experiments();
        for (int i = 1; i < 5; i++)
        {
            System.out.println("a1: " + Arrays.toString(puzzle.astarh1((int) Math.pow(10, i))));
            System.out.println("a2: " + Arrays.toString(puzzle.astarh2((int) Math.pow(10, i))));
            System.out.println("b1: " + Arrays.toString(puzzle.beam((int) Math.pow(10, i), 1)));
            System.out.println("b10: " + Arrays.toString(puzzle.beam((int) Math.pow(10, i), 10)));
            System.out.println("b100: " + Arrays.toString(puzzle.beam((int) Math.pow(10, i), 100000)));
        }

        System.out.println("a1: " + Arrays.toString(puzzle.astarh1(Integer.MAX_VALUE)));
        System.out.println("a2: " + Arrays.toString(puzzle.astarh2(Integer.MAX_VALUE)));
        System.out.println("b1: " + Arrays.toString(puzzle.beam(Integer.MAX_VALUE, 1)));
        System.out.println("b10: " + Arrays.toString(puzzle.beam(Integer.MAX_VALUE, 10)));
        System.out.println("b100: " + Arrays.toString(puzzle.beam(Integer.MAX_VALUE, 100)));
    }
}
