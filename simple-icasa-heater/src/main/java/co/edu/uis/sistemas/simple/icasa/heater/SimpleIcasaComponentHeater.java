package co.edu.uis.sistemas.simple.icasa.heater;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.temperature.Heater;


@Component(name="SimpleIcasaComponentHeater")
@Instantiate
public class SimpleIcasaComponentHeater {
	
	@Requires(id="heaters")
	private Heater[] heaters;
	
	private Thread modifyHeatersThread;
	
	@Bind(id="heaters")
	protected void bindLight(Heater Heater) {
		System.out.println("A new heater has been added to the platform " + Heater.getSerialNumber());
	}

	protected List<Heater> getHeaters() {
		return Collections.unmodifiableList(Arrays.asList(heaters));
	}

	
	@Validate
	public void start() {
		modifyHeatersThread = new Thread(new ModifyHeatersRunnable());
		modifyHeatersThread.start();
	}
	
	@Invalidate
	public void stop() throws InterruptedException {
		modifyHeatersThread.interrupt();
		modifyHeatersThread.join();
	}

	
	class ModifyHeatersRunnable implements Runnable {

		public void run() {
						
			boolean running = true;
			
			boolean onOff = false;
			while (running) {
				onOff = !onOff;
				//try {
					List<Heater> heaters = getHeaters();
					for (Heater heater : heaters) {
						heater.setPowerLevel(0.2);
					}
					//Thread.sleep(1000);					
				/*} catch (InterruptedException e) {
					running = false;
				}*/
			}
			
		}
		
	}
	
}
