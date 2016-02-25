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

			if (!(numPlayers==3)) {
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

		int[] bestMove;

		printGrid();

		while (winner==0 && !(numMoves==9) ) {

			if (!player1turn && numPlayers == 1) {
				System.out.println("Computer's Turn");

				bestMove=minimax(9-numMoves,true);
				xCoord=bestMove[1];
				yCoord=bestMove[2];

				checkLegalMove(bestMove[1],bestMove[2]);

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

	public static int[] minimax(int depth, boolean compTurn) {
		int score, bestX=-1, bestY=-1;
		if (compTurn)
			score=-1000;
		else
			score=1000;
		int currentScore;
		if (depth==0||hasWinner())
			score=getBoardScore();
		else {
			List<int[]> possibleMoves = new ArrayList<int[]>();
			possibleMoves = generateMoves();
			for (int[] move : possibleMoves) {
				if (compTurn) {
					cells[move[0]][move[1]]=2;
					currentScore=minimax(depth-1,false)[0];
					if (currentScore>score) {
						score=currentScore;
						bestX=move[0];
						bestY=move[1];
					}	
				}
				else {
					cells[move[0]][move[1]]=1;
					currentScore=minimax(depth-1,true)[0];
					if (currentScore<score) {
						score=currentScore;
						bestX=move[0];
						bestY=move[1];
					}
				}
				cells[move[0]][move[1]]=0;
			}
		}
		int[] bestCombo = new int[] {score,bestX,bestY};
		return bestCombo;
	}
	
	public static List<int[]> generateMoves() {
		List<int[]> listMoves = new ArrayList<int[]>();
		
		for (int i=0;i<ROWS;i++)
			for (int j=0;j<COLS;j++)
				if (cells[i][j]==0)
					listMoves.add(new int[] {i,j});
		
		return listMoves;
	}
	
	public static int getBoardScore() {
		int score=0;		

		score+=getScore(new int[] {0,0},new int[] {0,1},new int[] {0,2}); //checks rows score
		score+=getScore(new int[] {1,0},new int[] {1,1},new int[] {1,2});
		score+=getScore(new int[] {2,0},new int[] {2,1},new int[] {2,2});

		score+=getScore(new int[] {0,0},new int[] {1,0},new int[] {2,0}); //checks cols score
		score+=getScore(new int[] {0,1},new int[] {1,1},new int[] {2,1});
		score+=getScore(new int[] {0,2},new int[] {1,2},new int[] {2,2});
		
		score+=getScore(new int[] {0,0},new int[] {1,1},new int[] {2,2}); //checks diagonals score
		score+=getScore(new int[] {2,0},new int[] {1,1},new int[] {0,2});
		
		return score;
	}
	
	//using heuristics to determine the value of each win by the combinations in the cells
	public static int getScore(int[] a, int[] b, int[] c) {
		int score=0;
		
		//first cell
		if (cells[a[0]][a[1]]==2)
			score=1;
		else if (cells[a[0]][a[1]]==1)
			score=-1;
		
		//second cell
		if (cells[b[0]][b[1]]==2)
			if (score==1)
				score=10;
			else if (score==-1)
				return 0;
			else if (score==0) //empty first cell
				score=1;
		
		if (cells[b[0]][b[1]]==1)
			if (score==-1)
				score=-10;
			else if (score==1)
				return 0;
			else if (score==0) //empty first cell
				score=-1;
		
		//third cell
		if (cells[c[0]][c[1]]==2)
			if (score>0)
				score*=10; //if cell has anything in either cell, multiply score by 10 (ie. from 10 to 100, or 1 to 10)
			else if (score==0)
				score=1;
			else if (score<0) //empty first cell
				return 0;
		
		if (cells[c[0]][c[1]]==1)
			if (score<0)
				score*=10; //if cell has anything in either cell, multiply score by 10 (ie. from -10 to -100, or -1 to -10)
			else if (score==0)
				score=-1;
			else if (score>0) //empty first cell
				return 0;
		
		return score;
		
		
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

		if ((cells[0][0]==cells[1][1]) && (cells[1][1]==cells[2][2]))  //checks down diagonal
			if (!(cells[0][0]==0))
				winner=cells[0][0];

		if ((cells[2][0]==cells[1][1]) && (cells[1][1]==cells[0][2]))  //checks up diagonal
			if (!(cells[2][0]==0))
				winner=cells[2][0];

	}		


	public static boolean hasWinner() {
		boolean isWinner=false;
		for (int i=0;i<ROWS;i++) { 
			if ((cells[i][0]==cells[i][1]) && (cells[i][1]==cells[i][2])) //checks rows
				if (!(cells[i][0]==0))
					isWinner=true;

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