package com.example.labise.ViewModel

object FormatterViewModel {

    fun formatForFirebaseDatabase(stringToFormat : String) : String{

        if(stringToFormat != null){
            var modifyStringToFormat = stringToFormat

            modifyStringToFormat = modifyStringToFormat.replace('@','-')
            modifyStringToFormat = modifyStringToFormat.replace('.','-')

            return modifyStringToFormat
        }else{
            return ""
        }

    }

}