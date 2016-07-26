package me.zouooh.slark.request;

import java.io.Serializable;

public class FormFileItem implements Serializable{
	private static final long serialVersionUID = 1L;
	private String formFieldName;
	private String fileName;
	private String type;

	public FormFileItem(String formFieldName, String fileName,String type)
	{
		this.formFieldName = formFieldName;
		this.fileName = fileName;
		this.type = type;
	}

	public String getFormFieldName()
	{
		return formFieldName;
	}

	public void setFormFieldName(String formFieldName)

	{
		this.formFieldName = formFieldName;
	}

	public String getFileName()

	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
