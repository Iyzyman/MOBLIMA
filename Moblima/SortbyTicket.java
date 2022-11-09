package Moblima.Utils;
import java.util.Comparator;

import Moblima.Entities.Movie;
/**\
 * Class to sort movies by ticket sales
 * @author Ian
 *
 */
public class SortbyTicket implements Comparator<Movie> {
	/**
	 *compares size of ticket arrays of each movie and sort in descending order
	 */
	@Override
    public int compare(Movie o1, Movie o2) {

        return Integer.compare(o1.getTicketsSize(), o2.getTicketsSize());
}
}