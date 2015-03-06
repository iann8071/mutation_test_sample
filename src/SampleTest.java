import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.firefox.FirefoxDriver;


public class SampleTest {
	FirefoxDriver driver;
	String url = "http://url";

	@Before
	public void initDriver() throws Exception {
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
	}

	@After
	public void close() {
		driver.quit();
	}

	@Test
	public void test(){
		driver.get(url);
	}
}
