package datasync.datasync;

import java.util.Date;

public class Customer {
    private long id;
    private String name;
    private Date lastUpdated;
/**
 * 
 * @return
 */
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}
    
}