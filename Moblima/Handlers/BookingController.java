package Moblima.Handlers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Moblima.Entities.Booking;
import Moblima.Entities.Seats;
import Moblima.Entities.Show;
import Moblima.Entities.Ticket;
import Moblima.Entities.User;
import Moblima.Utils.Settings;
import Moblima.Utils.UtilityInputs;
import Moblima.Entities.Cinema.HallType;
import Moblima.Exceptions.InvalidInputException;
import Moblima.Exceptions.SeatsNotAvailableException;

public class BookingController {
	private UserHandler userHandler; 
	private final String STUDENT_PRICE_STANDARD = "ticket_price_student_standard";
	private final String ADULT_PRICE_STANDARD = "ticket_price_adult_standard";

	private final String STUDENT_PRICE_PREMIUM = "ticket_price_student_premium";
	private final String ADULT_PRICE_PREMIUM = "ticket_price_adult_premium";

	private final String STUDENT_PRICE_VIP = "ticket_price_student_vip";
	private final String ADULT_PRICE_VIP = "ticket_price_adult_vip";

	private final String PUBLIC_HOLIDAYS = "public_holiday_dates";
	private final String DISCOUNT_DAyS = "weekly_discount_days";
	private final String DISCOUNT_DAYS_RATE = "weekly_discount_rates";
	
	public BookingController(UserHandler userHandler) {
		this.userHandler = userHandler;
	}

	public static void bookMenu(){
		System.out.println("--------------MOBLIMA BOOKING MENU!--------------");
		System.out.println("| 01: List All Shows                            |");
		System.out.println("| 02: Search Shows by Name                      |");
		System.out.println("| 03: View Movie by Location                    |");
		System.out.println("| 04: Go Back                                   |");
		System.out.println("-------------------------------------------------");
		System.out.print("Enter option ('4' to return): ");
	}

	public int getMenuChoice(){
		return UtilityInputs.getIntUserInput();
	}

	public ArrayList<Show> getShowList(int choice){
		ShowHandler showHandler = ShowHandler.getInstance();
		CineplexHandler cineplexHandler = CineplexHandler.getInstance();
		
		ArrayList<Show> shows = null;
		try{
			switch(choice){
				case 1:
					shows = showHandler.getAllShows();
					break;
				case 2:
					String searchString = UtilityInputs.getSearchString();
					shows = showHandler.searchShows(searchString);
					break;
				case 3:
					cineplexHandler.printAllCineplex();
					try{
						int userInput = UtilityInputs.getCineplex();
						if (userInput > cineplexHandler.getSize()) throw new InvalidInputException("Cineplex does not exist");
						shows = showHandler.getAllShowsByLocation(cineplexHandler.getAllCineplex().get(userInput-1));
					} catch(InvalidInputException e ){
						System.out.println(e.getMessage());
					} 
					break;
				case 4:
					return null;
				default:
					throw new InvalidInputException("Invalid Input, please enter only 1 - 4");
			}
		}catch(InvalidInputException e){
			System.out.println(e.getMessage());
		}
		if (shows == null){
			System.out.println("No shows found");
		}
		return shows;
	}

	public ArrayList<Ticket> bookShow (ArrayList<Show> shows, User user1){
		SeatHandler seatHandler = SeatHandler.getInstance();
		Booking newBooking = new Booking(user1);
		ShowHandler.printAllShows(shows);
		Show selectedShow = UtilityInputs.getShow(shows);
		if (selectedShow == null) return null;

		newBooking.setShow(selectedShow);
		seatHandler.printAvailableSeats(selectedShow);
		newBooking.setAdultTicket(UtilityInputs.getNumberOfTicket("Adult"));
		newBooking.setStudentTicket(UtilityInputs.getNumberOfTicket("Student"));

		if (newBooking.getStudentTicketNum() == 0 && newBooking.getAdultTicketNum() == 0){
			System.out.println("Cancelling booking");
			return null;
		}

		ArrayList<Seats> seats = selectSeats(newBooking);
		if (seats != null){
			newBooking.setSeats(seats);
			ArrayList<Ticket> tickets = bookSeats(newBooking);
			return tickets;
		}
		return null;
	}

	public ArrayList<Seats> selectSeats (Booking newBooking){
		SeatHandler seatHandler = SeatHandler.getInstance();
        ArrayList<Seats> chosenSeats = new ArrayList<Seats>();
		int totalTickets = newBooking.getAdultTicketNum() + newBooking.getStudentTicketNum();
		if (!seatHandler.checkCapacity(totalTickets, newBooking.getShow())){
			System.out.println("Not enough tickets remaining");
			return null;
		}

        while (true){
			for(int j = 0; j < totalTickets; j++){
				while (true){
					try{
						Seats s1 = UtilityInputs.getSeatSelection(j);
						if (s1 == null) return null;
						if (seatHandler.checkSeatAvailability(s1, newBooking.getShow()) && !SeatHandler.duplicateSeatInput(s1, chosenSeats)){
							chosenSeats.add(s1);
							break;
						}else{
							throw new SeatsNotAvailableException("Seat not available.\nEnter 0 to exit");
						}
					} catch (SeatsNotAvailableException e){
						System.out.println(e.getMessage());
						continue;
					}
					
				}
			}
			break;
		}
        return chosenSeats;
    }

	public ArrayList<Ticket> bookSeats(Booking newBooking){
        ArrayList<Seats> seatlist = newBooking.getSeats();
        int confirmation = 0;
        if (seatlist != null){
            confirmation = bookingConfirmation(newBooking);
        }
        ArrayList<Ticket> ticketList = new ArrayList<>();
        if (confirmation == 1){
            for(Seats s: seatlist) {
				if (newBooking.getAdultTicketNum() > 0){
					ticketList.add(newBooking.getShow().bookTicket(newBooking.getUser(), s, newBooking.getAdultPrice()));
					newBooking.setAdultTicket(newBooking.getAdultTicketNum()-1);
				} else {
					ticketList.add(newBooking.getShow().bookTicket(newBooking.getUser(), s, newBooking.getStudentPrice()));
					newBooking.setStudentTicket(newBooking.getStudentTicketNum()-1);
				}
			}
        }
        return ticketList;
    }

	public boolean isHoliday(Settings settings, Calendar cal){
		try{
			String ph_dates = settings.getProperty(PUBLIC_HOLIDAYS);
			String[] arrOfStr = ph_dates.split(",");
			for (String s : arrOfStr){
				if (Integer.parseInt(s.split("/", 2)[0]) == cal.get(Calendar.DAY_OF_MONTH) && Integer.parseInt(s.split("/", 2)[1]) == cal.get(Calendar.MONTH) + 1){
					return true;
				}
			}
		} catch (NullPointerException e){
			return false;
		}
		
		return false;
	}

	public int weeklyDiscounts(Settings settings, Calendar cal){
		String discount_days = settings.getProperty(DISCOUNT_DAyS);
		try{
			String[] days = discount_days.split(",");
		for (String s : days){
			if(Integer.parseInt(s) == cal.get(Calendar.DAY_OF_WEEK)){
				String discount_rate = settings.getProperty(DISCOUNT_DAYS_RATE);
				return Integer.parseInt(discount_rate);
			}
		}
		} catch (NullPointerException e){
			return 0;
		}
		return 0;		
	}

	public void calcPrice(Booking newBooking){
		Settings settings = Settings.getInstance();
		Calendar calendar = Calendar.getInstance();
		int discounts = 0;
		Double studentPrice = 0.0;
		Double adultPrice = 0.0;
		HallType cinemaClass = newBooking.getShow().getCinema().getCinemaClass();
		Date showtime = newBooking.getShow().getShowTime();
		calendar.setTime(showtime);
		try{
			if (cinemaClass == HallType.STANDARD){
				studentPrice = Double.parseDouble(settings.getProperty(STUDENT_PRICE_STANDARD));
				adultPrice = Double.parseDouble(settings.getProperty(ADULT_PRICE_STANDARD));
			} else if (cinemaClass == HallType.PREMIUM){
				studentPrice = Double.parseDouble(settings.getProperty(STUDENT_PRICE_PREMIUM));
				adultPrice = Double.parseDouble(settings.getProperty(ADULT_PRICE_PREMIUM));
			} else if (cinemaClass == HallType.VIP) {
				studentPrice = Double.parseDouble(settings.getProperty(STUDENT_PRICE_VIP));
				adultPrice = Double.parseDouble(settings.getProperty(ADULT_PRICE_VIP));
			}
		} catch (NumberFormatException e){
			System.out.println("Invalid pricing for student or adult, please check approach Admins or Call us");
			return;
		}

		try{
			discounts = weeklyDiscounts(settings, calendar);
			studentPrice -= discounts;
			adultPrice -= discounts;
			if (isHoliday(settings, calendar)){
				studentPrice += Double.parseDouble(settings.getProperty("public_holiday_price_increase"));
				adultPrice += Double.parseDouble(settings.getProperty("public_holiday_price_increase"));
			}
		}catch (NumberFormatException e) {}

		newBooking.setAdultPrice(adultPrice);
		newBooking.setStudentPrice(studentPrice);
	}

	public String generateTransactionID(Booking newBooking){
		String transactionID = Integer.toString(newBooking.getShow().getCinema().getCinemaID());;
		final int CINEMACODELENGTH = 4;
		while (transactionID.length() < CINEMACODELENGTH){
			transactionID = "0" + transactionID;
		}
		Date now = new Date();
		SimpleDateFormat DateFor = new SimpleDateFormat("yyyyMMddHHmm");
		String stringDate= DateFor.format(now);;

		transactionID = transactionID + stringDate;
		return transactionID;
	}

    public int bookingConfirmation(Booking newBooking){
    	SeatHandler seatHandler = SeatHandler.getInstance();
		calcPrice(newBooking);
		if (newBooking.getStudentPrice() == 0.0 || newBooking.getAdultPrice() == 0) return 0;
		double totalPrice = newBooking.getStudentTicketNum() * newBooking.getStudentPrice() + newBooking.getAdultTicketNum() * newBooking.getAdultPrice();
        while(true){
			try{
				char confirmation = UtilityInputs.getConfirmation(totalPrice);
				if (Character.toUpperCase(confirmation) == 'Y'){
					for (Seats s: newBooking.getSeats()){
						seatHandler.removeSeats(s, newBooking.getShow());
					}
					System.out.println("Your transaction ID is: " + generateTransactionID(newBooking));
					return 1;
				} else if (Character.toUpperCase(confirmation) == 'N'){
					System.out.println("Booking unsuccessful");
					System.out.println("Returning back to main menu");
					return 0;
				} else {
					throw new InvalidInputException("Enter only Y or N");
				}
			}catch (InvalidInputException e){
				System.out.println(e.getMessage());
			}
            
        }
    }

}