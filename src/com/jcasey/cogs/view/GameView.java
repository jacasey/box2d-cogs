package com.jcasey.cogs.view;

import java.util.ArrayList;

import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.jcasey.cogs.controller.GameLoop;
import com.jcasey.cogs.model.CogModel;
import com.jcasey.cogs.model.GameWorld;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	GameLoop gameLoop;
	GameWorld world;

	private float scale = 0.01f;

	public GameView(Context context) {
		super(context);

		setup();
	}

	public GameView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		setup();
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setup();
	}

	public void setup() {
		getHolder().addCallback(this);
		paint.setAntiAlias(true);
	}

	final Path polygon = new Path();
	final Paint paint = new Paint();

	public void render(Canvas canvas) {
		// clear the screen
		canvas.drawColor(Color.WHITE);
		
		for (Body b = world.getBodyList(); b != null; b = b.getNext())
		{
			if(b.getUserData() != null)
			{
				polygon.reset();
				CogModel cog = (CogModel)b.getUserData(); // retrieve cog (vertices, colour, teeth) from body to render simulation
				ArrayList <Vec2> points = cog.getPoints();
				
				boolean first = true;
				
				for(Vec2 point : points)
				{
					Vec2 src =  b.getWorldPoint(point);
					
					float x = box2DXToCanvas(src.x);
					float y = box2DYToCanvas(src.y);

					if(first)
					{
						polygon.moveTo(x,y);
						first = false;
					}
					polygon.lineTo(x, y);
				}
				polygon.close();
				
				paint.setStrokeWidth(3);
				paint.setColor(cog.getColor());
				paint.setAlpha(255);
				paint.setStyle(Style.STROKE);
				canvas.drawPath(polygon, paint);
				
				paint.setStyle(Style.FILL);
				paint.setColor(cog.getColor());
				
				paint.setAlpha(130);
				canvas.drawPath(polygon, paint);
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	float scaledWidth = 0;
	float width = 0;
	float scaledHeight = 0;

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Rect surfaceFrame = holder.getSurfaceFrame();

		width = surfaceFrame.width();
		scaledWidth = width * scale;
		scaledHeight = surfaceFrame.height() * scale;

		world = new GameWorld(scaledWidth, scaledHeight);

		gameLoop = new GameLoop(world, holder, this);
		gameLoop.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	public final float canvasYToBox2D(float y) {
		float box2d = ((scaledHeight / 2) - (y * scale));
		return box2d;
	}

	public final float canvasXToBox2D(float x) {
		float box2d = (x * scale) - (scaledWidth / 2);
		return box2d;
	}

	public final float box2DYToCanvas(float y) {
		return (scaledHeight - (y + (scaledHeight / 2f))) / scale;
	}
	
	public final float box2DXToCanvas(float x) {
		return ((x + (scaledWidth / 2f))) / scale;
	}

}
