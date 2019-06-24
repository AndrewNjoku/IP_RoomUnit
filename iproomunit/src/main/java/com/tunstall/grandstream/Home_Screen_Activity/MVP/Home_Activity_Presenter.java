package com.tunstall.grandstream.Home_Screen_Activity.MVP;

import com.tunstall.grandstream.AppSettings;
import com.tunstall.grandstream.Base.Base_Presenter;

public class Home_Activity_Presenter implements Base_Presenter {

    Home_Activity_Contract.View myView;


    public Home_Activity_Presenter(Home_Activity_Contract.View home_activity) {

        this.myView=home_activity;
    }

    @Override
    public void start() {


    }

    @Override
    public void stop() {


    }

    public void showErrorMessageInFragment(int dialogueID)

    {


        myView.showAlert(dialogueID);

    }
    public void showErrorMessageInFragment(int dialogueID,AppSettings appSettings)

    {

        myView.showAlert(dialogueID,appSettings);
    }
    public void showErrorMessageInFragmentWithMessage(int dialogueID,String message)

    {

        myView.showAlert(dialogueID,message);
    }
}
