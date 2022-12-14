package Moblima.DataBase;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import Moblima.Entities.Cineplex;
import Moblima.Entities.Movie;
import Moblima.Entities.Movie.MovieStatus;
import Moblima.Entities.Movie.MovieType;
import Moblima.Entities.Cinema.HallType;
import Moblima.Handlers.CinemaHandler;
import Moblima.Handlers.CineplexHandler;
import Moblima.Handlers.MovieHandler;
import Moblima.Handlers.SeatHandler;
import Moblima.Handlers.ShowHandler;
import Moblima.Utils.UtilityOutput;

/**
 * Class to initialize example 
 * @author Whole team
 * @version 1.0
 */
public class ExampleAdder {
	/**
	 * Default Constructor
	 */
	public ExampleAdder(){}

	/**
	 * Initialize our examples for testing
	 */
	public static void initializeExample() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
		
		CineplexHandler cineplexHandler = CineplexHandler.getInstance();
		MovieHandler movieHandler = MovieHandler.getInstance();
		ShowHandler showHandler = ShowHandler.getInstance();
		SeatHandler seatHandler = SeatHandler.getInstance();
		CinemaHandler cinemaHandler = CinemaHandler.getInstance();
	  
		Cineplex jurong = cineplexHandler.addCineplex("JurongPoint", cinemaHandler);
		Cineplex jem = cineplexHandler.addCineplex("JEM", cinemaHandler);
		Cineplex jcube = cineplexHandler.addCineplex("JCube", cinemaHandler);
	  
		cinemaHandler.addCinema(HallType.STANDARD, 35, jurong);
		cinemaHandler.addCinema(HallType.PREMIUM, 10, jurong);
		cinemaHandler.addCinema(HallType.VIP, 1, jurong);

		cinemaHandler.addCinema(HallType.STANDARD, 35, jem);
		cinemaHandler.addCinema(HallType.PREMIUM, 10, jem);
		cinemaHandler.addCinema(HallType.VIP, 1, jem);

		cinemaHandler.addCinema(HallType.STANDARD, 35, jcube);
		cinemaHandler.addCinema(HallType.PREMIUM, 10, jcube);
		cinemaHandler.addCinema(HallType.VIP, 1, jcube);
		
		String dateInString_passed = "1/1/2021 10:00:00 AM";
		String dateInString_coming = "23/12/2022 09:00:00 AM";
		String christmas_date = "25/12/2022 13:00:00 AM";
		String tuesday = "27/12/2022 13:00:00 AM";
		try {
			Date date_passed = formatter.parse(dateInString_passed);
			Date date = formatter.parse(dateInString_coming);
			Date christmas = formatter.parse(christmas_date);
			Date tuesdate = formatter.parse(tuesday);

			showHandler.addShows(date, movieHandler.getMovie().get(0), CinemaHandler.getInstance().getCinemaFromCineplex(jurong).get(0), seatHandler);
			showHandler.addShows(date,movieHandler.getMovie().get(1), CinemaHandler.getInstance().getCinemaFromCineplex(jurong).get(1), seatHandler);
			showHandler.addShows(christmas,movieHandler.getMovie().get(1), CinemaHandler.getInstance().getCinemaFromCineplex(jurong).get(2), seatHandler);
			showHandler.addShows(date,movieHandler.getMovie().get(1), CinemaHandler.getInstance().getCinemaFromCineplex(jurong).get(0), seatHandler);
			showHandler.addShows(date_passed, movieHandler.getMovie().get(0), CinemaHandler.getInstance().getCinemaFromCineplex(jurong).get(0), seatHandler);

			showHandler.addShows(date, movieHandler.getMovie().get(0), CinemaHandler.getInstance().getCinemaFromCineplex(jem).get(1), seatHandler);
			showHandler.addShows(date,movieHandler.getMovie().get(2), CinemaHandler.getInstance().getCinemaFromCineplex(jem).get(0), seatHandler);
			showHandler.addShows(tuesdate,movieHandler.getMovie().get(1), CinemaHandler.getInstance().getCinemaFromCineplex(jem).get(0), seatHandler);
			showHandler.addShows(date,movieHandler.getMovie().get(2), CinemaHandler.getInstance().getCinemaFromCineplex(jem).get(0), seatHandler);
			showHandler.addShows(christmas, movieHandler.getMovie().get(0), CinemaHandler.getInstance().getCinemaFromCineplex(jem).get(0), seatHandler);

			showHandler.addShows(date, movieHandler.getMovie().get(0), CinemaHandler.getInstance().getCinemaFromCineplex(jcube).get(0), seatHandler);
			showHandler.addShows(date,movieHandler.getMovie().get(2), CinemaHandler.getInstance().getCinemaFromCineplex(jcube).get(0), seatHandler);
			showHandler.addShows(tuesdate,movieHandler.getMovie().get(1), CinemaHandler.getInstance().getCinemaFromCineplex(jcube).get(0), seatHandler);
			showHandler.addShows(date,movieHandler.getMovie().get(2), CinemaHandler.getInstance().getCinemaFromCineplex(jcube).get(0), seatHandler);
			showHandler.addShows(tuesdate, movieHandler.getMovie().get(0), CinemaHandler.getInstance().getCinemaFromCineplex(jcube).get(0), seatHandler);

		} catch (ParseException e) {
		 e.printStackTrace();
		}
	   }
	
	/**
	 * Prints example after initialized
	 */
	public static void showExample() {
		CineplexHandler cineplexHandler = CineplexHandler.getInstance();
		ShowHandler showHandler = ShowHandler.getInstance();
		
		UtilityOutput.printMessage("Show all cineplexes:");
		cineplexHandler.printAllCineplex();
		UtilityOutput.printMessage("\n\nShow all cinemas:");
		for (Cineplex temp : cineplexHandler.getAllCineplex()) {
			UtilityOutput.printObjectList(CinemaHandler.getInstance().getCinemaFromCineplex(temp));
		   }
		UtilityOutput.printMessage("\nShow all shows:");
		UtilityOutput.printMessage(showHandler.getAllShows().toString());
	}
	
	/**
	 * Read movie and adds them to instance for examples
	 * @param fileName file name to read from
	 * @throws FileNotFoundException file cannot be found
	 */
	public static void readMovieFromTextFile(String fileName) throws FileNotFoundException{
		MovieHandler movieHandler = MovieHandler.getInstance();
    	FileReader movieDatabase = new FileReader(fileName);
    	Scanner read = new Scanner(movieDatabase);
    	read.useDelimiter("\\|");
    	read.nextLine();
    	
    	String movieName, movieDirector, movieSynopsis, movieCast;
    	MovieStatus movieStatus;
    	MovieType movieType;
    	Movie newMovie;
    	while(read.hasNext()) {
    		//read.next();
    		movieName = read.next();
    		movieType = MovieType.valueOf(read.next());
    		movieStatus  = MovieStatus.valueOf(read.next());
    		movieDirector = read.next();
    		movieSynopsis = read.next();
    		movieCast = read.next();
    		newMovie = new Movie(movieName, movieType, movieStatus, movieDirector, movieSynopsis, movieCast);
			movieHandler.addMovie(newMovie);
    	}
    	read.close();
	}
	
}
