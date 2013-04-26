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
	
	//Para listar los calentadores de la casa
	@Requires(id="heaters")
	private Heater[] heaters;
	
	//Para escuchar los eventos de cambio de propiedad de los termometros
	@Requires(id="thermometers")
	private Thermometer[] thermometers;
	
	//Para listar los enfriadores de la casa
	@Requires(id="coolers")
	private Cooler[] coolers;
	
	
	//Cuando se agrega un termometro se pone a escuchar el cambio de sus propiedades!
	@Bind(id="thermometers")
	protected void bindThermometer(Thermometer thermometer) {
		thermometer.addListener(this);
	}
	
	//Cuando se elimina un termometro se deja a escuchar el cambio de sus propiedades!
	@Unbind(id="thermometers")
	protected void unbindThermometers(Thermometer thermometer)
	{
		//Se apagan los calentadores y enfriadores del cuarto donde está el termometro que se quito
		for (Heater heater : heaters)
		{
			if(heater.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME) == thermometer.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME))
				heater.setPowerLevel(0.0);
		}
		
		
		for (Cooler cooler : coolers)
		{
			//El enfriador de la habitación donde está el termometro debe ser apagado
			if(cooler.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME) == thermometer.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME))
				cooler.setPowerLevel(0.0);
		}
		
		thermometer.removeListener(this);
	}

	//Lista los calentadores de la casa
	protected List<Heater> getHeaters() {
		return Collections.unmodifiableList(Arrays.asList(heaters));
	}
	
	//Lista los enfriadores de la casa
	protected List<Cooler> getCoolers() {
		return Collections.unmodifiableList(Arrays.asList(coolers));
	}

	public void deviceAdded(GenericDevice arg0) {
		// TODO Auto-generated method stub
		
	}

	public void devicePropertyAdded(GenericDevice arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	//Evento que se ejecuta cuando a los termometros se les afecta alguna propiedad
	public void devicePropertyModified(GenericDevice device, String property, Object value) {
		
		//Se asegura que la pripiedad que se modificó sea la temperatura
		if(property == Thermometer.THERMOMETER_CURRENT_TEMPERATURE)
		{
			//Obtiene La lista de calentadores y enfriadores
			List<Heater> heaters = getHeaters();
			List<Cooler> coolers = getCoolers();
			//La temperatura es muy baja?
			if(Double.parseDouble(value.toString()) <= 290)
			{
				//Recorre todos los heaters
				for (Heater heater : heaters) {
					//La localización del calentador es la misma que el termometro que bajo de temperatura?
					if(heater.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME) == device.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME))
					{
						//Empieza a calentar
						heater.setPowerLevel(1.0);
						//Recorre todos los enfriadores
						for (Cooler cooler : coolers)
						{
							//El enfriador de la habitación donde está el termometro debe ser apagado
							if(cooler.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME) == device.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME))
								cooler.setPowerLevel(0.0);
						}
					}
				}
			}
			//Se elevó la temperatura?
			else if(Double.parseDouble(value.toString()) >= 300)
			{
				//Recorro los enfriadores
				for (Cooler cooler : coolers) 
				{
					//El enfriador está en la misma localización del termometro?
					if(cooler.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME) == device.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME))
					{
						//Empieza a enfriar
						cooler.setPowerLevel(1.0);
						//Recorre los calentadores para apagr el que esté en la misma habitación del termometro
						for (Heater heater : heaters)
						{
							if(heater.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME) == device.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME))
								heater.setPowerLevel(0.0);
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
