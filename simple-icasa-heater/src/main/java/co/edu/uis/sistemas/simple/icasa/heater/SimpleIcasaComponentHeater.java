package co.edu.uis.sistemas.simple.icasa.heater;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;

import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.temperature.Cooler;
import fr.liglab.adele.icasa.device.temperature.Heater;
import fr.liglab.adele.icasa.device.temperature.Thermometer;


@Component(name="SimpleIcasaComponentHeater")
@Instantiate
public class SimpleIcasaComponentHeater implements DeviceListener{
	
	@Requires(id="heaters")
	private Heater[] heaters;
	
	@Requires(id="thermometers")
	private Thermometer[] thermometers;
	
	@Requires(id="coolers")
	private Cooler[] coolers;
	
	private Thread modifyHeatersThread;
	
	@Bind(id="thermometers")
	protected void bindThermometer(Thermometer thermometer) {
		thermometer.addListener(this);
	}
	
	@Unbind(id="thermometers")
	protected void unbindThermometers(Thermometer thermometer)
	{
		thermometer.removeListener(this);
	}

	protected List<Heater> getHeaters() {
		return Collections.unmodifiableList(Arrays.asList(heaters));
	}
	
	protected List<Thermometer> getThermometers() {
		return Collections.unmodifiableList(Arrays.asList(thermometers));
	}
	
	protected List<Cooler> getCoolers() {
		return Collections.unmodifiableList(Arrays.asList(coolers));
	}

	
	@Validate
	public void start() {
		/*modifyHeatersThread = new Thread(new ModifyHeatersRunnable());
		modifyHeatersThread.start();*/
	}
	
	@Invalidate
	public void stop() throws InterruptedException {
		modifyHeatersThread.interrupt();
		modifyHeatersThread.join();
	}

	
	/*class ModifyHeatersRunnable implements Runnable {

		public void run() {
						
			boolean running = true;
			
			while (running) {
				//try {
					List<Heater> heaters = getHeaters();
					for (Heater heater : heaters) {
						heater.setPowerLevel(0.2);
					}
					//Thread.sleep(1000);					
				/*} catch (InterruptedException e) {
					running = false;
				}
			}
			
		}
		
	}*/


	public void deviceAdded(GenericDevice arg0) {
		// TODO Auto-generated method stub
		
	}

	public void devicePropertyAdded(GenericDevice arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	public void devicePropertyModified(GenericDevice device, String property, Object value) {
		// TODO Auto-generated method stub
		if(property == Thermometer.THERMOMETER_CURRENT_TEMPERATURE)
		{
			List<Heater> heaters = getHeaters();
			List<Cooler> coolers = getCoolers();
			if(Double.parseDouble(value.toString()) <= 290)
			{
				for (Heater heater : heaters) {
					if(heater.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME) == device.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME))
					{
						heater.setPowerLevel(0.2);
						for (Cooler cooler : coolers)
						{
							if(cooler.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME) == device.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME))
							{
								cooler.setPowerLevel(0.0);
							}
						}
					}
				}
			}
			else if(Double.parseDouble(value.toString()) >= 300)
			{
				for (Cooler cooler : coolers) 
				{
					if(cooler.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME) == device.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME))
					{
						cooler.setPowerLevel(0.2);
						for (Heater heater : heaters)
						{
							if(heater.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME) == device.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME))
							{
								heater.setPowerLevel(0.0);
							}
						}
					}
				}
			}
		}
	}

	public void devicePropertyRemoved(GenericDevice arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	public void deviceRemoved(GenericDevice arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
