package fantasy;

public class Bid implements Comparable<Bid> {

	private String teamName;
	private String ownerName;
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	private String biddedPlayer;
	private String droppedPlayer;
	private int amount;
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	public String getBiddedPlayer() {
		return biddedPlayer;
	}
	public void setBiddedPlayer(String biddedPlayer) {
		this.biddedPlayer = biddedPlayer;
	}
	public String getDroppedPlayer() {
		return droppedPlayer;
	}
	public void setDroppedPlayer(String droppedPlayer) {
		this.droppedPlayer = droppedPlayer;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	@Override
	public int compareTo(Bid o) {
	    int result =  o.getAmount()-  getAmount();
	    if(result==0) {
	        int result1=o.getBiddedPlayer().compareToIgnoreCase(o.getBiddedPlayer()) ;
	        if(result1==0) {
		        return o.getDroppedPlayer().compareToIgnoreCase(o.getDroppedPlayer()) ;
	        
	        } 
	        else
	        {
	        	return result1;
	        }
	    }
	    else {
	        return result;
	    }
	}
	
	@Override
	public boolean equals(Object obj) {
		
		Bid b=(Bid)obj;
		
		return teamName.equals(b.teamName) && ownerName.equals(b.ownerName) && droppedPlayer.equals(b.droppedPlayer) && biddedPlayer.equals(b.biddedPlayer) && amount==b.amount;
	}
	@Override
	public int hashCode() {
		
		return teamName.hashCode()+13+biddedPlayer.hashCode()+4*biddedPlayer.hashCode()+droppedPlayer.hashCode()+6*ownerName.hashCode();
	}
	@Override
	public String toString() {
		return "Bid [OwnerName=" + ownerName +"  teamName=" + teamName + ", biddedPlayer=" + biddedPlayer + ", droppedPlayer=" + droppedPlayer
				+ ", amount=" + amount + "]";
	}

}
