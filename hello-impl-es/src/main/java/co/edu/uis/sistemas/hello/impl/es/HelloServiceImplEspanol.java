package co.edu.uis.sistemas.hello.impl.es;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;

import co.edu.uis.sistemas.hello.api.HelloService;


@Component(name="HelloServiceComponentEspanol")
@Provides(specifications=HelloService.class)
@Instantiate
public class HelloServiceImplEspanol implements HelloService {

	public String sayBye(String name) {
		return "Adiós " + name;
	}

	public String sayHello(String name) {
		return "Hola " + name;
	}

}
