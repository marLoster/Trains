package trainMaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Math;
import trains.*;

public class RouteFinder {
	
	static double a = 0.5;
	
	private static long SmallDistance(Train train, long time) {
		
		double F = 1.0;
		int P = train.getPower() * 1000;
		int m = train.getMass() * 1000;
		
		return (long) ((P*P)/(8*F*F*m*m*a*a*a) - (Math.sqrt((8*P)/(F*m*9))) * Math.pow(P/(2*F*m*a*a), 1.5) - ((P*P)/(2*F*m*a*a))/(2*F*m*a)
									   + (Math.sqrt((8*P)/(F*m*9))) * Math.pow(time, 1.5) - (P*time)/(2*F*m*a)
									   + Math.pow(SpeedStage2(train,time), 2)/(2*a));
	}
	
	private static double SpeedStage2(Train train, long time) {
		
		double F = 1.0;
		int P = train.getPower() * 1000;
		int m = train.getMass() * 1000;
		
		return Math.sqrt((2*time*P)/(F*m)) - P/(2*F*m*a);
	
	}
	
	public static long CalculateTime(Train train, int distance) {
		
		double F = 1.0;
		double Vmax = train.getmaxSpeed() * (5.0/18);
		long P = train.getPower() * 1000;
		long m = train.getMass() * 1000;
		
		if (P==0) {
			System.out.println("Engine not detected!");
		}
		
		//Step 1 check if distance allows to reach max speed
		double t = (F*m*Vmax*Vmax)/(2*P) + Vmax/(2*a) + (P)/(8*F*m*a*a);
		double t0 = P/(2*F*m*a*a);
		
		//System.out.println(t);
		//System.out.println(t0);
		
		//System.out.println((P*P)/(8*F*F*m*m*a*a*a) + (Vmax*Vmax)/(2*a) + (Math.sqrt((8*P)/(F*m*9))) * Math.pow(t, 1.5) - (P*t)/(2*F*m*a)
		//		- (Math.sqrt((8*P)/(F*m*9))) * Math.pow(t0, 1.5) + (P*t0)/(2*F*m*a));
		
		if (distance >= (P*P)/(8*F*F*m*m*a*a*a) + (Vmax*Vmax)/(2*a) + (Math.sqrt((8*P)/(F*m*9))) * Math.pow(t, 1.5) - (P*t)/(2*F*m*a)
																	- (Math.sqrt((8*P)/(F*m*9))) * Math.pow(t0, 1.5) + (P*t0)/(2*F*m*a)) {
			
			//System.out.println("case a");
			
			distance -= (P*P)/(8*F*F*m*m*a*a*a) + (Vmax*Vmax)/(2*a) + (Math.sqrt((8*P)/(F*m*9))) * Math.pow(t, 1.5) - (P*t)/(2*F*m*a)
																	- (Math.sqrt((8*P)/(F*m*9))) * Math.pow(t0, 1.5) + (P*t0)/(2*F*m*a);
			/*
			System.out.println(t);
			System.out.println(t0);
			System.out.println(distance);
			System.out.println(Vmax);
			System.out.println(distance/Vmax);
			System.out.println(Vmax/a);
			*/
			
			return (long) (t0 + t + distance/Vmax + Vmax/a);
		}
		
		else {
			
			//System.out.println("case b");
			
			long time;
			
			time = (long)  (((distance - (2*P*P)/(8 * Math.pow(F*m*a, 2) * a)) * 2 * F * m * a * a)/
				(Math.sqrt(2*P*F*m)*a*a*(4/3) + P*a - Math.sqrt((2*P*P*P)/F*m)));
			
			if (SmallDistance(train,time) > distance) {
				while (SmallDistance(train,time) > distance) {
					time--;
				}
				return time;
			}
			else {
				while (SmallDistance(train,time) < distance) {
					time++;
				}
				return time-1;
			}
		}
		
	}
	
	private static void PrintStation(Station name) {
		int size = name.getName().length();
		int horizontal_gap = 1;
		int vertical_gap = 0;
		
		for(int i=0;i<size + horizontal_gap*2 + 2;i++) {
			System.out.print("-");
		}
		
		System.out.println();
		
		for(int i=0;i<vertical_gap;i++) {
			System.out.print("|");
		
			for(int j=0;j<size + horizontal_gap*2; j++) {
				System.out.print(" ");
			}
			
			System.out.println("|");
		}
		
		System.out.print("|");
		
		for(int i=0;i<horizontal_gap;i++) {
			System.out.print(" ");
		}
		
		System.out.print(name.getName());
		
		for(int i=0;i<horizontal_gap;i++) {
			System.out.print(" ");
		}
		
		System.out.println("|");
		
		for(int i=0;i<vertical_gap;i++) {
			System.out.print("|");
		
			for(int j=0;j<size + horizontal_gap*2; j++) {
				System.out.print(" ");
			}
			
			System.out.println("|");
		}
		
		for(int i=0;i<size + horizontal_gap*2 + 2;i++) {
			System.out.print("-");
		}
		System.out.println();
		
	}
	
	private static void PrintConnection(TrainLine line, Station direction, int stops, int time) {
		
		int vertical_gap = 1;
		
		for(int i=0;i<vertical_gap;i++) {
			System.out.println("  |");
		}
		
		System.out.println("  | " + line.getName() + " to: " + direction.getName());
		System.out.println("  | Travel " + Integer.toString(stops) + (stops > 1 ? " stops" : " stop"));
		
		if (time > 0) {
			System.out.printf("  | ET: %02d:%02d:%02d",time/3600,(time%3600)/60,time%60);
			System.out.println();
		}
		
		for(int i=0;i<vertical_gap;i++) {
			System.out.println("  |");
		}
		
	}
	
	private static void PrintRoute(Station start,ArrayList<Connection> route) {
		
		route = EliminateUsellesExchanges(route);
		
		PrintStation(start);
		
		Train train;
		int i=0;
		int totalTime=0;
		int stopCountStart;
		int time;
		boolean displayTime = true;
		while(i<route.size()) {
		
			stopCountStart = i;
			train = route.get(i).getLine().getTrain();
			if (train != null) {
				time = (int) CalculateTime(train,route.get(i).getDistance());
			}
			else {
				time = -1;
				displayTime = false;
			}
			
			while(i+1<route.size() && route.get(i).getLine().equals(route.get(i+1).getLine()) && Math.abs(route.get(i).getStationIndex() - route.get(i+1).getStationIndex()) == 1) {
				i++;
				if (time >= 0) {
					time += (int) CalculateTime(train,route.get(i).getDistance());
				}
			}
			
			if (displayTime) {
				totalTime += time;
			}
			
			PrintConnection(route.get(stopCountStart).getLine(),route.get(stopCountStart).getStation(),i-stopCountStart+1,time);
			PrintStation(route.get(i).getStation());
			i++;
		}
		
		if(displayTime) {
			System.out.printf("Total travel time: %02d:%02d:%02d",totalTime/3600,(totalTime%3600)/60,totalTime%60);
		}
	}
	
	private static ArrayList<Connection> EliminateUsellesExchanges(ArrayList<Connection> route) {
		for(int i=1;i<route.size();i++) {
			if (route.get(i-1).getLine().equals(route.get(i).getLine())) {
				continue;
			}
			if (route.get(i).getStation().getConnections().contains(new Connection(route.get(i).getStation(),route.get(i-1).getLine(),route.get(i).getDistance(),route.get(i-1).getStationIndex()+1)) || route.get(i).getStation().getConnections().contains(new Connection(route.get(i).getStation(),route.get(i-1).getLine(),route.get(i).getDistance(),route.get(i-1).getStationIndex()-1))) {
				route.add(i, new Connection(route.get(i).getStation(),route.get(i-1).getLine(),route.get(i).getDistance(),-1) );
				route.remove(i+1);
			}
		}
		return route;
	}
	
	public static void LowestDistance(Station start, Station dest) {

		if(start.equals(dest)) {
			System.out.println("Start and destination is the same");
			return;
		}
		
		ArrayList<Connection> res = FewestExchangesSilent(start,dest);
		
		if (res.isEmpty()) {
			System.out.println("Connection has not been found");
			return;
		}
		
		ArrayList<Connection> stack = new ArrayList<Connection>();
		ArrayList<Integer> prev = new ArrayList<Integer>();
		ArrayList<Integer> distance = new ArrayList<Integer>();
		
		HashMap<Station, Integer> stationDistances = new HashMap<Station, Integer>(30);
		
		int currDistance = 0;
		
		for(Connection connection : res) {
			currDistance += connection.getDistance();
		}
		
		Station currStation = start;
		Connection prevConn;
		int i=0;
		
		stationDistances.put(start, 0);
		
		for(Connection connection: start.getConnections()) {
			
			if (connection.getDistance() < currDistance) {
			
				if (connection.getStation().equals(dest)) {
					res.add(connection);
					currDistance = connection.getDistance();
					continue;
				}
				
				if (stationDistances.containsKey(connection.getStation()) && stationDistances.get(connection.getStation()) <=  connection.getDistance()) {
					continue;
				}
			
				stack.add(connection);
				prev.add(-1);
				distance.add(connection.getDistance());
				stationDistances.put(connection.getStation(), connection.getDistance());
			}
		}
		

		
		while(i < stack.size()) {

			prevConn = stack.get(i);
			currStation = prevConn.getStation();
			
			for(Connection connection: currStation.getConnections()) {
				
				if (connection.getDistance() + distance.get(i) < currDistance) {
				
					if (connection.getStation().equals(dest)) {
						res.clear();
						res.add(connection);
						
						for(int j=i;j>=0;j=prev.get(j)) {
							res.add(0,stack.get(j));
						}
						
						currDistance = connection.getDistance();
						continue;
					}
					
					if (stationDistances.containsKey(connection.getStation()) && stationDistances.get(connection.getStation()) <=  connection.getDistance() + distance.get(i)) {
						continue;
					}

					stack.add(connection);
					prev.add(i);
					distance.add(connection.getDistance() + distance.get(i));
					stationDistances.put(connection.getStation(),connection.getDistance() + distance.get(i));
				
				}
			}
			
			i++;
		}
		
		
		PrintRoute(start,res);
		
	}
	
	public static void FewestExchangesPretty(Station start, Station dest) {

		ArrayList<Connection> res = new ArrayList<Connection>();
		
		if(start.equals(dest)) {
			System.out.println("Start and destination is the same");
			return;
		}
		
	
		ArrayList<TrainLine> lines = new ArrayList<TrainLine>();
		ArrayList<TrainLine> prev_line = new ArrayList<TrainLine>();
		
		
		for(TrainLine i: start.getLines()) {
			lines.add(i);
			prev_line.add(null);
		}
		
		int i=0;
		
		while(i<lines.size()) {
			
			if (lines.get(i).getStations().contains(dest)) {

				TrainLine previousLine = prev_line.get(i);
				TrainLine nextLine = lines.get(i);
				Station transfer;
				Station currentTarget = dest;
				
				while(!(previousLine == null)) {
		
					transfer = null;
					
					for(Exchange j: previousLine.getExchanges()) {
						if (j.getLine().equals(nextLine)) {
							transfer = j.getStation();
							break;
						}
					}
		
					if (transfer == null) {
						System.out.println("An error has occured");
						return;
					}
					
					if (nextLine.getStations().indexOf(transfer) < nextLine.getStations().indexOf(currentTarget)) {
						for(int j = nextLine.getStations().indexOf(transfer);j<nextLine.getStations().indexOf(currentTarget);j++) {
							for(Connection connection: nextLine.getStations().get(j).getConnections()) {
								if (connection.getLine().equals(nextLine) && connection.getStation().equals(nextLine.getStations().get(j+1))) {
									res.add(j - nextLine.getStations().indexOf(transfer), connection);
									break;
								}
							}
						}
					}
					else {
						for(int j = nextLine.getStations().indexOf(transfer);j>nextLine.getStations().indexOf(currentTarget);j--) {
							for(Connection connection: nextLine.getStations().get(j).getConnections()) {
								if (connection.getLine().equals(nextLine) && connection.getStation().equals(nextLine.getStations().get(j-1))) {
									res.add(nextLine.getStations().indexOf(transfer) - j, connection);
									break;
								}
							}
						}						
					}
					
					currentTarget = transfer;
					nextLine = previousLine;
					previousLine = prev_line.get(lines.indexOf(nextLine));
				}
				
				if (nextLine.getStations().indexOf(start) < nextLine.getStations().indexOf(currentTarget)) {
					for(int j = nextLine.getStations().indexOf(start);j<nextLine.getStations().indexOf(currentTarget);j++) {
						for(Connection connection: nextLine.getStations().get(j).getConnections()) {
							if (connection.getLine().equals(nextLine) && connection.getStation().equals(nextLine.getStations().get(j+1))) {
								res.add(j - nextLine.getStations().indexOf(start), connection);
								break;
							}
						}
					}
				}
				else {
					for(int j = nextLine.getStations().indexOf(start);j>nextLine.getStations().indexOf(currentTarget);j--) {
						for(Connection connection: nextLine.getStations().get(j).getConnections()) {
							if (connection.getLine().equals(nextLine) && connection.getStation().equals(nextLine.getStations().get(j-1))) {
								res.add(nextLine.getStations().indexOf(start) - j, connection);
								break;
							}
						}
					}						
				}				
				
				PrintRoute(start,res);
				return;
			}
			
			else {
				for(Exchange j: lines.get(i).getExchanges()) {
					if (!lines.contains(j.getLine())) {
						lines.add(j.getLine());
						prev_line.add(lines.get(i));
					}
				}
			}
			i++;
		}
		
		System.out.println("Route has not been found");
		return;
		
	}
	
	private static ArrayList<Connection> FewestExchangesSilent(Station start, Station dest) {

		ArrayList<Connection> res = new ArrayList<Connection>();
		
		if(start.equals(dest)) {
			return res;
		}
		
	
		ArrayList<TrainLine> lines = new ArrayList<TrainLine>();
		ArrayList<TrainLine> prev_line = new ArrayList<TrainLine>();
		
		
		for(TrainLine i: start.getLines()) {
			lines.add(i);
			prev_line.add(null);
		}
		
		int i=0;
		
		while(i<lines.size()) {
			
			if (lines.get(i).getStations().contains(dest)) {

				TrainLine previousLine = prev_line.get(i);
				TrainLine nextLine = lines.get(i);
				Station transfer;
				Station currentTarget = dest;
				
				while(!(previousLine == null)) {
		
					transfer = null;
					
					for(Exchange j: previousLine.getExchanges()) {
						if (j.getLine().equals(nextLine)) {
							transfer = j.getStation();
							break;
						}
					}
		
					if (transfer == null) {
						System.out.println("An error has occured");
						return null;
					}
					
					if (nextLine.getStations().indexOf(transfer) < nextLine.getStations().indexOf(currentTarget)) {
						for(int j = nextLine.getStations().indexOf(transfer);j<nextLine.getStations().indexOf(currentTarget);j++) {
							for(Connection connection: nextLine.getStations().get(j).getConnections()) {
								if (connection.getLine().equals(nextLine) && connection.getStation().equals(nextLine.getStations().get(j+1))) {
									res.add(j - nextLine.getStations().indexOf(transfer), connection);
									break;
								}
							}
						}
					}
					else {
						for(int j = nextLine.getStations().indexOf(transfer);j>nextLine.getStations().indexOf(currentTarget);j--) {
							for(Connection connection: nextLine.getStations().get(j).getConnections()) {
								if (connection.getLine().equals(nextLine) && connection.getStation().equals(nextLine.getStations().get(j-1))) {
									res.add(nextLine.getStations().indexOf(transfer) - j, connection);
									break;
								}
							}
						}						
					}
					
					currentTarget = transfer;
					nextLine = previousLine;
					previousLine = prev_line.get(lines.indexOf(nextLine));
				}
				
				if (nextLine.getStations().indexOf(start) < nextLine.getStations().indexOf(currentTarget)) {
					for(int j = nextLine.getStations().indexOf(start);j<nextLine.getStations().indexOf(currentTarget);j++) {
						for(Connection connection: nextLine.getStations().get(j).getConnections()) {
							if (connection.getLine().equals(nextLine) && connection.getStation().equals(nextLine.getStations().get(j+1))) {
								res.add(j - nextLine.getStations().indexOf(start), connection);
								break;
							}
						}
					}
				}
				else {
					for(int j = nextLine.getStations().indexOf(start);j>nextLine.getStations().indexOf(currentTarget);j--) {
						for(Connection connection: nextLine.getStations().get(j).getConnections()) {
							if (connection.getLine().equals(nextLine) && connection.getStation().equals(nextLine.getStations().get(j-1))) {
								res.add(nextLine.getStations().indexOf(start) - j, connection);
								break;
							}
						}
					}						
				}				
				
				return res;
			}
			
			else {
				for(Exchange j: lines.get(i).getExchanges()) {
					if (!lines.contains(j.getLine())) {
						lines.add(j.getLine());
						prev_line.add(lines.get(i));
					}
				}
			}
			i++;
		}
		
		return res;
		
	}

	
	public static void FewestExchanges(Station start, Station dest) {

		if(start.equals(dest)) {
			System.out.println("Start and destination is the same");
			return;
		}
		
		ArrayList<TrainLine> lines = new ArrayList<TrainLine>();
		ArrayList<TrainLine> prev_line = new ArrayList<TrainLine>();
		
		for(TrainLine i: start.getLines()) {
			lines.add(i);
			prev_line.add(null);
		}
		
		int i=0;
		
		while(i<lines.size()) {
			
			if (lines.get(i).getStations().contains(dest)) {
				String res = null;
				TrainLine previousLine = prev_line.get(i);
				TrainLine nextLine = lines.get(i);
				
				res = "Get on " + lines.get(i).getName() + "\n\t Get off at " + dest.getName() + "\n";
				while(!(previousLine == null)) {
		
					Station transfer = null;
					
					for(Exchange j: previousLine.getExchanges()) {
						if (j.getLine().equals(nextLine)) {
							transfer = j.getStation();
							break;
						}
					}
		
					if (transfer == null) {
						System.out.println("An error has occured");
						return;
					}
					
					res = "Get on " + previousLine.getName() + "\n\t Get off at " + transfer.getName() + "\n" + res;
					nextLine = previousLine;
					previousLine = prev_line.get(lines.indexOf(nextLine));
				}
				System.out.println(res);
				return;
			}
			
			else {
				for(Exchange j: lines.get(i).getExchanges()) {
					if (!lines.contains(j.getLine())) {
						lines.add(j.getLine());
						prev_line.add(lines.get(i));
					}
				}
			}
			i++;
		}
		
		
		System.out.println("Route has not been found");
		return;
		
	}

	public static void FewestExchangesStops(Station start, Station dest) {

		if(start.equals(dest)) {
			System.out.println("Start and destination is the same");
			return;
		}
		
		ArrayList<TrainLine> lines = new ArrayList<TrainLine>();
		ArrayList<Exchange> lineExchange = new ArrayList<Exchange>();
		
		for(TrainLine i: start.getLines()) {
			lines.add(i);
			lineExchange.add(null);
		}
		
		int i=0;
		
		while(i<lines.size()) {
			
			if (lines.get(i).getStations().contains(dest)) {
				String res = null;
				Exchange exchange = lineExchange.get(i);
				TrainLine nextLine = lines.get(i);
				Station before;
				
				if (exchange == null) {
					before = start;
				}
				else {
					before = exchange.getStation();
				}
				
				
				res = "Get on " + lines.get(i).getName() + "\n\t Ride " + Math.abs(lines.get(i).getStations().indexOf(before) - lines.get(i).getStations().indexOf(dest)) + " stops to " + dest.getName() + "\n";
				
				while(!(exchange == null)) {
					
					if (lineExchange.get(lines.indexOf(exchange.getLine()))==null) {
						before=start;
					}
					else {
						before = lineExchange.get(lines.indexOf(exchange.getLine())).getStation();
					}
					
					res = "Get on " + exchange.getLine().getName() + "\n\t Ride " + Math.abs(exchange.getLine().getStations().indexOf(before) - exchange.getLine().getStations().indexOf(exchange.getStation())) + " stops to " + exchange.getStation().getName() + "\n" + res;
					nextLine = exchange.getLine();
					exchange = lineExchange.get(lines.indexOf(nextLine));
				}
				
				System.out.println(res);
				return;
			}
			
			else {
				for(Exchange j: lines.get(i).getExchanges()) {
					if (!lines.contains(j.getLine())) {
						lines.add(j.getLine());
						lineExchange.add(new Exchange(j.getStation(), lines.get(i)));
					}
				}
			}
			i++;
		}
		
		
		System.out.println("Route has not been found");
		return;
		
	}
}
