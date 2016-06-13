import org.apache.commons.configuration2.XMLConfiguration;

public class Configurazioni {
	public static void main(String[] args) {
		XMLConfiguration configCreate = new XMLConfiguration();
		configCreate.setFileName("settings.xml");
		configCreate.addProperty("somesetting", "somevalue");
		configCreate.save();
	}
}
