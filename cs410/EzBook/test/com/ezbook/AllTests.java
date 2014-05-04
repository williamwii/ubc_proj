package com.ezbook;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AmazonServiceTest.class, httpRequestPHP.class, ServerTest.class, ServerUtilTest.class, ClientPlaceTest.class, com.ezbook.client.AllTests.class})
public class AllTests {

}
