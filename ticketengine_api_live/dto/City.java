package dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.SerializedName;
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="cities")
public class City {
	 @XmlAttribute(name = "id")
	 @SerializedName("id")
	private int cityid;
	
	public int getCityid() {
		return cityid;
	}
	public void setCityid(int cityid) {
		this.cityid = cityid;
	}
	public String getCityname() {
		return cityname;
	}
	public void setCityname(String cityname) {
		this.cityname = cityname;
	}
	 @XmlAttribute(name = "name")
	 @SerializedName("name")
	private String cityname;

}
