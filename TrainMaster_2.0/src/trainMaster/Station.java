package trainMaster;
import namedObjects.*;
import java.util.ArrayList;

/**
 * Station object which holds information about
 * lines stopping at the station
 * and next stops for those lines
 */
public class Station extends NamedObject{

	private static final long serialVersionUID = -1381010956568694060L;
	ArrayList<Connection> connections;
	ArrayList<TrainLine> lines;
	
	Station(String name) {
		super(name);
		this.connections = new ArrayList<Connection>();
		this.lines = new ArrayList<TrainLine>();
	}
	
	public void addLine(TrainLine line, int distance) {			//adds line to the station
		
		if (line.getStations().size() > 1) {																//add connections to this station and previous one	
			Station prev = line.getStations().get(line.getStations().size() - 2);
			prev.getConnections().add(new Connection(this,line,distance,line.getStations().size() - 2));
			connections.add(new Connection(prev,line,distance,line.getStations().size() - 1));
		}
		
		if (!(lines.contains(line))) {																		
			lines.add(line);																				//add line if necessary
			for(TrainLine stationLine: lines) {																//update lines' exchanges if necessary
				if (stationLine.equals(line)) {
					continue;
				}
				line.getExchanges().add(new Exchange(this,stationLine));
				stationLine.getExchanges().add(new Exchange(this,line));
			}
		}
	}
	
	public void addLineWithoutConnection(TrainLine line) {						//adds line to the station without changing connections
		if (!(lines.contains(line))) {
			lines.add(line);													//add line if necessary
			for(TrainLine stationLine: lines) {									//update lines' exchanges if necessary
				if (stationLine.equals(line)) {
					continue;
				}
				line.getExchanges().add(new Exchange(this,stationLine));
				stationLine.getExchanges().add(new Exchange(this,line));
			}
		}		
	}
	
	public void deleteLine(TrainLine line) {		//Removes line ONLY IF it doesn't go through the station !!!
		if (!(line.getStations().contains(this)))
			lines.remove(line);
	}
	
	public void printLines() {
		for(TrainLine i: lines) {
			System.out.println(i.getName());
		}
	}
	
	public void printLines(String message) {
		for(TrainLine i: lines) {
			System.out.print(message);
			System.out.println(i.getName());
		}
	}
	
	public void printConnections() {
		for(Connection i: connections) {
			i.printInfo();
			System.out.println("---------");
		}
	}

	public ArrayList<Connection> getConnections() {
		return connections;
	}

	public void setConnections(ArrayList<Connection> connections) {
		this.connections = connections;
	}

	public ArrayList<TrainLine> getLines() {
		return lines;
	}

	public void setLines(ArrayList<TrainLine> lines) {
		this.lines = lines;
	}
}
