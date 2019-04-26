/*
* Othello.java
*
* This class represents a Othello (TM)
* game, which allows two players to place
* pieces onto a board.  Each move can 
* result in outflanking 0 or more opponent's 
* piece.
*
* Theme: WeBareBears
* IceBear vs PanPan
* By: Elisa Luan
* ICS3U
*/

    public class Othello {
   
   /* constants */   
      final int MAXGAME;  	 	// the number of games a player needs to win to win the match  
      final int NUMPLAYER;  	// number of players in the game
      final int NUMROW;		 	// number of rows in the game board
      final int NUMCOL;	 	  	// number of columns in the game board
      final int EMPTY = -1; 	// represents an empty square on the game board 	
      final int PLAYER1 = 0;	// identification of player 1
      final int PLAYER2 = 1; 	// identification of player 2 
      final int PLAYERAI = 2; // identification of the AI (player 2)
   
      OthelloGUI gui;
      int numMove;				// number moves that have passed
      int curPlayer;				// represents which player is currently played
      int board[][];				// represents the array which stores the icons on the board
      int score[];				// represents the score of the player for number of matches won
      int check[][];				// represents the availability of an icon to be placed at a location on the board
      int flips;					// represents the number of flips that have occurred during a turn
   
   /*
   * Constructor:  Othello
   */
       public Othello(OthelloGUI gui) {
         this.gui = gui;
         NUMPLAYER = gui.NUMPLAYER;
         NUMROW = gui.NUMROW;
         NUMCOL = gui.NUMCOL;
         MAXGAME = gui.MAXGAME;
         flips = 0;													// initialization of all the values to an initial value (check for flip consistency)
         board = new int[NUMROW][NUMCOL];						// most of these values will be EMPTY or 0 to indicate nothing has been added
         check = new int [NUMROW][NUMCOL];
         score = new int [MAXGAME];
         for (int i = 0; i < NUMPLAYER; i++){
            score[i] = 0;
         }
         curPlayer = PLAYER1;
         numMove = 0; 
         initBoard();												// calls the method initBoard to be activated upon call
         gui.setPlayerScore(PLAYER1,0);
         gui.setPlayerScore(PLAYER2,0);     
      }
   
   /*
   * play
   * This method will be called when a square is clicked.  Parameter "row" and "column" is 
   * the location of the square that is clicked by the user
   */
   
       public void play (int row, int column) {						// the method that registers a click and performs game logic to flip / validate
         int flips = 0;
      
         if (validMove(row,column) == true){							// calls for the method to check if move is valid - if validMove is true...
            numMove++;															//	increases the number of moves done
            gui.setPiece(row, column, curPlayer);					 	// sets the piece with the given row and column inputted
            board[row][column] = curPlayer;							 	// assigns the array value of the location to be equal to the player value
            flips+= outFlankHori(row, column, curPlayer);			// calculates the number of flips created horizontally
            flips+= outFlankVert(row, column, curPlayer);	 		// calculates the number of flips created vertically
            flips+= outFlankDiag(row, column, curPlayer);	 		// calculates the number of flips created diagonally
            pieceCounter(); 													// calls the piece counting method to calculate the number of pieces on the board
         	
            if (flips > 0){											 		// if the flips is greater than 0...
               gui.showOutflankMessage(curPlayer, flips);					// outputs the number of flanks
            }         
            if (curPlayer == PLAYER1){										// if current player is player 1
               curPlayer = PLAYER2;												// changes current player to player 2
               AI();																	// calls the AI method to play ( YOU MAY COMMENT OUT TO HAVE PLAYER VS PLAYER)
            }
            else if (curPlayer == PLAYER2){								// if current player is player 2
               curPlayer = PLAYER1;												// changes current player to player 1
            }
            gui.setNextPlayer(curPlayer);									// changes the icon of the current player to the next player  
         }
         else{																	// if validMove is false...
            gui.showInvalidMoveMessage();									// displays a message to indicate it is an invalid move
         }
      
         flips = 0;															// resets the flip count to be 0
      
         if (numMove == NUMCOL*NUMROW-4){								// checks if it is end-game, where the number of moves is equal to number of possible moves - if they're equal...
         
            if (checkWinner() == EMPTY){										// checks with the checkWinner() method to see the value - if it is equal to EMPTY...
               gui.showTieGameMessage();											// it will be indicated that it is a tie-game scenario
               gui.resetGameBoard();												// resets the board to be blank
               numMove = 0;															// resets the move count to 0
               initBoard();															// calls the initBoard() method to intiailize the game status
               curPlayer = PLAYER1;													// makes the current player (starting player) to be player 1
            }
         
            if (checkWinner() > EMPTY){										// if the checkWinner() value is not equal to EMPTY...
               gui.showWinnerMessage(checkWinner());							// indicates which player is the winner based on the checkWinner() value
               score[checkWinner()]++;												// increases the score of the winning player by 1
               gui.setPlayerScore(checkWinner(), score[checkWinner()]);	// changes the score within the GUI of the winner with the appropriate score
            
               if (score[PLAYER1] == MAXGAME){									// if the score of player 1 is equal to the minimum games needed to win...
                  gui.showFinalWinnerMessage(PLAYER1);							// displays the appropriate winner message for the entire game for player 1
               }															
               else if (score[PLAYER2] == MAXGAME){							// if the score of player 2 is equal to the minimum games needed to win...
                  gui.showFinalWinnerMessage(PLAYER2);							// displays the appropriate winner message for the entire game for player 2
               }
               else{																		// if neither player has sufficient wins to win the entire game then...
                  gui.resetGameBoard();											// resets the board to be blank
                  initBoard();														// calls the initBoard() method to initialize the game status
                  curPlayer = PLAYER1;												// makes the current player (starting player) to be player 1
                  numMove = 0; 														// resets the number of moves to be 0
               }  
            }
         }     
      }   
   
       public void initBoard () {										// the method that is called to initialize the board to the starting status
      
         for (int i = 0 ; i < NUMROW; i++){
            for (int j = 0; j < NUMCOL; j++){
               board[i][j] = EMPTY; 									// initializes the board values to be EMPTY
               check[i][j] = 0;											// initializes the check values to be unavailable
            }
         }	
      	
         board[3][3] = PLAYER1;											// initializes the board values to be for a specific player
         board[3][4] = PLAYER2;
         board[4][3] = PLAYER2;
         board[4][4] = PLAYER1;
         gui.setPiece(3, 3, PLAYER1);									// places the pieces in the GUI for the specific player
         gui.setPiece(3, 4, PLAYER2);
         gui.setPiece(4, 3, PLAYER2);
         gui.setPiece(4, 4, PLAYER1); 
         for (int i = 2; i < 6; i++){	
            for (int j = 2; j < 6; j++){
               if (board[i][j] == EMPTY){								// if the location happens to not be taken by a piece
                  check[i][j] = 1;											// makes all the pieces around the flipped ones valid to be flipped
               }
            }
         }
         
      	pieceCounter();													// calls the piece counting method to update the number of pieces on the board
      	
      }
   
       public boolean validMove (int row, int col){				// the method that is called to verify if a move is valid
      
         if (check[row][col] == 1){										// if the provided row and column is valid (in a location that isn't occupied or has value 1)...
            for (int i = row-1; i <= row+1; i++){						// goes through the row before and after the given row 					
               for (int j = col-1; j <= col+1; j++){					// goes through the column before and after the given column
                  if (i > -1 && i < 8 && j > -1 && j < 8){			// checks if row and column is within range
                     if (board[i][j] == EMPTY){							// checks if the board value is EMPTY
                        check[i][j] = 1;									// changes the availability value of the location to be valid, or 1
                     }
                  }
               }
            }
            check[row][col] = 0;											// changes the availability of the entered location to be invalid as it is now taken
            return true;													// returns a true boolean to indicate it is indeed a valid move
         }
         return false;														// if the provided row and column is not valid, it returns a false boolean to indicate it is invalid
      }
   
       public int outFlankHori (int row, int col, int player){		// method used to detect the valid flanks horizontally to a location	
      	
         int sum = 0;															// initializes a sum to return number of flips per side
         int flips = 0;															// initializes a sum to return number of flips in total
         boolean finish = false;												// boolean to determine when the checking has ended from one side
         boolean AIcheck = false;
      
         if (player == PLAYERAI){											// if the player sent in was the AI...
            player = PLAYER2;														// sets the player to be the second player
            AIcheck = true;														// turns on the AIcheck 
         }
      
         if (col != 7){															// if the column is already not at the left edge...
            for (int i = col+1; i < NUMCOL; i++){							// goes through the possible columns until it reaches the right edge
            
               if (board[row][i] == player && finish == false){		// if the location is occupied by a piece of the same type and this is the first instance...
                  finish = true;														// declares the check to allow this to be the cap on this side
                  flips+= sum;               									// adds the flips accumulated during the check to the total sum
                  for (int j = i; j > col && AIcheck == false; j--){		// goes backwards from the cap to flip the pieces to the input location if it's not the AI
                     gui.setPiece(row, j, player);								// places the piece
                     board[row][j] = player;										// declares the flipped pieces to be of the same kind
                  }
               }
               else if (board[row][i] == EMPTY){							// otherwise if the piece is occupied by nothing...
                  finish = true;														// the check is declared true to end the search for valid flips
               }
               else{																	// otherwise if the piece is occupied by the opposing piece...
                  sum++;																// the sum for the number of valid flips increases by 1
               }
            }
            finish = false;													// reinitializes the cap check to be false for right to left check
            sum = 0;																// reinitializes the sum to 0 for right to left counting
         }
      	
         if (col != 0){															// if the column is already not at the right edge...
            for (int i = col-1; i > -1; i--){								// goes through the possible columns until it reaches the left edge
            
               if (board[row][i] == player && finish == false){		// if the location is occupied by a piece of the same type and this is the first instance...
                  finish = true;														// declares the check to allow this to be the cap on this side
                  flips+= sum;														// adds the flips accumulated during the check to the total sum
                  for (int j = i; j < col && AIcheck == false; j++){		// goes backwards from the cap to flip the pieces to the input location if it's not the AI
                     gui.setPiece(row, j, player);								// places the piece
                     board[row][j] = player;										// delcares the flipped pieces to be of the same kind
                  }
               }
               else if (board[row][i] == EMPTY){							// otherwise if the piece is occupied by nothing...
                  finish = true;														// the check is declared true to end the search for valid flips
               }
               else{																	// otherwise if the piece is occupied by the opposing piece...
                  sum++;																// the sum for the number of valid flips increases by 1
               }
            }
         }
         return flips;															// returns the total amount of flips performed 
      }
   
       public int outFlankVert (int row, int col, int player){		// method used to detect the valid flanks vertically to a location
      
         int sum = 0;															// initializes a sum to return number of flips per side
         int flips = 0;															// intiailizes a sum to return number of flips in total
         boolean finish = false;												// boolean to determine when the checking has ended from one side
         boolean AIcheck = false;
      
         if (player == PLAYERAI){											// if the player sent in was the AI...
            player = PLAYER2;														// sets the player to be the second player
            AIcheck = true;														// turns on the AIcheck
         }
      
         if (row != 7){															// if the row is already not at the bottom edge...
            for (int i = row+1; i < NUMROW; i++){							// goes through the possible rows until it reaches the bottom edge
            
               if (board[i][col] == player && finish == false){		// if the location is occupied by a piece of the same type and this is the first instnace...
                  finish = true;														// declares the check to allow this to be the cap on this side
                  flips+= sum;               									// adds the flips accumulated during the check to the total sum
                  for (int j = i; j > row && AIcheck == false; j--){		// goes backwards from the cap to flip the pieces to the input location if it's not the AI
                     gui.setPiece(j, col, player);								// places the piece
                     board[j][col] = player;										// declares the flipped pieces to be of the same kind
                  }
               }
               else if (board[i][col] == EMPTY){							// otherwise if the piece is occupied by nothing...
                  finish = true;														// the check is declared true to end the search for valid flips
               }
               else{																	// otherwise if the piece is occupied by the opposing piece...
                  sum++;																// the sum for the number of valid flips increases by 1
               }
            }   
            finish = false;													// reinitializes the cap check to be false for top to bottom check
            sum = 0;																// reinitializes the sum to 0 for top to bottom counting
         }
      	
         if (row != 0){															// if the row is already not at the top edge...
            for (int i = row-1; i > -1; i--){								// goes through the possible rows until it reaches the top edge
            
               if (board[i][col] == player && finish == false){		// if the location is occupied by a piece of the same type and this is the first instance...
                  finish = true;														// declares the check to allow this to be the cap on this side
                  flips+= sum;														// adds the flips accumulated during the check to the total sum
                  for (int j = i; j < row && AIcheck == false; j++){		// goes backwards from the cap to flips the pieces to the input location if it's not the AI
                     gui.setPiece(j, col, player);								// places the piece
                     board[j][col] = player;										// delcares the flipped pieces to be of the same kind
                  }
               }
               else if (board[i][col] == EMPTY){							// otherwise if the piece is occupied by nothing...
                  finish = true;														// the check is declared true to end the search for valid flips
               }
               else{																	// otherwise if the piece is occupied by the opposing piece...
                  sum++;																// the sum for the number of valid flips increases by 1
               }
            }
         }
         return flips;															// returns the total amount of flips performed
      }
   
       public int outFlankDiag (int row, int col, int player){   					// method used to detect valid flanks diagonally from a location
      
         int sum = 0;																		// initializes a sum to return number of flips per side
         int flips = 0;																		// initializes a sum to return number of flips in total
         int add = 1;																		// initializes a counter to add onto a location's X & Y to traverse diagonally
         boolean finish = false;															// boolean used to halt a check in a certain direction after the parameters have been met
         boolean AIcheck = false;
      
         if (player == PLAYERAI){														// if the player sent in was the AI...
            player = PLAYER2;																	// sets the player to be the second player
            AIcheck = true;																	// turns on the AIcheck
         }
      
         while(((row+add != 8) && (col+add != 8))&& (finish == false)){			// if it is not already at the bottom right corner, it will run this loop to traverse
            if (board[row+add][col+add] == player){									// if the location is occupied by a piece of the same type...
               finish = true;																		// the check is declared true to end the search for valid flips
               flips+= sum;																		// adds the flips accumulated during the check to the total sum
               for (int j = add; j > 0 && AIcheck ==  false; j--){					// goes backwards from the cap to flip the pieces to the input location if it's not the AI
                  gui.setPiece(row+j,col+j , player);										// places the piece
                  board[row+j][col+j] = player;												// declares the flipped pieces to be of the same kind
               }
            }
            else if (board[row+add][col+add] == EMPTY){								// otherwise if the piece is occupied by nothing...
               finish = true;																		// the check is declared true to end the search for valid flips
            }
            else{																					// otherwise if the piece is occupied by the opposing piece...
               sum++;																				// the sum for the number of valid flips increases by 1
            }
            add++;																				// counter for traversing increases by 1
         }
      	
         finish = false;																		// resets the boolean to allow the checking again
         add = 1;																					// resets the counter for traversing
         sum = 0;																					// resets the sum for checking
      
         while(((row-add != -1) && (col-add != -1)) && (finish == false)){		// if it is not already at the top left corner, it will run this loop to traverse
            if (board[row-add][col-add] == player){									// if the location is occupied by a piece of the same type...
               finish = true;																		// the check is declared true to end the search for valid flips
               flips+= sum;																		// adds the flips accumulated during the check to the total sum
               for (int j = add; j > 0 && AIcheck == false; j--){						// goes backwards from the cap to flip the pieces to the input location if it's not the AI
                  gui.setPiece(row-j,col-j , player);										// places the piece
                  board[row-j][col-j] = player;												// declares the flipped pieces to be of the same kind
               }
            }
            else if (board[row-add][col-add] == EMPTY){								// otherwise if the piece is occupied by nothing...
               finish = true;																		// the check is declared true to end the search for valid flips
            }
            else{																					// otherwise if the piece is occupied by the opposing piece...
               sum++;																				// the sum for the number of valid flips increases by 1
            }
            add++;																				// counter for traversing increases by 1
         }
      	
         finish = false;																		// resets the boolean to allow the checking again
         add = 1;																					// resets the counter for traversing
         sum = 0;																					// resets the sum for checking
      
         while(((row-add != -1) && (col+add != 8)) && (finish == false)){		// if it is not already at the bottom left corner, it will run this loop to traverse
            if (board[row-add][col+add] == player){									// if the location is occupied by a piece of the same type
               finish = true;																		// the check is declared true to end the search for valid flips
               flips+= sum;																		// adds the flips accumulated during the check to the total sum
               for (int j = add; j > 0 && AIcheck == false; j--){						// goes backwards from the cap to flip the pieces to the input location if it's not the AI
                  gui.setPiece(row-j,col+j , player);										// places the piece
                  board[row-j][col+j] = player;												// declares the flipped pieces to be of the same kind
               }
            }
            else if (board[row-add][col+add] == EMPTY){								// otherwise if the piece is occupied by nothing...
               finish = true;																		// the check is declared true to end the search for valid flips
            }
            else{																					// otherwise if the piece is occupied by the opposing piece...
               sum++;																				// the sum for the number of valid flips increases by 1
            }
            add++;																				// counter for traversing increases by 1
         }	
      	
         finish = false;																		// resets the boolean to allow the checking again
         add = 1;																					// resets the counter for traversing
         sum = 0;																					// resets the sum for checking
      
         while(((row+add != 8) && (col-add != -1))&& (finish == false)){		// if it is not already at the top right corner, it will run this loop to traverse
            if (board[row+add][col-add] == player){									// if the location is occupied by a piece of the same type
               finish = true;																		// the check is declared true to end the search for valid flips
               flips+= sum;																		// adds the flips accumulated during the check to the total sum
               for (int j = add; j > 0 && AIcheck == false; j--){						// goes backwards from the cap to flip the pieces to the input location if it's not the AI
                  gui.setPiece(row+j,col-j , player);										// pieces the piece
                  board[row+j][col-j] = player;												// declares the flipped pieces to be of the same kind
               }
            }
            else if (board[row+add][col-add] == EMPTY){								// otherwise if the piece is occupied by nothing...
               finish = true;																		// the check is declared true to end the search for valid flips
            }
            else{																					// otherwise if the piece is occupied by the opposing piece...
               sum++;																				// the sum for the number of valid flips increases by 1
            }
            add++;																				// counter for traversing increases by 1
         }
         return flips; 																		// returns the number of flips performed
      }
   
       public int checkWinner () { 							// method called to determine who is the winner
      
         int count1 = 0;										// initializes a count for counting number of pieces for player 1
         int count2 = 0;										// initializes a count for counting number of pieces for player 2
      	
         for (int i = 0; i < NUMROW; i++){					
            for (int j = 0; j < NUMROW; j++){
               if (board[i][j] == PLAYER1){				// if the board value is player 1...
                  count1++;										// adds onto the player 1 count
               }
               else if (board[i][j] == PLAYER2){		// otherwise if the board value is player 2...
                  count2++;										// adds onto the player 2 count
               }
            }
         }			
         if (count1 > count2){								// if the player 1 piece count is greater than the player 2 piece count...
            return PLAYER1;										// returns the PLAYER1 value
         }
         else if (count2 > count1){							// if the player 2 piece count is greater than the player 1 piece count...
            return PLAYER2;										// returns the PLAYER2 value
         }
         return EMPTY;											// returns EMPTY value if it is a tie game (both previous if's are not satisfied)
      }
   
       public void AI (){												// method for using the AI
      
         int max = 0;													// initialization for the maximum flips the AI can take
         int total = 0;													// initialization for the total amount of flips a certain position can make
         int rowMax = 0;												// the row for the position that provides the greatest amount of flips
         int colMax = 0;												// the column for the position that provides the greatest amount of flips
         int[][] random = new int [NUMROW][NUMCOL];			// the array for storing the multiple values if more than one location for max flips is valid
         int sameCount = 0;											// the number of positions that have the same max value
         int randomCap;													// the number of the element within the array to be randomly selected
         int countRandom = 0;       								// a counter to count up to the cap to the specific element
         boolean played = false;										// a boolean to determine whether a call to the method has been successful
      
         for (int i = 0; i < NUMROW; i++){						// goes through possible available positions on the board
            for (int j = 0; j < NUMCOL; j++){
               if (check[i][j] == 1){
                  total += outFlankHori(i, j, PLAYERAI);		// determines number of flips horizontally from the available position
                  total += outFlankVert(i, j, PLAYERAI);		// determines number of flips vertically from the available position
                  total += outFlankDiag(i, j, PLAYERAI);		// determines number of flips diagonally from the available position
                  if (total > max){									// if the number of flips at the position is greater than or equal to the max...
                     for (int k = 0; k < NUMROW; k++){
                        for (int l = 0; l < NUMCOL; l++){
                           random[k][l] = 0;							// resets all the values in the array to store multiples values to be 0						
                        }
                     }
                     max = total;										// stores the new maximum flips
                     rowMax = i;											// stores the row for the maximum flips
                     colMax = j;											// stores the column for the maximum flips
                     sameCount = 0;										// resets the number of numbers with the same max flips to be 0
                     random[i][j] = 1;									// stores the specific value with the same value to be valid 
                  }
                  else if (total == max){
                     rowMax = i;											// stores the row for the maximum flips
                     colMax = j;											// stores the column for the maximum flips
                     sameCount++;										// increases the number of numbers with the same
                     random[i][j] = 1;									// stores the specific value with the same value to be valid
                  }
               }
               total = 0;												// resets the storage of flips for a position to 0
            }
         }
      
         if (sameCount > 0){											// if the number of positions that have the max value is greater than
            randomCap = (int)(Math.random()*sameCount +1) - 1;	// generates the random cap
            for (int i = 0; i < NUMROW; i++){
               for (int j = 0; j < NUMCOL; j++){
                  if (random[i][j] == 1){								// if the position is a valid location...
                     if (countRandom == randomCap){					// if the counter is equal the random cap...
                        play(i,j);											// calls the play method using the row and column which produces the most amount of flips if it satisfies the single cap
                        played = true;										// turns the boolean to indicate the play called is true
                     }
                     countRandom++;										// adds one to the counter
                  }
               }
            }
         }
         else if (played == false){								// if the play method hasn't been called...
            play(rowMax, colMax);									// calls the play method using the row and column which produces the most amount of flips   
         }
      }
   	
       public void pieceCounter (){							// method used to determine number of pieces on the board
      
         int count1 = 0;										// initialization of a counter for pieces belonging to PLAYER1
         int count2 = 0;										// initialization of a counter for pieces belonging to PLAYER2
         for (int i  = 0; i < NUMROW; i++){				// circulates through all the possible panels on the board
            for (int j = 0; j < NUMCOL; j++){		
               if (board[i][j] == PLAYER1){				// if the current piece belongs to PLAYER1...				
                  count1++;										// increases the PLAYER1 counter
               }
               else if (board[i][j] == PLAYER2){		// if the current piece belongs to PLAYER2...
                  count2++;										// increases the PLAYER2 counter
               }
            }	
         }
         gui.setPieceCount(PLAYER1, count1);				// updates the piece count for PLAYER1
      	gui.setPieceCount(PLAYER2, count2);				// updates the piece count for PLAYER2
      }
   }