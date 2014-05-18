package org.voimala.votingapp.activities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.voimala.votingapp.R;
import org.voimala.votingapp.connections.aani.AaniConnection;
import org.voimala.votingapp.datasource.voting.Voting;
import org.voimala.votingapp.datasource.voting.VotingContainer;
import org.voimala.votingapp.fragments.adapters.TabsPagerAdapter;
import org.voimala.votingapp.sensors.MotionSensor;
import org.voimala.votingapp.services.LongPollingService;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    private ViewPager viewPager = null;
    private TabsPagerAdapter tabsPagerAdapter = null;
    private ActionBar actionBar = null;
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeEverything();
    }
    
    private void initializeEverything() {
        initializeViewPager();
        initializeActionBar();
        initializeTabs();
        initializeSettings();
        initializeConnectionManager();
        initializeVotingContainer();
        initializeLongPollingService();
        initializeMotionSensor();
    }

    private void initializeViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabsPagerAdapter);
        
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(final int position) {
                getActionBar().setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
        });
    }
    
    private void initializeActionBar() {
        actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    }

    private void initializeTabs() {
        String[] tabs = {getResources().getString(R.string.tab_open_votings),
                        getResources().getString(R.string.tab_closed_votings),
                        getResources().getString(R.string.tab_upcoming_votes)
                        };
        for (String tabName : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tabName).setTabListener(this));
        }
    }
    
    private void initializeVotingContainer() {
        VotingContainer.getInstance().setContext(this);
        VotingContainer.getInstance().initialize();
    }
    

    private void initializeConnectionManager() {
        AaniConnection.setContext(this);
    }
    
    private void initializeSettings() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        
        initializeActionBarListeners(menu);

        return super.onCreateOptionsMenu(menu);
    }

    private void initializeActionBarListeners(final Menu menu) {
        MenuItem menuItemPreferences = menu.findItem(R.id.menu_item_settings);
        menuItemPreferences.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                Intent intent = new Intent(MainActivity.this, PreferencesActivity.class);
                startActivity(intent);
                return false;
            }
        });
        
        /* The barcode scanner uses open source ZXing library.
         * The implementation has been done with the help of the following Tuts+ article:
         * http://code.tutsplus.com/tutorials/android-sdk-create-a-barcode-reader--mobile-17162
         * Thank you Tuts+ and Sue Smith!
         * 
         * This kind of implementation requires the user to download ZXing app from Play store
         * if he/she does not have it installed already. I tried to embedded the library into
         * this project so that the user would not need to download anything, but for some
         * reason it did not work as I expected.
         */
        MenuItem menuItemScan = menu.findItem(R.id.menu_item_scan);
        menuItemScan.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
                scanIntegrator.initiateScan();
                return false;
            }
        });
    }
    
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        // Retrieving barcode scan result.
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            parseQrCode(scanContent);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    
    private void parseQrCode(final String scanContent) {
        String votingId = "";
        try {
            Pattern uuidPattern = Pattern.compile(".{8}-.{4}-.{4}-.{4}-.{12}");
            Matcher matcher = uuidPattern.matcher(scanContent);
            
            
            if (matcher.find()) {
                votingId = matcher.group();
            }
        } catch (Exception e) {
            // Continue...
        }
        
        if (!votingId.isEmpty()) {
            openVotingId(votingId);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "This QR code does not contain a voting id.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private boolean openVotingId(final String votingId) {
        Voting voting = VotingContainer.getInstance().findOpenVotingById(votingId);
        if (voting != null) {
            Intent intent = new Intent(this, OpenVotingActivity.class);
            intent.putExtra("votingId", voting.getId());
            startActivity(intent);
            return true;
        }
        
        voting = VotingContainer.getInstance().findClosedVotingById(votingId);
        if (voting != null) {
            Intent intent = new Intent(this, ClosedVotingActivity.class);
            intent.putExtra("votingId", voting.getId());
            startActivity(intent);
            return true;
        }
        
        voting = VotingContainer.getInstance().findUpcomingVotingById(votingId);
        if (voting != null) {
            Intent intent = new Intent(this, UpcomingVotingActivity.class);
            intent.putExtra("votingId", voting.getId());
            startActivity(intent);
            return true;
        }
        
        Toast toast = Toast.makeText(getApplicationContext(),
                "Voting id does not found.", Toast.LENGTH_SHORT);
        toast.show();
        return false;
    }

    private void initializeLongPollingService() {
           Intent intent = new Intent(this, LongPollingService.class);
           this.startService(intent); 
    }
    
    private void initializeMotionSensor() {
        MotionSensor.getInstance().initialize(this);
    }

    @Override
    public void onTabReselected(final Tab tab, final FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(final Tab tab, final FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(final Tab tab, final FragmentTransaction fragmentTransaction) {
    }

}
