package com.tunstall.grandstream.Home_Screen_Activity.MVP;

import android.app.DialogFragment;

import com.tunstall.grandstream.AppSettings;
import com.tunstall.grandstream.Base.Base_Presenter;
import com.tunstall.grandstream.Base.Base_View;
import com.tunstall.grandstream.Fragments.MVP.AlertDialogFragment;


public class Home_Activity_Contract {



    interface View extends Base_View<Presenter> {

      void showAlert(int dialogId);


      void showAlert(int dialogId, AppSettings appSettings);


      void showAlert(int dialogId, String message);
    }

    interface Presenter extends Base_Presenter<View> {

        void getCharacters(boolean isOnline);

        boolean getLoadingState();

        boolean isLastPage();
}

}
