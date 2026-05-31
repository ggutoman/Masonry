package org.gag.appdriver.Libraries.TextLibrary

class TextFormatter {

    fun ExtractFromCharacter(fsWholeText : String, fsChar : String) : List<String>{

        return fsWholeText.split(fsChar).let {
            if(it.size < 1 ){
                listOf()
            } else {
               it
            }
        }
    }

    fun ReplaceText(fsWholeText : String, fsChar : String, fnPart : Int, newText : String) : String{

        return fsWholeText.replace(fsWholeText.split(fsChar).get(fnPart), newText)
    }
}