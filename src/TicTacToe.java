import java.io.*;
import java.util.*;

public class TicTacToe   {

	static final int ROWS = 3;
	static final int COLS = 3;

	static int[][] cells = new int[ROWS][COLS];
	static boolean player1turn;
	static int numPlayers=-1;
	static int winner;
	static int numMoves=0;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		displayMainMenuOptions();
	}

	//displays the main menu of the game
	public static void displayMainMenuOptions() throws IOException {

		while (!(numPlayers==3)) {
			System.out.println("Hello! Welcome to Yang's Tic-Tac-Toe Game. Select an option below. (Ex. 1 for 2-player)"); //displays the menu

			System.out.println();

			System.out.println("1. Play against a computer");
			System.out.println("2. Play against another player");
			System.out.println("3. Exit");

			System.out.println();

			System.out.println("Please select an option from the menu: (1,2,3)");

			numPlayers = getValidRangeInteger(1,3);

			if (!(numPlayers==3)) { //Sneaky code manipulation, if there's three players then there's no game
				playGame();

			}	
		}

		System.out.println("Thank you for playing!"); //end of game
	}

	//ensures that the user enters a valid integer within the min and max boundaries
	public static int getValidRangeInteger(int min, int max) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); //br instantiation

		int userInt=-1;

		while (userInt<min || userInt>max) {
			try {
				String input = br.readLine();
				userInt = Integer.parseInt(input);

			} catch (NumberFormatException e){ //illegal input
				System.out.println("Please enter a valid integer");
			}
			if (userInt<min || userInt>max)
				System.out.println("Please enter a valid option from the menu: ("+min+" to "+max+")");
		}
		return userInt;

	}

	//sets up the intial conditions of the game and also calls the next appropriate move ex. who goes first
	public static void playGame() throws IOException {

		int selection=-1;

		resetGame();

		System.out.println("Choose 1 for choosing who goes first, or choose 2 for it to be randomly decided");

		selection=getValidRangeInteger(1,2);

		decideWhoGoesFirst(selection);

		while (winner==0 && !(numMoves==9)) {

			decideMove();

		}

		displayWinner();


	}

	//resets the game, runs every iteration of the game
	public static void resetGame() {

		resetGrid();

		winner=0;

		numMoves=0;
	}

	//displays the winner of the game
	public static void displayWinner() {
		if (numMoves==9 && winner==0)
			System.out.println("Game ended in a tie.");
		else {
			System.out.print("The winner is ");
			if (winner==1)
				System.out.print("Player 1");
			else if (winner==2)
				if (numPlayers==2)
					System.out.print("Player 2");
				else
					System.out.print("Computer");
		}

		System.out.println();
		System.out.println();
	}

	//assists the user in entering their move
	public static void decideMove() throws IOException {

		int xCoord, yCoord;

		int[] pcMove;

		printGrid();

		while (winner==0 && !(numMoves==9) ) {

			if (!player1turn && numPlayers == 1) {
				System.out.println("Computer's Turn");

				pcMove=compMove();
				xCoord=pcMove[0];
				yCoord=pcMove[1];

				checkLegalMove(pcMove[0],pcMove[1]);

			}	
			else {
				if (player1turn) //Displays correct prompt of either Player 1 or 2
					System.out.println("Player 1's Turn");
				else
					System.out.println("Player 2's Turn");

				System.out.println("Please enter the coordinates of your piece X. (First, input the value for X, then the value for Y)");
				System.out.println("Please enter your value for the X coordinate");
				xCoord=getValidRangeInteger(0,2);
				System.out.println("Please enter your value for the Y coordinate");
				yCoord=getValidRangeInteger(0,2);

				checkLegalMove(xCoord,yCoord);

			}

			printGrid();

			checkWinner();

		}

	}

	//checks if there is a winner in the game
	public static void checkWinner() {
		for (int i=0;i<ROWS;i++) { 
			if ((cells[i][0]==cells[i][1]) && (cells[i][1]==cells[i][2])) { //checks rows
				if (!(cells[i][0]==0))
					winner=cells[i][0];
			}
			else if ((cells[0][i]==cells[1][i]) && (cells[1][i]==cells[2][i]))  //checks cols
				if (!(cells[0][i]==0))
					winner=cells[0][i];
		}

		if ((cells[0][0]==cells[1][1]) && (cells[1][1]==cells[2][2]))  //checks top left down right diagonal
			if (!(cells[0][0]==0))
				winner=cells[0][0];

		if ((cells[2][0]==cells[1][1]) && (cells[1][1]==cells[0][2]))  //checks bottom left top right diagonal
			if (!(cells[2][0]==0))
				winner=cells[2][0];

	}		

	//determines in an array the coordinates for the next best possible move by the computer
	//depth level of 9, meaning 9 moves
	//returns the best move to make in terms of the computer	
	public static int[] compMove() {
		int[] bestMove = minimax(9, true);
		return bestMove;
	}

	//using the minimax algorithm, returns the best possible move by the computer returning it in an array {xCoordinate, yCoordinate}
	public static int[] minimax(int depth, boolean isComp) {

		List<int[]> possibleMoves = generateMoves(); //goes through the grid to find all the possible moves that can be made

		int bestScore;

		if (isComp) //Computer's turn
			bestScore = -1000; //Anything can be higher than this value
		else	//Player's Turn
			bestScore = 1000; //Anything can be lower than this value


		int currScore; //an integer that scores the current possible score
		int bestX = -1; //setting the xCoordinate as -1 by default to prevent a valid default return
		int bestY = -1; //setting the yCoordinate as -1 by default to prevent a valid default return

		if (possibleMoves.isEmpty() || depth == 0) { //no more possible moves, either from there already being a winner or full board
			bestScore = evaluate(); 
		}	
		else {
			for (int[] move : possibleMoves) { //for every pair of moves in the moveset
				if (isComp) {  //computer's hypothetical turn

					cells[move[0]][move[1]]=2; //"suppose" the computer makes their move here

					currScore = minimax(depth - 1, false)[0]; //returns back the score and not the coordinates

					if (currScore > bestScore) { //if our current score is better than the best score possible
						bestScore = currScore; //then our score becomes the best
						bestX = move[0]; //we remember the position of the the best move
						bestY = move[1];
					}

				} 
				else {  //player's hypothetical turn

					cells[move[0]][move[1]]=1; //"suppose" player makes their move here

					currScore = minimax(depth - 1, true)[0]; //returns back the score and not the coordinatess

					if (currScore < bestScore) { //if the current chance of winning for the player is worse than their best chance
						bestScore = currScore; //the current chance of winning for the computer becomes the best
						bestX = move[0]; //we remember the position of the the best move
						bestY = move[1];
					}

				}
				cells[move[0]][move[1]]=0; //reverses the move on the board
			}
		}
		
		int[] bestCombo = new int[] {bestScore, bestX, bestY};
		return bestCombo; //returns the chance of winning, and the best coordinates to move to to make it
	}

	public static boolean hasWinner() {
		boolean isWinner=false;
		for (int i=0;i<ROWS;i++) { 
			if ((cells[i][0]==cells[i][1]) && (cells[i][1]==cells[i][2])) { //checks rows
				if (!(cells[i][0]==0))
					isWinner=true;
			}
			else if ((cells[0][i]==cells[1][i]) && (cells[1][i]==cells[2][i]))  //checks cols
				if (!(cells[0][i]==0))
					isWinner=true;
		}

		if ((cells[0][0]==cells[1][1]) && (cells[1][1]==cells[2][2]))  //checks top left down right diagonal
			if (!(cells[0][0]==0))
				isWinner=true;

		if ((cells[2][0]==cells[1][1]) && (cells[1][1]==cells[0][2]))  //checks bottom left top right diagonal
			if (!(cells[2][0]==0))
				isWinner=true;
		return isWinner;
	}		

	//generates the list of possible moves that can be made and returns it in a list of int arrays
	public static List<int[]> generateMoves() {

		List<int[]> possibleMoves = new ArrayList<int[]>(); // allocate List
		int[] testCoordinate = new int[2];

		// checks if there are already any winners, if there are then no moves will be returned
		if (hasWinner()) {
			return possibleMoves;  
		}

		// Search for any empty cells and adds it to the List
		for (int x = 0; x < ROWS; x++) 
			for (int y = 0; y < COLS; y++) 
				if (cells[x][y] == 0) { //no move made here
					testCoordinate[0]=x; //x-coordinate
					testCoordinate[1]=y; //y-coordinate
					possibleMoves.add(testCoordinate);
				}

		return possibleMoves;

	}

	//evaluates the possibility of winning in terms of score by computing each of the win scenarios
	public static int evaluate() {
		int totalScore = 0; //total score of the 8 combinations
		int[] coord1,coord2,coord3 =new int[2];

		for (int i=0;i<ROWS;i++) {
			coord1=new int[] {i,0};
			coord2=new int[] {i,1};
			coord3=new int[] {i,2};
			totalScore += lineScore(coord1,coord2,coord3); //accumulates first, second, and third rows
			
			coord1=new int[] {0,i};
			coord2=new int[] {1,i};
			coord3=new int[] {2,i};
			totalScore += lineScore(coord1,coord2,coord3); //accumulates first, second, and third columns
		}

		totalScore += lineScore(new int[] {0,0},new int[] {1,1},new int[] {2,2});  //diagonal going down
		totalScore += lineScore(new int[] {2,0},new int[] {1,1},new int[] {0,2});  //diagonal going up

		return totalScore;
	}

	//heuristically evaluates the score of each line by determining the combination of cells
	//ranges from -100, -10, -1, 0, 1, 10, 100 depending on the combination of cells
	//ex. xox returns 0 since they cancel out, xx returns 10, oo returns -10, x_x returns 10 (_ being blank), etc. etc.
	private static int lineScore(int[] coord1, int[] coord2, int[] coord3) {
		int score = 0;

		int x1=coord1[0];
		int y1=coord1[1];

		int x2=coord2[0];
		int y2=coord2[1];

		int x3=coord3[0];
		int y3=coord3[1];

		// checks if first cell is computers
		// increments score to 1
		// if player's
		// changes to -1
		// else leaves as 0 if blank, does not need to be coded
		if (cells[x1][y1] == 2)  			
			score = 1;						
		else if (cells[x1][y1] == 1) 		
			score = -1; 				
		
		// if computer occupies second cell
		// based on elimination, if first score is occupied, turns score into 10
		// if something cancels out the first cell like xo, then returns 0, not just making score 0 since theres no chance either way
		// else score becomes 1 since there is nothing in first cell
		if (cells[x2][y2] == 2) 			
			if (score == 1) 				
				score *= 10;						
			 else if (score == -1)  			
				return 0;				
			 else   							
				score = 1; //blank cell and then a computer cell

		// if player owns second cell
		// if first cell is player's, then score becomes 10 since second cell is also player's
		// if first cell is computers, then returns 0 since ox cancels out, and anything past that also has 0 chance of winning
		// else score becomes -1 since first cell is blank
		else if (cells[x2][y2] == 1) 	
			if (score == -1)  					
				score *= 10;					
			else if (score == 1)			
				return 0;		
			else  						 
				score = -1; //blank cell and then a player cell					

		// if computer has third cell
		// checks if score is greater than 0
		// the only possiblity for score to be greater than 0 at this point is if computer can still win from this line
		// which can make score 10 or 100, depending
		// else if the player has any chance of winning on this line
		// x cancels it out, making you return 0
		// the deduction from computer or player either having a possibility to win and for that to be calculated
		// followed along with the fact that all things that were cancelled out (ex. xo, ox, etc) were already returned
		// states that score could only be _ _ x now
		// having a score of 1
		if (cells[x3][y3] == 2) 			
			if (score > 0)					
				score *= 10;					
			else if (score < 0) 			
				return 0;						
			else							
				score = 1;	//two blank cells and a comp cell						

		// if player has third cell
		// checks if score is less than 0
		// the only possiblity for score to be less than 0 at this point is if player can still win from this line
		// which can make score -10 or -100, depending
		// else if the computer has any chance of winning on this line
		// o cancels it out, making you return 0 
		// similar to the condition above, if player cant win and computer cant win, and a tie is already returned
		// then the last cell is _ _ o
		// having a score of 1
		else if (cells[x3][y3] == 1)	
			if (score < 0) 					
				score *= 10;
			else if (score > 0) 			
				return 0;						
			else							
				score = -1;	//two blank cells and a player cell

		return score;
	}

	//determines if a move is legal (ie. nobody has moved there already)
	//also increments the number of moves made
	private static void checkLegalMove(int xCoord, int yCoord) {
		if (cells[xCoord][yCoord]==0) {
			if (player1turn)
				cells[xCoord][yCoord]=1;
			else
				cells[xCoord][yCoord]=2;
			if (player1turn)
				player1turn=false;
			else
				player1turn=true;
			numMoves++;
		}
		else 
			System.out.println("Coordinate already used. Please enter a different coordinate");
	}

	//simple double for loop for resetting the grid
	//#CleanCode
	public static void resetGrid() {
		for (int i=0;i<ROWS;i++) 
			for (int j=0;j<COLS;j++)
				cells[i][j]=0;
	}

	//prints the grid of the game
	public static void printGrid() {

		System.out.println(); //padding

		//print y label and indexes
		System.out.println("\t\t\t  Y");
		System.out.println("\t      |   0   |   1   |   2   |");

		//print rows
		for (int i=0;i<COLS;i++) {

			if (i==((COLS-1)/2))
				System.out.print("  X   |   "+i+"   |");
			else
				System.out.print("      |   "+i+"   |");

			for (int j=0;j<ROWS;j++) {

				System.out.print("   "); //spacing

				if (cells[i][j]==1)
					System.out.print("X");
				else if (cells[i][j]==2)
					System.out.print("O");
				else
					System.out.print(" ");

				System.out.print("   |");

			}

			System.out.println(); //padding

		}

		System.out.println(); //padding

	}

	//using Math.rand to determine who goes first, or gives the user the option of choosing
	public static void decideWhoGoesFirst(int selection) throws IOException {

		if (selection==1) {
			System.out.println("Who would you like to go first? Player 1 or Player 2? (Enter 1 for Player 1, or Enter 2 for Player 2)");
			selection=getValidRangeInteger(1,2);
			if (selection==1) 
				player1turn=true;
			else if (selection==2)
				player1turn=false;
		}
		else if (selection==2) {
			System.out.println("A random player will be decided.");
			int randInt;
			randInt = (int) Math.round(Math.random());
			if (randInt == 0) 
				player1turn=true;
			else
				player1turn=false;
		}
		if (player1turn)
			System.out.println("Player 1 will go first.");
		else
			System.out.println("Player 2 will go first.");


	}



}
