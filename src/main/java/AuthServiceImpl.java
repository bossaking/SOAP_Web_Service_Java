import services.AuthService;

import javax.jws.HandlerChain;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceProvider;
import java.util.Locale;

@WebService(targetNamespace = "namespace")
@HandlerChain(file="handler-chain.xml")
public class AuthServiceImpl implements AuthService {
    @Override
    public boolean logIn(@WebParam(name = "username") String username, @WebParam(name = "password") String password) {
        username = username.toLowerCase(Locale.ROOT);
        return username.equals("admin") && password.equals("Admin123@");
    }
}
