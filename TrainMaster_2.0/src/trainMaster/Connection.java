package trainMaster;

import java.io.Serializable;

/**Connection object specifies which stations are 
 * reachable within one stop from the station
 * the distance, train line, and index of the station
 * in the given line is also specified
 */
public class Connection implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7262338169334033641L;
	Station station;
	TrainLine line;
	int distance; 		// in meters
	int stationIndex;
	
	Connection(Station station, TrainLine line, int distance, int stationIndex) {
		this.station = station;
		this.line = line;
		this.distance = distance;
		this.stationIndex = stationIndex;
	}
	
	public void printInfo() {
		System.out.println("Direction: " + station.getName());
		System.out.println("line: " + line.getName());
		System.out.println("distance: " + Integer.toString(distance) + " m");
		System.out.println("Index: " + Integer.toString(stationIndex));
	}
	
	public Station getStation() {
		return station;
	}
	public TrainLine getLine() {
		return line;
	}
	public int getDistance() {
		return distance;
	}

	public int getStationIndex() {
		return stationIndex;
	}

	public void setStationIndex(int stationIndex) {
		this.stationIndex = stationIndex;
	}
	
}
