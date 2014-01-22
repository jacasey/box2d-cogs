package com.jcasey.cogs.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.jcasey.cogs.controller.GameLoop;
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
	final Transform transform = new Transform();
	final Vec2 origin = new Vec2(0, 0);
	final Vec2[] spliced =new Vec2[Settings.maxPolygonVertices];

	
	public void render(Canvas canvas) {
		// clear the screen
		canvas.drawColor(Color.WHITE);

		polygon.setFillType(FillType.INVERSE_WINDING);
//		ArrayList<Vec2> pts = new ArrayList<Vec2>();
		
		polygon.reset();
		
		int done = 0;
		
		LinkedList<PolygonShape> polygons = new LinkedList<PolygonShape>();
		
		for (Body b = world.getBodyList(); b != null; b = b.getNext()) {
			// reset our polygon
			
//			pts.clear();

			
			if(b.getUserData() != null)
			{
				ArrayList <Vec2> points = (ArrayList <Vec2>)b.getUserData();
				
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
			}
			
			boolean first = true;
			
			int fixtures = 0;
			
			polygons.clear();
			
			Fixture fixture = b.getFixtureList();
			
			while(fixture != null)
			{
				// get all the polygon shapes
			
				ShapeType type = fixture.getType();
				if (type == ShapeType.POLYGON)
				{
					PolygonShape polygonShape = (PolygonShape) fixture.getShape();
					
					if(fixture.getUserData() !=null)
					{
//						polygons.add(polygonShape);
					}
				}
				
				fixture = fixture.getNext();
			}
			
			
			//TODO optmisation do this at world creation time
			for(Iterator<PolygonShape> polygonShapeIterator = polygons.descendingIterator(); polygonShapeIterator.hasNext();)
			{
				
				
				if(fixtures == 6)
				{
					System.err.println("toasty");
//					break;
				}
				
				PolygonShape polygonShape = polygonShapeIterator.next();
				
				if(polygonShape.getVertexCount() < 4)
				{
//					continue;
				}
				
				Vec2[] vertices = polygonShape.getVertices();
				
//				// reset our spliced array each time we render the screen
//				Arrays.fill(spliced, new Vec2(0,0));
//				
////				int pivot = base(fixtures, teeth);
//				
//				System.arraycopy(vertices, pivot, spliced, 0, polygonShape.getVertexCount() - pivot);
//				System.arraycopy(vertices, 0, spliced, polygonShape.getVertexCount() - pivot, pivot);

				
				// setup the vector transform
				float angle = b.getAngle();
				
				Vec2 start = null;

				for (int i = 0; i < polygonShape.getVertexCount(); i++)
				{
					Vec2 src =  b.getWorldPoint(spliced[i]);
					
					float x = box2DXToCanvas(src.x);
					float y = box2DYToCanvas(src.y);

					if(i == 0)
					{
						canvas.drawCircle(x,y,4,paint);
						polygon.moveTo(x,y);
					}
					
//					if (!vertices[i].equals(origin))
					{
						polygon.lineTo(x, y);
					}
					
				}
				fixtures ++;
			}
//			
//			while (fixture != null) {
//				ShapeType type = fixture.getType();
//				if (type == ShapeType.POLYGON) {
//					PolygonShape polygonShape = (PolygonShape) fixture.getShape();
//					if (fixture.getUserData() != null)
//					{
//						fixtures ++;
////						if(fixtures > 6)
////						{
////							break;
////						}
//						
//						Vec2 position = b.getPosition();
//
//						
//						
//						Vec2[] vertices = polygonShape.getVertices();
//
//						// setup the vector transform
//						float angle = b.getAngle();
//						
////						transform.set(origin, angle);
//						Vec2 start = null;
//						
//						LinkedList<Vec2> pts = new LinkedList<Vec2>();
//						
//						for (int i = 0; i < 7; i++)
//						{
////							Vec2 src = transform(position, vertices[i]);
//							System.err.println(vertices[i]);
////							System.err.println(src);
//						
//							Vec2 src =  b.getWorldPoint(vertices[i]);
//							
//							float x = box2DXToCanvas(src.x);
//							float y = box2DYToCanvas(src.y);
//
//							if (!vertices[i].equals(origin))
//							{
////								if (i == 0)
////								{
////									canvas.drawCircle(x, y, 4, paint);
////									
//////									polygon.close();
////									polygon.moveTo(x, y);
////								}
////									start = src;
////									
//////									Vec2 prev = pts.pollFirst();
////									
////									canvas.drawCircle(x, y, 4, paint);
////									
//////									if(prev != null)
////									{
//////										polygon.lineTo(prev.x, prev.y);
////									}
////									
////								}
////								if(i == base(fixtures, teeth))
////								{
////									
////									
////									polygon.moveTo(x, y);
//////									pts.push(src);
////								}
////								else
//								{
//									polygon.lineTo(x, y);
//								}
//							}
//						}
////						if(start != null)
////						{
////							float x = box2DXToCanvas(start.x);
////							float y = box2DYToCanvas(start.y);
//////							
////							polygon.lineTo(x, y);
////						}
//					}
//					
//					
////					polygon.close();
//
//				} else if (type == ShapeType.CIRCLE) {
//					CircleShape shape = (CircleShape) fixture.getShape();
//
//					float x = box2DXToCanvas(b.getPosition().x);
//					float y = box2DYToCanvas(b.getPosition().y);
//
//					paint.setColor(Color.RED);
//					paint.setAlpha(130);
//					paint.setStyle(Style.FILL);
//					canvas.drawCircle(x, y, shape.getRadius() / scale, paint);
//
//					paint.setStyle(Style.STROKE);
//					paint.setColor(Color.RED);
//					paint.setAlpha(255);
//					canvas.drawCircle(x, y, shape.getRadius() / scale, paint);
//				}
//				fixture = fixture.getNext();
//				
//			}
//
//		}
//		polygon.close();
		paint.setStrokeWidth(3);
		paint.setColor(Color.RED);
		// paint.setAlpha(130);
		// polygon.setFillType(Path.FillType.WINDING);
		paint.setStyle(Style.STROKE);
		canvas.drawPath(polygon, paint);
		}
		
		// paint.setStyle(Style.STROKE);
		// paint.setColor(Color.RED);
		// paint.setAlpha(255);
		// canvas.drawPath(polygon, paint);
	}

	private int base(float i, float teeth) {
		
		if(teeth == 0)
		{
			return 0;
		}
		
		float res = i / teeth;
		
		int index = 0;
		
		if (res < 0.25f)
		{
			index = 5;
		}
		else if (res < 0.5f)
		{
			index = 3;
		}
		else if (res < 0.75f)
		{
			index = 1;
		}
		else if (res < 1.0f)
		{
			index = 0;
		}
		return index;

	}

	public Vec2 transform(Vec2 position, Vec2 vertex) {
		// multiply the shape by the body's angle
		Vec2 src = Transform.mul(transform, vertex);

		// offset the shape vector by the body's position
		src = src.add(position);
		return src;
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

	static public final float map(float value,

              float istart, float istop,
              float ostart, float ostop)
	{

		return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
	}
	
	public final float box2DXToCanvas(float x) {
		//return map(x,-scaledWidth,+scaledWidth,0,width);
		return ((x + (scaledWidth / 2f))) / scale;
	}

}
