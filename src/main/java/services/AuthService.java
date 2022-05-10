package services;


import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService(targetNamespace = "namespace")
public interface AuthService {

    @WebMethod
    boolean logIn(String username, String password);

}
