package com.mikealbert.data;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.mikealbert.testing.BaseTest;
import com.mikealbert.data.comparator.MaintScheduleRuleComparator;
import com.mikealbert.data.entity.MaintScheduleRule;


public class MSComparatorTest extends BaseTest {

// null is greater than a value
	
	MaintScheduleRuleComparator comparator = new MaintScheduleRuleComparator();
	MaintScheduleRule a;
	MaintScheduleRule b;

	@Before
	public void setUp() {
		a = new MaintScheduleRule();
		b = new MaintScheduleRule();
	}
	
	
	
	@Test
	public void testBadComparator(){
		
		a.setYear("2016");
		a.setMakeCode("FORD");
		a.setMakeModelDesc("FOCUS");
		a.setFuelTypeGroup("GAS");
		a.setModelTypeDesc("CAR");
		
		b.setYear("2016");
		b.setMakeCode("FORD");
		b.setMakeModelDesc(null);
		b.setFuelTypeGroup("DIESEL");
		b.setModelTypeDesc("TRUCK");

		assertEquals(comparator.compare(a, b), -1);
		assertEquals(comparator.compare(b, a), 1);
	}
	
	@Test
	public void testModelAgainstNoModel(){
		
		a.setYear("2016");
		a.setMakeCode("FORD");
		a.setMakeModelDesc("FOCUS");
		a.setFuelTypeGroup("GAS");
		a.setModelTypeDesc("CAR");
		
		b.setYear("2016");
		b.setMakeCode("FORD");
		b.setMakeModelDesc(null);
		b.setFuelTypeGroup("GAS");
		b.setModelTypeDesc("CAR");

		assertEquals(comparator.compare(a, b), -1);
		assertEquals(comparator.compare(b, a), 1);
	}

	
	@Test
	public void testMake(){
		
		a.setYear("2016");
		a.setMakeCode("FORD");
		b.setYear("2016");
		b.setMakeCode("FORD");
		
		assertEquals(comparator.compare(a, b), 0);
		
		b.setMakeCode("TOYOTA");
		assertEquals(comparator.compare(a, b), -1);
		assertEquals(comparator.compare(b, a), 1);

		b.setMakeCode(null);
		assertEquals(comparator.compare(a, b), -1);
		assertEquals(comparator.compare(b, a), 1);

	}

	@Test
	public void testMakeModel(){
		
		a.setYear("2016");
		a.setMakeCode("FORD");
		a.setModelTypeDesc("CAR");
		a.setMakeModelDesc("FOCUS");
		b.setYear("2016");
		b.setMakeCode("FORD");
		b.setModelTypeDesc("CAR");
		b.setMakeModelDesc("FOCUS");
		
		assertEquals(comparator.compare(a, b), 0);
		
		b.setMakeModelDesc("TAURUS");
		assertEquals(comparator.compare(a, b), -1);
		assertEquals(comparator.compare(b, a), 1);

		b.setMakeModelDesc(null);
		assertEquals(comparator.compare(a, b), -1);
		assertEquals(comparator.compare(b, a), 1);
	}

	@Test
	public void testMakeFuel(){
		
		a.setYear("2016");
		a.setMakeCode("FORD");
		a.setFuelTypeGroup("GAS");
		b.setYear("2016");
		b.setMakeCode("FORD");
		b.setFuelTypeGroup("GAS");
		
		assertEquals(comparator.compare(a, b), 0);
		
		b.setFuelTypeGroup("DIESEL");
		assertEquals(comparator.compare(a, b), 1);
		assertEquals(comparator.compare(b, a), -1);

		b.setFuelTypeGroup(null);
		assertEquals(comparator.compare(a, b), -1);
		assertEquals(comparator.compare(b, a), 1);
	}

	@Test
	public void testMakeType(){
		
		a.setYear("2016");
		a.setMakeCode("FORD");
		a.setModelTypeDesc("CAR");
		b.setYear("2016");
		b.setMakeCode("FORD");
		b.setModelTypeDesc("CAR");

		assertEquals(comparator.compare(a, b), 0);
		
		b.setModelTypeDesc("TRUCK");
		assertEquals(comparator.compare(a, b), -1);
		assertEquals(comparator.compare(b, a), 1);

		b.setModelTypeDesc(null);
		assertEquals(comparator.compare(a, b), -1);
		assertEquals(comparator.compare(b, a), 1);
	}

	@Test
	public void testMakeTypeFuel(){
		
		a.setYear("2016");
		a.setMakeCode("FORD");
		a.setModelTypeDesc("CAR");
		a.setFuelTypeGroup("GAS");
		b.setYear("2016");
		b.setMakeCode("FORD");
		b.setModelTypeDesc("CAR");
		b.setFuelTypeGroup("GAS");

		assertEquals(comparator.compare(a, b), 0);
		
		b.setFuelTypeGroup("DIESEL");
		assertEquals(comparator.compare(a, b), 1);
		assertEquals(comparator.compare(b, a), -1);

		b.setFuelTypeGroup(null);
		assertEquals(comparator.compare(a, b), -1);
		assertEquals(comparator.compare(b, a), 1);
	}

	@Test
	public void testMakeAgainstFuel(){
		
		a.setYear("2016");
		a.setMakeCode("FORD");
		b.setYear("2016");
		b.setFuelTypeGroup("GAS");

		assertEquals(comparator.compare(a, b), -1);
		assertEquals(comparator.compare(b, a), 1);
	}

	@Test
	public void testMakeAgainstType(){
		
		a.setYear("2016");
		a.setMakeCode("FORD");
		b.setYear("2016");
		b.setModelTypeDesc("CAR");

		assertEquals(comparator.compare(a, b), -1);
		assertEquals(comparator.compare(b, a), 1);
	}

	@Test
	public void testTypeAgainstFuel(){
		
		a.setYear("2016");
		a.setModelTypeDesc("TRUCK");
		b.setYear("2016");
		b.setFuelTypeGroup("DIESEL");

		assertEquals(comparator.compare(a, b), -1);
		assertEquals(comparator.compare(b, a), 1);
		
		a.setFuelTypeGroup("GAS");
		assertEquals(comparator.compare(a, b), -1);
		assertEquals(comparator.compare(b, a), 1);

		b.setModelTypeDesc("CAR");;
		assertEquals(comparator.compare(a, b), 1);
		assertEquals(comparator.compare(b, a), -1);

	}

	@Test
	public void testMakeTypeModelFuelAgainstNoFuel(){
		
		a.setYear("2016");
		a.setMakeCode("FORD");
		a.setModelTypeDesc("CAR");
		a.setMakeModelDesc("FOCUS");
		b.setYear("2016");
		b.setMakeCode("FORD");
		b.setModelTypeDesc("CAR");
		b.setMakeModelDesc("FOCUS");

		assertEquals(comparator.compare(a, b), 0);

		b.setFuelTypeGroup("DIESEL");

		assertEquals(comparator.compare(a, b), 1);
		assertEquals(comparator.compare(b, a), -1);
		

	}

}
