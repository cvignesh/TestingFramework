package com.ausum.qa.core;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.asserts.Assertion;
import org.testng.asserts.SoftAssert;

public class BaseTest {
	
	public static WebDriver driver = null;
	public String baseUrl = null;
	protected Assertion hardAssert = new Assertion();
	protected SoftAssert softAssert = new SoftAssert();
	private static final Logger LOGGER = Logger.getLogger(BaseTest.class);
	public static Properties configuration = new Properties();
	public InputStream input = null;
	
	@BeforeMethod(alwaysRun = true)
	public void setUp() 
	{
		try {
			input = new FileInputStream(System.getProperty("user.dir")+"\\resources\\config.properties");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			configuration.load(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setBrowser(configuration.getProperty("BrowserName"));
		launchUrl();
	}
	
	private void launchUrl() {
		String url = configuration.getProperty("ApplicationUrl");
		driver.get(url);
		LOGGER.debug("Launching "+url);
		driver.manage().window().maximize();
		LOGGER.debug("Maximizing Window");
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		LOGGER.debug("Setting Synchronization time as 20 seconds");
		driver.manage().deleteAllCookies();
		LOGGER.debug("Deleting Cookies");		
	}

	public void takeScreenshot() throws IOException
	{
	 String folderName = getClass().getSimpleName();
     File screenshotFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
	 String imageFileName = driver.getCurrentUrl().substring(50)+new SimpleDateFormat("MM-dd-yyyy_HH-ss").format(new GregorianCalendar().getTime()) + ".png";
	 File imgaeFileLocation = new File(System.getProperty("user.dir")+"\\target\\"+folderName+"\\"+imageFileName);
	 FileUtils.copyFile(screenshotFile, imgaeFileLocation);
	 LOGGER.debug("Taking Screenshot");
	}
	private void setBrowser(String browserName)
	{
		if("firefox".equals(browserName))
		{
			driver = new FirefoxDriver();
			LOGGER.debug("Opening Firefox Browser");
		}
		else if("chrome".equals(browserName))
		{
			if(null == System.getProperty("webdriver.chrome.driver"))
			{
				System.setProperty("webdriver.chrome.driver",System.getProperty("user.dir")+"\\resources\\chromedriver.exe");
			}
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--test-type");
			options.addArguments("--disable-extensions");
			driver = new ChromeDriver();
			LOGGER.debug("Opening Chrome Browser");
		}
	}
	
	@AfterMethod
	public void closeBrowser()
	{
		driver.quit();
	}
}
