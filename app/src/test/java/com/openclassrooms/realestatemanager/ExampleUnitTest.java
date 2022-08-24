package com.openclassrooms.realestatemanager;

import org.junit.Test;

import static org.junit.Assert.*;

import androidx.test.platform.app.InstrumentationRegistry;

import com.openclassrooms.realestatemanager.UI.fragments.SimulatorFragment;
import com.openclassrooms.realestatemanager.placeholder.PlaceholderContent;
import com.openclassrooms.realestatemanager.utils.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void getPriceDollarToEur_isCorrect() {
        PlaceholderContent.PlaceholderItem testResidPrice = new PlaceholderContent.PlaceholderItem();
        testResidPrice.setResidPrice("100000");

        int testedResidPrice = Utils.convertDollarToEuro(Integer.parseInt(testResidPrice.getResidPrice()));
        int expectedResidPrice = 98000;

        assertEquals(expectedResidPrice, testedResidPrice);
    }

    @Test
    public void getPriceEurToDollar_isCorrect() {
        PlaceholderContent.PlaceholderItem testResidPrice = new PlaceholderContent.PlaceholderItem();
        testResidPrice.setResidPrice("100000");

        int testedResidPrice = Utils.convertEuroToDollar(Integer.parseInt(testResidPrice.getResidPrice()));
        int expectedResidPrice = 102000;

        assertEquals(expectedResidPrice, testedResidPrice);
    }

    @Test
    public void getInterestCost_isCorrect() {
        int testDuration = 14;
        int testLoanAmount = 100000;

        int testedInterestCost = SimulatorFragment.getInterestCost(testDuration, testLoanAmount);
        int expectedInterestCost = 1330;

        assertEquals(expectedInterestCost, testedInterestCost);
    }

    @Test
    public void getMonthlyFee_isCorrect() {
        int testDuration = 14;
        int testLoanAmount = 100000;
        double testInterest = 0.0133;

        int testedMonthlyFee = SimulatorFragment.getMonthlyFee(testDuration, testLoanAmount, testInterest);
        int expectedMonthlyFee = 652;

        assertEquals(expectedMonthlyFee, testedMonthlyFee);
    }

    @Test
    public void getTodayDate_isCorrect() {
        DateFormat testDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String expectedDateFormat = testDateFormat.format(new Date());

        String testedDateFormat = Utils.getTodayDate();

        assertEquals(expectedDateFormat, testedDateFormat);
    }
}