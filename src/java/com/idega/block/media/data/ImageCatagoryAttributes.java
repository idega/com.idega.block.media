//idega 2000 - eiki
package com.idega.block.media.data;


//import java.util.*;
import java.sql.*;
import com.idega.data.*;

public class ImageCatagoryAttributes extends GenericEntity{

	public ImageCatagoryAttributes(){
		super();
	}

	public ImageCatagoryAttributes(int id)throws SQLException{
		super(id);
	}

	public void initializeAttributes(){
		addAttribute(getIDColumnName());
		addAttribute("image_catagory_id","Catagory",true,true, "java.lang.Integer","many-to-one","com.idega.block.media.data.ImageCatagory");
                addAttribute("attribute_name","Attribute Name",true,true, "java.lang.String");
                addAttribute("attribute_value","Attribute Value",true,true, "java.lang.String");
	}

	public String getEntityName(){
		return "image_catagory_attributes";
	}

        public void setName(String name) {
          setAttributeName(name);
        }

        public String getName() {
          return getAttributeName();
        }

        public void setAttributeName(String name) {
          setColumn("attribute_name",name);
        }

        public String getAttributeName() {
          return (String) getStringColumnValue("attribute_name");
        }

        public void setAttributeValue(String value) {
          setColumn("attribute_value",value);
        }

        public String getAttributeValue() {
          return getStringColumnValue("attribute_value");
        }

        public void setImageCatagoryId(int id) {
          setColumn("image_catagory_id",new Integer(id));
        }

        public int getImageCatagoryId() {
          return getIntColumnValue("image_catagory_id");
        }


}
