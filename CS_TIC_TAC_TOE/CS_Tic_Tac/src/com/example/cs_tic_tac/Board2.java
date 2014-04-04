package com.example.cs_tic_tac;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class Board2 extends Activity {
	protected EditText eText2;
	protected EditText eText3;
	Game game;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_board2);
		
		game = new Game();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.board2, menu);
		return true;
	}
	
	public void sendPoints(View v){
		eText2 = (EditText) v.findViewById(R.id.xcoor);
		eText3 = (EditText) v.findViewById(R.id.ycoor);
		
		String xcoor = eText2.getText().toString();
		String ycoor = eText3.getText().toString();
		int xVal = Integer.parseInt(xcoor);
		int yVal = Integer.parseInt(ycoor);
		if(game.makeMove(1,xVal,yVal)){
			//send message to server.
		}
		
	}

}
