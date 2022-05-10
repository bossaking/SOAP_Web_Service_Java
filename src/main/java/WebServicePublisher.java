import DAL.HibernateFactory;
import entities.Event;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.jws.HandlerChain;
import javax.servlet.annotation.WebFilter;
import javax.xml.ws.Endpoint;
import java.util.Date;
import java.util.List;

public class WebServicePublisher {
    public static void main(String[] args){
        Endpoint.publish("http://localhost:8080/api/login", new AuthServiceImpl());
        Endpoint.publish("http://localhost:8080/api/events", new EventServiceImpl());
    }
}
