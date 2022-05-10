package services;

import entities.Event;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@WebService(targetNamespace = "namespace")
public interface EventService {

    @WebMethod
    void storeEvent(Date date, String name, String type, String description);

    @WebMethod
    List<Event> getEvents();

    @WebMethod
    List<Event> getEventsForDay(Date date);

    @WebMethod
    List<Event> getEventsForWeek(Date date);

    @WebMethod
    Event getEventById(String id);

    @WebMethod
    void updateEvent(String id, Date date, String name, String type, String description);

    @WebMethod
    byte[] getPdf(List<Event> events);
}
