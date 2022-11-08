package Moblima.Handlers;

import java.util.ArrayList;

import Moblima.Entities.Cineplex;
import Moblima.Utils.Settings;

public class CineplexHandler {
	
	private ArrayList<Cineplex> allCineplex;
	private static int cineplexCounter=0;
	private static CineplexHandler instance = null;
	
    public static CineplexHandler getInstance() {
        if (instance == null) {
            instance = new CineplexHandler();
        }
        return instance;
    }
	public CineplexHandler() {
		allCineplex = new ArrayList<Cineplex>();
	}
	
	public Cineplex addCineplex(String location, CinemaHandler cinemaHandler) {
		Cineplex newCineplex = new Cineplex(location);
		allCineplex.add(newCineplex);
		cineplexCounter++;
		cinemaHandler.initializeCinema(newCineplex);
		return newCineplex;
	   }
	
	public void printAllCineplex() {
		int count = 1;
		for (Cineplex temp : allCineplex){
			System.out.print(count++ + ". " + temp);
		}
		System.out.println();
    }
	
	public ArrayList<Cineplex> getAllCineplex(){
		return allCineplex;
	}
	
	public int getSize() {
		return cineplexCounter;
	}

	public void removeCineplex(Cineplex c){
		this.allCineplex.remove(c);
		c = null;
	   }
	
}