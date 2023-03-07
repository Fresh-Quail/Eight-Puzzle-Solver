import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class EightPuzzle {

    // Represents the heuristic to be used for search
    private String heuristic = "";
    // Default value we shall say is 25 for maximum nodes to be considered
    private int maxNodes = 1000;
    // Represents the eight puzzle - initial state is goal
    private PuzzleState puzzle8 = new PuzzleState(new char[]{'b', '1', '2', '3', '4', '5', '6', '7', '8'}, null, "", 0, 0);
    //Represents the Random variable used for random moves
    private Random random = new Random();

    /**
     * Reads a sequence of commands from a given text file
     * Takes a file location as input
     */
    public EightPuzzle(String textFile) throws FileNotFoundException, InvocationTargetException, IllegalAccessException {
        //Sets the seed for the program
        random.setSeed(22222);
        //Represents the text file
        Scanner sc = new Scanner(new File(textFile));
        //Represents all of the possible commands
        Method[] methods = this.getClass().getMethods();
        Method[] stateMethods = PuzzleState.class.getMethods();
        //Represents the current command
        String command;

        //Iterates through the text file while it is not empty
        while(sc.hasNext())
        {
            //Takes the next command
            command = sc.next();
            //Special case if the program is asked to solve
            if(command.equals("solve"))
            {
                command += sc.next().replace("-", "");
            }

            //Finds the command and invokes it
            for (int i = 0; i < methods.length; i++)
            {
                if(command.equals(methods[i].getName()))
                {
                    //Special case for printState command
                    if(command.equals("printState"))
                        methods[i].invoke(this);
                    else
                        methods[i].invoke(this, sc.next());
                }
            }
            for (int i = 0; i < stateMethods.length; i++)
            {
                if(command.equals((stateMethods[i].getName())))
                {
                    //Special case for setState command
                    if (command.equals("setState"))
                        stateMethods[i].invoke(puzzle8, sc.next() + sc.next() + sc.next());
                    else
                        stateMethods[i].invoke(puzzle8, sc.next());
                }
            }
        }

    }

    /**
     * Prints the current puzzle state
     */
    public void printState()
    {
        //Prints the current state to the terminal
        System.out.println(String.valueOf(puzzle8.state).substring(0, 3) + " " + String.valueOf(puzzle8.state).substring(3, 6)
                + " " +  String.valueOf(puzzle8.state).substring(6));
    }

    /**
     * Sets the Maximum number of nodes to be considered during a search
     */
    public void maxNodes(String str)
    {
        maxNodes = Integer.parseInt(str);
    }

    /**
     * Solves the puzzle from its current state using A-star and the given heuristic
     */
    public LinkedList<String> solveAstar(String heuristic)
    {
        //Represents the number of nodes that have been considered - always considers the first state
        int numNodes = 1;
        //Represents the parent state, i.e. the current state generating adjacent states, while considering states
        PuzzleState currentState;
        // Represents whether we have reached the max number of nodes
        boolean maxReached = numNodes >= maxNodes;

        //Queue that will be utilized for A-star
        PriorityQueue<PuzzleState> aStarQ = new PriorityQueue<>((o1, o2) ->  (o1.moves + o1.heuristicVal) - (o2.moves + o2.heuristicVal));
        HashMap<String, Integer> reached = new HashMap<>();

        //Sets the heuristic to be used and adds the start state for search
        this.heuristic = heuristic;
        aStarQ.add(new PuzzleState(puzzle8.state.clone(), null, "", 0, puzzle8.blankPos));


        //Iterates until the goal state is about to be popped from the queue or maxNodes has been reached
        while(!(aStarQ.peek().toString().equals("b12345678")) && !maxReached){

            //Pops the next state and adds it to the reached set
            currentState = aStarQ.poll();
            reached.putIfAbsent(currentState.toString(), currentState.heuristicVal);

            //For each plausible neighboring state, add it to the queue if it has not been reached
            for (PuzzleState adjacentState: currentState.adjacentStates())
            {
                if(!reached.containsKey(adjacentState.toString())) {
                    //Increment the number of considered nodes if it is not already in the reached set
                    numNodes++;
                    //Add it to frontier
                    aStarQ.add(adjacentState);

                    if(numNodes >= maxNodes) {
                        System.out.println("The maximum number of nodes to be considered during search has been exceeded - Nodes genned: " + numNodes);
                        maxReached = true;
                        break;
                    }
                } // Otherwise, if it exists in reached but we have a smaller cost for this state
                //Replace it in reached and add it to the queue
                else if(adjacentState.heuristicVal < reached.get(adjacentState.toString())) {
                    reached.remove(adjacentState.toString());
                    reached.put(adjacentState.toString(), adjacentState.heuristicVal);
                    aStarQ.add(adjacentState);
                    numNodes++;

                    if(numNodes >= maxNodes) {
                        System.out.println("The maximum number of nodes to be considered during search has been exceeded - Nodes genned: " + numNodes);
                        maxReached = true;
                        break;
                    }
                }
            }
        }

        //Sets the current state to the last state of the search
        puzzle8.setState(aStarQ.peek().toString());

        //If the goal was found, print the move sequence and number of moves taken
        //aStarQ.peek() should represent the un-popped goal state, if the queue is note empty
        if(aStarQ.peek().toString().equals("b12345678")) {
            //The set of moves to the goal state
            LinkedList<String> moveset = new LinkedList<>();
            //Node pointer to create the set of moves
            PuzzleState node = aStarQ.peek();

            //If the initial state was not the goal state
            //Checking if there was a move to the goal state
            if(node.move.length() != 0)
                moveset.addFirst(node.move);
            else
                moveset.addFirst("None");

            //Iterates through all the parents in order to construct the set of moves to the goal
            while(node.previous != null && node.previous.previous != null)
            {
                node = node.previous;
                moveset.addFirst(node.move);
            }
            System.out.println("Moves required: " + aStarQ.peek().moves + "\nSet of moves from start state: " + moveset.toString());
            return moveset;
        }
        return new LinkedList<>();
    }

    /**
     * UCS implementation based off A-star to attempt to provide the optimal path
     * @return The list of moves taken to get to the goal state if it exists
     */
    private LinkedList<String> optimalPath()
    {
        //Represents the parent state while considering states
        PuzzleState currentState;
        int numNodes = 0;
        //Queue that will be utilized for A-star
        PriorityQueue<PuzzleState> aStarQ = new PriorityQueue<>((o1, o2) ->  (o1.moves) - (o2.moves));
        HashSet<String> reached = new HashSet<>();

        //Sets the heuristic to be used
        this.heuristic = "";
        aStarQ.add(new PuzzleState(puzzle8.state, null, "", 0, puzzle8.blankPos));


        //Iterates until the goal state is about to be popped from the queue or the queue is empty
        while(!(aStarQ.peek().toString().equals("b12345678"))){

            //Pops the next state and adds it to the reached set, incrementing the number of nodes considered
            currentState = aStarQ.poll();
            reached.add(currentState.toString());
            numNodes++;

            //For each plausible neighboring state, add it to the queue if it has not been reached
            for (PuzzleState adjacentState: currentState.adjacentStates())
            {
                if(!reached.contains(adjacentState.toString())) {
                    //Increment the number of considered nodes if it is not already in the reached set
                    numNodes++;
                    //Add it to frontier
                    aStarQ.add(adjacentState);
                }
            }
        }
        //Sets the current state to the last state of the search
        puzzle8.setState(aStarQ.peek().toString());

        //If the goal was found, print the move sequence and number of moves taken
        //aStarQ.peek() should represent the un-popped goal state, if the queue is note empty
        if(aStarQ.peek().toString().equals("b12345678")) {
            //The set of moves to the goal state
            LinkedList<String> moveset = new LinkedList<>();
            //Node pointer to create the set of moves
            PuzzleState node = aStarQ.peek();

            //If the initial state was not the goal state
            //Checking if there was a move to the goal state
            if(node.move.length() != 0)
                moveset.addFirst(node.move);
            else
                moveset.addFirst("None");

            //Iterates through all the parents in order to construct the set of moves to the goal
            while(node.previous != null && node.previous.previous != null)
            {
                node = node.previous;
                moveset.addFirst(node.move);
            }
            System.out.println("Moves required: " + aStarQ.peek().moves + "\nSet of moves from start state: " + moveset);
            return moveset;
        }
        return new LinkedList<>();
    }

    /**
     * Solves the puzzle from its current state using local beam search with 'k' states
     * Utilizes 'h2' as its heuristic
     * @return The list of moves taken to get to the goal state if it exists
     */
    public LinkedList<String> solvebeam(String str)
    {
        //Represents the number of states to keep
        int numStates = Integer.parseInt(str);
        //Represents the number of nodes that have been considered - always considers the start state
        int numNodes = 1;
        //Represents whether maxNodes has been reached
        boolean maxReached = numNodes >= maxNodes;
        //Represents the best k states
        List<PuzzleState> bestK = new ArrayList<>();
        //Represents the state that the search ends on
        PuzzleState endState = puzzle8;
        // Queue that will be to sort best to worst neighboring states
        PriorityQueue<PuzzleState> neighbors = new PriorityQueue<>((o1, o2) ->  (o1.heuristicVal) - (o2.heuristicVal));

        //Sets the heuristic to be used for search
        heuristic = "h2";
        //Assigns the first state in our best 'k'
        bestK.add(new PuzzleState(puzzle8.state.clone(), null, "", 0, puzzle8.blankPos));

        // Iterates until the goal state has been found or it has been determined it cannot be reached
        // Does not iterate if already at goal state
        // And there are still more states to consider
        while(!maxReached && !endState.toString().equals("b12345678") && !bestK.isEmpty()){
            // For the best k states, consider and add all their neighbors
            for(PuzzleState state: bestK)
            {
                // Produces the neighboring states for each state
                for (PuzzleState adjacentState: state.adjacentStates())
                {
                    // And adds the neighboring states to the queue
                        neighbors.add(adjacentState);
                        //Increases the number of nodes we have considered
                        numNodes++;
                        if (numNodes >= maxNodes)
                        {
                             System.out.println("The maximum number of nodes to be considered during search has been exceeded - Nodes genned: " + numNodes);
                            //Assigns the last state of the puzzle as the end state
                            endState = state;
                            maxReached = true;
                            break;
                        }
                }
                if(maxReached) break;
            }

            // Takes the next 'k' best states from the queue
            bestK.clear();
            //Tries to adds states as long as there are not K states in bestK and there are children to add
            while(bestK.size() < numStates && !neighbors.isEmpty())
            {
                // Only adds it if it's value is smaller than its parent state, and not already in bestK
                if(neighbors.peek().heuristicVal < neighbors.peek().previous.heuristicVal && !bestK.contains(neighbors.peek())) {
                    bestK.add(neighbors.peek());
                }

                // Breaks out if the goal has been found and sets it as the end state
                // Removes the element at the head of the queue as it has been considered
                if(neighbors.peek().toString().equals("b12345678")) {
                    endState = neighbors.remove();
                    break;
                }
                else
                    neighbors.remove();
            }
            // And discards the rest of the states
            neighbors.clear();
        }

        //Assigns the last state of the puzzle to the programs puzzle
        puzzle8.setState(endState.toString());

        //If the goal was found, print the move sequence and number of moves taken
        //aStarQ.peek() should represent the un-popped goal state, if the queue is note empty
        if(endState.toString().equals("b12345678"))
        {
            //If the goal was found, print the move sequence and number of moves taken
            //The set of moves to the goal state
            LinkedList<String> moveset = new LinkedList<>();
            //Node pointer to create the set of moves
            PuzzleState node = endState;

            moveset.addFirst(node.move);
            //Iterates through all the parents in order to construct the set of moves to the goal
            while (node.previous != null && node.previous.previous != null)
            {
                node = node.previous;
                moveset.addFirst(node.move);
            }
            System.out.println("Moves required: " + endState.moves + "\nSet of moves from start state: " + moveset);
            return moveset;
        }
        return new LinkedList<>();
    }

    /**
     * Represents a puzzle state
     * If a move is passed as a parameter to this class, takes the given state and conducts the move
     */
    public class PuzzleState{
        /** Represents 'this' puzzle state */
        private char[] state;
        /** Represents the previous puzzle state */
        private final PuzzleState previous;
        /** Represents the move required to reach this puzzle state */
        private final String move;
        /** Represents the number of moves required to reach this state */
        private final int moves;
        /** Represents the heuristic value for this state */
        private int heuristicVal;
        /** Represents the blank position for this state */
        private int blankPos;

        /**
         * Initializes this state
         * @param state The character array of the previous state
         * @param move The move that will be taken to get to the next state ('this' is the next state)
         * @param previous The parent PuzzleState object of this state
         */
        public PuzzleState(char[] state, PuzzleState previous, String move, int moves, int blankPos){
            this.state = state;
            this.previous = previous;
            this.move = move;
            this.moves = moves;
            this.blankPos = blankPos;
            this.move(move);
            if(heuristic.equals("h1"))
            {
                heuristicVal = misplaced();
            }
            else if(heuristic.equals("h2"))
            {
                heuristicVal = distances();
            }
            //Conduct the move on this state, if it is a move
        }

        /**
         * Sets the state of the puzzle using input from a text file, using the given Scanner object
         */
        public void setState(String str)
        {
            //Takes the elements of the string and transcribes it into the puzzle state
            for (int i = 0; i < 9; i++)
            {
                //Skips the spaces in the string
                state[i] = str.charAt(i);
                //Updates the position of the blank tile
                if(state[i] == 'b'){
                    blankPos = i;
                }
            }
        }

        /**
         * Moves the blank tile up, down, left, or right
         * Does not allow illegal moves
         * @throws RuntimeException If an illegal move is made
         */
        public void move(String move)
        {
            if(this.legalMove(move))
            {
                if (move.equals("up"))
                {
                    state[blankPos] = state[blankPos - 3];
                    state[blankPos - 3] = 'b';
                    blankPos -= 3;
                }
                if (move.equals("down"))
                {
                    state[blankPos] = state[blankPos + 3];
                    state[blankPos + 3] = 'b';
                    blankPos += 3;
                }
                if (move.equals("right"))
                {
                    state[blankPos] = state[blankPos + 1];
                    state[blankPos + 1] = 'b';
                    blankPos++;
                }
                if (move.equals("left"))
                {
                    state[blankPos] = state[blankPos - 1];
                    state[blankPos - 1] = 'b';
                    blankPos--;
                }
            }
            else
                throw new RuntimeException("Illegal Move");
        }

        /**
         * Randomizes the current state of this puzzle
         */
        public void randomizeState(String str)
        {
            //Represents the number of random moves to be made
            int numMoves = Integer.parseInt(str);
            //Sets the state to the goal state
            setState("b12345678");

            //Represents one of the four possible moves
            String move = randomMove();
            for (int i = 0; i < numMoves; i++)
            {
                //Only does legal moves
                while(!legalMove(move))
                    move = randomMove();
                move(move);
            }
        }

        /**
         * Checks if the current move is a legal move
         */
        private boolean legalMove(String move)
        {
            //Defines the bounds/illegal moves of the puzzle
            if(blankPos < 3 && move.equals("up"))
                return false;
            if(blankPos > 5 && move.equals("down"))
                return false;
            if(blankPos % 3 == 0 && move.equals("left"))
                return false;
            if(blankPos % 3 == 2 && move.equals("right"))
                return false;

            return true;
        }

        /**
         * Chooses a random move to make
         */
        private String randomMove()
        {
            //Possible values of 0 to 3
            int rand = random.nextInt(4);
            String move;
            if(rand == 0)
                move = "up";
            else if(rand == 1)
                move = "right";
            else if(rand == 2)
                move = "down";
            else
                move = "left";
            return move;
        }

        /**
         * Computes all of the states adjacent to this state
         * @return A list of the states adjacent to this state
         */
        private List<PuzzleState> adjacentStates()
        {
            List<PuzzleState> states = new LinkedList<>();
            // Finds possible states and adds them to the array
            if(legalMove("up")) states.add(new PuzzleState(state.clone(), this, "up", moves + 1, blankPos));
            if(legalMove("right")) states.add(new PuzzleState(state.clone(), this, "right", moves + 1, blankPos));
            if(legalMove("down")) states.add(new PuzzleState(state.clone(), this, "down", moves + 1, blankPos));
            if(legalMove("left" )) states.add(new PuzzleState(state.clone(), this, "left", moves + 1, blankPos));
            return states;
        }

        /**
         * Counts the number of misplaced tiles in this puzzle state
         */
        private int misplaced()
        {
            int misplaced = 0;
            //Checks each tile for displacement
            for (int i = 1; i < 9; i++)
            {
                //Skips the spaces in the string
                //Counts the number of misplaced tiles, not including the blank tile
                if(state[i] != (char)(48 + i) && state[i] != 'b')
                    misplaced++;
            }
            return misplaced;
        }

        /**
         * Computes the sum of the distances of the tiles from their goal positions
         */
        private int distances()
        {
            int distances = 0;
            //Counts the distance for each tile
            for (int i = 0; i < 9; i++)
            {
                char thisChar = state[i];
                //Skips the spaces in the string
                //Takes the distance each tile is from its designated position
                if(thisChar - 48 != i && thisChar != 'b') {
                    // Computes the distance by taking the difference of both:
                    // The row it is supposed to be in (thisChar/3),
                    // And the row it is in (i / 3), i.e. ((9 / 3)) = row 3
                    // Plus the difference of
                    // The column it is supposed to be in,
                    // And the column it is in (i - 3(i / 3)), ex. 7 - 3(7 / 3) = 7 - 3*2 = column 1
                    distances += Math.abs((i / 3) - ((thisChar - 48) / 3)) +
                                                Math.abs((i - 3 * (i / 3)) - ((thisChar - 48) - 3 * ((thisChar - 48) / 3)));
                }

            }
            return distances;
        }

        /**
         * Returns the string representation of the puzzle state
         */
        public String toString()
        {
            return String.valueOf(state);
        }

    }

    public void main(String[] args) throws FileNotFoundException, InvocationTargetException, IllegalAccessException {
        EightPuzzle puzzle = new EightPuzzle(args[0]);
    }
}
