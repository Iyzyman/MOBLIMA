package Moblima.DataBase;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import Moblima.Entities.Cineplex;
import Moblima.Entities.Movie;
import Moblima.Entities.Show;
import Moblima.Entities.User;
import Moblima.Entities.Cinema.HallType;
import Moblima.Handlers.CinemaHandler;
import Moblima.Handlers.CineplexHandler;
import Moblima.Handlers.MovieHandler;
import Moblima.Handlers.SeatHandler;
import Moblima.Handlers.ShowHandler;

public class ExampleAdder {
	
	public static void initializeExample() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
		
		CineplexHandler cineplexHandler = CineplexHandler.getInstance();
		MovieHandler movieHandler = MovieHandler.getInstance();
		ShowHandler showHandler = ShowHandler.getInstance();
		SeatHandler seatHandler = SeatHandler.getInstance();
		
		Cineplex jurong = cineplexHandler.addCineplex("JurongPoint");
		CinemaHandler JurongPoint = new CinemaHandler("JurongPoint");
		JurongPoint.addCinema(HallType.STANDARD, 35, jurong);
		JurongPoint.addCinema(HallType.PREMIUM, 10, jurong);
		cineplexHandler.getAllCineplex().get(cineplexHandler.getSize()-1).setHall(JurongPoint);
		JurongPoint.addCinema(HallType.VIP, 1, jurong);
		
		User ayush = new User("Ayush","ayus@gmail.com",3293131);

		//Movie ironMan = new Movie("Iron Man","showing","Jon Favreaue","AAA", "Example Cast...", movieHandler);
       // Movie avengers = new Movie("Avengers: End Game", "showing","Jon Favreaue","BBB", "Example Cast...", movieHandler);
        String dateInString = "23/12/2020 09:00:00 AM";
		try {
			Date date = formatter.parse(dateInString);
			Show show1 = showHandler.addShows(date, movieHandler.getMovie().get(0), cineplexHandler.getAllCineplex().get(0).getHall().get(0), seatHandler);
			Show show2 = showHandler.addShows(date,movieHandler.getMovie().get(1),cineplexHandler.getAllCineplex().get(0).getHall().get(0), seatHandler);
			Show show3 = showHandler.addShows(date,movieHandler.getMovie().get(1),cineplexHandler.getAllCineplex().get(0).getHall().get(1), seatHandler);
			Show show4 = showHandler.addShows(date,movieHandler.getMovie().get(1),cineplexHandler.getAllCineplex().get(0).getHall().get(2), seatHandler);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public static void showExample() {
		CineplexHandler cineplexHandler = CineplexHandler.getInstance();
		cineplexHandler.printAllCineplex();
		System.out.println("\nShow all cinemas:");
		cineplexHandler.getAllCineplex().get(0).printAllCinema();
	}
	
	public static void readMovieFromTextFile(String fileName) throws FileNotFoundException{
		MovieHandler movieHandler = MovieHandler.getInstance();
    	FileReader movieDatabase = new FileReader(fileName);
    	Scanner read = new Scanner(movieDatabase);
    	read.useDelimiter("\\||\\r\\n");
    	read.nextLine();
    	String movieName, movieStatus, movieDirector, movieSynopsis, movieCast;
    	Movie newMovie;
    	while(read.hasNext()) {
    		//read.next();
    		movieName = read.next();
    		movieStatus = read.next();
    		movieDirector = read.next();
    		movieSynopsis = read.next();
    		movieCast = read.next();
    		newMovie = new Movie(movieName, movieStatus, movieDirector, movieSynopsis, movieCast);
			movieHandler.addMovie(newMovie);
    	}
    	read.close();
	}
	
}