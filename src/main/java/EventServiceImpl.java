import DAL.HibernateFactory;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import entities.Event;
import org.hibernate.Session;
import services.EventService;

import javax.jws.HandlerChain;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.persistence.Query;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.MTOM;
import javax.xml.ws.soap.SOAPBinding;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@MTOM
@WebService(targetNamespace = "namespace")
@BindingType(value = SOAPBinding.SOAP11HTTP_MTOM_BINDING)
public class EventServiceImpl implements EventService {

    @Override
    public void storeEvent(@WebParam(name = "date") Date date, @WebParam(name = "name") String name,
                           @WebParam(name = "type") String type, @WebParam(name = "description") String description) {
        Session session = HibernateFactory.getSessionFactory().openSession();
        session.beginTransaction();

        Event event = new Event();
        event.setName(name);
        event.setType(type);
        event.setDescription(description);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        event.setDate(calendar.get(Calendar.DAY_OF_MONTH));
        event.setWeek(calendar.get(Calendar.WEEK_OF_MONTH));
        event.setMonth(calendar.get(Calendar.MONTH));
        event.setYear(calendar.get(Calendar.YEAR));

        session.persist(event);

        session.getTransaction().commit();
        session.close();
    }

    @Override
    public List<Event> getEvents() {
        Session session = HibernateFactory.getSessionFactory().openSession();

        Query query = session.createQuery("from Event");

        return new ArrayList<>((List<Event>) query.getResultList());
    }

    @Override
    public List<Event> getEventsForDay(@WebParam(name = "date") Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        Session session = HibernateFactory.getSessionFactory().openSession();

        Query query = session.createQuery("from Event where year=:year and month=:month and date=:day");
        query.setParameter("year", year);
        query.setParameter("month", month);
        query.setParameter("day", day);

        return new ArrayList<>((List<Event>) query.getResultList());
    }

    @Override
    public List<Event> getEventsForWeek(@WebParam(name = "date") Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        Session session = HibernateFactory.getSessionFactory().openSession();

        Query query = session.createQuery("from Event where year=:year and month=:month and week=:week");
        query.setParameter("year", year);
        query.setParameter("month", month);
        query.setParameter("week", week);

        return new ArrayList<>((List<Event>) query.getResultList());
    }

    @Override
    public Event getEventById(@WebParam(name = "id") String id) {
        Session session = HibernateFactory.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Event where id=unhex(:id)");
        id = id.replace("-", "");
        query.setParameter("id", id);
        return (Event) query.getSingleResult();
    }

    @Override
    public void updateEvent(@WebParam(name = "id") String id, @WebParam(name = "date") Date date, @WebParam(name = "name") String name,
                            @WebParam(name = "type") String type, @WebParam(name = "description") String description) {

        Session session = HibernateFactory.getSessionFactory().openSession();
        session.beginTransaction();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        id = id.replace("-", "");

        Query query = session.createQuery("update Event set date=:day, month=:month, week=:week, year=:year, type=:type, description=:description" +
                ", name=:name where id=unhex(:id)");

        query.setParameter("id", id);
        query.setParameter("day", day);
        query.setParameter("year", year);
        query.setParameter("month", month);
        query.setParameter("week", week);
        query.setParameter("name", name);
        query.setParameter("type", type);
        query.setParameter("description", description);

        query.executeUpdate();
    }

    @Override
    public byte[] getPdf(@WebParam(name = "events") List<Event> events) {
        Document doc = new Document();
        try {
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream("file.pdf"));
            System.out.println("PDF created.");
            doc.open();
            for(Event event : events){
                doc.add(new Paragraph(event.getName() + " (" + event.getType() + ")"));
                doc.add(new Paragraph(event.getDate() + "." + (( event.getMonth() + 1) < 9 ? ("0" + (event.getMonth() + 1)) : (event.getMonth() + 1)) + "." + event.getYear()));
                doc.add(new Paragraph(event.getDescription()));
                doc.add(new Paragraph("---"));
            }

            doc.close();
            writer.close();
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }

        byte[] pdf;

        try {
            pdf = Files.readAllBytes(Path.of("file.pdf"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(Arrays.toString(pdf));
        return pdf;
    }

}
