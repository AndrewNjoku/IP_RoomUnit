package com.tunstall.grandstream.Fragments.MVP;

import com.tunstall.grandstream.Base.Base_Presenter;
import com.tunstall.grandstream.Base.Base_View;


public class AlertDialogue_Contract {



    interface View extends Base_View<Presenter> {

        showE

    }

    interface Presenter extends Base_Presenter<View> {
        void getCharacters(boolean isOnline);
        boolean getLoadingState();
        boolean isLastPage();
}

}
