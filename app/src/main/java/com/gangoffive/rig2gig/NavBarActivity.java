package com.gangoffive.rig2gig;


import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class NavBarActivity extends AppCompatActivity
{
    /**
     * This method is used to create the navigation bar.
     * @param savedInstanceState This is the saved previous state passed from the previous fragment/activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //Factory design pattern
        switch(menuItem.getItemId())
        {
            case R.id.nav_my_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyProfileFragment()).commit();
                break;
            case R.id.nav_my_band:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyBandFragment()).commit();
                break;
            case R.id.nav_create_performer_advertisement:
                Intent performerAdIntent = new Intent(this, CreatePerformerAdvertisement.class);
                startActivity(performerAdIntent);
                break;
            case R.id.nav_create_venue_advertisement:
                Intent venueAdIntent = new Intent(this, CreateVenueAdvertisement.class);
                startActivity(venueAdIntent);
                break;
            case R.id.nav_create_band_advertisement:
                Intent bandAdIntent = new Intent(this, CreateBandAdvertisement.class);
                startActivity(bandAdIntent);
                break;
            case R.id.nav_create_musician_advertisement:
                Intent musicianAdIntent = new Intent(this, CreateMusicianAdvertisement.class);
                startActivity(musicianAdIntent);
                break;
            case R.id.nav_create_band:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CreateBandFragment()).commit();
                break;
            case R.id.nav_view_bands:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ViewPerformersFragment()).commit();
                break;
            case R.id.nav_view_venues:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ViewVenuesFragment()).commit();
                break;
            case R.id.nav_notifications:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotificationsFragment()).commit();
                break;
            case R.id.nav_about_us:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutUsFragment()).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                break;
            case R.id.nav_privacy_policy:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PrivacyPolicyFragment()).commit();
                break;
            default:
                break;
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
        super.onCreate(savedInstanceState);

        //Decide which navbar to display.
        NavigationContext navigationContext = new NavigationContext();
        startActivity(new Intent(this, navigationContext.navBarFinder(CredentialActivity.userType)));
    }
}
