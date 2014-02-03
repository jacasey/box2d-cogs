package com.jcasey.cogs.controller;

import android.graphics.Canvas;
import android.os.Handler;
import android.view.SurfaceHolder;

import com.jcasey.cogs.model.GameWorld;
import com.jcasey.cogs.view.GameView;

public class GameLoop extends Thread
{
	private boolean running = true;

	SurfaceHolder surfaceHolder;
	GameView gameView;
	GameWorld world;
	Handler handler = null;
		
	public GameLoop(GameWorld world, SurfaceHolder surfaceHolder,GameView gameView)
	{		
		super("GameLoop");
		
		this.world = world;
		this.surfaceHolder = surfaceHolder;
		this.gameView = gameView;
		
		
	}
	
	public void run()
	{
		Canvas canvas = null;
		while(running)
		{		
			world.step();
						
			try
			{
				canvas = surfaceHolder.lockCanvas();
				synchronized (surfaceHolder)
				{
					gameView.render(canvas);
				}
			}
			finally
			{
				if (canvas != null)
				{
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public Handler getHandler() {
		return handler;
	}
	
	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
}
