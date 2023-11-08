package Espresso;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.chatalk.LoginActivity;
import com.example.chatalk.MainActivity;
import com.example.chatalk.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.espresso.action.ViewActions;

import android.content.Intent;


import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import android.content.Intent;
import androidx.test.espresso.intent.Intents;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EspressoTesting {

    @Rule
    public IntentsTestRule<LoginActivity> activityRule = new IntentsTestRule<>(LoginActivity.class);
    protected String postContent,email,password ;
    public void setup(){
        //login
        email = "ductransa01@gmail.com";
        password = "duc161203";

        //post
        postContent = "Hello World";

    }

//    Login Test
    @Test
    public void LoginTest(){
        setup();
        Espresso.onView(ViewMatchers.withId(R.id.Email))
                .perform(ViewActions.typeText(email), ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withId(R.id.Password))
                .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withId(R.id.loginbtn)).perform(ViewActions.click());



    }

    @Test
    public void PostTest(){
        setup();

        Espresso.onView(ViewMatchers.withId(R.id.inputAddPost)).perform(ViewActions.typeText(postContent),ViewActions.closeSoftKeyboard());

    }


}
