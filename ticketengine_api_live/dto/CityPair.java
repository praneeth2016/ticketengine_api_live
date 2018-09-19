package dto;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.SerializedName;
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="SourceDestinationPairs")
public class CityPair {
	@XmlAttribute(name = "SourceId")
	 @SerializedName("SourceId")
	private int sourceid;
	
	@XmlAttribute(name = "SourceName")
	 @SerializedName("SourceName")
	private String sourcename;
	
	@XmlAttribute(name = "DestId")
	 @SerializedName("DestId")
	private int destid;
	
	@XmlAttribute(name = "DestName")
	 @SerializedName("DestName")
	private String destname;

	public int getSourceid() {
		return sourceid;
	}

	public void setSourceid(int sourceid) {
		this.sourceid = sourceid;
	}

	public String getSourcename() {
		return sourcename;
	}

	public void setSourcename(String sourcename) {
		this.sourcename = sourcename;
	}

	public int getDestid() {
		return destid;
	}

	public void setDestid(int destid) {
		this.destid = destid;
	}

	public String getDestname() {
		return destname;
	}

	public void setDestname(String destname) {
		this.destname = destname;
	}
	

}
