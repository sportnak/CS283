package com.example.cs_tic_tac;

public class Game {
	protected int board[][] = new int[][] {{0, 0, 0},
							  {0, 0, 0},
							  {0, 0, 0}};
	Boolean user1 = true;
	Boolean user2 = false;
	protected int soln[] = new int[] {1, 1, 1};
	protected int soln2[] = new int[] {2, 2, 2};
	
	//constructor.
	public Game(){

	}
	
	public Boolean makeMove(int user, int x, int y){
		if(verifyMove(user, x, y)){
			board[x][y] = user;
			if(user == 1){
				user1 = false;
				user2 = true;
			} else {
				user1 = true;
				user2 = false;
			}
			return winner();
		} else {
			System.out.println("Invalid Move");
			return false;
		}
	}
	
	public Boolean verifyMove(int user, int x, int y){
		if(user == 1){
			if(user1){
				if(board[x][y] == 0){
					return true;
				}
				return false;
			} else {
				return false;
			}
		} else {
			if(user2){
				if(board[x][y] == 0){
					return true;
				}
				return false;
			}
			else {
				return false;
			}
		}
	}
	
	public Boolean winner(){
		for(int i = 0; i < 3; i++){
			if(arrayComp(board[i], soln) || arrayComp(board[i], soln2)){
				return true;
			} else if(board[1][i] == board [2][i] && board[2][i] == board[3][i]){
				return true;
			}
		}
		if(board[1][1] == board[2][2] && board[2][2] == board[3][3]){
			return true;
		} else if (board[1][3] == board[2][2] && board[2][2] == board[3][1]){
			return true;
		}
		
		return true;
	}
	
	public Boolean arrayComp(int arr1[], int arr2[]){
		if(arr1.length != arr2.length){
			return false;
		} else {
			for(int i = 0; i < arr1.length; i++){
				if(arr1[i] != arr2[i]){
					return false;
				}
			}
		}
		return true;
	}

}