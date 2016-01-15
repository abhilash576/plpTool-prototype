package edu.asu.plp.tool.prototype.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ProjectFile
{
	private PLPProject project;
	private StringProperty nameProperty;
	
	public ProjectFile(PLPProject project, String name)
	{
		this.project = project;
		this.nameProperty = new SimpleStringProperty(name);
	}
	
	public String getName()
	{
		return nameProperty.get();
	}
	
	public void setName(String name)
	{
		nameProperty.set(name);
	}
	
	public StringProperty nameProperty()
	{
		return nameProperty;
	}
	
	public PLPProject getProject()
	{
		return project;
	}
}
