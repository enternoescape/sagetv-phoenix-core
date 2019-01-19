package test.junit;

import org.junit.BeforeClass;
import org.junit.Test;
import sagex.phoenix.weather.ICurrentForecast;
import sagex.phoenix.weather.IForecastPeriod;
import sagex.phoenix.weather.IForecastPeriod.Type;
import sagex.phoenix.weather.ILongRangeForecast;
import sagex.phoenix.weather.IWeatherSupport2.Units;
import sagex.phoenix.weather.yahoo.YahooWeatherSupport2;
import test.InitPhoenix;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author jusjoken
 */
public class TestYahooWeather {
    @BeforeClass
    public static void init() throws IOException {
        InitPhoenix.init(true, true);
    }

    @Test
    public void testYahooWeather() {
        YahooWeatherSupport2 w = new YahooWeatherSupport2();
        w.removeLocation();
        w.setLocation("55373");
        System.out.println("Yahoo location: " + w.getLocation());

        if (w.hasError()) {
            fail("Failed to set location: " + w.getError());
        }

        w.setUnits(Units.Standard);
        assertEquals("Failed to update units to Standard", true, w.getUnits().equals(Units.Standard));

        boolean updated = w.update();
        System.out.println("Yahoo location: " + w.getLocation() + " Name '" + w.getLocationName() + "'");
        if (w.hasError()) {
            fail("Failed to update weather: " + w.getError());
        }
        assertEquals("Failed to update weather", true, updated);

        updated = w.update();
        if (w.hasError()) {
            fail("Failed to update weather: " + w.getError());
        }
        assertEquals("Weather updated, but should not have", false, updated);

        assertEquals("Rockford, MN", w.getLocationName());

        testWeather(w);

        // now retest with metric for the same location
        w.setUnits(Units.Metric);
        assertEquals("Failed to update units to Metric", true, w.getUnits().equals(Units.Metric));
        updated = w.update();
        testWeather(w);

        // repeat tests for Canada postal
        w.removeLocation();
        w.setLocation("n5y5m4");
        System.out.println("Yahoo location: " + w.getLocation());

        if (w.hasError()) {
            fail("Failed to set location: " + w.getError());
        }

        w.setUnits(Units.Metric);
        assertEquals("Failed to update units to Metric", true, w.getUnits().equals(Units.Metric));

        updated = w.update();
        System.out.println("Yahoo location: " + w.getLocation() + " Name '" + w.getLocationName() + "'");
        if (w.hasError()) {
            fail("Failed to update weather: " + w.getError());
        }
        assertEquals("Failed to update weather", true, updated);

        updated = w.update();
        if (w.hasError()) {
            fail("Failed to update weather: " + w.getError());
        }
        assertEquals("Weather updated, but should not have", false, updated);

        assertEquals("London, ON", w.getLocationName());

        testWeather(w);

    }

    private void testWeather(YahooWeatherSupport2 w) {
        ICurrentForecast curWeather = w.getCurrentWeather();
        System.out.println(w.getLocationName() + " - " + w.getUnits().name() + " - " + w.getRecordedDate() + " - " + curWeather);
        // assertEquals("Current forecast day not correct", "Now",
        // curWeather.getDay());
        assertEquals("Current forecast type not correct", Type.Current, curWeather.getType());
        assertNotNull("Temp was null", curWeather.getTemp());
        assertNotNull("Date was null", curWeather.getDate());
        assertNotNull("Code was null", curWeather.getCode());
        assertNotNull("Condition was null", curWeather.getCondition());

        // validate supported elements
        SupportedTest("curWeather.getCloudCover", curWeather.getCloudCover(), false);
        SupportedTest("curWeather.getCode", curWeather.getCode(), true);
        SupportedTest("curWeather.getCondition", curWeather.getCondition(), true);
        SupportedTest("curWeather.getDescription", curWeather.getDescription(), false);
        SupportedTest("curWeather.getDewPoint", curWeather.getDewPoint(), false);
        SupportedTest("curWeather.getFeelsLike", curWeather.getFeelsLike(), true);
        SupportedTest("curWeather.getHumid", curWeather.getHumid(), true);
        SupportedTest("curWeather.getPrecip", curWeather.getPrecip(), false);
        SupportedTest("curWeather.getPressure", curWeather.getPressure(), true);
        SupportedTest("curWeather.getPressureDir", curWeather.getPressureDir(), true);
        SupportedTest("curWeather.getSunrise", curWeather.getSunrise(), true);
        SupportedTest("curWeather.getSunset", curWeather.getSunset(), true);
        SupportedTest("curWeather.getTemp", curWeather.getTemp(), true);
        SupportedTest("curWeather.getUVIndex", curWeather.getUVIndex(), false);
        SupportedTest("curWeather.getUVWarn", curWeather.getUVWarn(), false);
        SupportedTest("curWeather.getVisibility", curWeather.getVisibility(), true);
        SupportedTest("curWeather.getWindDir", curWeather.getWindDir(), true);
        SupportedTest("curWeather.getWindDirText", curWeather.getWindDirText(), true);
        SupportedTest("curWeather.getWindSpeed", curWeather.getWindSpeed(), true);

        List<ILongRangeForecast> days = w.getForecasts();
        assertTrue("No forecast data", days.size() > 0);
        int daynum = 0;
        for (ILongRangeForecast wd : days) {
            IForecastPeriod day = wd.getForecastPeriodDay();
            System.out.println("  Day " + daynum + " '" + day);
            IForecastPeriod night = wd.getForecastPeriodNight();
            System.out.println("Night " + daynum + " '" + night);
            if (daynum == 0) {
                assertNotNull("Night Period for first day was null", night);
                assertEquals("Forecast type for '" + daynum + "' not correct", Type.Night, night.getType());
            }
            if (daynum == 1) {
                assertNotNull("Day Period for second day was null", day);
                assertEquals("Forecast type for '" + daynum + "' not correct", Type.Day, day.getType());
            }
            if (day != null) {
                assertNotNull("No Condition", day.getCondition());
                assertNotNull("No Code", day.getCode());
                assertNotNull("No Date", day.getDate());
                assertNotNull("No Temp", day.getTemp());
                assertNotNull("No Type", day.getType());
                assertEquals(day.getType(), Type.Day);
                // validate supported elements
                SupportedTest("day.getCode", day.getCode(), true);
                SupportedTest("day.getCondition", day.getCondition(), true);
                SupportedTest("day.getHumid", day.getHumid(), false);
                SupportedTest("day.getTemp", day.getTemp(), true);
                SupportedTest("day.getWindDir", day.getWindDir(), false);
                SupportedTest("day.getWindDirText", day.getWindDirText(), false);
                SupportedTest("day.getWindSpeed", day.getWindSpeed(), false);
                SupportedTest("day.getDescription", day.getDescription(), false);
                SupportedTest("day.getPrecip", day.getPrecip(), false);
            }

            if (night != null) {
                assertNotNull("No Condition", night.getCondition());
                assertNotNull("No Code", night.getCode());
                assertNotNull("No Date", night.getDate());
                assertNotNull("No Temp", night.getTemp());
                assertNotNull("No Type", night.getType());
                assertEquals(night.getType(), Type.Night);
                // validate supported elements
                SupportedTest("night.getCode", night.getCode(), true);
                SupportedTest("night.getCondition", night.getCondition(), true);
                SupportedTest("night.getHumid", night.getHumid(), false);
                SupportedTest("night.getTemp", night.getTemp(), true);
                SupportedTest("night.getWindDir", night.getWindDir(), false);
                SupportedTest("night.getWindDirText", night.getWindDirText(), false);
                SupportedTest("night.getWindSpeed", night.getWindSpeed(), false);
                SupportedTest("night.getDescription", night.getDescription(), false);
                SupportedTest("night.getPrecip", night.getPrecip(), false);
            }
            daynum++;
        }

    }

    private void SupportedTest(String testElement, int valueToTest, boolean expectedResult) {
        assertEquals("Supported not set correctly for '" + testElement + "'", expectedResult,
                valueToTest != IForecastPeriod.iNotSupported);
    }

    private void SupportedTest(String testElement, String valueToTest, boolean expectedResult) {
        assertEquals("Supported not set correctly for '" + testElement + "'", expectedResult,
                !valueToTest.equals(IForecastPeriod.sNotSupported));
    }
}
