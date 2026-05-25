package org.gag.appdriver.App.Dashboard

import android.content.Context
import org.gag.appdriver.Constants.MENU_ITEM_CONSTANTS
import org.gag.appdriver.Constants.MENU_PARENT_CONSTANTS
import org.gag.appdriver.Room.ML_DBF

class Dashboard(loInstance : Context) {

    val poDB = ML_DBF.getDatabase(loInstance)

    fun GetUserInfo() = poDB

    fun GetParentMenus(fnUserLvl : Int) : List<MENU_PARENT_CONSTANTS>{

        return buildList{

            for (entries in MENU_PARENT_CONSTANTS.entries){

                if (entries.fnActive > 0){

                    if (fnUserLvl >= 0){
                        add(entries)
                    }
                }
            }

        }
    }

    fun GetParentItems(fnUserLvl : Int, fsParentIDx : String) : List<MENU_ITEM_CONSTANTS>{

        return buildList {

            for (items in MENU_ITEM_CONSTANTS.entries){

                if (items.fnActive > 0){

                    if (fsParentIDx.equals(items.fsParentIDx)){
                        if (fnUserLvl >= 0){
                            add(items)
                        }
                    }
                }
            }
        }
    }
}