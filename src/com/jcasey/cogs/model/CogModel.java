package com.jcasey.cogs.model;

import java.util.ArrayList;

import org.jbox2d.common.Vec2;

public class CogModel
{
	private int color;
	private ArrayList <Vec2> points = new ArrayList <Vec2> ();
	private int teeth;
	private boolean selected;

	public CogModel(ArrayList <Vec2> points, int color, int teeth)
	{
		this.teeth = teeth;
		this.points = points;
		this.color = color;
	}

	public int getColor() {
		return color;
	}

	public ArrayList<Vec2> getPoints() {
		return points;
	}

	public int getTeeth() {
		return teeth;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}