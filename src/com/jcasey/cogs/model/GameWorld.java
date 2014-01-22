package com.jcasey.cogs.model;

import java.util.ArrayList;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class GameWorld
{
	private World world;
	
	private float timeStep;
	private int velocityIterations;
	private int positionIterations;
	
	private final Vec2 gravity = new Vec2(0.0f, 0.0f);
	
	private float width;
	private float height;
	
	final float PI = (float) Math.PI;
	
	public GameWorld(float width, float height)
	{
		this.timeStep = 1f/30f;
		this.velocityIterations = 4;
		this.positionIterations = 2;
		
		this.width = width;
		this.height = height;
		
		world = new World(gravity);
		
		world.setSleepingAllowed(true);

//		Body gear1 = createCog(1.0f, 20, 0.1f, new Vec2(-2,0.2f));
//		Body gear2 = createCog(1.8f, 36, 0.1f, new Vec2(0.83f,0));
		Body gear3 = createCog(1.3f, 26, 0.1f, new Vec2(3.2f,2.1f));
		
		Fixture fixture = gear3.getFixtureList();
		PolygonShape polygonShape = (PolygonShape) fixture.getShape();
		
		polygonShape.getVertices();
		
		
	    final float top = height/2f;
		final float bottom = -top;
	    
		final float right = width/2f;
		final float left = -right;
		
		Body ground = createWall(right, 0.01f , 0f, bottom,0);
//		
//		RevoluteJointDef jd1 = new RevoluteJointDef();
//		jd1.bodyA = ground;
//		jd1.bodyB = gear1;
//		jd1.localAnchorA = ground.getLocalPoint(gear1.getWorldCenter());
//		jd1.localAnchorB = gear1.getLocalPoint(gear1.getWorldCenter());
//		jd1.referenceAngle = gear1.getAngle() - ground.getAngle();
//		jd1.enableMotor = true;
//		jd1.motorSpeed = 5f;
//		jd1.maxMotorTorque = 1000000000;
//		
//		RevoluteJoint joint1 = (RevoluteJoint)world.createJoint(jd1);
//		
//		RevoluteJointDef jd2 = new RevoluteJointDef();
//		jd2.bodyA = ground;
//		jd2.bodyB = gear2;
//		jd2.localAnchorA = ground.getLocalPoint(gear2.getWorldCenter());
//		jd2.localAnchorB = gear2.getLocalPoint(gear2.getWorldCenter());
//		jd2.referenceAngle = gear2.getAngle() - ground.getAngle();
//
//		RevoluteJoint joint2 = (RevoluteJoint)world.createJoint(jd2);
//		
//		RevoluteJointDef jd3 = new RevoluteJointDef();
//		jd3.bodyA = ground;
//		jd3.bodyB = gear3;
//		jd3.localAnchorA = ground.getLocalPoint(gear3.getWorldCenter());
//		jd3.localAnchorB = gear3.getLocalPoint(gear3.getWorldCenter());
//		jd3.referenceAngle = gear3.getAngle() - ground.getAngle();
//
//		RevoluteJoint joint3 = (RevoluteJoint)world.createJoint(jd3);
		
//		GearJointDef jointDef = new GearJointDef();		
//		jointDef.joint1 = joint1;
//		jointDef.joint2 = joint2;
//		jointDef.bodyA = gear1;
//		jointDef.bodyB = gear2;
//		jointDef.type = JointType.GEAR;
//		jointDef.ratio = 2 / 1;
		
//		GearJoint joint = (GearJoint) world.createJoint(jointDef);
		
	}

	public Body createWall(float width, float height, float x, float y, float angle) {
		PolygonShape groundBox = new PolygonShape();
		groundBox.setAsBox(width,height);
	    
		FixtureDef groundFixture = new FixtureDef();
	    groundFixture.shape = groundBox;
	    groundFixture.density = 1.0f;
	    groundFixture.friction = 0.8f;
	    
	    BodyDef bd = new BodyDef();
	    bd.type = BodyType.STATIC;
	    bd.angle = angle;
	    bd.position.set(x,y);
	    
	    Body body = world.createBody(bd);
		body.createFixture(groundFixture);
		
		return body;
	}
	
	private Body createCog(float r2, int teeth, float difference, Vec2 position)
	{
		BodyDef cog = new BodyDef();
		cog.type = BodyType.DYNAMIC;
		cog.position = position;
		
		float r0 = r2 - difference;
		float r1 = r2 + difference;
		
		float da =  PI / (float)teeth;
		float a0 =  PI /2f;		

		ArrayList <Vec2> points = new ArrayList <Vec2> ();

		
		Body body = null;
        {
        	body = world.createBody(cog);
		
        	
    		float x7 = 0;
    		float y7 = 0;
    		
			for(int i = 0; i<teeth; i++)
			{	
				System.err.println(Math.toDegrees(a0));
				
				Vec2[] tooth = new Vec2[6];
				
				int vertex = 0;
				
				float x1;
				float y1;
				

				x1 = (float) (r0 * Math.cos(a0));
				y1 = (float) (r0 * Math.sin(a0));
	
				Vec2 point1 = new Vec2(x1,y1);
				points.add(point1);
				tooth [vertex++] = point1;
				
				float x2 = (float) (r2 * Math.cos(a0));
				float y2 = (float) (r2 * Math.sin(a0));
				
				Vec2 point2 = new Vec2(x2,y2);
				points.add(point2);
				tooth [vertex++] = point2;
				
				a0 -= da /3f;
				
				float x3 = (float) (r1 * Math.cos(a0));
				float y3 = (float) (r1 * Math.sin(a0));
				
				Vec2 point3 = new Vec2(x3,y3);
				points.add(point3);
				tooth [vertex++] = point3;
				a0 -= da /3f;
	
				float x4 = (float) (r1 * Math.cos(a0));
				float y4 = (float) (r1 * Math.sin(a0));
				
				Vec2 point4 = new Vec2(x4,y4);
				points.add(point4);
				tooth [vertex++] = point4;
				
				a0 -= da /3f;
				
				float x5 = (float) (r2 * Math.cos(a0));
				float y5 = (float) (r2 * Math.sin(a0));
				
				Vec2 point5 = new Vec2(x5,y5);
				points.add(point5);
				tooth [vertex++] = point5;
				
				float x6 = (float) (r0 * Math.cos(a0));
				float y6 = (float) (r0 * Math.sin(a0));
				
				Vec2 point6 = new Vec2(x6,y6);
				points.add(point6);
				tooth [vertex++] = point6;
				
				float b0 = a0; 
				
				b0 -= da;
				
				x7 = (float) (r0 * Math.cos(b0));
				y7 = (float) (r0 * Math.sin(b0));
				
				PolygonShape toothShape = new PolygonShape();

				toothShape.set(tooth, tooth.length);
//				toothShape.m_vertices = tooth;
				
				FixtureDef toothFd = new FixtureDef();
			    toothFd.shape = toothShape;
			    toothFd.density = 1.0f;
			    toothFd.friction = 0.8f;
			    toothFd.userData = i;
			    
	
				body.createFixture(toothFd);
				
				Vec2[] flat = new Vec2[3];
				flat[0] = new Vec2(0,0);
				flat[1] = point6;
				Vec2 point7 = new Vec2(x7,y7);
				flat[2] = point7;
				
//				points.add(point7);
				
				PolygonShape flatShape = new PolygonShape();
				flatShape.set(flat, flat.length);
				
				FixtureDef flatFd = new FixtureDef();
				flatFd.shape = flatShape;
				flatFd.density = 1.0f;
				flatFd.friction = 0.8f;
//				flatFd.userData = i;
				
				body.createFixture(flatFd);		
				
				a0 -= da;
			}
        }
        body.setUserData(points);
		return body;
	}


	
	public void step()
	{
		// update the Box2D world step by step. Synchronize on the world object
		// so that we do not add objects to the game world at the same time as
		// updating / stepping through the world
		synchronized(world)
		{
			world.step(timeStep, velocityIterations, positionIterations);
		}
	}

	public Body getBodyList() {
//		synchronized(world)
		{
			return world.getBodyList();
		}
	}
}
