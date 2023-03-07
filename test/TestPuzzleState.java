import org.junit.jupiter.api.*;

import java.io.FileNotFoundException;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TestPuzzleState extends Assertions {
    EightPuzzle eightPuzzle;
    Class<?> puzzleStateClass;
    Constructor<?> constructor;
    EightPuzzle.PuzzleState puzzleState;
    Method legalMove;

    public TestPuzzleState() throws FileNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        eightPuzzle = new EightPuzzle(System.getProperty("user.dir") + "\\test\\Empty.txt");
        puzzleStateClass = EightPuzzle.class.getDeclaredClasses()[0];
        constructor = puzzleStateClass.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        puzzleState = (EightPuzzle.PuzzleState)constructor.newInstance(eightPuzzle, new char[9], null, "", 0, 0);
        legalMove = puzzleStateClass.getDeclaredMethod("legalMove", String.class);
        legalMove.setAccessible(true);
    }

    @Test
    public void testSetState() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        Field blankPos = puzzleStateClass.getDeclaredField("blankPos");
        blankPos.setAccessible(true);
        //Tests init on Empty
        puzzleState = (EightPuzzle.PuzzleState)constructor.newInstance(eightPuzzle, new char[]{'8', '7', '6', 'b', '5', '4', '3', '2', '1'}, null, "", 0, 3);
        assertEquals("876b54321", puzzleState.toString());
        assertEquals(3, blankPos.get(puzzleState));
        //Tests setState
        puzzleState.setState("8765b4321");
        assertEquals("8765b4321", puzzleState.toString());
        assertEquals(4, blankPos.get(puzzleState));
        puzzleState.setState("1234567b8");
        assertEquals(puzzleState.toString(), "1234567b8");
        assertEquals(7, blankPos.get(puzzleState));
    }

    @Test
    public void testMove() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        char[][] x = new char[9][9];
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if(j < i)
                    x[i][j] = (char)(49 + j);
                else
                    x[i][j + 1] = (char)(49 + j);
            }
            x[i][i] = 'b';
        }

        EightPuzzle.PuzzleState[] puzzleState = new EightPuzzle.PuzzleState[9];
        for (int i = 0; i < 9; i++)
        {
            puzzleState[i] = (EightPuzzle.PuzzleState)constructor.newInstance(eightPuzzle, x[i], null, "", 0, i);
        }

        for (int i = 0; i < 9; i++)
        {
            System.out.println("Current State: " + puzzleState[i].toString());
            if ((boolean) legalMove.invoke(puzzleState[i], "up"))
            {
                puzzleState[i].move("up");
                assertEquals(puzzleState[i].toString().charAt(i - 3), 'b');
                assertEquals(puzzleState[i].toString().charAt(i), (char)(49 + i - 3));
                if ((boolean) legalMove.invoke(puzzleState[i], "up"))
                {
                    puzzleState[i].move("up");
                    assertEquals(puzzleState[i].toString().charAt(i - 6), 'b');
                    assertEquals(puzzleState[i].toString().charAt(i - 3), (char)(49 + i - 6));
                    if ((boolean) legalMove.invoke(puzzleState[i], "up"))
                        try{puzzleState[i].move("up");} catch (RuntimeException e){} catch (Exception m){fail();}
                    puzzleState[i].move("down");
                    assertEquals(puzzleState[i].toString().charAt(i - 3), 'b');
                    assertEquals(puzzleState[i].toString().charAt(i), (char)(49 + i - 3));
                }
                puzzleState[i].move("down");
                assertEquals(puzzleState[i].toString().charAt(i), 'b');
            }

            if ((boolean) legalMove.invoke(puzzleState[i], "down"))
            {
                puzzleState[i].move("down");
                assertEquals(puzzleState[i].toString().charAt(i + 3), 'b');
                assertEquals(puzzleState[i].toString().charAt(i), (char)(48 + i + 3));
                if ((boolean) legalMove.invoke(puzzleState[i], "down"))
                {
                    puzzleState[i].move("down");
                    assertEquals(puzzleState[i].toString().charAt(i + 6), 'b');
                    assertEquals(puzzleState[i].toString().charAt(i + 3), (char)(48 + i + 6));
                    if ((boolean) legalMove.invoke(puzzleState[i], "down"))
                    {
                        try{puzzleState[i].move("down");} catch (RuntimeException ignored){} catch (Exception m){fail();}
                    }
                    puzzleState[i].move("up");
                    assertEquals(puzzleState[i].toString().charAt(i + 3), 'b');
                    assertEquals(puzzleState[i].toString().charAt(i), (char)(48 + i + 3));
                }
                puzzleState[i].move("up");
                assertEquals(puzzleState[i].toString().charAt(i), 'b');
            }

        }
        for (int i = 0; i < 9; i++)
        {
            if((boolean)legalMove.invoke(puzzleState[i], "right"))
            {
                puzzleState[i].move("right");
                assertEquals(puzzleState[i].toString(), puzzleState[i + 1].toString());
                puzzleState[i].move("left");
                puzzleState[i].move("right");
                if((boolean)legalMove.invoke(puzzleState[i], "right"))
                {
                    puzzleState[i].move("right");
                    if((boolean)legalMove.invoke(puzzleState[i], "right"))
                    {
                        try{puzzleState[i].move("right");} catch (RuntimeException ignored){} catch (Exception m){fail();}
                    }
                    assertEquals(puzzleState[i].toString(), puzzleState[i + 2].toString());
                    puzzleState[i].move("left");
                }
                puzzleState[i].move("left");
            }
            if((boolean)legalMove.invoke(puzzleState[i], "left"))
            {
                puzzleState[i].move("left");
                assertEquals(puzzleState[i].toString(), puzzleState[i - 1].toString());
                puzzleState[i].move("right");
                puzzleState[i].move("left");
                if((boolean)legalMove.invoke(puzzleState[i], "left"))
                {
                    puzzleState[i].move("left");
                    if((boolean)legalMove.invoke(puzzleState[i], "left"))
                    {
                        try{puzzleState[i].move("left");} catch (RuntimeException ignored){} catch (Exception m){fail();}
                    }
                    assertEquals(puzzleState[i].toString(), puzzleState[i - 2].toString());
                    puzzleState[i].move("right");
                }
                puzzleState[i].move("right");
            }

        }

    }

    @Test
    public void testMisplaced() throws IllegalAccessException, NoSuchFieldException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        Field heuristic = EightPuzzle.class.getDeclaredField("heuristic");
        heuristic.setAccessible(true);
        heuristic.set(eightPuzzle, "h1");
        Method misplaced = puzzleStateClass.getDeclaredMethod("misplaced");
        misplaced.setAccessible(true);
        EightPuzzle.PuzzleState[] puzzleState = new EightPuzzle.PuzzleState[9];
        char[][] x = new char[9][9];

        for (int i = 1; i < 9; i++)
        {
            Arrays.fill(x[i], (char)(48 + i));
            puzzleState[i] = (EightPuzzle.PuzzleState)constructor.newInstance(eightPuzzle, x[i], null, "", 0, i);
            System.out.println(puzzleState[i].toString());
            assertEquals(7, misplaced.invoke(puzzleState[i]));
        }

    }

    @Test
    public void testAdjacentStates() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        EightPuzzle.PuzzleState[] puzzleState = new EightPuzzle.PuzzleState[9];
        char[][] x = new char[9][9];
        Method adjacentStates = puzzleStateClass.getDeclaredMethod("adjacentStates");
        adjacentStates.setAccessible(true);

        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if (j < i)
                    x[i][j] = (char) (49 + j);
                else
                    x[i][j + 1] = (char) (49 + j);
            }
            x[i][i] = 'b';
            puzzleState[i] = (EightPuzzle.PuzzleState)constructor.newInstance(eightPuzzle, x[i], null, "", 0, i);
        }

        int n = 0;
        int m = 0;
        for (int i = 0; i < 9; i++)
        {
            System.out.println("Current State: " + puzzleState[i].toString());
            List<EightPuzzle.PuzzleState> adjacents = (List<EightPuzzle.PuzzleState>) adjacentStates.invoke(puzzleState[i]);
            if ((boolean) legalMove.invoke(puzzleState[i], "up"))
            {
                puzzleState[i].move("up");
                assertEquals(puzzleState[i].toString(), adjacents.get(n).toString());
                puzzleState[i].move("down");
                n++;
            }

            if ((boolean) legalMove.invoke(puzzleState[i], "right"))
            {
                puzzleState[i].move("right");
                assertNotEquals(puzzleState[i], adjacents.get(1));
                assertEquals(puzzleState[i].toString(), adjacents.get(n).toString());
                puzzleState[i].move("left");
                n++;
            }

            if ((boolean) legalMove.invoke(puzzleState[i], "down"))
            {
                puzzleState[i].move("down");
                assertEquals(puzzleState[i].toString(), adjacents.get(n).toString());
                puzzleState[i].move("up");
                n++;
            }

            if ((boolean) legalMove.invoke(puzzleState[i], "left"))
            {
                puzzleState[i].move("left");
                assertEquals(puzzleState[i].toString(), adjacents.get(n).toString());
                puzzleState[i].move("right");
                n++;
            }
            for(EightPuzzle.PuzzleState state: adjacents)
                m++;
            assertEquals(n, m);
            n = 0;
            m = 0;
        }
    }

    @Test
    public void testCorrectPathAstar() throws NoSuchMethodException, FileNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, InstantiationException {
        EightPuzzle puzzle = new EightPuzzle(System.getProperty("user.dir") + "\\test\\Empty.txt");
        char[][] x = new char[9][9];
        Method randomizeState = puzzleStateClass.getDeclaredMethod("randomizeState", String.class);
        randomizeState.setAccessible(true);
        Field puzzle8 = EightPuzzle.class.getDeclaredField("puzzle8");
        puzzle8.setAccessible(true);

        puzzle.maxNodes("1000");
        EightPuzzle.PuzzleState testGoalPath = (EightPuzzle.PuzzleState) constructor.newInstance(puzzle, new char[9], null, "", 0, 0);
        for (int i = 0; i < 1; i++)
        {
            //Randomizes the state for the search algorithm
            randomizeState.invoke((puzzle8.get(puzzle)), String.valueOf(1000));
            System.out.println("Initial State: " + puzzle8.get(puzzle).toString());

            //Assigns said random state to our testGoalPath
            testGoalPath.setState(puzzle8.get(puzzle).toString());

            //Gets the moveset for that Astar made on its path
            LinkedList<String> moveset = puzzle.solveAstar("h1");
            //Makes sure the puzzle reached the goal state
            assertEquals("b12345678", puzzle8.get(puzzle).toString());

            //Does the moves on the testGoalPath
            for (int j = 0; j != moveset.size();)
            {
                testGoalPath.move(moveset.remove());
            }
            //Checks if the moves outputted by Astar actually reach the goal State
            assertEquals("b12345678", testGoalPath.toString());
        }
    }

    @Test
    public void testOptimalPathAstar() throws NoSuchMethodException, FileNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, InstantiationException {
        EightPuzzle puzzle = new EightPuzzle(System.getProperty("user.dir") + "\\test\\Empty.txt");
        char[][] x = new char[9][9];
        Method randomizeState = puzzleStateClass.getDeclaredMethod("randomizeState", String.class);
        randomizeState.setAccessible(true);
        Method optimalPathM = EightPuzzle.class.getDeclaredMethod("optimalPath");
        optimalPathM.setAccessible(true);
        Method setState = puzzleStateClass.getDeclaredMethod("setState", String.class);
        setState.setAccessible(true);
        Field puzzle8 = EightPuzzle.class.getDeclaredField("puzzle8");
        puzzle8.setAccessible(true);

        puzzle.maxNodes("999999");
        EightPuzzle optimalPath = new EightPuzzle(System.getProperty("user.dir") + "\\test\\Empty.txt");
        for (int i = 0; i < 100; i++)
        {
            //Randomizes the state for the search algorithm
            randomizeState.invoke((puzzle8.get(puzzle)), String.valueOf(i));
            System.out.println("Initial State: " + puzzle8.get(puzzle).toString());
            //Assigns said random state to our optimalPath and gets the optimal moveset
            setState.invoke(puzzle8.get(optimalPath), puzzle8.get(puzzle).toString());
            LinkedList<String> optimalMoveSet = (LinkedList<String>) optimalPathM.invoke(optimalPath);
            //Makes sure the puzzle reached the goal state
            assertEquals("b12345678", puzzle8.get(optimalPath).toString());

            //Gets the moveset that Astar made on its path
            LinkedList<String> moveset = puzzle.solveAstar("h1");
            //Makes sure the puzzle reached the goal state
            assertEquals("b12345678", puzzle8.get(puzzle).toString());


            //Checks if the moves outputted by Astar actually is the optimal path
            assertEquals(optimalMoveSet.size(), moveset.size());
        }
    }

    @Test
    public void testCorrectPathSolveBeam() throws NoSuchMethodException, FileNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, InstantiationException {
        EightPuzzle beamSearch = new EightPuzzle(System.getProperty("user.dir") + "\\test\\Empty.txt");
        char[][] x = new char[9][9];
        Method randomizeState = puzzleStateClass.getDeclaredMethod("randomizeState", String.class);
        randomizeState.setAccessible(true);
        Field puzzle8 = EightPuzzle.class.getDeclaredField("puzzle8");
        puzzle8.setAccessible(true);

        beamSearch.maxNodes("1000");
        EightPuzzle.PuzzleState testGoalPath = (EightPuzzle.PuzzleState) constructor.newInstance(beamSearch, new char[9], null, "", 0, 0);
        for (int i = 1; i < 1000; i++)
        {
            //Randomizes the state for the search algorithm
            randomizeState.invoke((puzzle8.get(beamSearch)), String.valueOf(25));
            System.out.println("Initial State: " + puzzle8.get(beamSearch).toString());

            //Assigns said random state to our testGoalPath
            testGoalPath.setState(puzzle8.get(beamSearch).toString());

            //Gets the moveset local beam search made on its path
            LinkedList<String> moveset = beamSearch.solvebeam(String.valueOf(10));
            //Makes sure the puzzle reached the goal state
            assertEquals("b12345678", puzzle8.get(beamSearch).toString(), "The number of puzzles solved: " + i);

            //Does the moves on the testGoalPath
            for (int j = 0; j != moveset.size(); )
            {
                testGoalPath.move(moveset.remove());
            }
            //Checks if the moves outputted by Astar actually reach the goal State
            assertEquals("b12345678", testGoalPath.toString());
        }
    }

}
