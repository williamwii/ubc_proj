package com.ezbook.client;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

//import com.ezbook.client.ui.EditListingViewImpl;

@RunWith(Suite.class)
@SuiteClasses({ EditListingViewImplTest.class, MarketplaceViewImplTest.class,SubscriptionsViewImplTest.class , PostViewImplTest.class, RestOfViewImplTest.class})
public class AllTests {

}
