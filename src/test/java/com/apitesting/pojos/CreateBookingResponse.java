package com.apitesting.pojos;

public class CreateBookingResponse{
    private int bookingid;
    private Booking booking;
    public int getBookingid(){
        return bookingid;
    }

    public void setBookingid(int bookingid){
        this.bookingid=bookingid;
    }

    public void setBooking(Booking booking){
        this.booking = booking;
    }
    public Booking getBooking(){
        return booking;
    }
}
