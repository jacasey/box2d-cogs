package com.jcasey.cogs.view;

import java.util.ArrayList;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJoint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

import com.jcasey.cogs.controller.GameLoop;
import com.jcasey.cogs.model.CogModel;
import com.jcasey.cogs.model.GameWorld;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, OnTouchListener {
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
		
		setOnTouchListener(this);
	}

	final Path polygon = new Path();
	final Paint paint = new Paint();

	float speed;
	float m;
	float distance;
	float time;
	
	public void render(final Canvas canvas) {
		// clear the screen
		canvas.drawColor(Color.WHITE);
		
		paint.setColor(Color.BLACK);
		canvas.drawText(""+speed, 5, 10, paint);
		canvas.drawText(""+m, 5, 20, paint);
		canvas.drawText(""+distance, 5, 30, paint);
		canvas.drawText(""+time, 5, 40, paint);
		
		Body b = world.getBodyList();
		
		while( b != null)
		{
			if(b.getUserData() != null && b.getUserData() instanceof CogModel)
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
				
				if(cog.isSelected())
				{
					paint.setAlpha(200);
				}
				else
				{
					paint.setAlpha(120);
				}
				
				canvas.drawPath(polygon, paint);
			}
			synchronized(world.world)
			{
				b = b.getNext();
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
		boolean retry = true;
		
		gameLoop.setRunning(false);
		while(retry)
		{
			try
			{
				gameLoop.join();
				retry = false;
			}
			catch(InterruptedException e)
			{
				
			}
		}
	}

	Body selected = null;
	
	long previousEvent;
	
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		if(event.getActionMasked() == MotionEvent.ACTION_UP)
		{
			if(selected != null)
			{
				distance = 0f;
			}
		}
		if(event.getActionMasked() == MotionEvent.ACTION_MOVE)
		{
			long deltaTime = event.getEventTime() - previousEvent;
			
			if(selected != null && deltaTime <=3000)
			{
				float finalX = event.getX(0);
				float finalY = event.getY(0);
								
				if(startY > finalY)
				{
					m = -1;
				}
				else
				{
					m = 1;
				}
				
				// pythagoras
				distance = (float) Math.sqrt(Math.pow((double) (canvasXToBox2D(finalX) - canvasXToBox2D(startX)),2) + Math.pow((double) (canvasYToBox2D(finalY) - canvasYToBox2D(startY)),2));
				
				time = deltaTime / 1000f;
				
				speed = distance / (deltaTime / 1000f);

				//TODO good to work out what the proper formula is here
				selected.applyTorque(speed * m * selected.getInertia());
				
				return true;
			}
		}
		else if(event.getActionMasked() == MotionEvent.ACTION_DOWN)
		{
			handleActionDown(event);
		}
		return true;
	}

	float startX;
	float startY;
	
	public void handleActionDown(MotionEvent event)
	{
		previousEvent = event.getEventTime();
		
		startX = event.getX(event.getActionIndex());
		startY = event.getY(event.getActionIndex());
		
		float worldX = canvasXToBox2D(event.getX(event.getActionIndex()));
		float worldY = canvasYToBox2D(event.getY(event.getActionIndex()));
		
		
		final Vec2 mousePoint = new Vec2(worldX,worldY);

		AABB aabb = new AABB();
		aabb.lowerBound.set(mousePoint.x - 0.01f, mousePoint.y - 0.01f);
		aabb.upperBound.set(mousePoint.x + 0.01f, mousePoint.y + 0.01f);
		
		QueryCallback callback = new QueryCallback()
		{
			@Override
			public boolean reportFixture(Fixture fixture)
			{
				Shape shape  = fixture.getShape();
				
				Body body = fixture.getBody();
				if(shape.getType().compareTo(ShapeType.POLYGON) == 0)
				{
					if(body.getUserData() instanceof CogModel)
					{
						synchronized(world.world)
						{
							for(Joint joint = world.world.getJointList(); joint != null; joint = joint.getNext())
							{
								if (joint instanceof RevoluteJoint)
								{
									RevoluteJoint revolute = (RevoluteJoint)joint;
									revolute.setMotorSpeed(0f);
								}
							}
						}

						CogModel cogModel = (CogModel) body.getUserData();							
						cogModel.setSelected(true);

						for(Body cog: world.getCogs())
						{
							CogModel model = (CogModel) cog.getUserData();
							if(!model.equals(cogModel))
							{
								model.setSelected(false);
							}
						}
						
						selected = body;
						
						return false;
					}							
				}

				return true;
			}
		};
		
		synchronized(world.world)
		{
			world.world.queryAABB(callback, aabb);
		}
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
